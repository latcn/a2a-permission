package io.github.latcn.a2a.permission.admin.engine;

import io.github.latcn.a2a.permission.admin.domain.entity.Permission;
import io.github.latcn.a2a.permission.admin.infra.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MandatoryRuleInjector {

    private final PermissionMapper permissionMapper;

    public String inject(String businessRule, String permCode) {
        if (businessRule == null || "1=0".equals(businessRule.trim())) {
            return "1=0";
        }

        Permission permission = permissionMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Permission>()
                        .eq(Permission::getPermissionCode, permCode)
        );

        if (permission == null || permission.getMandatoryRowRuleTemplate() == null
                || permission.getMandatoryRowRuleTemplate().isEmpty()
        //        || "1=1".equals(permission.getMandatoryRowRuleTemplate().trim())
        ) {
            return businessRule;
        }

        return "(" + businessRule + ") AND (" + permission.getMandatoryRowRuleTemplate() + ")";
    }
}