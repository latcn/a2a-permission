package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLog {

    private Long id;

    private String traceId;

    private String operationType;

    private Long operatorId;

    private Long targetUserId;

    private Long targetRoleId;

    private Long targetPermissionId;

    private String targetClientId;

    private Object operationDetail;

    private String operationResult;

    private String clientIp;

    private String userAgent;

    private LocalDateTime createdAt;
}