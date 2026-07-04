package io.github.latcn.a2a.permission.admin.domain.repository;

import io.github.latcn.a2a.permission.admin.domain.model.UserRole;

import java.util.List;

public interface UserRoleRepository {

    UserRole save(UserRole userRole);

    void deleteByUserIdAndRoleId(Long userId, Long roleId);

    void deleteByRoleId(Long roleId);

    List<UserRole> findByUserId(Long userId);

    List<UserRole> findByRoleId(Long roleId);
}