package io.github.latcn.a2a.permission.admin.domain.repository;

import io.github.latcn.a2a.permission.admin.domain.model.AuditLog;

import java.util.List;
import java.util.Optional;

public interface AuditLogRepository {

    AuditLog save(AuditLog auditLog);

    Optional<AuditLog> findByTraceId(String traceId);

    List<AuditLog> findByOperatorId(Long operatorId);

    List<AuditLog> findByOperationType(String operationType);
}