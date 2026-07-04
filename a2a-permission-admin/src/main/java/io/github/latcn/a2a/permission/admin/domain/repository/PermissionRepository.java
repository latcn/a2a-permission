package io.github.latcn.a2a.permission.admin.domain.repository;

import io.github.latcn.a2a.permission.admin.domain.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

    Optional<Permission> findById(Long id);

    Permission save(Permission permission);

    void delete(Long id);

    List<Permission> findByIds(List<Long> ids);

    List<String> findAllPermissionCodes();
}