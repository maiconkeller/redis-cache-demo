package com.example.demo.cep.service;

import com.example.demo.cep.client.ViaCepClient;
import com.example.demo.cep.dto.CepResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class CepService {

    private final ViaCepClient viaCepClient;
    private final ObjectMapper objectMapper;
    private final CacheServiceWrapper cacheServiceWrapper;

    public CepService(ViaCepClient viaCepClient, ObjectMapper objectMapper, CacheServiceWrapper cacheServiceWrapper) {
        this.viaCepClient = viaCepClient;
        this.objectMapper = objectMapper;
        this.cacheServiceWrapper = cacheServiceWrapper;
    }

    public Mono<CepResponse> findByCep(final String cep) {
        return cacheServiceWrapper
                .exists(cep)
                .flatMap(exists -> {
                    if (exists) {
                        return cacheServiceWrapper.get(cep);
                    } else {
                        return viaCepClient
                            .findByCep(cep)
                            .flatMap(response -> cacheServiceWrapper.save(cep, response));
                    }
                })
                .flatMap(this::handleResponse);
    }

    private Mono<CepResponse> handleResponse(String response) {
        if (!isEmpty(response)) {
            try {
                return Mono.just(objectMapper.readValue(response, CepResponse.class));
            } catch (Exception ex) {
                log.error("Erro ao tentar converter resposta do CEP", ex);
            }
        }
        return Mono.empty();
    }

}
