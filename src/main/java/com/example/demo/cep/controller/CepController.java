package com.example.demo.cep.controller;

import com.example.demo.cep.dto.CepResponse;
import com.example.demo.cep.service.CepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/cep")
public class CepController {

    @Autowired
    private CepService cepService;

    @GetMapping("{cep}")
    public Mono<CepResponse> findByCep(@PathVariable String cep) {
        return cepService.findByCep(cep);
    }


}
