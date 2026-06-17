package io.github.latcn.a2a.permission.admin.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class MandatoryRuleInjector {

    private static final String MANDATORY_ROLE = "system:mandatory";

    public void inject(Set<String> permissions, Map<String, String> rowRules) {
        injectSystemPermissions(permissions);
        injectSystemRowRules(rowRules);
    }

    private void injectSystemPermissions(Set<String> permissions) {
        permissions.add("system:health:read");
        permissions.add("system:status:read");
    }

    private void injectSystemRowRules(Map<String, String> rowRules) {
        rowRules.putIfAbsent("system:config", "1=1");
    }

    public boolean isMandatoryPermission(String permission) {
        return permission != null && permission.startsWith("system:");
    }
}