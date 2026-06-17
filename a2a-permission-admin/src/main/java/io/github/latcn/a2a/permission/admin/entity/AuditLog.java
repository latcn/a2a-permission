package io.github.latcn.a2a.permission.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "t_audit_log", autoResultMap = true)
public class AuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String traceId;

    private String operationType;

    private Long operatorId;

    private Long targetUserId;

    private Long targetRoleId;

    private Long targetPermissionId;

    private String targetClientId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object operationDetail;

    private String operationResult;

    private String clientIp;

    private String userAgent;

    private LocalDateTime createdAt;
}