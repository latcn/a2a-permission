package io.github.latcn.a2a.permission.admin.engine;

import io.github.latcn.a2a.permission.admin.entity.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class WildcardExpander {

    private static final Pattern WILDCARD_PATTERN = Pattern.compile("[*?]");

    public Set<String> expand(List<Permission> permissions) {
        Set<String> expanded = new HashSet<>();

        for (Permission perm : permissions) {
            String permKey = buildPermissionKey(perm);
            if (containsWildcard(permKey)) {
                expanded.addAll(matchWildcard(permKey, permissions));
            } else {
                expanded.add(permKey);
            }
        }

        return expanded;
    }

    private String buildPermissionKey(Permission perm) {
        StringBuilder sb = new StringBuilder();
       /* if (perm.getNamespace() != null) {
            sb.append(perm.getNamespace()).append(":");
        }*/
        sb.append(perm.getPermissionCode());
        return sb.toString();
    }

    private boolean containsWildcard(String pattern) {
        return WILDCARD_PATTERN.matcher(pattern).find();
    }

    private Set<String> matchWildcard(String pattern, List<Permission> allPermissions) {
        Set<String> matched = new HashSet<>();
        String regex = pattern.replace("*", ".*").replace("?", ".");

        for (Permission perm : allPermissions) {
            String permKey = buildPermissionKey(perm);
            if (permKey.matches(regex)) {
                matched.add(permKey);
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