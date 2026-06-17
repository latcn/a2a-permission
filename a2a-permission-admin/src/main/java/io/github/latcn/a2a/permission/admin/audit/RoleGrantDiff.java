package io.github.latcn.a2a.permission.admin.audit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RoleGrantDiff extends AuditDiff {

    private Long operatorId;
    private Long roleId;
    private String roleName;
    private Set<Long> targetUserIds;
    private boolean grant;

    public RoleGrantDiff(Long operatorId, Long roleId, String roleName, Set<Long> targetUserIds, boolean grant) {
        super("ROLE_GRANT", operatorId);
        this.roleId = roleId;
        this.roleName = roleName;
        this.targetUserIds = targetUserIds;
        this.grant = grant;
    }

    @Override
    public String getSummary() {
        String action = grant ? "Granted" : "Revoked";
        return action + " role '" + roleName + "' (ID: " + roleId + ") to " + targetUserIds.size() + " users";
    }

    @Override
    public String toDisplayString() {
        String action = grant ? "授予" : "撤销";
        return action + "角色: " + roleName + ", 用户数: " + targetUserIds.size();
    }
}