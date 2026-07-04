package io.github.latcn.a2a.permission.admin.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ExpressionOptimizer {

    private static final Pattern TRUE_PATTERN = Pattern.compile("^(1\\s*=\\s*1|true|TRUE)$");
    private static final Pattern FALSE_PATTERN = Pattern.compile("^(1\\s*=\\s*0|false|FALSE)$");

    public Map<String, String> optimize(Map<String, String> allRowRules){
        return null;
    }

    public String optimize(String expression) {
        if (expression == null || expression.isEmpty()) {
            return expression;
        }

        String trimmed = expression.trim();

        if (isAlwaysTrue(trimmed)) {
            return "1=1";
        }

        if (isAlwaysFalse(trimmed)) {
            return "1=0";
        }

        return simplifyAnd(expression);
    }

    private boolean isAlwaysTrue(String expr) {
        return TRUE_PATTERN.matcher(expr).matches();
    }

    private boolean isAlwaysFalse(String expr) {
        return FALSE_PATTERN.matcher(expr).matches();
    }

    private String simplifyAnd(String expression) {
        if (!expression.contains(" AND ") && !expression.contains(" AND")) {
            return expression;
        }

        String[] parts = expression.split("\\s+AND\\s+", -1);
        List<String> simplified = new ArrayList<>();

        for (String part : parts) {
            String trimmed = part.trim();
            if (!isAlwaysFalse(trimmed) && !trimmed.isEmpty()) {
                simplified.add(trimmed);
            }
        }

        if (simplified.isEmpty()) {
            return "1=0";
        }

        if (simplified.size() == 1) {
            return simplified.get(0);
        }

        return String.join(" AND ", simplified);
    }

    public boolean isTriviallyTrue(String expression) {
        return isAlwaysTrue(expression);
    }

    public boolean isTriviallyFalse(String expression) {
        return isAlwaysFalse(expression);
    }
}