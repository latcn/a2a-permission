package io.github.latcn.a2a.permission.admin.audit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ErrorDiff extends AuditDiff {

    private String errorCode;
    private String errorMessage;
    private String operation;

    public ErrorDiff(Long operatorId, String errorCode, String errorMessage, String operation) {
        super("ERROR", operatorId);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.operation = operation;
    }

    @Override
    public String getSummary() {
        return "Error during operation '" + operation + "': [" + errorCode + "] " + errorMessage;
    }

    @Override
    public String toDisplayString() {
        return "操作失败: " + operation + ", 错误码: " + errorCode + ", 错误信息: " + errorMessage;
    }
}