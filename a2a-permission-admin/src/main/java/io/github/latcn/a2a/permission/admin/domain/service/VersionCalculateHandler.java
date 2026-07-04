package io.github.latcn.a2a.permission.admin.domain.service;

import io.github.latcn.a2a.permission.admin.domain.model.Role;
import io.github.latcn.a2a.permission.admin.domain.model.User;
import io.github.latcn.a2a.permission.api.dto.RoleInfo;
import io.github.latcn.a2a.permission.api.dto.UserFullPermissionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class VersionCalculateHandler extends PermissionCalculateHandler {

    private final CombinedVersionCalculator combinedVersionCalculator;

    @Override
    public void handle(PermissionContext context) {
        try {
            log.debug("VersionCalculateHandler starting for userId: {}", context.getUserId());

            User user = context.getUser();
            Long userPermVersion = user != null ? user.getPermVersion() : 0L;
            context.setUserPermVersion(userPermVersion);

            Map<Long, Long> roleVersions = context.getRoles().stream()
                    .collect(Collectors.toMap(Role::getId, Role::getRoleVersion));
            context.setRoleVersions(roleVersions);

            String combinedVersion = combinedVersionCalculator.calculate(userPermVersion, roleVersions);
            context.setCombinedVersion(combinedVersion);

            List<RoleInfo> roleInfos = context.getRoles().stream()
                    .map(role -> RoleInfo.builder()
                            .id(role.getId())
                            .roleName(role.getRoleName())
                            .priority(role.getPriority())
                            .roleVersion(role.getRoleVersion())
                            .build())
                    .collect(Collectors.toList());

            UserFullPermissionDTO result = UserFullPermissionDTO.builder()
                    .userId(context.getUserId())
                    .username(user != null ? user.getUsername() : null)
                    .permissions(context.getExpandedPermissions())
                    .rowRules(context.getFinalRowRules())
                    .roles(roleInfos)
                    .combinedVersion(combinedVersion)
                    .userPermVersion(userPermVersion)
                    .roleVersions(roleVersions)
                    .departmentPath(new ArrayList<>())
                    .build();

            context.setResult(result);

            log.debug("VersionCalculateHandler completed for userId: {}, combinedVersion: {}",
                    context.getUserId(), combinedVersion);

        } catch (Exception e) {
            log.error("VersionCalculateHandler failed for userId: {}", context.getUserId(), e);
            context.setError(e);
        }
    }
}