package org.example.voting.repository;

import org.example.voting.model.Episode;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface EpisodeRepository extends Repository<Episode, Integer> {
    <S extends Episode> S save(S entity);
    Optional<Episode> findById(Integer id);
}