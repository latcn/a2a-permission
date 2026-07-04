package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Department {

    private Long id;

    private String deptName;

    private Long parentId;

    private String path;

    private Integer status;

    private String description;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}