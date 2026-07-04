package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class A2AAcl {

    private Long id;

    private String sourceClientId;

    private String targetClientId;

    private Set<String> allowedScopes;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}