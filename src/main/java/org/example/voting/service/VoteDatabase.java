package org.example.voting.service;

import java.time.LocalDateTime;

public interface VoteDatabase {
    void recordVote(String voteId, String contestantId);
    LocalDateTime getEpisodeEndTime(String episodeId);
}