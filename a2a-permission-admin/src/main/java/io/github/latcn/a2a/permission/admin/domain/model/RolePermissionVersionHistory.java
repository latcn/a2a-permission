package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class RolePermissionVersionHistory {

    private Long id;

    private Long roleId;

    private Long oldVersion;

    private Long newVersion;

    private String triggerOperation;

    private Long auditLogId;

    private Set<String> affectedPerms;

    private LocalDateTime createdAt;
}