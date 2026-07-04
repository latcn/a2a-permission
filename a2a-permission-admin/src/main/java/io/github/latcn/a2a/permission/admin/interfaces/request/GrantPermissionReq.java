package io.github.latcn.a2a.permission.admin.interfaces.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantPermissionReq {

    private Long roleId;

    private Set<Long> permissionIds;
}