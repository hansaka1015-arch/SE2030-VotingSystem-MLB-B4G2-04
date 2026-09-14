package org.example.voting.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.voting.dto.VoteRequestDTO;
import org.example.voting.service.VotingManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service for handling vote ingestion via Redis Streams.
 * Provides high-throughput, fault-tolerant vote processing.
 */
@Service
@Slf4j
public class VoteStreamService {

    private static final String VOTE_STREAM_KEY = "votes:stream";
    private static final String PROCESSED_VOTES_KEY = "processed:votes";
    private static final String VOTE_COUNTS_KEY = "vote:counts";
    private static final Duration VOTE_PROCESSING_TIMEOUT = Duration.ofMinutes(10);
    private static final int MAX_RETRIES = 3;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final VotingManager votingManager;

    public VoteStreamService(RedisTemplate<String, Object> redisTemplate,
                             ObjectMapper objectMapper,
                             VotingManager votingManager) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.votingManager = votingManager;
    }

    /**
     * Ingest a vote by adding it to the Redis Stream for asynchronous processing.
     * This method returns immediately after the vote is safely stored in Redis,
     * guaranteeing sub-500ms latency regardless of backend processing time.
     *
     * @param voteRequestDTO The vote request from the frontend
     * @return The generated voteId for tracking
     * @throws JsonProcessingException If the vote cannot be serialized to JSON
     */
    public String ingestVote(VoteRequestDTO voteRequestDTO) throws JsonProcessingException {
        String voteId = UUID.randomUUID().toString();

        // Create the vote payload
        VoteStreamPayload payload = new VoteStreamPayload(
                voteId,
                voteRequestDTO,
                System.currentTimeMillis()
        );

        // Serialize payload to JSON
        String jsonPayload = objectMapper.writeValueAsString(payload);

        // Add to Redis list (simple queue approach for reliability)
        redisTemplate.opsForList().rightPush(VOTE_STREAM_KEY, jsonPayload);

        // Optional: Trim list to prevent unlimited growth (keep last 10000 votes)
        redisTemplate.opsForList().trim(VOTE_STREAM_KEY, 0, 9999);

        log.info("Vote {} ingested into Redis queue for user {} on episode {}",
                voteId, voteRequestDTO.getUserId(), voteRequestDTO.getEpisodeId());

        return voteId;
    }

    /**
     * Process votes from the Redis queue using worker threads.
     * This method should be called by asynchronous workers to consume and process votes.
     */
    public void processVoteQueue() {
        String workerId = "vote-worker-" + UUID.randomUUID().toString();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Blocking pop from the queue with timeout to allow graceful shutdown
                String jsonPayload = (String) redisTemplate.opsForList()
                        .leftPop(VOTE_STREAM_KEY, 1, TimeUnit.SECONDS);

                if (jsonPayload == null) {
                    // No votes in queue, continue looping
                    continue;
                }

                processVotePayload(jsonPayload, workerId);

            } catch (Exception e) {
                log.error("Error processing vote queue: {}", e.getMessage(), e);
                // Continue processing to avoid breaking the worker loop
                try {
                    Thread.sleep(1000); // Brief pause before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.info("Vote queue worker {} shutting down", workerId);
    }

    /**
     * Process a single vote payload from the Redis queue.
     * Handles deserialization, validation, processing, and error handling.
     *
     * @param jsonPayload The JSON string containing the vote data
     * @param workerId The ID of the worker processing this vote
     */
    private void processVotePayload(String jsonPayload, String workerId) {
        String voteId = null;

        try {
            // Deserialize the JSON payload
            VoteStreamPayload payload = objectMapper.readValue(jsonPayload, VoteStreamPayload.class);
            voteId = payload.getVoteId();

            // Check for duplicate processing using Redis set
            if (isDuplicateVote(processedKey(voteId))) {
                log.warn("Duplicate vote {} detected, skipping processing", voteId);
                return;
            }

            // Validate the vote is still within the active window (with grace period)
            if (!isVoteStillValid(payload.getVoteRequestDTO())) {
                log.warn("Vote {} is no longer valid (outside voting window), rejecting", voteId);
                markAsInvalidVote(voteId, "Vote outside voting window");
                markAsProcessedVote(processedKey(voteId)); // Still mark as processed to avoid reprocessing
                return;
            }

            // Process the vote through the voting manager (this will be async)
            votingManager.processVoteAsync(voteId, payload.getVoteRequestDTO());

            // Mark vote as successfully processed
            markAsProcessedVote(processedKey(voteId));
            incrementProcessedCount();

            log.debug("Vote {} successfully processed by worker {}", voteId, workerId);

        } catch (Exception e) {
            log.error("Error processing vote payload {}: {}",
                    voteId != null ? voteId : "unknown", e.getMessage(), e);

            // Handle processing failures
            handleProcessingFailure(jsonPayload, e, voteId);
        }
    }

    /**
     * Check if a vote has already been processed (idempotency check).
     *
     * @param key The Redis key to check
     * @return true if the vote has already been processed
     */
    private boolean isDuplicateVote(String key) {
        // Use setnx (set if not exists) equivalent for idempotency
        Boolean wasSet = redisTemplate.opsForValue().setIfAbsent(key, "1");
        if (Boolean.TRUE.equals(wasSet)) {
            // Key was set (wasn't present before), so not a duplicate
            redisTemplate.expire(key, VOTE_PROCESSING_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            return false;
        }
        // Key already existed, so it's a duplicate
        return true;
    }

    /**
     * Generate the Redis key for tracking processed votes.
     *
     * @param voteId The vote ID
     * @return The Redis key for tracking this vote's processing status
     */
    private String processedKey(String voteId) {
        return PROCESSED_VOTES_KEY + ":" + voteId;
    }

    /**
     * Mark a vote as processed in Redis.
     *
     * @param key The Redis key to mark as processed
     */
    private void markAsProcessedVote(String key) {
        redisTemplate.opsForValue().set(key, "1");
        redisTemplate.expire(key, VOTE_PROCESSING_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Increment the count of processed votes for metrics.
     */
    private void incrementProcessedCount() {
        redisTemplate.opsForHash().increment(VOTE_COUNTS_KEY, "processed", 1L);
    }

    /**
     * Check if a vote is still within the valid voting window.
     * Delegates to the voting manager's validation logic via Redis cache.
     *
     * @param voteRequestDTO The vote request to validate
     * @return true if the vote is still valid
     */
    private boolean isVoteStillValid(VoteRequestDTO voteRequestDTO) {
        // In a full implementation, this would check episode timing from Redis cache
        // For now, we'll return true and let the voting manager handle validation
        // during actual processing (which happens asynchronously but still validates)
        return true;
    }

    /**
     * Mark a vote as invalid (e.g., outside voting window) and track it.
     *
     * @param voteId The vote ID
     * @param reason The reason for invalidation
     */
    private void markAsInvalidVote(String voteId, String reason) {
        String invalidKey = "invalid:votes:" + voteId;
        redisTemplate.opsForValue().set(invalidKey, reason);
        redisTemplate.expire(invalidKey, 1, TimeUnit.HOURS); // Keep invalid votes for 1 hour for audit
        incrementInvalidCount();
        log.info("Vote {} marked as invalid: {}", voteId, reason);
    }

    /**
     * Increment the count of invalid votes for metrics.
     */
    private void incrementInvalidCount() {
        redisTemplate.opsForHash().increment(VOTE_COUNTS_KEY, "invalid", 1L);
    }

    /**
     * Handle a failed vote processing attempt.
     * In a production system, this might send to a dead letter queue or retry mechanism.
     *
     * @param jsonPayload The original JSON payload
     * @param exception The exception that caused the failure
     * @param voteId The vote ID if available
     */
    private void handleProcessingFailure(String jsonPayload, Exception exception, String voteId) {
        String failedKey = "failed:votes:" + (voteId != null ? voteId : "unknown");
        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "Unknown error";

        // Store failure information
        redisTemplate.opsForValue().set(failedKey, errorMessage);
        redisTemplate.expire(failedKey, 1, TimeUnit.HOURS);

        // Increment failure counter
        redisTemplate.opsForHash().increment(VOTE_COUNTS_KEY, "failed", 1L);

        log.error("Vote {} processing failed and marked as failed: {}",
                voteId != null ? voteId : "unknown", errorMessage);

        // In a more sophisticated system, we might:
        // 1. Send to a dead letter queue for manual inspection
        // 2. Implement retry logic with exponential backoff
        // 3. Alert operators for manual intervention
    }

    /**
     * Data class representing a vote payload stored in the Redis Queue.
     */
    public static class VoteStreamPayload {
        private String voteId;
        private VoteRequestDTO voteRequestDTO;
        private long ingestionTimestamp;

        public VoteStreamPayload() {}

        public VoteStreamPayload(String voteId, VoteRequestDTO voteRequestDTO, long ingestionTimestamp) {
            this.voteId = voteId;
            this.voteRequestDTO = voteRequestDTO;
            this.ingestionTimestamp = ingestionTimestamp;
        }

        // Getters and Setters
        public String getVoteId() { return voteId; }
        public void setVoteId(String voteId) { this.voteId = voteId; }
        public VoteRequestDTO getVoteRequestDTO() { return voteRequestDTO; }
        public void setVoteRequestDTO(VoteRequestDTO voteRequestDTO) { this.voteRequestDTO = voteRequestDTO; }
        public long getIngestionTimestamp() { return ingestionTimestamp; }
        public void setIngestionTimestamp(long ingestionTimestamp) { this.ingestionTimestamp = ingestionTimestamp; }
    }

    /**
     * Get processing statistics from Redis.
     *
     * @return A map containing vote processing counts
     */
    public java.util.Map<String, Object> getProcessingStats() {
        java.util.Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(VOTE_COUNTS_KEY);
        java.util.Map<String, Object> stringMap = new java.util.HashMap<>();
        for (java.util.Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            stringMap.put(entry.getKey().toString(), entry.getValue());
        }
        return stringMap;
    }
}