package io.github.latcn.a2a.permission.admin.domain.service;

import io.github.latcn.a2a.permission.admin.domain.model.*;
import io.github.latcn.a2a.permission.api.dto.UserFullPermissionDTO;
import lombok.Data;

import java.util.*;

@Data
public class PermissionContext {

    private Long userId;
    private User user;
    private List<UserRole> userRoles;
    private List<Role> roles;
    private List<RolePermission> rolePermissions;
    private List<Permission> permissions;

    private Set<String> expandedPermissions;

    private Map<String, String> rowRules;
    private Map<String, String> optimizedRowRules;
    private Map<String, String> finalRowRules;

    private Long userPermVersion;
    private Map<Long, Long> roleVersions;
    private String combinedVersion;

    private UserFullPermissionDTO result;
    private Throwable error;

    public PermissionContext(Long userId) {
        this.userId = userId;
        this.userRoles = new ArrayList<>();
        this.roles = new ArrayList<>();
        this.rolePermissions = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.expandedPermissions = new HashSet<>();
        this.rowRules = new HashMap<>();
        this.optimizedRowRules = new HashMap<>();
        this.finalRowRules = new HashMap<>();
        this.roleVersions = new HashMap<>();
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }
}