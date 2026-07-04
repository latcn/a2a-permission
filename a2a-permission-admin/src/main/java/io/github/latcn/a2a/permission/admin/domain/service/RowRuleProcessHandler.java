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
public class RowRuleProcessHandler extends PermissionCalculateHandler {

    private final RowRuleMerger rowRuleMerger;
    private final ExpressionOptimizer expressionOptimizer;
    private final MandatoryRuleInjector mandatoryRuleInjector;

    @Override
    public void handle(PermissionContext context) {
        try {
            log.debug("RowRuleProcessHandler starting for userId: {}", context.getUserId());

            if (context.getRoles().isEmpty()) {
                context.setRowRules(new HashMap<>());
                context.setOptimizedRowRules(new HashMap<>());
                context.setFinalRowRules(new HashMap<>());
                log.debug("No roles, rowRules is empty");
                return;
            }

            Map<Long, Permission> permMap = context.getPermissions().stream()
                    .collect(Collectors.toMap(Permission::getId, p -> p));

            Map<Long, List<RolePermission>> rolePermissionsMap = context.getRolePermissions().stream()
                    .collect(Collectors.groupingBy(RolePermission::getRoleId));

            Map<String, String> allRowRules = new HashMap<>();

            for (Role role : context.getRoles()) {
                List<RolePermission> rolePermissions = rolePermissionsMap.getOrDefault(role.getId(), Collections.emptyList());
                List<Permission> permissions = rolePermissions.stream()
                        .map(rp -> permMap.get(rp.getPermissionId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                Map<String, String> rowRules = rowRuleMerger.merge(role, rolePermissions, permissions);
                allRowRules.putAll(rowRules);
            }

            context.setRowRules(allRowRules);

            Map<String, String> optimizedRowRules = expressionOptimizer.optimize(allRowRules);
            context.setOptimizedRowRules(optimizedRowRules);

            Map<String, String> finalRowRules = new HashMap<>();
            for (Map.Entry<String, String> entry : optimizedRowRules.entrySet()) {
                String permCode = entry.getKey();
                String rule = entry.getValue();
                String injected = mandatoryRuleInjector.inject(rule, permCode);
                finalRowRules.put(permCode, injected);
            }

            context.setFinalRowRules(finalRowRules);

            log.debug("RowRuleProcessHandler completed for userId: {}, rowRules size: {}",
                    context.getUserId(), finalRowRules.size());

        } catch (Exception e) {
            log.error("RowRuleProcessHandler failed for userId: {}", context.getUserId(), e);
            context.setError(e);
        }
    }
}