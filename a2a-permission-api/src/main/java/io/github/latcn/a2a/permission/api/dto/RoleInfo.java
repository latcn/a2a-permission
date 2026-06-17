package io.github.latcn.a2a.permission.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleInfo {
    private Long id;
    private String roleName;
    private Integer priority;
    private Long roleVersion;
    private List<PermissionInfo> permissions;
}
