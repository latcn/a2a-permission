package io.github.latcn.a2a.permission.admin.engine;

import io.github.latcn.a2a.permission.admin.entity.Role;
import io.github.latcn.a2a.permission.admin.entity.Permission;
import io.github.latcn.a2a.permission.admin.entity.RolePermission;
import io.github.latcn.a2a.permission.api.enums.Effect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RowRuleMerger {

    public Map<String, String> merge(Role role, List<RolePermission> rolePermissions, List<Permission> permissions) {
        Map<Long, Permission> permMap = permissions.stream()
                .collect(Collectors.toMap(Permission::getId, p -> p));

        Map<String, List<RowRuleEntry>> rulesByResource = new HashMap<>();

        for (RolePermission rp : rolePermissions) {
            Permission perm = permMap.get(rp.getPermissionId());
            if (perm == null || rp.getRowRuleTemplate() == null || rp.getRowRuleTemplate().isEmpty()) {
                continue;
            }

            String resourceType = perm.getPermissionCode();
            Map<String, String> rowRuleMap = rp.getRowRuleTemplate();

            for (Map.Entry<String, String> entry : rowRuleMap.entrySet()) {
                String key = entry.getKey();
                String rowRule = entry.getValue();

                rulesByResource.computeIfAbsent(key, k -> new ArrayList<>())
                        .add(new RowRuleEntry(role.getPriority(), rp.getEffect(), rowRule));
            }
        }

        Map<String, String> mergedRules = new HashMap<>();

        for (Map.Entry<String, List<RowRuleEntry>> entry : rulesByResource.entrySet()) {
            String resourceType = entry.getKey();
            List<RowRuleEntry> rules = entry.getValue();

            rules.sort(Comparator.comparingInt((RowRuleEntry r) -> r.priority).reversed());

            StringBuilder merged = new StringBuilder();
            boolean hasDeny = false;
            boolean hasAllow = false;

            for (RowRuleEntry rule : rules) {
                if (rule.effect == Effect.DENY.getCode()) {
                    hasDeny = true;
                } else {
                    hasAllow = true;
                }
            }

            if (hasDeny) {
                for (RowRuleEntry rule : rules) {
                    if (rule.effect == Effect.DENY.getCode()) {
                        if (merged.length() > 0) {
                            merged.append(" AND ");
                        }
                        merged.append("(").append(rule.rowRule).append(")");
                    }
                }
            }

            if (hasAllow) {
                for (RowRuleEntry rule : rules) {
                    if (rule.effect == Effect.ALLOW.getCode()) {
                        if (merged.length() > 0) {
                            merged.append(" AND ");
                        }
                        merged.append("(").append(rule.rowRule).append(")");
                    }
                }
            }

            mergedRules.put(resourceType, merged.toString());
        }

        return mergedRules;
    }

    private static class RowRuleEntry {
        final int priority;
        final int effect;
        final String rowRule;

        RowRuleEntry(int priority, int effect, String rowRule) {
            this.priority = priority;
            this.effect = effect;
            this.rowRule = rowRule;
        }
    }
}