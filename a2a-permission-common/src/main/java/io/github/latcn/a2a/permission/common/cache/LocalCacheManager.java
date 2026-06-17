package io.github.latcn.a2a.permission.common.cache;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class LocalCacheManager<K, V> implements RemovalListener<K, V> {

    private final AsyncLoadingCache<K, V> cache;
    private final Map<K, Set<String>> reverseIndex;
    private final Map<String, Set<K>> tagToKeysIndex;
    private final String cacheName;

    public LocalCacheManager(String cacheName, CacheConfig config, Function<K, V> loader) {
        this.cacheName = cacheName;
        this.reverseIndex = new ConcurrentHashMap<>();
        this.tagToKeysIndex = new ConcurrentHashMap<>();
        
        this.cache = Caffeine.newBuilder()
                .maximumSize(config.getLocalMaxSize())
                .expireAfterWrite(config.getLocalExpireMinutes(), TimeUnit.MINUTES)
                .removalListener(this)
                .buildAsync((key, executor) -> CompletableFuture.supplyAsync(() -> loader.apply(key), executor));
    }

    public CompletableFuture<V> get(K key) {
        return cache.get(key);
    }

    public CompletableFuture<V> get(K key, Function<K, V> loader) {
        CompletableFuture<V> future = cache.get(key);
        if (future == null) {
            V value = loader.apply(key);
            put(key, value);
            return CompletableFuture.completedFuture(value);
        }
        return future;
    }

    public void put(K key, V value) {
        cache.put(key, CompletableFuture.completedFuture(value));
    }

    public void invalidate(K key) {
        cache.synchronous().invalidate(key);
    }

    public void invalidateAll() {
        cache.synchronous().invalidateAll();
        reverseIndex.clear();
        tagToKeysIndex.clear();
    }

    public void invalidateByTag(String tag) {
        Set<K> keys = tagToKeysIndex.get(tag);
        if (keys != null) {
            Set<K> keysCopy = new HashSet<>(keys);
            keysCopy.forEach(cache.synchronous()::invalidate);
            tagToKeysIndex.remove(tag);
        }
    }

    public void addTag(K key, String tag) {
        reverseIndex.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(tag);
        tagToKeysIndex.computeIfAbsent(tag, t -> ConcurrentHashMap.newKeySet()).add(key);
    }

    public void removeTag(K key, String tag) {
        Set<String> tags = reverseIndex.get(key);
        if (tags != null) {
            tags.remove(tag);
            if (tags.isEmpty()) {
                reverseIndex.remove(key);
            }
        }
        
        Set<K> keys = tagToKeysIndex.get(tag);
        if (keys != null) {
            keys.remove(key);
            if (keys.isEmpty()) {
                tagToKeysIndex.remove(tag);
            }
        }
    }

    public Set<String> getTags(K key) {
        return Collections.unmodifiableSet(reverseIndex.getOrDefault(key, Collections.emptySet()));
    }

    public long size() {
        return cache.synchronous().estimatedSize();
    }

    public Map<K, V> asMap() {
        return cache.synchronous().asMap();
    }

    @Override
    public void onRemoval(@Nullable K key, @Nullable V value, @NonNull RemovalCause cause) {
        if (key == null) {
            return;
        }
        
        log.debug("Cache [{}] removed key: {}, cause: {}", cacheName, key, cause);
        
        Set<String> tags = reverseIndex.remove(key);
        if (tags != null) {
            for (String tag : tags) {
                Set<K> keys = tagToKeysIndex.get(tag);
                if (keys != null) {
                    keys.remove(key);
                    if (keys.isEmpty()) {
                        tagToKeysIndex.remove(tag);
                    }
                }
            }
        }
    }

    public AsyncLoadingCache<K, V> getCache() {
        return cache;
    }

    public String getCacheName() {
        return cacheName;
    }
}