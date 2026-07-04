package io.github.latcn.a2a.permission.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantRoleReqDTO {

    private Long operatorId;

    private Long roleId;

    private Set<Long> userIds;
}