package io.github.latcn.a2a.permission.remote.fallback;

import io.github.latcn.a2a.permission.api.dto.*;
import io.github.latcn.a2a.permission.common.cache.CacheConstants;
import io.github.latcn.a2a.permission.common.cache.RedisCacheManager;
import io.github.latcn.a2a.permission.remote.client.RemotePermissionQueryService;
import io.github.latcn.a2a.permission.remote.enums.RemoteErrorEnum;
import io.github.latcn.archbase.foundation.result.Result;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PermissionQueryFallback implements RemotePermissionQueryService {

    private final RedisCacheManager redisCacheManager;

    public PermissionQueryFallback(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public Result<TokenExchangePrepareResponse> prepareTokenExchange(TokenExchangePrepareRequest request) {
        log.warn("Fallback: prepareTokenExchange called for userId: {}, clientId: {}", 
                request.getUserId(), request.getClientId());
        return Result.fail(RemoteErrorEnum.FALL_BACK);
    }

    @Override
    public Result<UserFullPermissionDTO> getUserFullPermissions(Long userId) {
        log.warn("Fallback: getUserFullPermissions called for userId: {}, attempting to retrieve from Redis cache", userId);
        
        try {
            String redisKey = CacheConstants.buildUserPermissionKey(userId);
            UserFullPermissionDTO cached = redisCacheManager.get(redisKey, UserFullPermissionDTO.class);
            
            if (cached != null) {
                log.info("Fallback: Successfully retrieved user permissions from Redis cache for userId: {}", userId);
                return Result.success(cached);
            }
            
            log.warn("Fallback: No cached user permissions found in Redis for userId: {}", userId);
        } catch (Exception e) {
            log.error("Fallback: Failed to retrieve user permissions from Redis for userId: {}", userId, e);
        }
        
        return Result.fail(RemoteErrorEnum.FALL_BACK);
    }

    @Override
    public Result<AgentDTO> getAgent(String clientId) {
        log.warn("Fallback: getAgent called for clientId: {}, attempting to retrieve from Redis cache", clientId);
        
        try {
            String redisKey = CacheConstants.buildAgentKey(clientId);
            AgentDTO cached = redisCacheManager.get(redisKey, AgentDTO.class);
            
            if (cached != null) {
                log.info("Fallback: Successfully retrieved agent from Redis cache for clientId: {}", clientId);
                return Result.success(cached);
            }
            
            log.warn("Fallback: No cached agent found in Redis for clientId: {}", clientId);
        } catch (Exception e) {
            log.error("Fallback: Failed to retrieve agent from Redis for clientId: {}", clientId, e);
        }

        return Result.fail(RemoteErrorEnum.FALL_BACK);
    }

    @Override
    public Result<AclCheckResultDTO> checkAcl(String sourceClientId, String targetClientId) {
        log.warn("Fallback: checkAcl called for source: {}, target: {}, attempting to retrieve from Redis cache", 
                sourceClientId, targetClientId);
        
        try {
            String redisKey = CacheConstants.buildAclKey(sourceClientId, targetClientId);
            AclCheckResultDTO cached = redisCacheManager.get(redisKey, AclCheckResultDTO.class);
            
            if (cached != null) {
                log.info("Fallback: Successfully retrieved ACL from Redis cache for source: {}, target: {}", 
                        sourceClientId, targetClientId);
                return Result.success(cached);
            }
            
            log.warn("Fallback: No cached ACL found in Redis for source: {}, target: {}", 
                    sourceClientId, targetClientId);
        } catch (Exception e) {
            log.error("Fallback: Failed to retrieve ACL from Redis for source: {}, target: {}", 
                    sourceClientId, targetClientId, e);
        }

        return Result.fail(RemoteErrorEnum.FALL_BACK);
    }
}