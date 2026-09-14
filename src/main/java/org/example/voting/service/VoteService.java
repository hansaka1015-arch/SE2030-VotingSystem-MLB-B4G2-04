package org.example.voting.service;

import org.example.voting.config.AsyncConfig;
import org.example.voting.dto.VoteRequest;
import org.example.voting.event.VoteRecordedEvent;
import org.example.voting.model.*;
import org.example.voting.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class VoteService {

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private FraudAnalyzerService fraudAnalyzerService;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private CreditWalletRepository creditWalletRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Value("${voting.free-vote-limit-per-episode:5}")
    private int freeVoteLimitPerEpisode;

    @Value("${voting.grace-period-seconds:5}")
    private int gracePeriodSeconds;

    /**
     * Main entry point for ingesting a vote. This method is called by the controller.
     * It immediately queues the vote for asynchronous processing and returns a receipt.
     *
     * @param voteRequest the vote request from the client
     * @param ipAddress   the IP address of the client
     * @param deviceFingerprint the device fingerprint
     * @return a VoteIngestResponse containing the voteId and status (ACCEPTED, REJECTED, FLAGGED)
     *         Note: The actual status is determined asynchronously, but we return a temporary receipt.
     *         For simplicity, we return a pending status and update later?
     *         However, the requirement is to return a confirmation receipt immediately.
     *         We can return a temporary voteId (or null) and status PROCESSING, but the frontend might not handle that.
     *         Alternatively, we can generate a voteId immediately (by inserting a pending vote) and then update it asynchronously.
     *         But the requirement says: "immediately pass the payload to an asynchronous worker and return a confirmation receipt."
     *         The confirmation receipt could be a simple acknowledgment that the vote was received for processing.
     *
     *         Let's change approach: We will create a vote record with status PROCESSING (or PENDING) and then update it asynchronously.
     *         However, the database schema does not have a PROCESSING status. It has ACCEPTED, REJECTED, FLAGGED.
     *         We can add a PENDING status? But we are restricted to the existing schema.
     *
     *         Alternatively, we can return a receipt without a voteId and then the frontend can poll? Not ideal.
     *
     *         Another idea: The asynchronous worker will process the vote and then update the live leaderboard. The frontend doesn't need the voteId immediately?
     *         But the requirement says "return a confirmation receipt". We can return a simple JSON with a message and a timestamp.
     *
     *         Given the ambiguity, we will design the service to process the vote asynchronously and then the controller will return a response immediately with a temporary status.
     *         We'll generate a temporary voteId (maybe negative) and then update the vote record with the real Id and status.
     *
     *         However, to keep it simple and within the scope, we will assume that the frontend only needs an acknowledgment that the vote was accepted for processing.
     *         We'll return a response with a status of "QUEUED" and a timestamp.
     *
     *         But note: the requirement says to return a confirmation receipt. We'll interpret that as a receipt that the vote was received and queued.
     *
     *         We'll change the VoteIngestResponse to have a status that can be QUEUED, and then the asynchronous processing will update the vote status in the database.
     *
     *         However, the vote must be persisted to the database eventually. We'll create a vote record with status QUEUED (which is not in the enum) -> so we cannot.
     *
     *         Let's look at the vote status enum: ACCEPTED, REJECTED, FLAGGED.
     *         We can use FLAGGED as a temporary state? Not really.
     *
     *         We decide to not store the vote until it is processed. Instead, we return a receipt immediately and then the asynchronous worker will store the vote.
     *         The receipt will contain a unique identifier (like a UUID) that we can use to track the vote? But we don't have a table for that.
     *
     *         Given the time, we will do the following:
     *         - The controller will call the service method that does the processing asynchronously.
     *         - The service method will do the entire processing (including saving the vote) but in a separate thread.
     *         - The controller will return immediately a response that says "Vote queued for processing" with a timestamp.
     *
     *         We will not return a voteId in the immediate response because the vote is not yet stored. The frontend can use the timestamp and user_id, etc. to match later if needed.
     *
     *         We'll create a simple response class for the controller.
     *
     *         Let's create the VoteIngestResponse class in the dto package.
     *
     *         However, note that the requirement says: "return a confirmation receipt". We'll return a receipt that the vote was accepted for processing.
     *
     *         We'll proceed with the asynchronous processing and then the controller returns immediately.
     */
    @Async
    public void processVoteAsync(VoteRequest voteRequest, String ipAddress, String deviceFingerprint) {
        // We'll do the processing here and save the vote.
        // We'll use a transaction to ensure consistency.
        processVoteInternal(voteRequest, ipAddress, deviceFingerprint);
    }

    @Transactional
    public void processVoteInternal(VoteRequest voteRequest, String ipAddress, String deviceFingerprint) {
        Instant now = Instant.now();

        // 1. ValidateActiveWindow: Check if the episode is open for voting.
        Episode episode = episodeRepository.findById(voteRequest.getEpisodeId())
                .orElseThrow(() -> new IllegalArgumentException("Episode not found: " + voteRequest.getEpisodeId()));

        // Check if the current time is within the voting window with grace period.
        Instant start = episode.getVotingStartTime().minusSeconds(gracePeriodSeconds);
        Instant end = episode.getVotingEndTime().plusSeconds(gracePeriodSeconds);

        if (now.isBefore(start) || now.isAfter(end)) {
            // Vote is outside the voting window (even with grace period) -> reject and log to audit.
            voteRepository.save(createVoteEntity(voteRequest, ipAddress, deviceFingerprint, "REJECTED", "FREE"));
            logAuditEvent("VOTE_REJECTED_OUTSIDE_WINDOW", voteRequest.getUserId(), ipAddress,
                    "Vote attempted outside voting window. Episode: " + voteRequest.getEpisodeId() +
                            ", Time: " + now + ", Window: " + episode.getVotingStartTime() + " to " + episode.getVotingEndTime());
            return;
        }

        // 2. Fraud Routing
        boolean fraudulent = fraudAnalyzerService.isFraudulent(voteRequest, ipAddress, deviceFingerprint);
        if (fraudulent) {
            voteRepository.save(createVoteEntity(voteRequest, ipAddress, deviceFingerprint, "FLAGGED", "FREE"));
            logAuditEvent("VOTE_FLAGGED_FRAUD", voteRequest.getUserId(), ipAddress,
                    "Vote flagged as potential fraud. User: " + voteRequest.getUserId() +
                            ", Episode: " + voteRequest.getEpisodeId() +
                            ", Contestant: " + voteRequest.getContestantId());
            return;
        }

        // 3. Quota & Credit Check
        int freeVoteCount = voteRepository.countByUserIdAndEpisodeIdAndStatus(
                voteRequest.getUserId(), voteRequest.getEpisodeId(), "ACCEPTED");

        String voteType;
        if (freeVoteCount < freeVoteLimitPerEpisode) {
            voteType = "FREE";
        } else {
            // Check credit wallet
            CreditWallet wallet = creditWalletRepository.findById(voteRequest.getUserId())
                    .orElseThrow(() -> new IllegalStateException("Credit wallet not found for user: " + voteRequest.getUserId()));
            if (wallet.getBalance() <= 0) {
                // Insufficient credits -> reject the vote.
                voteRepository.save(createVoteEntity(voteRequest, ipAddress, deviceFingerprint, "REJECTED", "FREE"));
                logAuditEvent("VOTE_REJECTED_INSUFFICIENT_CREDITS", voteRequest.getUserId(), ipAddress,
                        "User has insufficient credits. Balance: " + wallet.getBalance());
                return;
            }
            // Deduct one credit
            wallet.setBalance(wallet.getBalance() - 1);
            wallet.setUpdatedAt(now);
            creditWalletRepository.save(wallet);
            voteType = "PAID";
        }

        // 4. Execution: Record the vote as ACCEPTED.
        Vote vote = createVoteEntity(voteRequest, ipAddress, deviceFingerprint, "ACCEPTED", voteType);
        Vote savedVote = voteRepository.save(vote);

        // Trigger event for live leaderboard update.
        eventPublisher.publishEvent(new VoteRecordedEvent(this, savedVote));
    }

    private Vote createVoteEntity(VoteRequest voteRequest, String ipAddress, String deviceFingerprint, String status, String voteType) {
        Vote vote = new Vote();
        vote.setUserId(voteRequest.getUserId());
        vote.setContestantId(voteRequest.getContestantId());
        vote.setEpisodeId(voteRequest.getEpisodeId());
        vote.setVoteType(voteType);
        vote.setIpAddress(ipAddress);
        vote.setDeviceFingerprint(deviceFingerprint);
        vote.setSubmittedAt(Instant.now()); // Use server time for submitted_at
        vote.setStatus(status);
        return vote;
    }

    private void logAuditEvent(String eventType, Integer userId, String ipAddress, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEventType(eventType);
        auditLog.setActorUserId(userId);
        auditLog.setIpAddress(ipAddress);
        auditLog.setDetails(details);
        auditLog.setCreatedAt(Instant.now());
        auditLogRepository.save(auditLog);
    }
}