package io.github.latcn.a2a.permission.api.dto;

import io.github.latcn.a2a.permission.api.enums.Effect;
import lombok.Data;
import java.util.Map;

@Data
public class PermissionInfo {

    private Long id;
    private String permissionCode;
    private Effect effect;
    private Map<String, String> rowRuleTemplate;
}
