package io.github.latcn.a2a.permission.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@TableName(value = "t_permission_version_history", autoResultMap = true)
public class PermissionVersionHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long oldVersion;

    private Long newVersion;

    private String triggerOperation;

    private Long auditLogId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<String> affectedPermissions;

    private LocalDateTime createdAt;
}