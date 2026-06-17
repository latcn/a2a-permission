package io.github.latcn.a2a.permission.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.latcn.a2a.permission.api.dto.AclCheckResult;
import io.github.latcn.a2a.permission.api.dto.AgentDTO;
import io.github.latcn.a2a.permission.api.dto.UserFullPermissionDTO;
import io.github.latcn.a2a.permission.api.service.PermissionQueryService;
import io.github.latcn.a2a.permission.common.cache.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@AutoConfiguration
@EnableConfigurationProperties(CacheConfig.class)
@ConditionalOnProperty(prefix = "permission.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CommonAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public CacheConfig cacheConfig() {
        return new CacheConfig();
    }

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean
    public RedisCacheManager redisCacheManager(StringRedisTemplate redisTemplate, 
                                                ObjectMapper objectMapper,
                                                CacheConfig cacheConfig) {
        return new RedisCacheManager(redisTemplate, objectMapper, cacheConfig);
    }

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean
    public UserVersionCache userVersionCache(StringRedisTemplate redisTemplate, 
                                             CacheConfig cacheConfig) {
        return new UserVersionCache(redisTemplate, cacheConfig);
    }

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean
    public RoleVersionCache roleVersionCache(StringRedisTemplate redisTemplate, 
                                             CacheConfig cacheConfig) {
        return new RoleVersionCache(redisTemplate, cacheConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalCacheManager<Long, UserFullPermissionDTO> userPermissionLocalCache(CacheConfig cacheConfig) {
        return new LocalCacheManager<>("userPermissionCache", cacheConfig, 
                key -> null);
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalCacheManager<String, AgentDTO> agentLocalCache(CacheConfig cacheConfig) {
        return new LocalCacheManager<>("agentCache", cacheConfig, 
                key -> null);
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalCacheManager<String, AclCheckResult> aclLocalCache(CacheConfig cacheConfig) {
        return new LocalCacheManager<>("aclCache", cacheConfig, 
                key -> null);
    }

    @Bean
    @ConditionalOnBean(PermissionQueryService.class)
    @ConditionalOnMissingBean
    public CachedPermissionQueryService cachedPermissionQueryService(
            PermissionQueryService delegate,
            LocalCacheManager<Long, UserFullPermissionDTO> userPermissionCache,
            LocalCacheManager<String, AgentDTO> agentCache,
            LocalCacheManager<String, AclCheckResult> aclCache,
            RedisCacheManager redisCacheManager,
            UserVersionCache userVersionCache,
            RoleVersionCache roleVersionCache,
            CacheConfig cacheConfig) {
        return new CachedPermissionQueryService(
                delegate,
                userPermissionCache,
                agentCache,
                aclCache,
                redisCacheManager,
                userVersionCache,
                roleVersionCache,
                cacheConfig
        );
    }
}