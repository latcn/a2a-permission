package io.github.latcn.a2a.permission.admin.domain.service;

import io.github.latcn.a2a.permission.api.dto.UserFullPermissionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionCalculator {

    private final PermissionHandlerChain permissionHandlerChain;

    public UserFullPermissionDTO calculateFullPermissions(Long userId) {
        log.debug("PermissionCalculator starting for userId: {}", userId);

        PermissionContext context = new PermissionContext(userId);
        UserFullPermissionDTO result = permissionHandlerChain.execute(context);

        if (result == null) {
            log.warn("Permission calculation returned null for userId: {}", userId);
            return UserFullPermissionDTO.builder()
                    .userId(userId)
                    .permissions(Collections.emptySet())
                    .rowRules(Collections.emptyMap())
                    .roles(Collections.emptyList())
                    .build();
        }

        log.debug("PermissionCalculator completed for userId: {}", userId);
        return result;
    }
}