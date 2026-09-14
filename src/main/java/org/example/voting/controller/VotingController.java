package org.example.voting.controller;

import org.example.voting.dto.VoteRequestDTO;
import org.example.voting.dto.VoteResponseDTO;
import org.example.voting.redis.VoteStreamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "*")
public class VotingController {

    private final VoteStreamService voteStreamService;

    public VotingController(VoteStreamService voteStreamService) {
        this.voteStreamService = voteStreamService;
    }

    @PostMapping("/submit")
    public ResponseEntity<VoteResponseDTO> submitVote(@RequestBody VoteRequestDTO voteRequest) {
        try {
            // Ingest the vote into Redis Stream for asynchronous processing
            // This returns immediately after the vote is safely stored in Redis
            String voteId = voteStreamService.ingestVote(voteRequest);

            // Immediately return a response to guarantee sub-500ms latency
            VoteResponseDTO response = new VoteResponseDTO(
                    "ACCEPTED",
                    "Vote received. Processing in queue.",
                    voteId
            );

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            // If ingestion fails, return an error response
            VoteResponseDTO response = new VoteResponseDTO(
                    "ERROR",
                    "Failed to ingest vote: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}