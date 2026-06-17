package io.github.latcn.a2a.permission.admin.audit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RowRuleUpdateDiff extends AuditDiff {

    private Long operatorId;
    private Long roleId;
    private String roleName;
    private Long permissionId;
    private String permissionName;
    private String oldRowRule;
    private String newRowRule;

    public RowRuleUpdateDiff(Long operatorId, Long roleId, String roleName, Long permissionId,
                              String permissionName, String oldRowRule, String newRowRule) {
        super("ROW_RULE_UPDATE", operatorId);
        this.roleId = roleId;
        this.roleName = roleName;
        this.permissionId = permissionId;
        this.permissionName = permissionName;
        this.oldRowRule = oldRowRule;
        this.newRowRule = newRowRule;
    }

    @Override
    public String getSummary() {
        return "Updated row rule for permission '" + permissionName + "' in role '" + roleName + "'";
    }

    @Override
    public String toDisplayString() {
        return "更新行级规则: 角色=" + roleName + ", 权限=" + permissionName + ", 旧规则=" + oldRowRule + ", 新规则=" + newRowRule;
    }
}