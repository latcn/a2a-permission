package io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "t_permission", autoResultMap = true)
public class PermissionDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String permissionCode;

    private String actionCode;

    private Integer riskLevel;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> mandatoryRowRuleTemplate;

    private String description;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}