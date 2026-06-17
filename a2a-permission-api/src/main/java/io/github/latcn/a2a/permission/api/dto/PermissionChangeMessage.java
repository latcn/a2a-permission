package io.github.latcn.a2a.permission.api.dto;

import io.github.latcn.a2a.permission.api.enums.ChangeType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionChangeMessage {

    private ChangeType type;
    private Long userId;
    private Long roleId;
    private Long newVersion;
    private Long timestamp;
}