package io.github.latcn.a2a.permission.admin.engine;

import io.github.latcn.a2a.permission.api.dto.RoleInfo;
import io.github.latcn.a2a.permission.api.dto.UserFullPermissionDTO;
import io.github.latcn.a2a.permission.admin.domain.entity.User;
import io.github.latcn.a2a.permission.admin.domain.entity.Role;
import io.github.latcn.a2a.permission.admin.domain.entity.Permission;
import io.github.latcn.a2a.permission.admin.domain.entity.RolePermission;
import io.github.latcn.a2a.permission.admin.infra.mapper.UserMapper;
import io.github.latcn.a2a.permission.admin.infra.mapper.RoleMapper;
import io.github.latcn.a2a.permission.admin.infra.mapper.PermissionMapper;
import io.github.latcn.a2a.permission.admin.infra.mapper.RolePermissionMapper;
import io.github.latcn.a2a.permission.admin.infra.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionCalculator {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final RowRuleMerger rowRuleMerger;
    private final WildcardExpander wildcardExpander;
    private final MandatoryRuleInjector mandatoryRuleInjector;
    private final ExpressionOptimizer expressionOptimizer;
    private final CombinedVersionCalculator combinedVersionCalculator;
    private final UserContextEnricher userContextEnricher;

    public UserFullPermissionDTO calculateFullPermissions(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("User not found: {}", userId);
            return UserFullPermissionDTO.builder()
                    .userId(userId)
                    .permissions(Collections.emptySet())
                    .rowRules(Collections.emptyMap())
                    .roles(Collections.emptyList())
                    .build();
        }

        List<Long> roleIds = userRoleMapper.selectByUserId(userId).stream()
                .map(ur -> ur.getRoleId())
                .collect(Collectors.toList());

        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        roles.sort(Comparator.comparing(Role::getPriority).reversed());

        Map<Long, Role> roleMap = roles.stream()
                .collect(Collectors.toMap(Role::getId, r -> r));

        List<RolePermission> allRolePermissions = rolePermissionMapper.selectByRoleIds(roleIds);

        Map<Long, List<RolePermission>> rolePermissionsMap = allRolePermissions.stream()
                .collect(Collectors.groupingBy(RolePermission::getRoleId));

        Set<String> allPermissions = new HashSet<>();
        Map<String, String> allRowRules = new HashMap<>();

        for (Role role : roles) {
            List<RolePermission> rolePermissions = rolePermissionsMap.getOrDefault(role.getId(), Collections.emptyList());
            List<Permission> permissions = permissionMapper.selectBatchIds(
                    rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toList())
            );

            Set<String> expandedPermissions = wildcardExpander.expand(permissions);
            allPermissions.addAll(expandedPermissions);

            Map<String, String> rowRules = rowRuleMerger.merge(role, rolePermissions, permissions);
            allRowRules.putAll(rowRules);
        }

        Map<Long, Long> roleVersions = roles.stream()
                .collect(Collectors.toMap(Role::getId, Role::getRoleVersion));

        Map<String, String> optimizedRowRules = expressionOptimizer.optimize(allRowRules);

        Map<String, String> finalRowRules = new HashMap<>();
        for (Map.Entry<String, String> entry : optimizedRowRules.entrySet()) {
            String permCode = entry.getKey();
            String rule = entry.getValue();
            String injected = mandatoryRuleInjector.inject(rule, permCode);
            finalRowRules.put(permCode, injected);
        }

        String combinedVersion = combinedVersionCalculator.calculate(user.getPermVersion(), roleVersions);

        List<RoleInfo> roleInfos = roles.stream()
                .map(role -> RoleInfo.builder()
                        .id(role.getId())
                        .roleName(role.getRoleName())
                        .priority(role.getPriority())
                        .build())
                .collect(Collectors.toList());

        UserFullPermissionDTO result = UserFullPermissionDTO.builder()
                .userId(userId)
                .username(user.getUsername())
                .permissions(allPermissions)
                .rowRules(finalRowRules)
                .roles(roleInfos)
                .combinedVersion(combinedVersion)
                .userPermVersion(user.getPermVersion())
                .roleVersions(roleVersions)
                .build();

        return userContextEnricher.enrich(result, user);
    }
}