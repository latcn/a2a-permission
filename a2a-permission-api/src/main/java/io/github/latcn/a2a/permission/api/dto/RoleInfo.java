package io.github.latcn.a2a.permission.api.dto;


import lombok.Data;

import java.util.List;

@Data
public class RoleInfo {
    private Long id;
    private String roleName;
    private Integer priority;
    private Long roleVersion;
    private List<PermissionInfo> permissions;
}
