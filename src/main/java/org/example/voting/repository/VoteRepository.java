package org.example.voting.repository;

import org.example.voting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    // Added method to count accepted votes by user and episode for quota check
    Integer countByUserIdAndEpisodeIdAndStatus(@Param("userId") Integer userId, @Param("episodeId") Integer episodeId, @Param("status") String status);

    // Query to get episode end time for validation (fallback when not in cache)
    @Query("SELECT e.votingEndTime FROM Episode e WHERE e.episodeId = :episodeId")
    LocalDateTime getEpisodeEndTime(@Param("episodeId") Integer episodeId);
}