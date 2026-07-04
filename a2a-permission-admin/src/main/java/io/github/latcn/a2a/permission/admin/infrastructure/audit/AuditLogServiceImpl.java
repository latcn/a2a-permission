package io.github.latcn.a2a.permission.admin.infrastructure.audit;

import io.github.latcn.a2a.permission.admin.domain.audit.AuditLogService;
import io.github.latcn.a2a.permission.admin.domain.model.*;
import io.github.latcn.a2a.permission.admin.domain.repository.AuditLogRepository;
import io.github.latcn.a2a.permission.admin.application.dto.AuditLogDTO;
import io.github.latcn.a2a.permission.api.enums.OperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    public void recordAsync(AuditLogDTO dto) {
        try {
            record(dto);
        } catch (Exception e) {
            log.error("Failed to record audit log asynchronously", e);
        }
    }

    @Override
    public void record(AuditLogDTO dto) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setTraceId(dto.getTraceId());
            auditLog.setOperationType(dto.getOperationType().name());
            auditLog.setOperationResult(dto.getOperationResult().name());
            auditLog.setOperatorId(dto.getOperatorId());
            auditLog.setTargetUserId(dto.getTargetUserId());
            auditLog.setTargetRoleId(dto.getTargetRoleId());
            auditLog.setTargetPermissionId(dto.getTargetPermissionId());
            auditLog.setTargetClientId(dto.getTargetClientId());
            auditLog.setOperationDetail(dto.getOperationDetail());
            auditLog.setClientIp(dto.getClientIp());
            auditLog.setUserAgent(dto.getUserAgent());
            auditLog.setCreatedAt(LocalDateTime.now());

            auditLogRepository.save(auditLog);

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
        try {
            java.util.Map<String, Object> beforeMap = (java.util.Map<String, Object>) before;
            java.util.Map<String, Object> afterMap = (java.util.Map<String, Object>) after;

            Long operatorId = ((Number) beforeMap.get("operatorId")).longValue();
            Long roleId = ((Number) beforeMap.get("roleId")).longValue();
            String roleName = (String) beforeMap.get("roleName");
            boolean grant = (Boolean) afterMap.get("grant");
            java.util.Set<Long> targetUserIds = (java.util.Set<Long>) afterMap.get("targetUserIds");

            return new RoleGrantDiff(operatorId, roleId, roleName, targetUserIds, grant);
        } catch (Exception e) {
            log.warn("Failed to compute role grant diff", e);
            return null;
        }
    }

   
    private PermGrantDiff computePermGrantDiff(OperationType type, Object before, Object after) {
        try {
            java.util.Map<String, Object> beforeMap = (java.util.Map<String, Object>) before;
            java.util.Map<String, Object> afterMap = (java.util.Map<String, Object>) after;

            Long operatorId = ((Number) beforeMap.get("operatorId")).longValue();
            Long roleId = ((Number) beforeMap.get("roleId")).longValue();
            String roleName = (String) beforeMap.get("roleName");
            boolean grant = (Boolean) afterMap.get("grant");
            java.util.Set<Long> targetPermissionIds = (java.util.Set<Long>) afterMap.get("targetPermissionIds");

            return new PermGrantDiff(operatorId, roleId, roleName, targetPermissionIds, grant);
        } catch (Exception e) {
            log.warn("Failed to compute perm grant diff", e);
            return null;
        }
    }

   
    private RowRuleUpdateDiff computeRowRuleUpdateDiff(Object before, Object after) {
        try {
            java.util.Map<String, Object> beforeMap = (java.util.Map<String, Object>) before;
            java.util.Map<String, Object> afterMap = (java.util.Map<String, Object>) after;

            Long operatorId = ((Number) beforeMap.get("operatorId")).longValue();
            Long roleId = ((Number) beforeMap.get("roleId")).longValue();
            String roleName = (String) beforeMap.get("roleName");
            Long permissionId = ((Number) beforeMap.get("permissionId")).longValue();
            String permissionCode = (String) beforeMap.get("permissionCode");
            String oldRowRule = (String) afterMap.get("oldRowRule");
            String newRowRule = (String) afterMap.get("newRowRule");

            return new RowRuleUpdateDiff(operatorId, roleId, roleName, permissionId, permissionCode, oldRowRule, newRowRule);
        } catch (Exception e) {
            log.warn("Failed to compute row rule update diff", e);
            return null;
        }
    }
}