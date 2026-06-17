package io.github.latcn.a2a.permission.admin.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.latcn.a2a.permission.admin.entity.AuditLog;
import io.github.latcn.a2a.permission.admin.mapper.AuditLogMapper;
import io.github.latcn.a2a.permission.api.dto.AuditLogDTO;
import io.github.latcn.a2a.permission.api.enums.OperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    @Async
    public void recordAsync(AuditLogDTO dto) {
        try {
            record(dto);
        } catch (Exception e) {
            log.error("Failed to record audit log asynchronously", e);
        }
    }

    public void record(AuditLogDTO dto) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setTraceId(dto.getTraceId());
            auditLog.setOperationType(dto.getOperationType().name());
            auditLog.setOperationResult(dto.getOperationResult().name());
            auditLog.setOperatorId(dto.getOperatorId());
            auditLog.setTargetUserId(dto.getTargetUserId());
            auditLog.setTargetRoleId(dto.getTargetRoleId());
            auditLog.setClientIp(dto.getClientIp());
            auditLog.setUserAgent(dto.getUserAgent());
            auditLog.setCreatedAt(LocalDateTime.now());

            auditLogMapper.insert(auditLog);

            log.debug("Audit log recorded: {}", dto.getTraceId());

        } catch (Exception e) {
            log.error("Failed to record audit log", e);
            throw new RuntimeException("Failed to record audit log", e);
        }
    }

    public AuditDiff computeDiff(OperationType type, Object before, Object after) {
        if (type == OperationType.ROLE_GRANT || type == OperationType.ROLE_REVOKE) {
            return computeRoleGrantDiff(type, before, after);
        } else if (type == OperationType.PERM_GRANT || type == OperationType.PERM_REVOKE) {
            return computePermGrantDiff(type, before, after);
        } else if (type == OperationType.ROW_RULE_UPDATE) {
            return computeRowRuleUpdateDiff(before, after);
        }
        return null;
    }

    private RoleGrantDiff computeRoleGrantDiff(OperationType type, Object before, Object after) {
        return null;
    }

    private PermGrantDiff computePermGrantDiff(OperationType type, Object before, Object after) {
        return null;
    }

    private RowRuleUpdateDiff computeRowRuleUpdateDiff(Object before, Object after) {
        return null;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Failed to serialize object to JSON", e);
            return "{}";
        }
    }
}