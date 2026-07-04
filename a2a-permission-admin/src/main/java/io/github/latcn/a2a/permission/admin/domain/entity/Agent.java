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
@TableName(value = "t_agent", autoResultMap = true)
public class Agent {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String clientId;

    private String clientSecretHash;

    private LocalDateTime secretExpiresAt;

    private String agentName;

    private String frameworkType;

    private String agentCardUrl;

    private String publicKey;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<String> capabilities;

    private Integer status;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}