package io.github.latcn.a2a.permission.remote.fallback;

import io.github.latcn.a2a.permission.api.dto.AclCheckResultDTO;
import io.github.latcn.a2a.permission.api.dto.AgentDTO;
import io.github.latcn.a2a.permission.api.dto.TokenExchangePrepareRequest;
import io.github.latcn.a2a.permission.api.dto.TokenExchangePrepareResponse;
import io.github.latcn.a2a.permission.api.dto.UserFullPermissionDTO;
import io.github.latcn.a2a.permission.common.cache.RedisCacheManager;
import io.github.latcn.a2a.permission.remote.client.RemotePermissionQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PermissionQueryFallbackFactory implements org.springframework.cloud.openfeign.FallbackFactory<RemotePermissionQueryService> {

    private final RedisCacheManager redisCacheManager;

    public PermissionQueryFallbackFactory(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public RemotePermissionQueryService create(Throwable cause) {
        log.error("Feign client fallback triggered by: {}", cause.getClass().getSimpleName(), cause);
        return new PermissionQueryFallback(redisCacheManager) {
            @Override
            public TokenExchangePrepareResponse prepareTokenExchange(TokenExchangePrepareRequest request) {
                log.warn("Fallback triggered for prepareTokenExchange: {} - Error: {}",
                        request, cause.getMessage());
                return null;
            }

            @Override
            public UserFullPermissionDTO getUserFullPermissions(Long userId) {
                log.warn("Fallback triggered for getUserFullPermissions: userId={} - Error: {}",
                        userId, cause.getMessage());
                return tryGetFromCache(userId);
            }

            @Override
            public AgentDTO getAgent(String clientId) {
                log.warn("Fallback triggered for getAgent: clientId={} - Error: {}",
                        clientId, cause.getMessage());
                return tryGetAgentFromCache(clientId);
            }

            @Override
            public AclCheckResultDTO checkAcl(String sourceClientId, String targetClientId) {
                log.warn("Fallback triggered for checkAcl: source={}, target={} - Error: {}",
                        sourceClientId, targetClientId, cause.getMessage());
                return tryGetAclFromCache(sourceClientId, targetClientId);
            }

            private UserFullPermissionDTO tryGetFromCache(Long userId) {
                try {
                    return getUserFullPermissions(userId);
                } catch (Exception e) {
                    log.error("Failed to get from cache for userId: {}", userId, e);
                    return null;
                }
            }

            private AgentDTO tryGetAgentFromCache(String clientId) {
                try {
                    return getAgent(clientId);
                } catch (Exception e) {
                    log.error("Failed to get agent from cache for clientId: {}", clientId, e);
                    return null;
                }
            }

            private AclCheckResultDTO tryGetAclFromCache(String sourceClientId, String targetClientId) {
                try {
                    return getAclCheckResult(sourceClientId, targetClientId);
                } catch (Exception e) {
                    log.error("Failed to get ACL from cache", e);
                    return AclCheckResultDTO.builder()
                            .allowed(false)
                            .sourceClientId(sourceClientId)
                            .targetClientId(targetClientId)
                            .reason("Service unavailable: " + cause.getClass().getSimpleName())
                            .build();
                }
            }

            private AclCheckResultDTO getAclCheckResult(String sourceClientId, String targetClientId) {
                return null;
            }
        };
    }
}