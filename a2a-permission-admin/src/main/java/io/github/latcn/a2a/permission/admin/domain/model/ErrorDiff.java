package io.github.latcn.a2a.permission.admin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDiff implements AuditDiff {
    private Long operatorId;
    private String errorMessage;
    private String targetType;
    private Long targetId;
}