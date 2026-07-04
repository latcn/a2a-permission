package io.github.latcn.a2a.permission.admin.domain.repository;

import io.github.latcn.a2a.permission.admin.domain.model.RolePermission;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository {

    RolePermission save(RolePermission rolePermission);

    void deleteById(Long id);

    void deleteByRoleId(Long roleId);

    void deleteByPermissionId(Long permissionId);

    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);

    Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId);

    List<RolePermission> findByRoleIds(List<Long> roleIds);

    List<RolePermission> findByRoleId(Long roleId);
}