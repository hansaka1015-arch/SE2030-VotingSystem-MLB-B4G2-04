package org.example.voting.repository;

import org.example.voting.model.Vote;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VoteRepository extends Repository<Vote, Long> {
    <S extends Vote> S save(S entity);
    Optional<Vote> findById(Long id);
    // Added method to count accepted votes by user and episode for quota check
    Integer countByUserIdAndEpisodeIdAndStatus(@Param("userId") Integer userId, @Param("episodeId") Integer episodeId, @Param("status") String status);
}