package io.github.latcn.a2a.permission.admin.domain.messaging;

import io.github.latcn.a2a.permission.admin.application.dto.AuditLogDTO;

public interface AuditLogPublisher {

    void publishAuditLog(AuditLogDTO dto);
}