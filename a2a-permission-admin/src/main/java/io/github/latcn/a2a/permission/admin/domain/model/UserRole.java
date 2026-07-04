package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRole {

    private Long id;

    private Long userId;

    private Long roleId;

    private Long createdBy;

    private LocalDateTime createdAt;
}