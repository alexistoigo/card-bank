package com.example.cardbank.model;

import com.example.cardbank.model.enums.StatusCartao;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CartaoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numero;

    @Column(nullable = false)
    private String nomeTitular;

    @Enumerated(EnumType.STRING)
    private StatusCartao status = StatusCartao.PENDENTE;

    @Column(nullable = false)
    private String tipo; // "FISICO" ou "VIRTUAL"

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}