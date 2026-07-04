package io.github.latcn.a2a.permission.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRowRuleReqDTO {

    private Long operatorId;

    private Long roleId;

    private Long permissionId;

    private String newRowRule;
}