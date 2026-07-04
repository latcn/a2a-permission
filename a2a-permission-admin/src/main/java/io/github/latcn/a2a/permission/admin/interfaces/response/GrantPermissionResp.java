package io.github.latcn.a2a.permission.admin.interfaces.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantPermissionResp {

    private Boolean success;

    private Long roleId;

    private Integer permissionCount;

    private String message;
}