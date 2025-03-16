package com.example.cardbank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryWebhookPayload {

    @JsonProperty("tracking_id")
    private String trackingId;

    @JsonProperty("delivery_status")
    private String deliveryStatus;

    @JsonProperty("delivery_date")
    private LocalDateTime deliveryDate;

    @JsonProperty("delivery_return_reason")
    private String deliveryReturnReason;

    @JsonProperty("delivery_address")
    private String deliveryAddress;
}