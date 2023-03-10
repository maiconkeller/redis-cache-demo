package com.example.demo.cep.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Repository
public class CacheRepository {

    public final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public CacheRepository(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Value("${app-config.cache.ttl}")
    private Integer ttl;

    public Mono<Boolean> save(String key, String value) {
        return reactiveRedisTemplate
                .opsForValue()
                .set(key, value)
                .then(reactiveRedisTemplate.expire(key, Duration.ofSeconds(ttl)))
                .onErrorResume(throwable -> {
                    log.error("Erro ao tentar salvar dados no Redis para a chave: {}", key, throwable.getMessage());
                    return Mono.just(false);
                });
    }

    public Mono<String> get(String key) {
        return reactiveRedisTemplate
                .opsForValue()
                .get(key)
                .onErrorResume(throwable -> {
                    log.error("Erro ao tentar recuperar dados no Redis para a chave: {}", key, throwable.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> existsForKey(String key) {
        return reactiveRedisTemplate
                .hasKey(key)
                .onErrorResume(throwable -> {
                    log.error("Erro ao tentar consultar dados no Redis para a chave: {}", key, throwable.getMessage());
                    return Mono.just(Boolean.FALSE);
                });
    }



}
