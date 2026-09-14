package org.example.voting.service;

import org.example.voting.cache.EpisodeCacheService;
import org.example.voting.dto.VoteRequestDTO;
import org.example.voting.repository.VoteRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class VotingManager {

    // Interfaces representing modules built by your group members
    private final FraudAnalyzer fraudAnalyzer;           // Karunathilaka's module
    private final CreditLedger creditLedger;             // Sarah's module
    private final VoteRepository voteRepository;         // JPA Repository for persistence
    private final LeaderboardService leaderboardService; // Dissanayaka's module
    private final EpisodeCacheService episodeCacheService; // Redis cache for timing data

    // Configurable grace period to avoid network delay disputes
    private static final long GRACE_PERIOD_MS = 2000;

    public VotingManager(FraudAnalyzer fraudAnalyzer, CreditLedger creditLedger,
                         VoteRepository voteRepository, LeaderboardService leaderboardService,
                         EpisodeCacheService episodeCacheService) {
        this.fraudAnalyzer = fraudAnalyzer;
        this.creditLedger = creditLedger;
        this.voteRepository = voteRepository;
        this.leaderboardService = leaderboardService;
        this.episodeCacheService = episodeCacheService;
    }

    @Async("voteTaskExecutor") // Points to your custom thread pool configuration
    public void processVoteAsync(String voteId, VoteRequestDTO request) {

        // 1. Boundary Edge Policy: Validate Active Window (using Redis cache)
        if (!validateActiveWindow(request.getEpisodeId())) {
            // Log rejection for audit purposes
            return;
        }

        // 2. Fraud Analyzer Routing
        boolean isSuspicious = fraudAnalyzer.analyzeSuspiciousActivity(request.getUserId());
        if (isSuspicious) {
            fraudAnalyzer.flagAndHoldForReview(voteId, request);
            return;
        }

        // 3. Quota & Credit Enforcement
        int creditBalance = creditLedger.checkCreditBalance(request.getUserId());
        if (creditBalance <= 0) {
            // Log insufficient credit rejection
            return;
        }

        // 4. Execution & Persistence
        creditLedger.debitCredit(request.getUserId(), 1);
        recordVote(voteId, request);

        // 5. Trigger live UI updates
        leaderboardService.updateLiveLeaderboardTally(request.getContestantId());
    }

    private boolean validateActiveWindow(String episodeId) {
        LocalDateTime serverNow = LocalDateTime.now();

        // Fetch official schedule from Redis cache (loaded by Show Producer in UC-01)
        LocalDateTime episodeEndTime = episodeCacheService.getEpisodeEndTime(episodeId)
                .orElseGet(() -> voteRepository.getEpisodeEndTime(Integer.parseInt(episodeId)));

        // Calculate the absolute cutoff including the grace period
        LocalDateTime hardCutoff = episodeEndTime.plus(GRACE_PERIOD_MS, ChronoUnit.MILLIS);

        // Decision is based on server time, not the user's device time
        return serverNow.isBefore(hardCutoff);
    }

    @Transactional
    public void recordVote(String voteId, VoteRequestDTO request) {
        // Create and save vote entity
        org.example.voting.model.Vote vote = new org.example.voting.model.Vote();
        vote.setUserId(Integer.parseInt(request.getUserId()));
        vote.setContestantId(Integer.parseInt(request.getContestantId()));
        vote.setEpisodeId(Integer.parseInt(request.getEpisodeId()));
        vote.setVoteType("FREE"); // Default, will be updated by credit ledger logic
        vote.setIpAddress("0.0.0.0"); // Will be set from request in real implementation
        vote.setDeviceFingerprint("unknown"); // Will be set from request in real implementation
        vote.setSubmittedAt(java.time.Instant.now());
        vote.setStatus("ACCEPTED");

        voteRepository.save(vote);
    }
}