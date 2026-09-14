package org.example.voting.dto;

import java.time.Instant;

public class VoteIngestResponse {
    private String status; // e.g., "QUEUED", "ACCEPTED", etc. but we'll use a simple status for now.
    private String message;
    private Instant timestamp;

    // Constructor
    public VoteIngestResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now();
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}