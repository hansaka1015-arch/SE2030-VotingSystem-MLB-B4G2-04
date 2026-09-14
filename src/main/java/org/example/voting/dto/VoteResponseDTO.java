package org.example.voting.dto;

public class VoteResponseDTO {

    private String status;
    private String message;
    private String voteId; // Included if successful

    public VoteResponseDTO(String status, String message, String voteId) {
        this.status = status;
        this.message = message;
        this.voteId = voteId;
    }

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

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }
}