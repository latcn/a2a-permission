package io.github.latcn.a2a.permission.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
public class UserVersionCache {

    private static final String USER_VERSION_KEY_PREFIX = CacheConstants.USER_VERSION_CACHE_PREFIX;
    
    private final StringRedisTemplate redisTemplate;
    private final CacheConfig cacheConfig;

    public UserVersionCache(StringRedisTemplate redisTemplate, CacheConfig cacheConfig) {
        this.redisTemplate = redisTemplate;
        this.cacheConfig = cacheConfig;
    }

    public void setUserVersion(Long userId, Long version) {
        if (!cacheConfig.isEnableRedisCache()) {
            return;
        }
        String key = buildKey(userId);
        redisTemplate.opsForValue().set(key, String.valueOf(version), 
                cacheConfig.getRedisExpireHours(), TimeUnit.HOURS);
        log.debug("Set user version: userId={}, version={}", userId, version);
    }

    public Long getUserVersion(Long userId) {
        if (!cacheConfig.isEnableRedisCache()) {
            return null;
        }
        String key = buildKey(userId);
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                log.warn("Failed to parse user version for userId={}", userId, e);
                return null;
            }
        }
        return null;
    }

    public void deleteUserVersion(Long userId) {
        if (!cacheConfig.isEnableRedisCache()) {
            return;
        }
        String key = buildKey(userId);
        redisTemplate.delete(key);
        log.debug("Deleted user version: userId={}", userId);
    }

    public void incrementUserVersion(Long userId) {
        if (!cacheConfig.isEnableRedisCache()) {
            return;
        }
        String key = buildKey(userId);
        redisTemplate.opsForValue().increment(key);
        log.debug("Incremented user version: userId={}", userId);
    }

    private String buildKey(Long userId) {
        return USER_VERSION_KEY_PREFIX + userId;
    }
}