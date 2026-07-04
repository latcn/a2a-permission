package io.github.latcn.a2a.permission.admin.interfaces.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRowRuleResp {

    private Boolean success;

    private Long roleId;

    private Long permissionId;

    private String message;
}