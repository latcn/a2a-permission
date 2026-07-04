package io.github.latcn.a2a.permission.admin.domain.audit;

import io.github.latcn.a2a.permission.admin.application.dto.AuditLogDTO;

public interface AuditLogService {

    void recordAsync(AuditLogDTO dto);

    void record(AuditLogDTO dto);
}