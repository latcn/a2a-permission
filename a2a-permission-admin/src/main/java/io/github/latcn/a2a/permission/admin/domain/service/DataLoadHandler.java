package io.github.latcn.a2a.permission.admin.domain.service;

import io.github.latcn.a2a.permission.admin.domain.model.*;
import io.github.latcn.a2a.permission.admin.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoadHandler extends PermissionCalculateHandler {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void handle(PermissionContext context) {
        try {
            log.debug("DataLoadHandler starting for userId: {}", context.getUserId());

            Optional<User> userOpt = userRepository.findById(context.getUserId());
            if (userOpt.isEmpty()) {
                log.warn("User not found: {}", context.getUserId());
                return;
            }
            User user = userOpt.get();
            context.setUser(user);

            List<UserRole> userRoles = userRoleRepository.findByUserId(context.getUserId());
            context.setUserRoles(userRoles);

            List<Long> roleIds = userRoles.stream()
                    .map(UserRole::getRoleId)
                    .collect(Collectors.toList());

            if (roleIds.isEmpty()) {
                log.debug("No roles found for user: {}", context.getUserId());
                return;
            }

            List<Role> roles = roleRepository.findByIds(roleIds);
            roles.sort(Comparator.comparing(Role::getPriority).reversed());
            context.setRoles(roles);

            List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIds(roleIds);
            context.setRolePermissions(rolePermissions);

            Set<Long> permissionIds = rolePermissions.stream()
                    .map(RolePermission::getPermissionId)
                    .collect(Collectors.toSet());

            if (!permissionIds.isEmpty()) {
                List<Permission> permissions = permissionRepository.findByIds(new ArrayList<>(permissionIds));
                context.setPermissions(permissions);
            }

            log.debug("DataLoadHandler completed for userId: {}, roles: {}, permissions: {}",
                    context.getUserId(), roles.size(), context.getPermissions().size());

        } catch (Exception e) {
            log.error("DataLoadHandler failed for userId: {}", context.getUserId(), e);
            context.setError(e);
        }
    }
}