package org.example.voting.repository;

import org.example.voting.model.AuditLog;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AuditLogRepository extends Repository<AuditLog, Long> {
    <S extends AuditLog> S save(S entity);
    Optional<AuditLog> findById(Long id);
}