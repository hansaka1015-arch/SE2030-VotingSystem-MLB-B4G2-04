package org.example.voting.service;
import org.example.voting.dto.VoteRequestDTO;

public interface FraudAnalyzer {
    boolean analyzeSuspiciousActivity(String sessionId);
    void flagAndHoldForReview(String voteId, VoteRequestDTO request);
}