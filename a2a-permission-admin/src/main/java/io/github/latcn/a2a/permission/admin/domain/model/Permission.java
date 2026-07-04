package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Permission {

    private Long id;

    private String permissionCode;

    private String actionCode;

    private Integer riskLevel;

    private Map<String, String> mandatoryRowRuleTemplate;

    private String description;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}