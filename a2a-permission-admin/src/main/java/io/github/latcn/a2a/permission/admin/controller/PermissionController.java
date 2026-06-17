package io.github.latcn.a2a.permission.admin.controller;

import io.github.latcn.a2a.permission.admin.service.PermissionAdminService;
import io.github.latcn.a2a.permission.api.dto.*;
import io.github.latcn.a2a.permission.api.service.PermissionQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionAdminService permissionAdminService;
    private final PermissionQueryService permissionQueryService;

    @GetMapping("/user/{userId}/full-permissions")
    public ResponseEntity<UserFullPermissionDTO> getUserPermissions(@PathVariable Long userId) {
        log.info("Getting permissions for user: {}", userId);
        UserFullPermissionDTO result = permissionAdminService.getUserFullPermissions(userId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/token-exchange/prepare")
    public ResponseEntity<TokenExchangePrepareResponse> prepareTokenExchange(
            @Valid @RequestBody TokenExchangePrepareRequest request) {
        log.info("Preparing token exchange: {}", request);
        TokenExchangePrepareResponse response = permissionQueryService.prepareTokenExchange(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/agent/{clientId}")
    public ResponseEntity<AgentDTO> getAgent(@PathVariable String clientId) {
        log.info("Getting agent for clientId: {}", clientId);
        AgentDTO agent = permissionQueryService.getAgent(clientId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(agent);
    }

    @GetMapping("/acl/{sourceClientId}/{targetClientId}")
    public ResponseEntity<AclCheckResult> checkAcl(
            @PathVariable String sourceClientId,
            @PathVariable String targetClientId) {
        log.info("Checking ACL from {} to {}", sourceClientId, targetClientId);
        AclCheckResult result = permissionQueryService.checkAcl(sourceClientId, targetClientId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/roles/{roleId}/grant")
    public ResponseEntity<Map<String, Object>> grantRole(
            @PathVariable Long roleId,
            @RequestBody Set<Long> userIds) {
        log.info("Granting role {} to users: {}", roleId, userIds);
        Long operatorId = 1L;
        boolean success = permissionAdminService.grantRole(operatorId, roleId, userIds);
        return ResponseEntity.ok(Map.of("success", success, "roleId", roleId, "userCount", userIds.size()));
    }

    @PostMapping("/roles/{roleId}/revoke")
    public ResponseEntity<Map<String, Object>> revokeRole(
            @PathVariable Long roleId,
            @RequestBody Set<Long> userIds) {
        log.info("Revoking role {} from users: {}", roleId, userIds);
        Long operatorId = 1L;
        boolean success = permissionAdminService.revokeRole(operatorId, roleId, userIds);
        return ResponseEntity.ok(Map.of("success", success, "roleId", roleId, "userCount", userIds.size()));
    }

    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<Map<String, Object>> grantPermission(
            @PathVariable Long roleId,
            @RequestBody Set<Long> permissionIds) {
        log.info("Granting permissions {} to role: {}", permissionIds, roleId);
        Long operatorId = 1L;
        boolean success = permissionAdminService.grantPermission(operatorId, roleId, permissionIds);
        return ResponseEntity.ok(Map.of("success", success, "roleId", roleId, "permissionCount", permissionIds.size()));
    }

    @PutMapping("/roles/{roleId}/permissions/{permissionId}/row-rule")
    public ResponseEntity<Map<String, Object>> updateRowRule(
            @PathVariable Long roleId,
            @PathVariable Long permissionId,
            @RequestBody Map<String, String> body) {
        String newRowRule = body.get("rowRule");
        log.info("Updating row rule for role {} permission {}: {}", roleId, permissionId, newRowRule);
        Long operatorId = 1L;
        boolean success = permissionAdminService.updateRowRule(operatorId, roleId, permissionId, newRowRule);
        return ResponseEntity.ok(Map.of("success", success, "roleId", roleId, "permissionId", permissionId));
    }
}