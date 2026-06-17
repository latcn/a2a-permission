package io.github.latcn.a2a.permission.common.cache;

import io.github.latcn.a2a.permission.api.dto.*;
import io.github.latcn.a2a.permission.api.service.PermissionQueryService;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
public class CachedPermissionQueryService implements PermissionQueryService {

    private final PermissionQueryService delegate;
    private final LocalCacheManager<Long, UserFullPermissionDTO> userPermissionCache;
    private final LocalCacheManager<String, AgentDTO> agentCache;
    private final LocalCacheManager<String, AclCheckResult> aclCache;
    private final RedisCacheManager redisCacheManager;
    private final UserVersionCache userVersionCache;
    private final RoleVersionCache roleVersionCache;
    private final CacheConfig cacheConfig;

    public CachedPermissionQueryService(
            PermissionQueryService delegate,
            LocalCacheManager<Long, UserFullPermissionDTO> userPermissionCache,
            LocalCacheManager<String, AgentDTO> agentCache,
            LocalCacheManager<String, AclCheckResult> aclCache,
            RedisCacheManager redisCacheManager,
            UserVersionCache userVersionCache,
            RoleVersionCache roleVersionCache,
            CacheConfig cacheConfig) {
        this.delegate = delegate;
        this.userPermissionCache = userPermissionCache;
        this.agentCache = agentCache;
        this.aclCache = aclCache;
        this.redisCacheManager = redisCacheManager;
        this.userVersionCache = userVersionCache;
        this.roleVersionCache = roleVersionCache;
        this.cacheConfig = cacheConfig;
    }

    @Override
    public TokenExchangePrepareResponse prepareTokenExchange(TokenExchangePrepareRequest request) {
        return delegate.prepareTokenExchange(request);
    }

    @Override
    public UserFullPermissionDTO getUserFullPermissions(Long userId) {
        if (!cacheConfig.isEnableLocalCache()) {
            return delegate.getUserFullPermissions(userId);
        }

        try {
            UserFullPermissionDTO result = userPermissionCache.get(userId, 
                    key -> loadUserFullPermissions(key)).join();
            
            if (result != null && isCacheValid(result)) {
                return result;
            }
            
            userPermissionCache.invalidate(userId);
            return userPermissionCache.get(userId, 
                    key -> loadUserFullPermissions(key)).join();
        } catch (Exception e) {
            log.error("Failed to get user permissions from cache for userId: {}", userId, e);
            return delegate.getUserFullPermissions(userId);
        }
    }

    @Override
    public AgentDTO getAgent(String clientId) {
        if (!cacheConfig.isEnableLocalCache()) {
            return delegate.getAgent(clientId);
        }

        try {
            return agentCache.get(clientId, 
                    key -> loadAgent(key)).join();
        } catch (Exception e) {
            log.error("Failed to get agent from cache for clientId: {}", clientId, e);
            return delegate.getAgent(clientId);
        }
    }

    @Override
    public AclCheckResult checkAcl(String sourceClientId, String targetClientId) {
        if (!cacheConfig.isEnableLocalCache()) {
            return delegate.checkAcl(sourceClientId, targetClientId);
        }

        String cacheKey = CacheConstants.buildAclKey(sourceClientId, targetClientId);
        try {
            return aclCache.get(cacheKey, 
                    key -> loadAclCheckResult(sourceClientId, targetClientId)).join();
        } catch (Exception e) {
            log.error("Failed to check ACL from cache for source: {}, target: {}", 
                    sourceClientId, targetClientId, e);
            return delegate.checkAcl(sourceClientId, targetClientId);
        }
    }

    public void invalidateUserPermission(Long userId) {
        if (cacheConfig.isEnableLocalCache()) {
            userPermissionCache.invalidate(userId);
        }
        if (cacheConfig.isEnableRedisCache()) {
            String redisKey = CacheConstants.buildUserPermissionKey(userId);
            redisCacheManager.delete(redisKey);
        }
        log.debug("Invalidated user permission cache for userId: {}", userId);
    }

    public void invalidateAgent(String clientId) {
        if (cacheConfig.isEnableLocalCache()) {
            agentCache.invalidate(clientId);
        }
        if (cacheConfig.isEnableRedisCache()) {
            String redisKey = CacheConstants.buildAgentKey(clientId);
            redisCacheManager.delete(redisKey);
        }
        log.debug("Invalidated agent cache for clientId: {}", clientId);
    }

    public void invalidateAcl(String sourceClientId, String targetClientId) {
        if (cacheConfig.isEnableLocalCache()) {
            String cacheKey = CacheConstants.buildAclKey(sourceClientId, targetClientId);
            aclCache.invalidate(cacheKey);
        }
        if (cacheConfig.isEnableRedisCache()) {
            String redisKey = CacheConstants.buildAclKey(sourceClientId, targetClientId);
            redisCacheManager.delete(redisKey);
        }
        log.debug("Invalidated ACL cache for source: {}, target: {}", sourceClientId, targetClientId);
    }

    public void invalidateByRole(Long roleId) {
        if (cacheConfig.isEnableLocalCache()) {
            userPermissionCache.invalidateByTag("role:" + roleId);
        }
        log.debug("Invalidated caches by roleId: {}", roleId);
    }

    public void invalidateAll() {
        if (cacheConfig.isEnableLocalCache()) {
            userPermissionCache.invalidateAll();
            agentCache.invalidateAll();
            aclCache.invalidateAll();
        }
        log.debug("Invalidated all caches");
    }

    private UserFullPermissionDTO loadUserFullPermissions(Long userId) {
        UserFullPermissionDTO result = delegate.getUserFullPermissions(userId);
        
        if (result != null && cacheConfig.isEnableRedisCache()) {
            String redisKey = CacheConstants.buildUserPermissionKey(userId);
            redisCacheManager.set(redisKey, result);
            
            if (result.getRoles() != null) {
                for (RoleInfo role : result.getRoles()) {
                    userPermissionCache.addTag(userId, "role:" + role.getId());
                }
            }
        }
        
        return result;
    }

    private AgentDTO loadAgent(String clientId) {
        AgentDTO result = delegate.getAgent(clientId);
        
        if (result != null && cacheConfig.isEnableRedisCache()) {
            String redisKey = CacheConstants.buildAgentKey(clientId);
            redisCacheManager.set(redisKey, result);
        }
        
        return result;
    }

    private AclCheckResult loadAclCheckResult(String sourceClientId, String targetClientId) {
        AclCheckResult result = delegate.checkAcl(sourceClientId, targetClientId);
        
        if (result != null && cacheConfig.isEnableRedisCache()) {
            String redisKey = CacheConstants.buildAclKey(sourceClientId, targetClientId);
            redisCacheManager.set(redisKey, result);
        }
        
        return result;
    }

    private boolean isCacheValid(UserFullPermissionDTO cached) {
        if (cached == null || cached.getUserId() == null) {
            return false;
        }

        Long cachedUserVersion = userVersionCache.getUserVersion(cached.getUserId());
        if (cachedUserVersion != null && !cachedUserVersion.equals(cached.getUserPermVersion())) {
            log.debug("User version mismatch for userId: {}, cached: {}, current: {}", 
                    cached.getUserId(), cached.getUserPermVersion(), cachedUserVersion);
            return false;
        }

        if (cached.getRoleVersions() != null) {
            for (Map.Entry<Long, Long> entry : cached.getRoleVersions().entrySet()) {
                Long roleId = entry.getKey();
                Long cachedVersion = entry.getValue();
                Long currentVersion = roleVersionCache.getRoleVersion(roleId);
                
                if (currentVersion != null && !currentVersion.equals(cachedVersion)) {
                    log.debug("Role version mismatch for roleId: {}, cached: {}, current: {}", 
                            roleId, cachedVersion, currentVersion);
                    return false;
                }
            }
        }

        return true;
    }

    public PermissionQueryService getDelegate() {
        return delegate;
    }
}