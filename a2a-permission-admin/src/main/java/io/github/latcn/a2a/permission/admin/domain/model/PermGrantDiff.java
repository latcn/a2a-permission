package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class PermGrantDiff implements AuditDiff {
    private Long operatorId;
    private Long roleId;
    private String roleName;
    private Set<Long> permissionIds;
    private boolean grant;
}