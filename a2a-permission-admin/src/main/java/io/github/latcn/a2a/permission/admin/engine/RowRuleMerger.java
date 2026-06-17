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
                String table = entry.getKey();
                String rowRule = entry.getValue();
                String key = resourceType + ":" + table;

                rulesByResource.computeIfAbsent(key, k -> new ArrayList<>())
                        .add(new RowRuleEntry(role.getPriority(), rp.getEffect(), rowRule, resourceType, table));
            }
        }

        Map<String, String> mergedRules = new HashMap<>();

        for (Map.Entry<String, List<RowRuleEntry>> entry : rulesByResource.entrySet()) {
            List<RowRuleEntry> rules = entry.getValue();

            rules.sort(Comparator.comparingInt((RowRuleEntry r) -> r.priority).reversed());

            List<String> allowRules = new ArrayList<>();
            List<String> denyRules = new ArrayList<>();

            for (RowRuleEntry rule : rules) {
                if (rule.effect == Effect.DENY.getCode()) {
                    denyRules.add(rule.rowRule);
                } else {
                    allowRules.add(rule.rowRule);
                }
            }

            String allowPart = allowRules.stream()
                    .map(r -> "(" + r + ")")
                    .collect(Collectors.joining(" OR "));

            String denyPart = denyRules.stream()
                    .map(r -> "(" + r + ")")
                    .collect(Collectors.joining(" OR "));

            String merged;
            if (!allowRules.isEmpty() && !denyRules.isEmpty()) {
                merged = "(" + allowPart + ") AND NOT (" + denyPart + ")";
            } else if (!allowRules.isEmpty()) {
                merged = allowPart;
            } else if (!denyRules.isEmpty()) {
                merged = "NOT (" + denyPart + ")";
            } else {
                merged = "";
            }

            mergedRules.put(entry.getKey(), merged);
        }

        return mergedRules;
    }

    private static class RowRuleEntry {
        final int priority;
        final int effect;
        final String rowRule;
        final String resourceType;
        final String table;

        RowRuleEntry(int priority, int effect, String rowRule, String resourceType, String table) {
            this.priority = priority;
            this.effect = effect;
            this.rowRule = rowRule;
            this.resourceType = resourceType;
            this.table = table;
        }
    }
}