package io.github.latcn.a2a.permission.admin.application.query;

import io.github.latcn.a2a.permission.admin.domain.model.A2AAcl;
import io.github.latcn.a2a.permission.admin.domain.model.Agent;
import io.github.latcn.a2a.permission.admin.domain.repository.A2AAclRepository;
import io.github.latcn.a2a.permission.admin.domain.repository.AgentRepository;
import io.github.latcn.a2a.permission.admin.domain.service.PermissionCalculator;
import io.github.latcn.a2a.permission.api.dto.*;
import io.github.latcn.a2a.permission.api.service.PermissionQueryService;
import io.github.latcn.cache.spring.annotation.HccCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalPermissionQueryService implements PermissionQueryService {

    private final AgentRepository agentRepository;
    private final A2AAclRepository a2AAclRepository;
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
    public UserFullPermissionDTO getUserFullPermissions(Long userId) {
        return permissionCalculator.calculateFullPermissions(userId);
    }

    @HccCacheable(key="#clientId", ttl = 60)
    @Override
    public AgentDTO getAgent(String clientId) {
        log.debug("Fetching agent for clientId: {}", clientId);
        Optional<Agent> agentOpt = agentRepository.findByClientId(clientId);
        if (agentOpt.isEmpty()) {
            log.warn("Agent not found for clientId: {}", clientId);
            return null;
        }
        Agent agent = agentOpt.get();
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

    @HccCacheable(key = "'agent-acl:' + #sourceClientId + '-' + #targetClientId", ttl = 60)
    @Override
    public AclCheckResultDTO checkAcl(String sourceClientId, String targetClientId) {
        log.debug("Checking ACL from {} to {}", sourceClientId, targetClientId);
        Optional<A2AAcl> aclOpt = a2AAclRepository.findBySourceAndTarget(sourceClientId, targetClientId);
        if (aclOpt.isEmpty()) {
            log.info("No ACL found for {} -> {}, denying by default", sourceClientId, targetClientId);
            return AclCheckResultDTO.builder()
                    .allowed(false)
                    .sourceClientId(sourceClientId)
                    .targetClientId(targetClientId)
                    .reason("No ACL entry found")
                    .build();
        }
        A2AAcl acl = aclOpt.get();
        return AclCheckResultDTO.builder()
                .allowed(true)
                .sourceClientId(sourceClientId)
                .targetClientId(targetClientId)
                .allowedScopes(acl.getAllowedScopes())
                .reason("ACL match found")
                .build();
    }
}