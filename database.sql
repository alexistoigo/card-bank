-- Criação da base de dados
CREATE DATABASE IF NOT EXISTS cardbank_db;
USE cardbank_db;

-- Tabela Cliente
CREATE TABLE cliente (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         cpf VARCHAR(14) NOT NULL UNIQUE,
                         email VARCHAR(255) NOT NULL,
                         telefone VARCHAR(20) NOT NULL,
                         status VARCHAR(10) NOT NULL,
                         logradouro VARCHAR(255),
                         numero VARCHAR(20),
                         complemento VARCHAR(100),
                         bairro VARCHAR(100),
                         cidade VARCHAR(100),
                         estado VARCHAR(50),
                         cep VARCHAR(20)
);

-- Tabela CartaoCredito
CREATE TABLE cartao_credito (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                numero VARCHAR(20) NOT NULL UNIQUE,
                                nome_titular VARCHAR(255) NOT NULL,
                                status VARCHAR(10) NOT NULL,
                                tipo VARCHAR(10) NOT NULL,
                                cliente_id BIGINT,
                                cvv INT,
                                tracking_id VARCHAR(50),
                                expiration_date DATETIME,
                                CONSTRAINT fk_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);
