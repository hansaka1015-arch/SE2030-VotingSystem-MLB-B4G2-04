package com.votingsystem.model;

import java.sql.Timestamp;

public class Contestant {
    private int id;
    private String contestantCode;
    private String fullName;
    private String bioSummary;
    private String profileImageUrl;
    private String status; // ACTIVE, SAFE, AT RISK, ELIMINATED
    private int showId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Contestant() {}

    public Contestant(int id, String contestantCode, String fullName, String bioSummary, String profileImageUrl, String status, int showId) {
        this.id = id;
        this.contestantCode = contestantCode;
        this.fullName = fullName;
        this.bioSummary = bioSummary;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.showId = showId;
    }

    public Contestant(String contestantCode, String fullName, String bioSummary, String profileImageUrl, String status, int showId) {
        this.contestantCode = contestantCode;
        this.fullName = fullName;
        this.bioSummary = bioSummary;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.showId = showId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContestantCode() { return contestantCode; }
    public void setContestantCode(String contestantCode) { this.contestantCode = contestantCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBioSummary() { return bioSummary; }
    public void setBioSummary(String bioSummary) { this.bioSummary = bioSummary; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getShowId() { return showId; }
    public void setShowId(int showId) { this.showId = showId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
