package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Role {

    private Long id;

    private String roleName;

    private Integer priority;

    private Long roleVersion;

    private Integer status;

    private String description;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}