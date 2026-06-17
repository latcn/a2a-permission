package io.github.latcn.a2a.permission.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class UserFullPermissionDTO {

    private Long userId;
    private String username;
    private Set<String> permissions;
    private Map<String, String> rowRules;
    private List<RoleInfo> roles;
    private String combinedVersion;
    private Long userPermVersion;
    private Map<Long, Long> roleVersions;

}
