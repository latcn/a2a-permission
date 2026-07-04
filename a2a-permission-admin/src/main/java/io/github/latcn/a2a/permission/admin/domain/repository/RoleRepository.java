package io.github.latcn.a2a.permission.admin.domain.repository;

import io.github.latcn.a2a.permission.admin.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findById(Long id);

    Optional<Role> findByName(String name);

    Role save(Role role);

    void delete(Long id);

    int incrementVersionIfMatch(Long roleId, Long oldVersion);

    List<Role> findByIds(List<Long> ids);
}