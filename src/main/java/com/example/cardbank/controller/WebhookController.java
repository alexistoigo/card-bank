package com.example.cardbank.controller;

import com.example.cardbank.dto.CvvChangeWebhookPayload;
import com.example.cardbank.dto.DeliveryWebhookPayload;
import com.example.cardbank.model.CartaoCredito;
import com.example.cardbank.model.enums.StatusCartao;
import com.example.cardbank.repository.CartaoCreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    @Autowired
    private CartaoCreditoRepository cartaoCreditoRepository;

    @PostMapping("/delivery")
    public ResponseEntity<?> processDeliveryWebhook(@RequestBody DeliveryWebhookPayload payload) {
        Optional<CartaoCredito> optionalCard = cartaoCreditoRepository.findByTrackingId(payload.getTrackingId());
        if (!optionalCard.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cartão não encontrado para tracking_id " + payload.getTrackingId());
        }
        CartaoCredito cartao = optionalCard.get();
        if ("ENTREGUE".equalsIgnoreCase(payload.getDeliveryStatus())) {
            cartao.setStatus(StatusCartao.ENTREGUE);
            cartaoCreditoRepository.save(cartao);
        }
        return ResponseEntity.ok("Webhook de entrega processado com sucesso.");
    }

    @PostMapping("/cvv-change")
    public ResponseEntity<?> processCvvChangeWebhook(@RequestBody CvvChangeWebhookPayload payload) {
        Optional<CartaoCredito> optionalCard = cartaoCreditoRepository.findById(payload.getCardId());
        if (optionalCard.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cartão não encontrado para card_id " + payload.getCardId());
        }
        CartaoCredito cartao = optionalCard.get();
        cartao.setCvv(payload.getNextCvv());
        cartao.setExpirationDate(payload.getExpirationDate());
        cartaoCreditoRepository.save(cartao);
        return ResponseEntity.ok("CVV atualizado com sucesso.");
    }
}