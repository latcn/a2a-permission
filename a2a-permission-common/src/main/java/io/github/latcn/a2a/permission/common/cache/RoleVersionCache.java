package io.github.latcn.a2a.permission.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RoleVersionCache {

    private static final String ROLE_VERSION_KEY_PREFIX = CacheConstants.ROLE_VERSION_CACHE_PREFIX;
    
    private final StringRedisTemplate redisTemplate;
    private final CacheConfig cacheConfig;

    public RoleVersionCache(StringRedisTemplate redisTemplate, CacheConfig cacheConfig) {
        this.redisTemplate = redisTemplate;
        this.cacheConfig = cacheConfig;
    }

    public void setRoleVersion(Long roleId, Long version) {
        if (!cacheConfig.isEnableRedisCache()) {
            return;
        }
        String key = buildKey(roleId);
        redisTemplate.opsForValue().set(key, String.valueOf(version), 
                cacheConfig.getRedisExpireHours(), TimeUnit.HOURS);
        log.debug("Set role version: roleId={}, version={}", roleId, version);
    }

    public Long getRoleVersion(Long roleId) {
        if (!cacheConfig.isEnableRedisCache()) {
            return null;
        }
        String key = buildKey(roleId);
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                log.warn("Failed to parse role version for roleId={}", roleId, e);
                return null;
            }
        }
        return null;
    }

    public void deleteRoleVersion(Long roleId) {
        if (!cacheConfig.isEnableRedisCache()) {
            return;
        }
        String key = buildKey(roleId);
        redisTemplate.delete(key);
        log.debug("Deleted role version: roleId={}", roleId);
    }

    public void incrementRoleVersion(Long roleId) {
        if (!cacheConfig.isEnableRedisCache()) {
            return;
        }
        String key = buildKey(roleId);
        redisTemplate.opsForValue().increment(key);
        log.debug("Incremented role version: roleId={}", roleId);
    }

    private String buildKey(Long roleId) {
        return ROLE_VERSION_KEY_PREFIX + roleId;
    }
}