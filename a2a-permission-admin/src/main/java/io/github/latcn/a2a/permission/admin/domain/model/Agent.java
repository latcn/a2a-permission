package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class Agent {

    private Long id;

    private String clientId;

    private String clientSecretHash;

    private LocalDateTime secretExpiresAt;

    private String agentName;

    private String frameworkType;

    private String agentCardUrl;

    private String publicKey;

    private Set<String> capabilities;

    private Integer status;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}