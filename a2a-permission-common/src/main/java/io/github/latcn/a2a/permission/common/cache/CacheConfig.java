package io.github.latcn.a2a.permission.common.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "permission.cache")
public class CacheConfig {

    private int localMaxSize = CacheConstants.LOCAL_CACHE_MAX_SIZE;
    private int localExpireMinutes = CacheConstants.LOCAL_CACHE_EXPIRE_MINUTES;
    private int redisExpireHours = CacheConstants.REDIS_CACHE_EXPIRE_HOURS;
    private boolean enableLocalCache = true;
    private boolean enableRedisCache = true;
}