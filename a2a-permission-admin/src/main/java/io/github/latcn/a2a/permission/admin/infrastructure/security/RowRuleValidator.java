package io.github.latcn.a2a.permission.admin.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RowRuleValidator {

    private static final Set<String> DANGEROUS_FUNCTIONS = new HashSet<>();
    private static final Pattern SYSTEM_VARIABLE_PATTERN = Pattern.compile("@@");
    private static final Pattern SQL_COMMENT_PATTERN = Pattern.compile("(--|\\/\\*|\\*\\/|;--)");
    private static final Set<String> FORBIDDEN_KEYWORDS = new HashSet<>();

    @Value("${permission.security.whitelist.tables:}")
    private Set<String> whitelistTables = new HashSet<>();

    static {
        DANGEROUS_FUNCTIONS.add("SLEEP");
        DANGEROUS_FUNCTIONS.add("BENCHMARK");
        DANGEROUS_FUNCTIONS.add("LOAD_FILE");
        DANGEROUS_FUNCTIONS.add("INTO OUTFILE");
        DANGEROUS_FUNCTIONS.add("INTO DUMPFILE");
        DANGEROUS_FUNCTIONS.add("LOAD DATA");
        DANGEROUS_FUNCTIONS.add("HEX");
        DANGEROUS_FUNCTIONS.add("UNHEX");
        DANGEROUS_FUNCTIONS.add("OUTFILE");
        DANGEROUS_FUNCTIONS.add("DUMPFILE");

        FORBIDDEN_KEYWORDS.add("EXEC");
        FORBIDDEN_KEYWORDS.add("EXECUTE");
        FORBIDDEN_KEYWORDS.add("XP_");
        FORBIDDEN_KEYWORDS.add("SP_");
        FORBIDDEN_KEYWORDS.add("INFORMATION_SCHEMA");
        FORBIDDEN_KEYWORDS.add("PERFORMANCE_SCHEMA");
        FORBIDDEN_KEYWORDS.add("MYSQL");
    }

    public ValidationResult validate(String rowRule) {
        if (rowRule == null || rowRule.trim().isEmpty()) {
            return ValidationResult.failure("Row rule cannot be empty");
        }

        if (!validateNoDangerousFunctions(rowRule)) {
            return ValidationResult.failure("Row rule contains dangerous function");
        }

        if (!validateNoSystemVariables(rowRule)) {
            return ValidationResult.failure("Row rule contains system variables");
        }

        if (!validateNoSqlComments(rowRule)) {
            return ValidationResult.failure("Row rule contains SQL comments");
        }

        if (!validateNoForbiddenKeywords(rowRule)) {
            return ValidationResult.failure("Row rule contains forbidden keywords");
        }

        if (!validateNoForgottenQuotes(rowRule)) {
            return ValidationResult.failure("Row rule has unbalanced quotes");
        }

        if (!validateSqlSyntax(rowRule)) {
            return ValidationResult.failure("Row rule has invalid SQL syntax");
        }

        if (!validateNoSubquery(rowRule)) {
            return ValidationResult.failure("Subqueries are not allowed in row rules");
        }

        return ValidationResult.success();
    }

    private boolean validateNoDangerousFunctions(String rule) {
        String upperRule = rule.toUpperCase();
        for (String func : DANGEROUS_FUNCTIONS) {
            if (upperRule.contains(func)) {
                log.warn("Dangerous function detected: {}", func);
                return false;
            }
        }
        return true;
    }

    private boolean validateNoSystemVariables(String rule) {
        if (SYSTEM_VARIABLE_PATTERN.matcher(rule).find()) {
            log.warn("System variable detected in rule: {}", rule);
            return false;
        }
        return true;
    }

    private boolean validateNoSqlComments(String rule) {
        if (SQL_COMMENT_PATTERN.matcher(rule).find()) {
            log.warn("SQL comment detected in rule: {}", rule);
            return false;
        }
        return true;
    }

    private boolean validateNoForbiddenKeywords(String rule) {
        String upperRule = rule.toUpperCase();
        for (String keyword : FORBIDDEN_KEYWORDS) {
            if (upperRule.contains(keyword)) {
                log.warn("Forbidden keyword detected: {}", keyword);
                return false;
            }
        }
        return true;
    }

    private boolean validateNoForgottenQuotes(String rule) {
        int singleQuotes = 0;
        for (char c : rule.toCharArray()) {
            if (c == '\'') {
                singleQuotes++;
            }
        }
        return singleQuotes % 2 == 0;
    }

    private boolean validateSqlSyntax(String rule) {
        try {
            Expression expr = CCJSqlParserUtil.parseCondExpression(rule);
            return expr != null;
        } catch (JSQLParserException e) {
            log.warn("SQL syntax validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean validateNoSubquery(String rule) {
        try {
            Select select = (Select) CCJSqlParserUtil.parse("SELECT * FROM t WHERE " + rule);
            if (select.getSelectBody() instanceof PlainSelect) {
                PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
                return plainSelect.getWhere() != null;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean validateWhitelistTable(String tableName) {
        return whitelistTables == null || whitelistTables.isEmpty() || whitelistTables.contains(tableName);
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}