package com.example.demo.cep.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CacheServiceWrapper {

    private final LocalCacheService localCacheService;
    private final RedisCacheService redisCacheService;

    @Value("${app-config.cache.redis-enabled}")
    private Boolean redisEnabled;

    public CacheServiceWrapper(LocalCacheService localCacheService, RedisCacheService redisCacheService) {
        this.localCacheService = localCacheService;
        this.redisCacheService = redisCacheService;
    }

    public Mono<String> get(String key) {
        if (redisEnabled) {
            return redisCacheService.get(key);
        }
        return localCacheService.get(key);
    }

    public Mono<Boolean> exists(String key) {
        if (redisEnabled) {
            return redisCacheService.existsForKey(key);
        }
        return localCacheService.existsForKey(key);
    }

    public Mono<String> save(String key, String value) {
        if (redisEnabled) {
            return redisCacheService.save(key, value);
        }
        return localCacheService.save(key, value);
    }

}
