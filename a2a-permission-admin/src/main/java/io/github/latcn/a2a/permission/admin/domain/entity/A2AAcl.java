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
@TableName(value = "t_a2a_acl", autoResultMap = true)
public class A2AAcl {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sourceClientId;

    private String targetClientId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<String> allowedScopes;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}