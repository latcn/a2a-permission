package io.github.latcn.a2a.permission.admin.application.dto;

import io.github.latcn.a2a.permission.api.enums.OperationResult;
import io.github.latcn.a2a.permission.api.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
