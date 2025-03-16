package com.example.cardbank.model;

import com.example.cardbank.model.enums.StatusCartao;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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
    @JsonBackReference
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column
    private Integer cvv;

    @Column
    private String trackingId;

    @Column
    private LocalDateTime expirationDate;
}