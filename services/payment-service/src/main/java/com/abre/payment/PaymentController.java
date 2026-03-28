package com.abre.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final Map<String, Object> store = new ConcurrentHashMap<>();

    @Value("${payment.failure-rate:0.0}")
    private double failureRate;

    @PostMapping
    public ResponseEntity<Object> processPayment(@RequestBody Map<String, Object> body) {
        if (Math.random() < failureRate) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Payment processor unavailable"));
        }

        String id = UUID.randomUUID().toString();
        Map<String, Object> payment = Map.of(
                "id", id,
                "orderId", body.get("orderId"),
                "amount", body.get("amount"),
                "status", "SUCCESS"
        );
        store.put(id, payment);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPayment(@PathVariable String id) {
        Object payment = store.get(id);
        return payment != null ? ResponseEntity.ok(payment) : ResponseEntity.notFound().build();
    }
}