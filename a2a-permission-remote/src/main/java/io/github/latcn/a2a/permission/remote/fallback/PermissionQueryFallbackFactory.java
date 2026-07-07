package io.github.latcn.a2a.permission.remote.fallback;

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
        return new PermissionQueryFallback(redisCacheManager);
    }
}