package org.example.voting.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("episodes")
public class Episode {
    @Id
    private Integer episodeId;
    private Integer showId;
    private Integer episodeNumber;
    private String title;
    private Instant votingStartTime;
    private Instant votingEndTime;
    private String status; // SCHEDULED, VOTING_OPEN, VOTING_CLOSED, COMPLETED

    // Getters and Setters
    public Integer getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getVotingStartTime() {
        return votingStartTime;
    }

    public void setVotingStartTime(Instant votingStartTime) {
        this.votingStartTime = votingStartTime;
    }

    public Instant getVotingEndTime() {
        return votingEndTime;
    }

    public void setVotingEndTime(Instant votingEndTime) {
        this.votingEndTime = votingEndTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}