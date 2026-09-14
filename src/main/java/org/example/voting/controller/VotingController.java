package org.example.voting.controller;

import org.example.voting.dto.VoteRequestDTO;
import org.example.voting.dto.VoteResponseDTO;
import org.example.voting.service.VotingManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "*")
public class VotingController {

    private final VotingManager votingManager;

    public VotingController(VotingManager votingManager) {
        this.votingManager = votingManager;
    }

    @PostMapping("/submit")
    public ResponseEntity<VoteResponseDTO> submitVote(@RequestBody VoteRequestDTO voteRequest) {
        // Generate a tracking ID for the audit log before asynchronous processing
        String voteId = UUID.randomUUID().toString();

        // Pass the payload to the asynchronous worker queue to decouple database writes
        votingManager.processVoteAsync(voteId, voteRequest);

        // Immediately return a response to guarantee sub-500ms latency
        VoteResponseDTO response = new VoteResponseDTO(
                "ACCEPTED",
                "Vote received. Credit update in progress.",
                voteId
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}