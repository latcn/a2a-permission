package io.github.latcn.a2a.permission.common.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
public class RedisCacheManager {

    private static final String DEPT_SUB_CACHE_PREFIX = "dept:sub:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheConfig cacheConfig;

    public RedisCacheManager(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, CacheConfig cacheConfig) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.cacheConfig = cacheConfig;
    }

    public <T> void set(String key, T value) {
        if (!cacheConfig.isEnableRedisCache()) {
            return;
        }
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue,
                    cacheConfig.getRedisExpireHours(), TimeUnit.HOURS);
            log.debug("Set cache key: {}", key);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize value for key: {}", key, e);
            throw new RuntimeException("Failed to serialize cache value", e);
        }
    }

    public <T> void set(String key, T value, long timeout, TimeUnit unit) {
        if (!cacheConfig.isEnableRedisCache()) {
            return;
        }
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, timeout, unit);
            log.debug("Set cache key: {} with custom timeout", key);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize value for key: {}", key, e);
            throw new RuntimeException("Failed to serialize cache value", e);
        }
    }

    public <T> T get(String key, Class<T> clazz) {
        if (!cacheConfig.isEnableRedisCache()) {
            return null;
        }
        String jsonValue = redisTemplate.opsForValue().get(key);
        if (jsonValue == null) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonValue, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize value for key: {}", key, e);
            return null;
        }
    }

    public void delete(String key) {
        if (!cacheConfig.isEnableRedisCache()) {
            return;
        }
        redisTemplate.delete(key);
        log.debug("Deleted cache key: {}", key);
    }

    public void delete(Collection<String> keys) {
        if (!cacheConfig.isEnableRedisCache() || keys == null || keys.isEmpty()) {
            return;
        }
        redisTemplate.delete(keys);
        log.debug("Deleted {} cache keys", keys.size());
    }

    public boolean hasKey(String key) {
        if (!cacheConfig.isEnableRedisCache()) {
            return false;
        }
        Boolean hasKey = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(hasKey);
    }

    public boolean expire(String key, long timeout, TimeUnit unit) {
        if (!cacheConfig.isEnableRedisCache()) {
            return false;
        }
        Boolean result = redisTemplate.expire(key, timeout, unit);
        return Boolean.TRUE.equals(result);
    }

    public Long getExpire(String key) {
        if (!cacheConfig.isEnableRedisCache()) {
            return -1L;
        }
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public List<Long> getDeptSubIds(Long deptId) {
        String key = DEPT_SUB_CACHE_PREFIX + deptId;
        return get(key, List.class);
    }

    public void setDeptSubIds(Long deptId, List<Long> subIds) {
        String key = DEPT_SUB_CACHE_PREFIX + deptId;
        set(key, subIds);
    }
}