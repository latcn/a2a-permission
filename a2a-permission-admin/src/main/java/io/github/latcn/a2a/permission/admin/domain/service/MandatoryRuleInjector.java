package io.github.latcn.a2a.permission.admin.domain.service;

import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.PermissionDO;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper.PermissionMapper;
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

        PermissionDO permission = permissionMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PermissionDO>()
                        .eq(PermissionDO::getPermissionCode, permCode)
        );

        if (permission == null || permission.getMandatoryRowRuleTemplate() == null
                || permission.getMandatoryRowRuleTemplate().isEmpty()
        ) {
            return businessRule;
        }

        return "(" + businessRule + ") AND (" + permission.getMandatoryRowRuleTemplate() + ")";
    }
}