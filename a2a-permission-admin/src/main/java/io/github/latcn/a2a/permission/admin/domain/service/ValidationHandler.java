package io.github.latcn.a2a.permission.admin.domain.service;

import io.github.latcn.a2a.permission.admin.domain.model.Permission;
import io.github.latcn.a2a.permission.admin.domain.model.Role;
import io.github.latcn.a2a.permission.admin.domain.model.RolePermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationHandler extends PermissionCalculateHandler {

    private final WildcardExpander wildcardExpander;

    @Override
    public void handle(PermissionContext context) {
        try {
            log.debug("ValidationHandler starting for userId: {}", context.getUserId());

            if (context.getRoles().isEmpty()) {
                context.setExpandedPermissions(new HashSet<>());
                log.debug("No roles, expandedPermissions is empty");
                return;
            }

            Map<Long, Role> roleMap = context.getRoles().stream()
                    .collect(Collectors.toMap(Role::getId, r -> r));

            Map<Long, Permission> permMap = context.getPermissions().stream()
                    .collect(Collectors.toMap(Permission::getId, p -> p));

            Map<Long, List<RolePermission>> rolePermissionsMap = context.getRolePermissions().stream()
                    .collect(Collectors.groupingBy(RolePermission::getRoleId));

            Set<String> allExpandedPermissions = new HashSet<>();

            for (Role role : context.getRoles()) {
                List<RolePermission> rolePermissions = rolePermissionsMap.getOrDefault(role.getId(), Collections.emptyList());
                List<Permission> permissions = rolePermissions.stream()
                        .map(rp -> permMap.get(rp.getPermissionId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                Set<String> expanded = wildcardExpander.expand(permissions);
                allExpandedPermissions.addAll(expanded);
            }

            context.setExpandedPermissions(allExpandedPermissions);
            log.debug("ValidationHandler completed for userId: {}, expandedPermissions size: {}",
                    context.getUserId(), allExpandedPermissions.size());

        } catch (Exception e) {
            log.error("ValidationHandler failed for userId: {}", context.getUserId(), e);
            context.setError(e);
        }
    }
}