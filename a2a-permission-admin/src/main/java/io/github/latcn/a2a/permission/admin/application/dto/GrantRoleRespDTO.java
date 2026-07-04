package io.github.latcn.a2a.permission.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantRoleRespDTO {

    private Boolean success;

    private Long roleId;

    private Integer userCount;

    private String message;
}