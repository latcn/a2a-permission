package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class RolePermission {

    private Long id;

    private Long roleId;

    private Long permissionId;

    private Integer effect;

    private Map<String, String> rowRuleTemplate;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}