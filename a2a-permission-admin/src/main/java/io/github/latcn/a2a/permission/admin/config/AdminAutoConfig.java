package io.github.latcn.a2a.permission.admin.config;

import io.github.latcn.a2a.permission.admin.service.LocalPermissionQueryService;
import io.github.latcn.a2a.permission.api.service.PermissionQueryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminAutoConfig {

    @Bean
    @ConditionalOnMissingBean(PermissionQueryService.class)
    public PermissionQueryService permissionQueryService(LocalPermissionQueryService localService) {
        return localService;
    }
}