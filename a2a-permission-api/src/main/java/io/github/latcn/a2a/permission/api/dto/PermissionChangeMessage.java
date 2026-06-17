package io.github.latcn.a2a.permission.api.dto;

import io.github.latcn.a2a.permission.api.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionChangeMessage {

    private ChangeType type;
    private Set<Long> userIds;
    private Long roleId;
    private Long newVersion;
    private Long timestamp;
}