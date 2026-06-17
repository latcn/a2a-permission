package io.github.latcn.a2a.permission.api.dto;

import io.github.latcn.a2a.permission.api.enums.OperationResult;
import io.github.latcn.a2a.permission.api.enums.OperationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditLogDTO {

    private String traceId;
    private OperationType operationType;
    private Long operatorId;
    private Long targetUserId;
    private Long targetRoleId;
    private Long targetPermissionId;
    private String targetClientId;
    private OperationResult operationResult;
    private String operationDetail;
    private String clientIp;
    private String userAgent;
}
