package org.example.voting.controller;

import org.example.voting.dto.VoteIngestResponse;
import org.example.voting.dto.VoteRequest;
import org.example.voting.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping("/ingest")
    public ResponseEntity<VoteIngestResponse> ingestVote(
            @RequestBody VoteRequest voteRequest,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        String deviceFingerprint = request.getHeader("Device-Fingerprint");

        // Process the vote asynchronously
        voteService.processVoteAsync(voteRequest, ipAddress, deviceFingerprint);

        // Return an immediate confirmation receipt
        VoteIngestResponse response = new VoteIngestResponse(
                "QUEUED",
                "Vote received and queued for processing."
        );

        return ResponseEntity.accepted().body(response);
    }
}