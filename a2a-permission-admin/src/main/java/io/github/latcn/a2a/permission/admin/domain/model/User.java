package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;

    private String username;

    private String passwordHash;

    private LocalDateTime passwordExpiresAt;

    private Integer passwordFailedAttempts;

    private LocalDateTime passwordLockedUntil;

    private Long permVersion;

    private Integer status;

    private LocalDateTime lastLoginAt;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}