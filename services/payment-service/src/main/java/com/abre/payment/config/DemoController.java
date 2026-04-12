package com.abre.payment.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {
    private final RateLimiter demoRateLimiter;

    public DemoController(RateLimiter demoRateLimiter) {
        this.demoRateLimiter = demoRateLimiter;
    }

    @PostMapping("/inject-failure")
    public ResponseEntity<?> injectFailure(@RequestBody Map<String, Object> request) {
        try {
            return demoRateLimiter.executeSupplier(() -> {
                double failureRate = ((Number) request.getOrDefault("failureRate", 0.0)).doubleValue();
                // Store failure rate - will be wired to PaymentController later
                return ResponseEntity.ok(Map.of(
                        "status", "failure rate configured",
                        "failureRate", failureRate
                ));
            });
        } catch (RequestNotPermitted e) {
            return ResponseEntity.status(429).body(Map.of("error", "Rate limit exceeded"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        try {
            return demoRateLimiter.executeSupplier(() ->
                    ResponseEntity.ok(Map.of("status", "demo endpoint active"))
            );
        } catch (RequestNotPermitted e) {
            return ResponseEntity.status(429).body(Map.of("error", "Rate limit exceeded"));
        }
    }
}
