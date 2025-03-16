package com.example.cardbank.repository;

import com.example.cardbank.model.CartaoCredito;
import com.example.cardbank.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartaoCreditoRepository extends JpaRepository<CartaoCredito, Long> {
    List<CartaoCredito> findByCliente(Cliente cliente);

    Optional<CartaoCredito> findByTrackingId(String trackingId);
}
