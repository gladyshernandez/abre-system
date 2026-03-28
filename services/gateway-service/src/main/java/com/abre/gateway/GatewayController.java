package com.abre.gateway;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
public class GatewayController {

    private final RestClient restClient = RestClient.create();

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Gateway OK");
    }

    @PostMapping("/route/orders")
    public ResponseEntity<String> routeOrder(@RequestBody String body) {
        return restClient.post()
                .uri("http://localhost:8081/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(String.class);
    }
}