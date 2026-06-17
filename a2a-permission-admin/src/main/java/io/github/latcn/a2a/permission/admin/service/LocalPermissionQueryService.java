package io.github.latcn.a2a.permission.admin.service;

import io.github.latcn.a2a.permission.admin.engine.PermissionCalculator;
import io.github.latcn.a2a.permission.admin.entity.A2AAcl;
import io.github.latcn.a2a.permission.admin.entity.Agent;
import io.github.latcn.a2a.permission.admin.entity.User;
import io.github.latcn.a2a.permission.admin.mapper.A2AAclMapper;
import io.github.latcn.a2a.permission.admin.mapper.AgentMapper;
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
    private final AgentMapper agentMapper;
    private final A2AAclMapper a2AAclMapper;
    private final PermissionCalculator permissionCalculator;

    @Override
    public TokenExchangePrepareResponse prepareTokenExchange(TokenExchangePrepareRequest request) {
        log.debug("Preparing token exchange for request: {}", request);

        UserFullPermissionDTO fullPerm = permissionCalculator.calculateFullPermissions(request.getUserId());
        if (fullPerm == null) {
            log.warn("User not found or has no permissions: userId={}", request.getUserId());
            return TokenExchangePrepareResponse.builder()
                    .userId(request.getUserId())
                    .build();
        }

        return TokenExchangePrepareResponse.builder()
                .userId(fullPerm.getUserId())
                .username(fullPerm.getUsername())
                .combinedVersion(fullPerm.getCombinedVersion())
                .permissions(fullPerm.getPermissions())
                .rowRules(fullPerm.getRowRules())
                .roles(fullPerm.getRoles())
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
        Agent agent = agentMapper.selectByClientId(clientId);
        if (agent == null) {
            log.warn("Agent not found for clientId: {}", clientId);
            return null;
        }
        return AgentDTO.builder()
                .id(agent.getId())
                .clientId(agent.getClientId())
                .agentName(agent.getAgentName())
                .frameworkType(agent.getFrameworkType())
                .agentCardUrl(agent.getAgentCardUrl())
                .publicKey(agent.getPublicKey())
                .capabilities(agent.getCapabilities())
                .build();
    }

    @Override
    public AclCheckResult checkAcl(String sourceClientId, String targetClientId) {
        log.debug("Checking ACL from {} to {}", sourceClientId, targetClientId);
        A2AAcl acl = a2AAclMapper.selectBySourceAndTarget(sourceClientId, targetClientId);
        if (acl == null) {
            log.info("No ACL found for {} -> {}, denying by default", sourceClientId, targetClientId);
            return AclCheckResult.builder()
                    .allowed(false)
                    .sourceClientId(sourceClientId)
                    .targetClientId(targetClientId)
                    .reason("No ACL entry found")
                    .build();
        }
        return AclCheckResult.builder()
                .allowed(true)
                .sourceClientId(sourceClientId)
                .targetClientId(targetClientId)
                .allowedScopes(acl.getAllowedScopes())
                .reason("ACL match found")
                .build();
    }
}