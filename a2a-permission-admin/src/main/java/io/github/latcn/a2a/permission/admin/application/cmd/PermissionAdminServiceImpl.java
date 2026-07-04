package io.github.latcn.a2a.permission.admin.application.cmd;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.latcn.a2a.permission.admin.application.dto.*;
import io.github.latcn.a2a.permission.admin.domain.audit.AuditLogService;
import io.github.latcn.a2a.permission.admin.domain.messaging.PermissionChangePublisher;
import io.github.latcn.a2a.permission.admin.domain.model.*;
import io.github.latcn.a2a.permission.admin.domain.repository.*;
import io.github.latcn.a2a.permission.admin.infrastructure.security.RowRulePreparedBinder;
import io.github.latcn.a2a.permission.admin.infrastructure.security.RowRuleValidator;
import io.github.latcn.a2a.permission.api.dto.PermissionChangeMessage;
import io.github.latcn.a2a.permission.api.enums.ChangeType;
import io.github.latcn.a2a.permission.api.enums.Effect;
import io.github.latcn.a2a.permission.api.enums.OperationResult;
import io.github.latcn.a2a.permission.api.enums.OperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionAdminServiceImpl implements PermissionAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AgentRepository agentRepository;
    private final AuditLogService auditLogService;
    private final PermissionChangePublisher permissionChangePublisher;
    private final RowRuleValidator rowRuleValidator;
    private final RowRulePreparedBinder rowRulePreparedBinder;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public GrantRoleRespDTO grantRole(GrantRoleReqDTO req) {
        String traceId = UUID.randomUUID().toString();
        Long operatorId = req.getOperatorId();
        Long roleId = req.getRoleId();
        Set<Long> userIds = req.getUserIds();

        try {
            Optional<Role> roleOpt = roleRepository.findById(roleId);
            if (roleOpt.isEmpty()) {
                log.warn("Role not found: {}", roleId);
                return GrantRoleRespDTO.builder()
                        .success(false)
                        .roleId(roleId)
                        .userCount(userIds.size())
                        .message("Role not found")
                        .build();
            }
            Role role = roleOpt.get();

            for (Long userId : userIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleRepository.save(userRole);

                Optional<User> userOpt = userRepository.findById(userId);
                userOpt.ifPresent(user ->
                        userRepository.incrementVersionIfMatch(userId, user.getPermVersion())
                );
            }

            RoleGrantDiff diff = new RoleGrantDiff(operatorId, roleId, role.getRoleName(), userIds, true);
            recordAudit(traceId, operatorId, null, userIds, OperationType.ROLE_GRANT, OperationResult.SUCCESS, diff);

            PermissionChangeMessage message = PermissionChangeMessage.builder()
                    .type(ChangeType.USER)
                    .userIds(userIds)
                    .newVersion(System.currentTimeMillis())
                    .timestamp(System.currentTimeMillis())
                    .build();
            permissionChangePublisher.publishPermissionChange(message);

            return GrantRoleRespDTO.builder()
                    .success(true)
                    .roleId(roleId)
                    .userCount(userIds.size())
                    .message("Success")
                    .build();

        } catch (Exception e) {
            log.error("Failed to grant role", e);
            recordAudit(traceId, operatorId, null, userIds, OperationType.ROLE_GRANT, OperationResult.FAILED, null);
            return GrantRoleRespDTO.builder()
                    .success(false)
                    .roleId(roleId)
                    .userCount(userIds.size())
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public RevokeRoleRespDTO revokeRole(RevokeRoleReqDTO req) {
        String traceId = UUID.randomUUID().toString();
        Long operatorId = req.getOperatorId();
        Long roleId = req.getRoleId();
        Set<Long> userIds = req.getUserIds();

        try {
            Optional<Role> roleOpt = roleRepository.findById(roleId);
            if (roleOpt.isEmpty()) {
                log.warn("Role not found: {}", roleId);
                return RevokeRoleRespDTO.builder()
                        .success(false)
                        .roleId(roleId)
                        .userCount(userIds.size())
                        .message("Role not found")
                        .build();
            }
            Role role = roleOpt.get();

            for (Long userId : userIds) {
                userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);

                Optional<User> userOpt = userRepository.findById(userId);
                userOpt.ifPresent(user ->
                        userRepository.incrementVersionIfMatch(userId, user.getPermVersion())
                );
            }

            RoleGrantDiff diff = new RoleGrantDiff(operatorId, roleId, role.getRoleName(), userIds, false);
            recordAudit(traceId, operatorId, null, userIds, OperationType.ROLE_REVOKE, OperationResult.SUCCESS, diff);

            PermissionChangeMessage message = PermissionChangeMessage.builder()
                    .type(ChangeType.USER)
                    .userIds(userIds)
                    .newVersion(System.currentTimeMillis())
                    .timestamp(System.currentTimeMillis())
                    .build();
            permissionChangePublisher.publishPermissionChange(message);

            return RevokeRoleRespDTO.builder()
                    .success(true)
                    .roleId(roleId)
                    .userCount(userIds.size())
                    .message("Success")
                    .build();

        } catch (Exception e) {
            log.error("Failed to revoke role", e);
            recordAudit(traceId, operatorId, null, userIds, OperationType.ROLE_REVOKE, OperationResult.FAILED, null);
            return RevokeRoleRespDTO.builder()
                    .success(false)
                    .roleId(roleId)
                    .userCount(userIds.size())
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public GrantPermissionRespDTO grantPermission(GrantPermissionReqDTO req) {
        String traceId = UUID.randomUUID().toString();
        Long operatorId = req.getOperatorId();
        Long roleId = req.getRoleId();
        Set<Long> permissionIds = req.getPermissionIds();

        try {
            Optional<Role> roleOpt = roleRepository.findById(roleId);
            if (roleOpt.isEmpty()) {
                log.warn("Role not found: {}", roleId);
                return GrantPermissionRespDTO.builder()
                        .success(false)
                        .roleId(roleId)
                        .permissionCount(permissionIds.size())
                        .message("Role not found")
                        .build();
            }
            Role role = roleOpt.get();

            for (Long permId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permId);
                rp.setEffect(Effect.ALLOW.getCode());
                rolePermissionRepository.save(rp);
            }

            roleRepository.incrementVersionIfMatch(roleId, role.getRoleVersion());

            PermGrantDiff diff = new PermGrantDiff(operatorId, roleId, role.getRoleName(), permissionIds, true);
            recordAudit(traceId, operatorId, null, null, OperationType.PERM_GRANT, OperationResult.SUCCESS, diff);

            PermissionChangeMessage message = PermissionChangeMessage.builder()
                    .type(ChangeType.ROLE)
                    .roleId(roleId)
                    .newVersion(System.currentTimeMillis())
                    .timestamp(System.currentTimeMillis())
                    .build();
            permissionChangePublisher.publishPermissionChange(message);

            return GrantPermissionRespDTO.builder()
                    .success(true)
                    .roleId(roleId)
                    .permissionCount(permissionIds.size())
                    .message("Success")
                    .build();

        } catch (Exception e) {
            log.error("Failed to grant permission", e);
            recordAudit(traceId, operatorId, null, null, OperationType.PERM_GRANT, OperationResult.FAILED, null);
            return GrantPermissionRespDTO.builder()
                    .success(false)
                    .roleId(roleId)
                    .permissionCount(permissionIds.size())
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public UpdateRowRuleRespDTO updateRowRule(UpdateRowRuleReqDTO req) {
        String traceId = UUID.randomUUID().toString();
        Long operatorId = req.getOperatorId();
        Long roleId = req.getRoleId();
        Long permissionId = req.getPermissionId();
        String newRowRule = req.getNewRowRule();

        try {
            RowRuleValidator.ValidationResult validation = rowRuleValidator.validate(newRowRule);
            if (!validation.isValid()) {
                log.warn("Row rule validation failed: {}", validation.getMessage());
                return UpdateRowRuleRespDTO.builder()
                        .success(false)
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .message("Row rule validation failed: " + validation.getMessage())
                        .build();
            }

            Optional<RolePermission> rpOpt = rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId);
            if (rpOpt.isEmpty()) {
                log.warn("RolePermission not found: roleId={}, permissionId={}", roleId, permissionId);
                return UpdateRowRuleRespDTO.builder()
                        .success(false)
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .message("RolePermission not found")
                        .build();
            }
            RolePermission rp = rpOpt.get();

            String oldRowRule = null;
            Map<String, String> oldRowRuleTemplate = rp.getRowRuleTemplate();
            if (oldRowRuleTemplate != null && !oldRowRuleTemplate.isEmpty()) {
                oldRowRule = oldRowRuleTemplate.get("default");
            }

            Map<String, String> newRowRuleTemplate = new HashMap<>();
            newRowRuleTemplate.put("default", newRowRule);
            rp.setRowRuleTemplate(newRowRuleTemplate);
            rolePermissionRepository.save(rp);

            Optional<Role> roleOpt = roleRepository.findById(roleId);
            roleOpt.ifPresent(role ->
                    roleRepository.incrementVersionIfMatch(roleId, role.getRoleVersion())
            );

            Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);

            RowRuleUpdateDiff diff = new RowRuleUpdateDiff(
                    operatorId, roleId, roleOpt.map(Role::getRoleName).orElse(null),
                    permissionId, permissionOpt.map(Permission::getPermissionCode).orElse(null),
                    oldRowRule, newRowRule);

            recordAudit(traceId, operatorId, null, null, OperationType.ROW_RULE_UPDATE, OperationResult.SUCCESS, diff);

            PermissionChangeMessage message = PermissionChangeMessage.builder()
                    .type(ChangeType.ROLE)
                    .roleId(roleId)
                    .newVersion(System.currentTimeMillis())
                    .timestamp(System.currentTimeMillis())
                    .build();
            permissionChangePublisher.publishPermissionChange(message);

            return UpdateRowRuleRespDTO.builder()
                    .success(true)
                    .roleId(roleId)
                    .permissionId(permissionId)
                    .message("Success")
                    .build();

        } catch (Exception e) {
            log.error("Failed to update row rule", e);
            recordAudit(traceId, operatorId, null, null, OperationType.ROW_RULE_UPDATE, OperationResult.FAILED, null);
            return UpdateRowRuleRespDTO.builder()
                    .success(false)
                    .roleId(roleId)
                    .permissionId(permissionId)
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public CreateRoleRespDTO createRole(CreateRoleReqDTO req) {
        String traceId = UUID.randomUUID().toString();
        Long operatorId = req.getOperatorId();

        try {
            Role role = new Role();
            role.setRoleName(req.getRoleName());
            role.setDescription(req.getDescription());
            role.setPriority(0);
            role.setStatus(1);
            Role savedRole = roleRepository.save(role);

            recordAudit(traceId, operatorId, savedRole.getId(), null, OperationType.ROLE_CREATE, OperationResult.SUCCESS, null);

            return CreateRoleRespDTO.builder()
                    .success(true)
                    .roleId(savedRole.getId())
                    .roleName(savedRole.getRoleName())
                    .message("Success")
                    .build();

        } catch (Exception e) {
            log.error("Failed to create role", e);
            recordAudit(traceId, operatorId, null, null, OperationType.ROLE_CREATE, OperationResult.FAILED, null);
            return CreateRoleRespDTO.builder()
                    .success(false)
                    .roleName(req.getRoleName())
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public DeleteRoleRespDTO deleteRole(DeleteRoleReqDTO req) {
        String traceId = UUID.randomUUID().toString();
        Long operatorId = req.getOperatorId();
        Long roleId = req.getRoleId();

        try {
            Optional<Role> roleOpt = roleRepository.findById(roleId);
            if (roleOpt.isEmpty()) {
                log.warn("Role not found: {}", roleId);
                return DeleteRoleRespDTO.builder()
                        .success(false)
                        .roleId(roleId)
                        .message("Role not found")
                        .build();
            }
            Role role = roleOpt.get();

            rolePermissionRepository.deleteByRoleId(roleId);
            userRoleRepository.deleteByRoleId(roleId);
            roleRepository.delete(roleId);
            roleRepository.incrementVersionIfMatch(roleId, role.getRoleVersion());

            recordAudit(traceId, operatorId, roleId, null, OperationType.ROLE_DELETE, OperationResult.SUCCESS, null);

            PermissionChangeMessage message = PermissionChangeMessage.builder()
                    .type(ChangeType.ROLE)
                    .roleId(roleId)
                    .newVersion(System.currentTimeMillis())
                    .timestamp(System.currentTimeMillis())
                    .build();
            permissionChangePublisher.publishPermissionChange(message);

            return DeleteRoleRespDTO.builder()
                    .success(true)
                    .roleId(roleId)
                    .message("Success")
                    .build();

        } catch (Exception e) {
            log.error("Failed to delete role", e);
            recordAudit(traceId, operatorId, roleId, null, OperationType.ROLE_DELETE, OperationResult.FAILED, null);
            return DeleteRoleRespDTO.builder()
                    .success(false)
                    .roleId(roleId)
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public CreatePermissionRespDTO createPermission(CreatePermissionReqDTO req) {
        String traceId = UUID.randomUUID().toString();
        Long operatorId = req.getOperatorId();

        try {
            Permission permission = new Permission();
            permission.setPermissionCode(req.getPermissionCode());
            permission.setActionCode(req.getActionCode());
            Permission savedPermission = permissionRepository.save(permission);

            recordAudit(traceId, operatorId, null, null, OperationType.PERM_CREATE, OperationResult.SUCCESS, null);

            return CreatePermissionRespDTO.builder()
                    .success(true)
                    .permissionId(savedPermission.getId())
                    .permissionCode(savedPermission.getPermissionCode())
                    .message("Success")
                    .build();

        } catch (Exception e) {
            log.error("Failed to create permission", e);
            recordAudit(traceId, operatorId, null, null, OperationType.PERM_CREATE, OperationResult.FAILED, null);
            return CreatePermissionRespDTO.builder()
                    .success(false)
                    .permissionCode(req.getPermissionCode())
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public DeletePermissionRespDTO deletePermission(DeletePermissionReqDTO req) {
        String traceId = UUID.randomUUID().toString();
        Long operatorId = req.getOperatorId();
        Long permissionId = req.getPermissionId();

        try {
            Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
            if (permissionOpt.isEmpty()) {
                log.warn("Permission not found: {}", permissionId);
                return DeletePermissionRespDTO.builder()
                        .success(false)
                        .permissionId(permissionId)
                        .message("Permission not found")
                        .build();
            }

            rolePermissionRepository.deleteByPermissionId(permissionId);
            permissionRepository.delete(permissionId);

            recordAudit(traceId, operatorId, null, null, OperationType.PERM_DELETE, OperationResult.SUCCESS, null);

            return DeletePermissionRespDTO.builder()
                    .success(true)
                    .permissionId(permissionId)
                    .message("Success")
                    .build();

        } catch (Exception e) {
            log.error("Failed to delete permission", e);
            recordAudit(traceId, operatorId, null, null, OperationType.PERM_DELETE, OperationResult.FAILED, null);
            return DeletePermissionRespDTO.builder()
                    .success(false)
                    .permissionId(permissionId)
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public RegisterAgentRespDTO registerAgent(RegisterAgentReqDTO req) {
        String traceId = UUID.randomUUID().toString();
        Long operatorId = req.getOperatorId();

        try {
            Agent agent = new Agent();
            agent.setClientId(req.getClientId());
            agent.setClientSecretHash(req.getClientSecretHash());
            agent.setAgentName(req.getAgentName());
            agent.setFrameworkType(req.getFrameworkType());
            agent.setAgentCardUrl(req.getAgentCardUrl());
            agent.setPublicKey(req.getPublicKey());
            agent.setCapabilities(req.getCapabilities() != null ? Set.of(req.getCapabilities().split(",")) : Set.of());
            Agent savedAgent = agentRepository.save(agent);

            recordAudit(traceId, operatorId, null, null, OperationType.AGENT_REGISTER, OperationResult.SUCCESS, null);

            return RegisterAgentRespDTO.builder()
                    .success(true)
                    .agentId(savedAgent.getId())
                    .clientId(savedAgent.getClientId())
                    .message("Success")
                    .build();
        } catch (Exception e) {
            log.error("Failed to register agent", e);
            recordAudit(traceId, operatorId, null, null, OperationType.AGENT_REGISTER, OperationResult.FAILED, null);
            return RegisterAgentRespDTO.builder()
                    .success(false)
                    .clientId(req.getClientId())
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 轮转agent密钥
     * @param req
     * @return
     */
    @Override
    @Transactional
    public RotateAgentSecretRespDTO rotateAgentSecret(RotateAgentSecretReqDTO req) {
        String traceId = UUID.randomUUID().toString();
        Long operatorId = req.getOperatorId();

        try {
            Optional<Agent> agentOpt = agentRepository.findByClientId(req.getClientId());
            if (agentOpt.isEmpty()) {
                log.warn("Agent not found: {}", req.getClientId());
                return RotateAgentSecretRespDTO.builder()
                        .success(false)
                        .clientId(req.getClientId())
                        .message("Agent not found")
                        .build();
            }

            Agent agent = agentOpt.get();
            agent.setClientSecretHash(req.getNewSecretHash());
            agentRepository.save(agent);

            recordAudit(traceId, operatorId, null, null, OperationType.AGENT_SECRET_ROTATE, OperationResult.SUCCESS, null);

            return RotateAgentSecretRespDTO.builder()
                    .success(true)
                    .clientId(req.getClientId())
                    .message("Success")
                    .build();
        } catch (Exception e) {
            log.error("Failed to rotate agent secret", e);
            recordAudit(traceId, operatorId, null, null, OperationType.AGENT_SECRET_ROTATE, OperationResult.FAILED, null);
            return RotateAgentSecretRespDTO.builder()
                    .success(false)
                    .clientId(req.getClientId())
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    private void recordAudit(String traceId, Long operatorId, Long targetRoleId, Set<Long> targetUserIds,
                             OperationType operationType, OperationResult result, Object diff) {
        try {
            AuditLogDTO dto = AuditLogDTO.builder()
                    .traceId(traceId)
                    .operationType(operationType)
                    .operationResult(result)
                    .operatorId(operatorId)
                    .targetRoleId(targetRoleId)
                    .operationDetail(diff != null ? objectMapper.writeValueAsString(diff) : null)
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