package org.example.voting.service;

import org.example.voting.dto.VoteRequestDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class VotingManager {

    // Interfaces representing modules built by your group members
    private final FraudAnalyzer fraudAnalyzer;           // Karunathilaka's module
    private final CreditLedger creditLedger;             // Sarah's module
    private final VoteDatabase voteDatabase;             // Database repository
    private final LeaderboardService leaderboardService; // Dissanayaka's module

    // Configurable grace period to avoid network delay disputes
    private static final long GRACE_PERIOD_MS = 2000;

    public VotingManager(FraudAnalyzer fraudAnalyzer, CreditLedger creditLedger,
                         VoteDatabase voteDatabase, LeaderboardService leaderboardService) {
        this.fraudAnalyzer = fraudAnalyzer;
        this.creditLedger = creditLedger;
        this.voteDatabase = voteDatabase;
        this.leaderboardService = leaderboardService;
    }

    @Async("voteTaskExecutor") // Points to your custom thread pool configuration
    public void processVoteAsync(String voteId, VoteRequestDTO request) {

        // 1. Boundary Edge Policy: Validate Active Window
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
        voteDatabase.recordVote(voteId, request.getContestantId());

        // 5. Trigger live UI updates
        leaderboardService.updateLiveLeaderboardTally(request.getContestantId());
    }

    private boolean validateActiveWindow(String episodeId) {
        LocalDateTime serverNow = LocalDateTime.now();

        // Fetch official schedule from the database (configured by the Show Producer)
        LocalDateTime episodeEndTime = voteDatabase.getEpisodeEndTime(episodeId);

        // Calculate the absolute cutoff including the grace period
        LocalDateTime hardCutoff = episodeEndTime.plus(GRACE_PERIOD_MS, ChronoUnit.MILLIS);

        // Decision is based on server time, not the user's device time
        return serverNow.isBefore(hardCutoff);
    }
}