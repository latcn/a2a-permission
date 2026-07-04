package io.github.latcn.a2a.permission.admin.interfaces.web;

import io.github.latcn.a2a.permission.admin.application.cmd.PermissionAdminService;
import io.github.latcn.a2a.permission.admin.application.dto.*;
import io.github.latcn.a2a.permission.admin.infrastructure.mapper.InterfaceDtoMapper;
import io.github.latcn.a2a.permission.admin.interfaces.request.*;
import io.github.latcn.a2a.permission.admin.interfaces.response.GrantPermissionResp;
import io.github.latcn.a2a.permission.admin.interfaces.response.GrantRoleResp;
import io.github.latcn.a2a.permission.admin.interfaces.response.RevokeRoleResp;
import io.github.latcn.a2a.permission.admin.interfaces.response.UpdateRowRuleResp;
import io.github.latcn.a2a.permission.api.dto.AgentDTO;
import io.github.latcn.a2a.permission.api.dto.AclCheckResultDTO;
import io.github.latcn.a2a.permission.api.dto.TokenExchangePrepareRequest;
import io.github.latcn.a2a.permission.api.dto.TokenExchangePrepareResponse;
import io.github.latcn.a2a.permission.api.dto.UserFullPermissionDTO;
import io.github.latcn.a2a.permission.api.service.PermissionQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionAdminService permissionAdminService;
    private final PermissionQueryService permissionQueryService;
    private final InterfaceDtoMapper interfaceDtoMapper;

    @GetMapping("/user/{userId}/full-permissions")
    public ResponseEntity<UserFullPermissionDTO> getUserPermissions(@PathVariable Long userId) {
        log.info("Getting permissions for user: {}", userId);
        UserFullPermissionDTO result = permissionQueryService.getUserFullPermissions(userId);
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
    public ResponseEntity<AclCheckResultDTO> checkAcl(
            @PathVariable String sourceClientId,
            @PathVariable String targetClientId) {
        log.info("Checking ACL from {} to {}", sourceClientId, targetClientId);
        AclCheckResultDTO result = permissionQueryService.checkAcl(sourceClientId, targetClientId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/roles/{roleId}/grant")
    public ResponseEntity<GrantRoleResp> grantRole(
            @PathVariable Long roleId,
            @Valid @RequestBody GrantRoleReq req) {
        log.info("Granting role {} to users: {}", roleId, req.getUserIds());
        req.setRoleId(roleId);
        GrantRoleReqDTO reqDTO = interfaceDtoMapper.toGrantRoleReqDTO(req);
        GrantRoleRespDTO respDTO = permissionAdminService.grantRole(reqDTO);
        GrantRoleResp resp = interfaceDtoMapper.toGrantRoleResp(respDTO);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/roles/{roleId}/revoke")
    public ResponseEntity<RevokeRoleResp> revokeRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RevokeRoleReq req) {
        log.info("Revoking role {} from users: {}", roleId, req.getUserIds());
        req.setRoleId(roleId);
        RevokeRoleReqDTO reqDTO = interfaceDtoMapper.toRevokeRoleReqDTO(req);
        RevokeRoleRespDTO respDTO = permissionAdminService.revokeRole(reqDTO);
        RevokeRoleResp resp = interfaceDtoMapper.toRevokeRoleResp(respDTO);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<GrantPermissionResp> grantPermission(
            @PathVariable Long roleId,
            @Valid @RequestBody GrantPermissionReq req) {
        log.info("Granting permissions {} to role: {}", req.getPermissionIds(), roleId);
        req.setRoleId(roleId);
        GrantPermissionReqDTO reqDTO = interfaceDtoMapper.toGrantPermissionReqDTO(req);
        GrantPermissionRespDTO respDTO = permissionAdminService.grantPermission(reqDTO);
        GrantPermissionResp resp = interfaceDtoMapper.toGrantPermissionResp(respDTO);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/roles/{roleId}/permissions/{permissionId}/row-rule")
    public ResponseEntity<UpdateRowRuleResp> updateRowRule(
            @PathVariable Long roleId,
            @PathVariable Long permissionId,
            @Valid @RequestBody UpdateRowRuleReq req) {
        log.info("Updating row rule for role {} permission {}: {}", roleId, permissionId, req.getRowRule());
        UpdateRowRuleReqDTO reqDTO = interfaceDtoMapper.toUpdateRowRuleReqDTO(roleId, permissionId, req);
        UpdateRowRuleRespDTO respDTO = permissionAdminService.updateRowRule(reqDTO);
        UpdateRowRuleResp resp = interfaceDtoMapper.toUpdateRowRuleResp(respDTO);
        return ResponseEntity.ok(resp);
    }
}