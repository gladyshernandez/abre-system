package com.abre.order;

import com.abre.order.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final Map<String, Order> store = new ConcurrentHashMap<>();
    private final RestClient restClient = RestClient.create();

    @Value("${order.processing-delay-ms:0}")
    private long processingDelayMs;

    @Value("${payment.service-url}")
    private String paymentServiceUrl;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) throws InterruptedException {
        if (processingDelayMs > 0) Thread.sleep(processingDelayMs);

        try {
            ResponseEntity<String> paymentResponse = restClient.post()
                    .uri(paymentServiceUrl + "/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("orderId", order.getId(), "amount", order.getAmount()))
                    .retrieve()
                    .toEntity(String.class);

            order.setStatus(paymentResponse.getStatusCode().is2xxSuccessful()
                    ? "COMPLETED" : "PAYMENT_FAILED");

        } catch (Exception e) {
            order.setStatus("PAYMENT_FAILED");
        }

        store.put(order.getId(), order);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        Order order = store.get(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }
}