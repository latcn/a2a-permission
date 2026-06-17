package io.github.latcn.a2a.permission.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.latcn.a2a.permission.admin.audit.AuditDiff;
import io.github.latcn.a2a.permission.admin.audit.AuditLogService;
import io.github.latcn.a2a.permission.admin.audit.PermGrantDiff;
import io.github.latcn.a2a.permission.admin.audit.RoleGrantDiff;
import io.github.latcn.a2a.permission.admin.engine.PermissionCalculator;
import io.github.latcn.a2a.permission.admin.entity.*;
import io.github.latcn.a2a.permission.admin.mapper.*;
import io.github.latcn.a2a.permission.admin.producer.PermissionChangeProducer;
import io.github.latcn.a2a.permission.admin.security.RowRulePreparedBinder;
import io.github.latcn.a2a.permission.admin.security.RowRuleValidator;
import io.github.latcn.a2a.permission.api.dto.*;
import io.github.latcn.a2a.permission.api.enums.ChangeType;
import io.github.latcn.a2a.permission.api.enums.Effect;
import io.github.latcn.a2a.permission.api.enums.OperationResult;
import io.github.latcn.a2a.permission.api.enums.OperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionAdminService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final AuditLogService auditLogService;
    private final PermissionChangeProducer permissionChangeProducer;
    private final PermissionCalculator permissionCalculator;
    private final RowRuleValidator rowRuleValidator;
    private final RowRulePreparedBinder rowRulePreparedBinder;

    @Transactional
    public boolean grantRole(Long operatorId, Long roleId, Set<Long> userIds) {
        String traceId = UUID.randomUUID().toString();

        try {
            Role role = roleMapper.selectById(roleId);
            if (role == null) {
                log.warn("Role not found: {}", roleId);
                return false;
            }

            for (Long userId : userIds) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);

                userMapper.incrementVersionIfMatch(userId, userMapper.selectById(userId).getPermVersion());
            }

            RoleGrantDiff diff = new RoleGrantDiff(operatorId, roleId, role.getRoleName(), userIds, true);
            recordAudit(traceId, operatorId, null, userIds, OperationType.ROLE_GRANT, OperationResult.SUCCESS, diff);

            PermissionChangeMessage message = PermissionChangeMessage.builder()
                    .type(ChangeType.USER)
                    .userIds(userIds)
                    .newVersion(System.currentTimeMillis())
                    .timestamp(System.currentTimeMillis())
                    .build();
            permissionChangeProducer.sendPermissionChange(message);

            return true;

        } catch (Exception e) {
            log.error("Failed to grant role", e);
            recordAudit(traceId, operatorId, null, userIds, OperationType.ROLE_GRANT, OperationResult.FAILED, null);
            return false;
        }
    }

    @Transactional
    public boolean revokeRole(Long operatorId, Long roleId, Set<Long> userIds) {
        String traceId = UUID.randomUUID().toString();

        try {
            Role role = roleMapper.selectById(roleId);
            if (role == null) {
                log.warn("Role not found: {}", roleId);
                return false;
            }

            for (Long userId : userIds) {
                LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserRole::getUserId, userId).eq(UserRole::getRoleId, roleId);
                userRoleMapper.delete(wrapper);

                userMapper.incrementVersionIfMatch(userId, userMapper.selectById(userId).getPermVersion());
            }

            RoleGrantDiff diff = new RoleGrantDiff(operatorId, roleId, role.getRoleName(), userIds, false);
            recordAudit(traceId, operatorId, null, userIds, OperationType.ROLE_REVOKE, OperationResult.SUCCESS, diff);

            PermissionChangeMessage message = PermissionChangeMessage.builder()
                    .type(ChangeType.USER)
                    .userIds(userIds)
                    .newVersion(System.currentTimeMillis())
                    .timestamp(System.currentTimeMillis())
                    .build();
            permissionChangeProducer.sendPermissionChange(message);

            return true;

        } catch (Exception e) {
            log.error("Failed to revoke role", e);
            recordAudit(traceId, operatorId, null, userIds, OperationType.ROLE_REVOKE, OperationResult.FAILED, null);
            return false;
        }
    }

    @Transactional
    public boolean grantPermission(Long operatorId, Long roleId, Set<Long> permissionIds) {
        String traceId = UUID.randomUUID().toString();

        try {
            Role role = roleMapper.selectById(roleId);
            if (role == null) {
                log.warn("Role not found: {}", roleId);
                return false;
            }

            for (Long permId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permId);
                rp.setEffect(Effect.ALLOW.getCode());
                rolePermissionMapper.insert(rp);
            }

            roleMapper.incrementVersionIfMatch(roleId, role.getRoleVersion());

            PermGrantDiff diff = new PermGrantDiff(operatorId, roleId, role.getRoleName(), permissionIds, true);
            recordAudit(traceId, operatorId, null, null, OperationType.PERM_GRANT, OperationResult.SUCCESS, diff);

            PermissionChangeMessage message = PermissionChangeMessage.builder()
                    .type(ChangeType.ROLE)
                    .roleId(roleId)
                    .newVersion(System.currentTimeMillis())
                    .timestamp(System.currentTimeMillis())
                    .build();
            permissionChangeProducer.sendPermissionChange(message);

            return true;

        } catch (Exception e) {
            log.error("Failed to grant permission", e);
            recordAudit(traceId, operatorId, null, null, OperationType.PERM_GRANT, OperationResult.FAILED, null);
            return false;
        }
    }

    @Transactional
    public boolean updateRowRule(Long operatorId, Long roleId, Long permissionId, String newRowRule) {
        String traceId = UUID.randomUUID().toString();

        try {
            RowRuleValidator.ValidationResult validation = rowRuleValidator.validate(newRowRule);
            if (!validation.isValid()) {
                log.warn("Row rule validation failed: {}", validation.getMessage());
                return false;
            }

            RolePermission rp = rolePermissionMapper.selectByRoleAndPermission(roleId, permissionId);
            if (rp == null) {
                log.warn("RolePermission not found: roleId={}, permissionId={}", roleId, permissionId);
                return false;
            }

            String oldRowRule = null;
            Map<String, String> oldRowRuleTemplate = rp.getRowRuleTemplate();
            if (oldRowRuleTemplate != null && !oldRowRuleTemplate.isEmpty()) {
                oldRowRule = oldRowRuleTemplate.get("default");
            }

            Map<String, String> newRowRuleTemplate = new HashMap<>();
            newRowRuleTemplate.put("default", newRowRule);
            rp.setRowRuleTemplate(newRowRuleTemplate);
            rolePermissionMapper.updateById(rp);

            Role role = roleMapper.selectById(roleId);
            roleMapper.incrementVersionIfMatch(roleId, role.getRoleVersion());

            Permission permission = permissionMapper.selectById(permissionId);
            io.github.latcn.a2a.permission.admin.audit.RowRuleUpdateDiff diff =
                    new io.github.latcn.a2a.permission.admin.audit.RowRuleUpdateDiff(
                            operatorId, roleId, role.getRoleName(), permissionId,
                            permission != null ? permission.getPermissionCode() : null,
                            oldRowRule, newRowRule);

            recordAudit(traceId, operatorId, null, null, OperationType.ROW_RULE_UPDATE, OperationResult.SUCCESS, diff);

            PermissionChangeMessage message = PermissionChangeMessage.builder()
                    .type(ChangeType.ROLE)
                    .roleId(roleId)
                    .newVersion(System.currentTimeMillis())
                    .timestamp(System.currentTimeMillis())
                    .build();
            permissionChangeProducer.sendPermissionChange(message);

            return true;

        } catch (Exception e) {
            log.error("Failed to update row rule", e);
            recordAudit(traceId, operatorId, null, null, OperationType.ROW_RULE_UPDATE, OperationResult.FAILED, null);
            return false;
        }
    }

    public UserFullPermissionDTO getUserFullPermissions(Long userId) {
        return permissionCalculator.calculateFullPermissions(userId);
    }

    private void recordAudit(String traceId, Long operatorId, Long targetRoleId, Set<Long> targetUserIds,
                             OperationType operationType, OperationResult result, AuditDiff diff) {
        try {
            AuditLogDTO dto = AuditLogDTO.builder()
                    .traceId(traceId)
                    .operationType(operationType)
                    .operationResult(result)
                    .operatorId(operatorId)
                    .targetRoleId(targetRoleId)
                    //.diff(diff)
                    .build();

            if (targetUserIds != null && !targetUserIds.isEmpty()) {
                dto.setTargetUserId(targetUserIds.iterator().next());
            }

            auditLogService.recordAsync(dto);
        } catch (Exception e) {
            log.error("Failed to record audit log", e);
        }
    }
}