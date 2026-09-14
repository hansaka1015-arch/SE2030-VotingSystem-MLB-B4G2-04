package org.example.voting.dto;

import java.time.LocalDateTime;

public class VoteRequestDTO {

    private String userId;
    private String contestantId;
    private String episodeId;
    private LocalDateTime timestamp;

    public VoteRequestDTO() {
    }

    public VoteRequestDTO(String userId, String contestantId, String episodeId, LocalDateTime timestamp) {
        this.userId = userId;
        this.contestantId = contestantId;
        this.episodeId = episodeId;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContestantId() {
        return contestantId;
    }

    public void setContestantId(String contestantId) {
        this.contestantId = contestantId;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}