package io.github.latcn.a2a.permission.admin.infrastructure.mapper;

import io.github.latcn.a2a.permission.admin.domain.model.*;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DomainModelMapper {

    User toDomain(UserDO userDO);
    UserDO toDO(User user);
    List<User> toUserList(List<UserDO> userDOList);
    List<UserDO> toUserDOList(List<User> userList);

    Role toDomain(RoleDO roleDO);
    RoleDO toDO(Role role);
    List<Role> toRoleList(List<RoleDO> roleDOList);
    List<RoleDO> toRoleDOList(List<Role> roleList);

    Permission toDomain(PermissionDO permissionDO);
    PermissionDO toDO(Permission permission);
    List<Permission> toPermissionList(List<PermissionDO> permissionDOList);
    List<PermissionDO> toPermissionDOList(List<Permission> permissionList);

    UserRole toDomain(UserRoleDO userRoleDO);
    UserRoleDO toDO(UserRole userRole);
    List<UserRole> toUserRoleList(List<UserRoleDO> userRoleDOList);
    List<UserRoleDO> toUserRoleDOList(List<UserRole> userRoleList);

    RolePermission toDomain(RolePermissionDO rolePermissionDO);
    RolePermissionDO toDO(RolePermission rolePermission);
    List<RolePermission> toRolePermissionList(List<RolePermissionDO> rolePermissionDOList);
    List<RolePermissionDO> toRolePermissionDOList(List<RolePermission> rolePermissionList);

    Agent toDomain(AgentDO agentDO);
    AgentDO toDO(Agent agent);

    A2AAcl toDomain(A2AAclDO a2AAclDO);
    A2AAclDO toDO(A2AAcl a2AAcl);

    Department toDomain(DepartmentDO departmentDO);
    DepartmentDO toDO(Department department);
    List<Department> toDepartmentList(List<DepartmentDO> departmentDOList);
    List<DepartmentDO> toDepartmentDOList(List<Department> departmentList);

    AuditLog toDomain(AuditLogDO auditLogDO);
    AuditLogDO toDO(AuditLog auditLog);
    List<AuditLog> toAuditLogList(List<AuditLogDO> auditLogDOList);
    List<AuditLogDO> toAuditLogDOList(List<AuditLog> auditLogList);
}