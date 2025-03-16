package com.example.cardbank.controller;

import com.example.cardbank.model.CartaoCredito;
import com.example.cardbank.service.CartaoCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes/{clienteId}/cartoes")
public class CartaoCreditoController {

    @Autowired
    private CartaoCreditoService cartaoService;

    @PostMapping
    public CartaoCredito criarCartao(@PathVariable Long clienteId, @RequestBody CartaoCredito cartao) {
        return cartaoService.criarCartao(clienteId, cartao);
    }

    @GetMapping
    public List<CartaoCredito> listarCartoes(@PathVariable Long clienteId) {
        return cartaoService.listarCartoesPorCliente(clienteId);
    }

    // Endpoints para ativar, bloquear e reemitir cartão
    @PutMapping("/ativar/{cartaoId}")
    public CartaoCredito ativarCartao(@PathVariable Long cartaoId) {
        return cartaoService.ativarCartao(cartaoId);
    }

    @PutMapping("/bloquear/{cartaoId}")
    public CartaoCredito bloquearCartao(@PathVariable Long cartaoId) {
        return cartaoService.bloquearCartao(cartaoId);
    }

    @PutMapping("/reemitir/{cartaoId}")
    public CartaoCredito reemitirCartao(@PathVariable Long cartaoId, @RequestParam String motivo) {
        return cartaoService.reemitirCartao(cartaoId, motivo);
    }
}