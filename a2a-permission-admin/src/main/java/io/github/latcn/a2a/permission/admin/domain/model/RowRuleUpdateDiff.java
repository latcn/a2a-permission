package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RowRuleUpdateDiff implements AuditDiff {
    private Long operatorId;
    private Long roleId;
    private String roleName;
    private Long permissionId;
    private String permissionCode;
    private String oldRowRule;
    private String newRowRule;
}