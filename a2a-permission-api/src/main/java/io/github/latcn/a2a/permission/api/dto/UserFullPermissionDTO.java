package io.github.latcn.a2a.permission.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFullPermissionDTO {

    private Long userId;
    private String username;
    private Set<String> permissions;
    private Map<String, String> rowRules;
    private List<RoleInfo> roles;
    private String combinedVersion;
    private Long userPermVersion;
    private Map<Long, Long> roleVersions;
    private List<String> departmentPath;

}
