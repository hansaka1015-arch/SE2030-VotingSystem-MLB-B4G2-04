package org.example.voting.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class EpisodeCacheService {

    private static final String EPISODE_TIMINGS_KEY = "episode:timings";
    private static final long CACHE_TTL_HOURS = 24;

    private final RedisTemplate<String, Object> redisTemplate;

    public EpisodeCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Cache episode timing data when Show Producer schedules an episode (UC-01)
     * @param episodeId Unique identifier for the episode
     * @param startTime Episode voting start time
     * @param endTime Episode voting end time
     */
    public void cacheEpisodeTimings(String episodeId, LocalDateTime startTime, LocalDateTime endTime) {
        String hashKey = EPISODE_TIMINGS_KEY + ":" + episodeId;
        redisTemplate.opsForHash().put(hashKey, "startTime", startTime);
        redisTemplate.opsForHash().put(hashKey, "endTime", endTime);
        redisTemplate.expire(hashKey, CACHE_TTL_HOURS, TimeUnit.HOURS);
    }

    /**
     * Get episode end time from Redis cache for validation
     * @param episodeId Unique identifier for the episode
     * @return Optional containing end time if found in cache
     */
    public Optional<LocalDateTime> getEpisodeEndTime(String episodeId) {
        String hashKey = EPISODE_TIMINGS_KEY + ":" + episodeId;
        Object endTimeObj = redisTemplate.opsForHash().get(hashKey, "endTime");
        if (endTimeObj instanceof LocalDateTime) {
            return Optional.of((LocalDateTime) endTimeObj);
        }
        return Optional.empty();
    }

    /**
     * Get episode start time from Redis cache
     * @param episodeId Unique identifier for the episode
     * @return Optional containing start time if found in cache
     */
    public Optional<LocalDateTime> getEpisodeStartTime(String episodeId) {
        String hashKey = EPISODE_TIMINGS_KEY + ":" + episodeId;
        Object startTimeObj = redisTemplate.opsForHash().get(hashKey, "startTime");
        if (startTimeObj instanceof LocalDateTime) {
            return Optional.of((LocalDateTime) startTimeObj);
        }
        return Optional.empty();
    }

    /**
     * Check if episode timing data exists in cache
     * @param episodeId Unique identifier for the episode
     * @return true if timing data exists in cache
     */
    public boolean hasEpisodeTimings(String episodeId) {
        String hashKey = EPISODE_TIMINGS_KEY + ":" + episodeId;
        return redisTemplate.hasKey(hashKey);
    }
}