package io.github.latcn.a2a.permission.admin.infrastructure.mapper;

import io.github.latcn.a2a.permission.admin.application.dto.*;
import io.github.latcn.a2a.permission.admin.domain.model.Agent;
import io.github.latcn.a2a.permission.admin.domain.model.Role;
import io.github.latcn.a2a.permission.admin.domain.model.RolePermission;
import io.github.latcn.a2a.permission.admin.domain.model.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApplicationDtoMapper {

    @Mapping(target = "operatorId", ignore = true)
    GrantRoleReqDTO toGrantRoleReqDTO(GrantRoleReqDTO req);

    @Mapping(target = "roleId", source = "role.id")
    GrantRoleRespDTO toGrantRoleRespDTO(GrantRoleRespDTO resp);

    @Mapping(target = "operatorId", ignore = true)
    RevokeRoleReqDTO toRevokeRoleReqDTO(RevokeRoleReqDTO req);

    @Mapping(target = "roleId", source = "role.id")
    RevokeRoleRespDTO toRevokeRoleRespDTO(RevokeRoleRespDTO resp);

    @Mapping(target = "operatorId", ignore = true)
    GrantPermissionReqDTO toGrantPermissionReqDTO(GrantPermissionReqDTO req);

    @Mapping(target = "roleId", source = "role.id")
    GrantPermissionRespDTO toGrantPermissionRespDTO(GrantPermissionRespDTO resp);

    @Mapping(target = "operatorId", ignore = true)
    UpdateRowRuleReqDTO toUpdateRowRuleReqDTO(UpdateRowRuleReqDTO req);

    UpdateRowRuleRespDTO toUpdateRowRuleRespDTO(UpdateRowRuleRespDTO resp);

    @Mapping(target = "operatorId", ignore = true)
    CreateRoleReqDTO toCreateRoleReqDTO(CreateRoleReqDTO req);

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "roleName", source = "role.roleName")
    CreateRoleRespDTO toCreateRoleRespDTO(Role role, Boolean success, String message);

    @Mapping(target = "operatorId", ignore = true)
    DeleteRoleReqDTO toDeleteRoleReqDTO(DeleteRoleReqDTO req);

    @Mapping(target = "roleId", source = "role.id")
    DeleteRoleRespDTO toDeleteRoleRespDTO(Role role, Boolean success, String message);

    @Mapping(target = "operatorId", ignore = true)
    CreatePermissionReqDTO toCreatePermissionReqDTO(CreatePermissionReqDTO req);

    CreatePermissionRespDTO toCreatePermissionRespDTO(CreatePermissionRespDTO resp);

    @Mapping(target = "operatorId", ignore = true)
    DeletePermissionReqDTO toDeletePermissionReqDTO(DeletePermissionReqDTO req);

    DeletePermissionRespDTO toDeletePermissionRespDTO(DeletePermissionRespDTO resp);

    Agent toAgent(RegisterAgentReqDTO req);

    @Mapping(target = "agentId", source = "agent.id")
    @Mapping(target = "clientId", source = "agent.clientId")
    RegisterAgentRespDTO toRegisterAgentRespDTO(Agent agent, Boolean success, String message);

    RotateAgentSecretReqDTO toRotateAgentSecretReqDTO(RotateAgentSecretReqDTO req);

    @Mapping(target = "clientId", source = "agent.clientId")
    RotateAgentSecretRespDTO toRotateAgentSecretRespDTO(Agent agent, Boolean success, String message);

    UserRole toUserRole(Long userId, Long roleId);

    RolePermission toRolePermission(Long roleId, Long permissionId, Integer effect);
}