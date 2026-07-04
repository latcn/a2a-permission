package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PermissionVersionHistory {

    private Long id;

    private Long userId;

    private Long oldVersion;

    private Long newVersion;

    private String triggerOperation;

    private Long auditLogId;

    private Set<String> affectedPermissions;

    private LocalDateTime createdAt;
}