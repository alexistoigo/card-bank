package com.example.cardbank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CvvChangeWebhookPayload {

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("card_id")
    private Long cardId;

    @JsonProperty("next_cvv")
    private Integer nextCvv;

    @JsonProperty("expiration_date")
    private LocalDateTime expirationDate;
}