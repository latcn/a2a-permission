package io.github.latcn.a2a.permission.admin.audit;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RoleGrantDiff.class, name = "ROLE_GRANT"),
        @JsonSubTypes.Type(value = PermGrantDiff.class, name = "PERM_GRANT"),
        @JsonSubTypes.Type(value = RowRuleUpdateDiff.class, name = "ROW_RULE_UPDATE"),
        @JsonSubTypes.Type(value = ErrorDiff.class, name = "ERROR")
})
public abstract class AuditDiff {

    private String type;
    private Long operatorId;
    private Long targetUserId;
    private LocalDateTime timestamp;
    private String traceId;

    public AuditDiff(String type, Long operatorId) {
        this.type = type;
        this.operatorId = operatorId;
    }

    public abstract String getSummary();

    public abstract String toDisplayString();
}