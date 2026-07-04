package io.github.latcn.a2a.permission.admin.engine;

import io.github.latcn.a2a.permission.admin.domain.entity.Permission;
import io.github.latcn.a2a.permission.admin.infra.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class WildcardExpander {

    private static final Pattern WILDCARD_PATTERN = Pattern.compile("[*?]");

    private final PermissionMapper permissionMapper;

    public Set<String> expand(List<Permission> permissions) {
        Set<String> allDefinedPerms = new HashSet<>(permissionMapper.selectAllPermissionCodes());
        Set<String> expanded = new HashSet<>();

        for (Permission perm : permissions) {
            String permKey = buildPermissionKey(perm);
            if (containsWildcard(permKey)) {
                expanded.addAll(matchWildcard(permKey, allDefinedPerms));
            } else {
                if (allDefinedPerms.contains(permKey)) {
                    expanded.add(permKey);
                }
            }
        }

        return expanded;
    }

    private String buildPermissionKey(Permission perm) {
        return perm.getPermissionCode();
    }

    private boolean containsWildcard(String pattern) {
        return WILDCARD_PATTERN.matcher(pattern).find();
    }

    private Set<String> matchWildcard(String pattern, Set<String> allPermissions) {
        Set<String> matched = new HashSet<>();
        String regex = pattern.replace("*", ".*").replace("?", ".");

        for (String perm : allPermissions) {
            if (perm.matches(regex)) {
                matched.add(perm);
            }
        }

        return matched;
    }

    public Set<String> expandSingle(String pattern) {
        Set<String> result = new HashSet<>();
        result.add(pattern);
        return result;
    }

    public boolean validateWildcard(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return false;
        }
        String regex = pattern.replace("*", ".*").replace("?", ".");
        try {
            Pattern.compile(regex);
            return true;
        } catch (Exception e) {
            log.warn("Invalid wildcard pattern: {}", pattern, e);
            return false;
        }
    }
}