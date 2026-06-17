package io.github.latcn.a2a.permission.api.dto;

import io.github.latcn.a2a.permission.api.enums.Effect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionInfo {

    private Long id;
    private String permissionCode;
    private Effect effect;
    private Map<String, String> rowRuleTemplate;
}
