package com.accenture.infraestructura.manage_http.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/valid")
public class HealthController {
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    
    @GetMapping("/health")
    public Mono<String> health(ServerHttpRequest request) {
        logger.info("Health endpoint called. Headers: {}", request.getHeaders());
        return Mono.just("OK");
    }
}