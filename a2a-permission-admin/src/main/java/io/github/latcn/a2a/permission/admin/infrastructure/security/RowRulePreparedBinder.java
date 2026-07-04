package io.github.latcn.a2a.permission.admin.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RowRulePreparedBinder {

    private static final Pattern PARAM_PATTERN = Pattern.compile("\\?");
    private static final Pattern NAMED_PARAM_PATTERN = Pattern.compile(":(\\w+)");

    public BindingResult bind(String template, Map<String, Object> params) {
        if (template == null || template.isEmpty()) {
            return BindingResult.failure("Template cannot be empty");
        }

        try {
            String boundSql = template;
            Map<String, Object> boundParams = new HashMap<>();

            Matcher namedMatcher = NAMED_PARAM_PATTERN.matcher(template);
            StringBuffer sb = new StringBuffer();

            while (namedMatcher.find()) {
                String paramName = namedMatcher.group(1);
                Object value = params.get(paramName);

                if (value == null) {
                    return BindingResult.failure("Parameter '" + paramName + "' is missing");
                }

                boundParams.put(paramName, value);
                namedMatcher.appendReplacement(sb, "?");
            }
            namedMatcher.appendTail(sb);
            boundSql = sb.toString();

            return BindingResult.success(boundSql, boundParams);

        } catch (Exception e) {
            log.error("Failed to bind parameters", e);
            return BindingResult.failure("Binding failed: " + e.getMessage());
        }
    }

    public BindingResult bindPositional(String template, Object... params) {
        if (template == null || template.isEmpty()) {
            return BindingResult.failure("Template cannot be empty");
        }

        try {
            Matcher matcher = PARAM_PATTERN.matcher(template);
            StringBuffer sb = new StringBuffer();
            int paramIndex = 0;

            while (matcher.find()) {
                if (paramIndex >= params.length) {
                    return BindingResult.failure("Not enough parameters provided");
                }
                Object value = params[paramIndex];
                String replacement = value instanceof String ? "'" + escapeString((String) value) + "'" : String.valueOf(value);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                paramIndex++;
            }
            matcher.appendTail(sb);

            return BindingResult.success(sb.toString(), new HashMap<>());

        } catch (Exception e) {
            log.error("Failed to bind positional parameters", e);
            return BindingResult.failure("Binding failed: " + e.getMessage());
        }
    }

    private String escapeString(String value) {
        return value.replace("'", "''");
    }

    public String extractParamNames(String template) {
        if (template == null || template.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Matcher matcher = NAMED_PARAM_PATTERN.matcher(template);

        while (matcher.find()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(matcher.group(1));
        }

        return sb.toString();
    }

    public static class BindingResult {
        private final boolean success;
        private final String sql;
        private final Map<String, Object> params;
        private final String errorMessage;

        private BindingResult(boolean success, String sql, Map<String, Object> params, String errorMessage) {
            this.success = success;
            this.sql = sql;
            this.params = params;
            this.errorMessage = errorMessage;
        }

        public static BindingResult success(String sql, Map<String, Object> params) {
            return new BindingResult(true, sql, params, null);
        }

        public static BindingResult failure(String errorMessage) {
            return new BindingResult(false, null, null, errorMessage);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getSql() {
            return sql;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}