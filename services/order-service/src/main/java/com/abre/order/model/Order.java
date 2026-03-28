package com.abre.order.model;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class Order {
    private String id = UUID.randomUUID().toString();
    private String customerId;
    private double amount;
    private String status = "PENDING";
    private Instant createdAt = Instant.now();
}