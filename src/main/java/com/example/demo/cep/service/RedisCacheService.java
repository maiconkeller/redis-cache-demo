package com.example.demo.cep.service;

import com.example.demo.cep.repository.CacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class RedisCacheService {

    private final CacheRepository cacheRepository;

    public RedisCacheService(CacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    public Mono<String> save(String key, String value) {
        try {
            return cacheRepository
                    .save(key, value)
                    .flatMap(saved -> {
                        if (saved) {
                            log.info("Cache salvo para a chave {}", key);
                        } else {
                            log.info("Não foi possível salvar cache para a chave {}", key);
                        }
                        return Mono.just(value);
                    });
        } catch (Exception ex) {
            log.error("Erro ao tentar salvar o cache para chave {}", key, ex);
        }
        return Mono.just(value);
    }

    public Mono<String> get(String key) {
        try {
            return cacheRepository
                    .get(key)
                    .doOnNext(saved -> log.info("Retornado cache para a chave {}", key));
        } catch (Exception ex) {
            log.error("Erro ao tentar recuperar o cache para chave {}", key, ex);
        }
        return Mono.empty();
    }

    public Mono<Boolean> existsForKey(String key) {
        try {
            return cacheRepository
                    .existsForKey(key)
                    .doOnNext(saved -> {
                        if (saved) {
                            log.info("Cache existente para a chave {}", key);
                        } else {
                            log.info("Não foi possível encontrar cache para a chave {}", key);
                        }
                    });
        } catch (Exception ex) {
            log.error("Erro ao tentar recuperar o cache para chave {}", key, ex);
        }
        return Mono.just(Boolean.FALSE);
    }

}
