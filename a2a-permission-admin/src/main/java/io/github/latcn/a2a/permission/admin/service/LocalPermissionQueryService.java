package io.github.latcn.a2a.permission.admin.service;

import io.github.latcn.a2a.permission.admin.engine.PermissionCalculator;
import io.github.latcn.a2a.permission.admin.entity.User;
import io.github.latcn.a2a.permission.admin.mapper.UserMapper;
import io.github.latcn.a2a.permission.api.dto.*;
import io.github.latcn.a2a.permission.api.service.PermissionQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalPermissionQueryService implements PermissionQueryService {

    private final UserMapper userMapper;
    private final PermissionCalculator permissionCalculator;

    @Override
    public TokenExchangePrepareResponse prepareTokenExchange(TokenExchangePrepareRequest request) {
        log.debug("Preparing token exchange for request: {}", request);
        return TokenExchangePrepareResponse.builder()
                .build();
    }

    @Override
    @Cacheable(value = "userPermissions", key = "#userId", unless = "#result == null")
    public UserFullPermissionDTO getUserFullPermissions(Long userId) {
        log.debug("Fetching full permissions for user: {}", userId);
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("User not found: {}", userId);
            return null;
        }
        return permissionCalculator.calculateFullPermissions(userId);
    }

    @Override
    public AgentDTO getAgent(String clientId) {
        log.debug("Fetching agent for clientId: {}", clientId);
        return null;
    }

    @Override
    public AclCheckResult checkAcl(String sourceClientId, String targetClientId) {
        log.debug("Checking ACL from {} to {}", sourceClientId, targetClientId);
        return AclCheckResult.builder()
                .allowed(false)
                .reason("Not implemented")
                .build();
    }
}