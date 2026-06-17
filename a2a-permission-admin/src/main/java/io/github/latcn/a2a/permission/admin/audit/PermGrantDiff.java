package io.github.latcn.a2a.permission.admin.audit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PermGrantDiff extends AuditDiff {

    private Long operatorId;
    private Long roleId;
    private String roleName;
    private Set<Long> permissionIds;
    private boolean grant;

    public PermGrantDiff(Long operatorId, Long roleId, String roleName, Set<Long> permissionIds, boolean grant) {
        super("PERM_GRANT", operatorId);
        this.roleId = roleId;
        this.roleName = roleName;
        this.permissionIds = permissionIds;
        this.grant = grant;
    }

    @Override
    public String getSummary() {
        String action = grant ? "Granted" : "Revoked";
        return action + " " + permissionIds.size() + " permissions to role '" + roleName + "' (ID: " + roleId + ")";
    }

    @Override
    public String toDisplayString() {
        String action = grant ? "授予" : "撤销";
        return action + "权限: 角色 " + roleName + ", 权限数: " + permissionIds.size();
    }
}