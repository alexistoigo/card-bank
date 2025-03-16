package com.example.cardbank.service;

import com.example.cardbank.model.CartaoCredito;
import com.example.cardbank.model.Cliente;
import com.example.cardbank.model.enums.StatusCartao;
import com.example.cardbank.repository.CartaoCreditoRepository;
import com.example.cardbank.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartaoCreditoService {

    @Autowired
    private CartaoCreditoRepository cartaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public CartaoCredito criarCartao(Long clienteId, CartaoCredito cartao) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        cartao.setCliente(cliente);

        if ("VIRTUAL".equalsIgnoreCase(cartao.getTipo())) {
            List<CartaoCredito> cartoesFisicosValidos = cartaoRepository.findByCliente(cliente)
                    .stream()
                    .filter(c -> "FISICO".equalsIgnoreCase(c.getTipo()) && c.getStatus() == StatusCartao.ATIVADO)
                    .toList();
            if (cartoesFisicosValidos.isEmpty()) {
                throw new RuntimeException("Não é possível criar cartão virtual, pois nenhum cartão físico foi validado.");
            }
        }
        return cartaoRepository.save(cartao);
    }

    public List<CartaoCredito> listarCartoesPorCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return cartaoRepository.findByCliente(cliente);
    }

    public CartaoCredito ativarCartao(Long cartaoId) {
        CartaoCredito cartao = cartaoRepository.findById(cartaoId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        cartao.setStatus(StatusCartao.ATIVADO);
        return cartaoRepository.save(cartao);
    }

    public CartaoCredito bloquearCartao(Long cartaoId) {
        CartaoCredito cartao = cartaoRepository.findById(cartaoId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
        cartao.setStatus(StatusCartao.BLOQUEADO);
        return cartaoRepository.save(cartao);
    }

    public CartaoCredito reemitirCartao(Long cartaoId, String motivo) {
        CartaoCredito cartao = cartaoRepository.findById(cartaoId)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if ("FISICO".equalsIgnoreCase(cartao.getTipo())) {

            cartao.setStatus(StatusCartao.CANCELADO);
            cartaoRepository.save(cartao);

            CartaoCredito novoCartao = new CartaoCredito();
            novoCartao.setCliente(cartao.getCliente());
            novoCartao.setTipo("FISICO");
            novoCartao.setNomeTitular(cartao.getNomeTitular());
            novoCartao.setNumero(gerarNovoNumeroCartao());
            novoCartao.setStatus(StatusCartao.PENDENTE);
            return cartaoRepository.save(novoCartao);
        } else {
            cartao.setStatus(StatusCartao.PENDENTE);
            return cartaoRepository.save(cartao);
        }
    }

    private String gerarNovoNumeroCartao() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int digit = (int) (Math.random() * 10);
            sb.append(digit);
        }
        return sb.toString();
    }
}