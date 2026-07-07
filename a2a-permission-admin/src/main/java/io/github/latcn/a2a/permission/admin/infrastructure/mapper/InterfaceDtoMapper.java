package io.github.latcn.a2a.permission.admin.infrastructure.mapper;

import io.github.latcn.a2a.permission.admin.application.dto.*;
import io.github.latcn.a2a.permission.admin.interfaces.request.*;
import io.github.latcn.a2a.permission.admin.interfaces.response.GrantPermissionResp;
import io.github.latcn.a2a.permission.admin.interfaces.response.GrantRoleResp;
import io.github.latcn.a2a.permission.admin.interfaces.response.RevokeRoleResp;
import io.github.latcn.a2a.permission.admin.interfaces.response.UpdateRowRuleResp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterfaceDtoMapper {

    @Mapping(target = "operatorId", constant = "1L")
    GrantRoleReqDTO toGrantRoleReqDTO(GrantRoleReq req);

    GrantRoleResp toGrantRoleResp(GrantRoleRespDTO resp);

    @Mapping(target = "operatorId", constant = "1L")
    RevokeRoleReqDTO toRevokeRoleReqDTO(RevokeRoleReq req);

    RevokeRoleResp toRevokeRoleResp(RevokeRoleRespDTO resp);

    @Mapping(target = "operatorId", constant = "1L")
    GrantPermissionReqDTO toGrantPermissionReqDTO(GrantPermissionReq req);

    GrantPermissionResp toGrantPermissionResp(GrantPermissionRespDTO resp);

    @Mapping(target = "operatorId", constant = "1L")
    @Mapping(target = "newRowRule", source = "req.rowRule")
    UpdateRowRuleReqDTO toUpdateRowRuleReqDTO(Long roleId, Long permissionId, UpdateRowRuleReq req);

    UpdateRowRuleResp toUpdateRowRuleResp(UpdateRowRuleRespDTO resp);
}