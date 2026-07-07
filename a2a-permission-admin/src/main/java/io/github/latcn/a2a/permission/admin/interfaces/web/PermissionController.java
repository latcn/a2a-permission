package io.github.latcn.a2a.permission.admin.interfaces.web;

import cn.hutool.core.date.DateUtil;
import io.github.latcn.a2a.permission.admin.application.cmd.PermissionAdminService;
import io.github.latcn.a2a.permission.admin.application.dto.*;
import io.github.latcn.a2a.permission.admin.application.query.LocalPermissionQueryService;
import io.github.latcn.a2a.permission.admin.infrastructure.mapper.InterfaceDtoMapper;
import io.github.latcn.a2a.permission.admin.interfaces.request.GrantPermissionReq;
import io.github.latcn.a2a.permission.admin.interfaces.request.GrantRoleReq;
import io.github.latcn.a2a.permission.admin.interfaces.request.RevokeRoleReq;
import io.github.latcn.a2a.permission.admin.interfaces.request.UpdateRowRuleReq;
import io.github.latcn.a2a.permission.admin.interfaces.response.GrantPermissionResp;
import io.github.latcn.a2a.permission.admin.interfaces.response.GrantRoleResp;
import io.github.latcn.a2a.permission.admin.interfaces.response.RevokeRoleResp;
import io.github.latcn.a2a.permission.admin.interfaces.response.UpdateRowRuleResp;
import io.github.latcn.a2a.permission.api.dto.*;
import io.github.latcn.archbase.foundation.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionAdminService permissionAdminService;
    private final LocalPermissionQueryService permissionQueryService;
    private final InterfaceDtoMapper interfaceDtoMapper;

    @GetMapping("/user/{userId}/full-permissions")
    public Result<UserFullPermissionDTO> getUserPermissions(@PathVariable Long userId) {
        log.info("Getting permissions for user: {}", userId);
        UserFullPermissionDTO result = new UserFullPermissionDTO();//permissionQueryService.getUserFullPermissions(userId);
        result.setUserId(userId);
        result.setUsername(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        if (result == null) {
            return Result.success();
        }
        return Result.success(result);
    }

    @PostMapping("/token-exchange/prepare")
    public Result<TokenExchangePrepareResponse> prepareTokenExchange(
            @Valid @RequestBody TokenExchangePrepareRequest request) {
        log.info("Preparing token exchange: {}", request);
        TokenExchangePrepareResponse response = permissionQueryService.prepareTokenExchange(request);
        return Result.success(response);
    }

    @GetMapping("/agent/{clientId}")
    public Result<AgentDTO> getAgent(@PathVariable String clientId) {
        log.info("Getting agent for clientId: {}", clientId);
        AgentDTO agent = permissionQueryService.getAgent(clientId);
        if (agent == null) {
            return Result.success();
        }
        return Result.success(agent);
    }

    @GetMapping("/acl/{sourceClientId}/{targetClientId}")
    public Result<AclCheckResultDTO> checkAcl(
            @PathVariable String sourceClientId,
            @PathVariable String targetClientId) {
        log.info("Checking ACL from {} to {}", sourceClientId, targetClientId);
        AclCheckResultDTO result = permissionQueryService.checkAcl(sourceClientId, targetClientId);
        return Result.success(result);
    }

    @PostMapping("/roles/{roleId}/grant")
    public Result<GrantRoleResp> grantRole(
            @PathVariable Long roleId,
            @Valid @RequestBody GrantRoleReq req) {
        log.info("Granting role {} to users: {}", roleId, req.getUserIds());
        req.setRoleId(roleId);
        GrantRoleReqDTO reqDTO = interfaceDtoMapper.toGrantRoleReqDTO(req);
        GrantRoleRespDTO respDTO = permissionAdminService.grantRole(reqDTO);
        GrantRoleResp resp = interfaceDtoMapper.toGrantRoleResp(respDTO);
        return Result.success(resp);
    }

    @PostMapping("/roles/{roleId}/revoke")
    public Result<RevokeRoleResp> revokeRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RevokeRoleReq req) {
        log.info("Revoking role {} from users: {}", roleId, req.getUserIds());
        req.setRoleId(roleId);
        RevokeRoleReqDTO reqDTO = interfaceDtoMapper.toRevokeRoleReqDTO(req);
        RevokeRoleRespDTO respDTO = permissionAdminService.revokeRole(reqDTO);
        RevokeRoleResp resp = interfaceDtoMapper.toRevokeRoleResp(respDTO);
        return Result.success(resp);
    }

    @PostMapping("/roles/{roleId}/permissions")
    public Result<GrantPermissionResp> grantPermission(
            @PathVariable Long roleId,
            @Valid @RequestBody GrantPermissionReq req) {
        log.info("Granting permissions {} to role: {}", req.getPermissionIds(), roleId);
        req.setRoleId(roleId);
        GrantPermissionReqDTO reqDTO = interfaceDtoMapper.toGrantPermissionReqDTO(req);
        GrantPermissionRespDTO respDTO = permissionAdminService.grantPermission(reqDTO);
        GrantPermissionResp resp = interfaceDtoMapper.toGrantPermissionResp(respDTO);
        return Result.success(resp);
    }

    @PutMapping("/roles/{roleId}/permissions/{permissionId}/row-rule")
    public Result<UpdateRowRuleResp> updateRowRule(
            @PathVariable Long roleId,
            @PathVariable Long permissionId,
            @Valid @RequestBody UpdateRowRuleReq req) {
        log.info("Updating row rule for role {} permission {}: {}", roleId, permissionId, req.getRowRule());
        UpdateRowRuleReqDTO reqDTO = interfaceDtoMapper.toUpdateRowRuleReqDTO(roleId, permissionId, req);
        UpdateRowRuleRespDTO respDTO = permissionAdminService.updateRowRule(reqDTO);
        UpdateRowRuleResp resp = interfaceDtoMapper.toUpdateRowRuleResp(respDTO);
        return Result.success(resp);
    }
}