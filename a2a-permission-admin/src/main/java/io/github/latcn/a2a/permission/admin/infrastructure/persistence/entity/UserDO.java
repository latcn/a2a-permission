package io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class UserDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String passwordHash;

    private LocalDateTime passwordExpiresAt;

    private Integer passwordFailedAttempts;

    private LocalDateTime passwordLockedUntil;

    private Long permVersion;

    private Integer status;

    private LocalDateTime lastLoginAt;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}