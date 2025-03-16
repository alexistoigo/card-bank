# CARD-BANK API

Projeto para entrevista alt-bank.

## Visão Geral

Este projeto implementa uma API REST para o gerenciamento de clientes e cartões de crédito. A API permite:
- Cadastro de clientes com dados cadastrais e endereço.
- Criação, listagem, ativação, bloqueio e reemissão de cartões de crédito (físico e virtual).
- Validação do CPF para garantir que o valor possua 11 dígitos (desconsiderando formatação) e não contenha caracteres inválidos.
- Garantia de que um cartão virtual só seja criado se o cliente possuir pelo menos um cartão físico validado (ativado).
- **Processamento de Webhooks:**
   - Recebimento da resposta da transportadora sobre a entrega do cartão físico. Ao receber o webhook, o status do cartão é atualizado para **ENTREGUE** e não é ativado automaticamente, garantindo maior segurança.
   - Atualização automática do CVV do cartão virtual, onde o número do CVV e a data de expiração são modificados periodicamente.

## Criacao Banco de Dados via docker

```bash
docker run --name cardbank -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:latest
```

O arquivo com o sql para criacao das tabelas é [database.sql](https://github.com/alexistoigo/card-bank/blob/main/database.sql).

## Endpoints e Exemplos de Payload
### Endpoints para Cliente
1. Criar Cliente
   Método: POST
   URL: http://localhost:8080/clientes
   Body (JSON):
    ```json
    {
        "cpf": "123.456.789-00",
        "email": "teste@exemplo.com",
        "telefone": "11999999999",
        "endereco": {
            "logradouro": "Rua Exemplo",
            "numero": "100",
            "complemento": "Apto 101",
            "bairro": "Centro",
            "cidade": "São Paulo",
            "estado": "SP",
            "cep": "01000-000"
        }
    }
   ```
2. Listar Clientes
   Método: GET
   URL: http://localhost:8080/clientes

3. Buscar Cliente por CPF
   Método: GET
   URL: http://localhost:8080/clientes/cpf/123.456.789-00
4. Inativar Cliente
   Método: PUT
   URL: http://localhost:8080/clientes/{id}/inativar

### Endpoints para Cartão de Crédito
1. Criar Cartão de Crédito
   Método: POST
   URL: http://localhost:8080/clientes/{clienteId}/cartoes
   Exemplo para Cartão Físico:
    ```json
      {
         "numero": "1234-5678-9012-3456",
         "nomeTitular": "João Silva",
         "tipo": "FISICO"
      }
   ```
   Exemplo para Cartão Virtual:
   ```json
   {
       "numero": "9876-5432-1098-7654",
       "nomeTitular": "João Silva",
       "tipo": "VIRTUAL"
   }
   ```
2. Listar Cartões de um Cliente
      Método: GET
      URL: http://localhost:8080/clientes/{clienteId}/cartoes
3. Ativar Cartão
   Método: PUT
   URL: http://localhost:8080/clientes/{clienteId}/cartoes/ativar/{cartaoId}
4. Bloquear Cartão
   Método: PUT
   URL: http://localhost:8080/clientes/{clienteId}/cartoes/bloquear/{cartaoId}
5. Reemitir Cartão
   Método: PUT
   URL: http://localhost:8080/clientes/{clienteId}/cartoes/reemitir/{cartaoId}?motivo=Perda

### Endpoints para Webhooks

1. Resposta da Transportadora sobre a Entrega do Cartão
Método: POST
URL: http://localhost:8080/webhooks/delivery
Payload (JSON):
```json
   {
      "tracking_id": "tracking id do cartão",
      "delivery_status": "ENTREGUE",
      "delivery_date": "2025-03-14T10:15:30",
      "delivery_return_reason": "motivo em caso de problemas",
      "delivery_address": "endereço de entrega"
  }
```
       Observação: Ao receber o webhook, o status do cartão físico será atualizado para ENTREGUE. A ativação manual do cartão só poderá ocorrer após essa confirmação, garantindo maior segurança.

2. Mudança Automática do CVV do Cartão Virtual
Método: POST
URL: http://localhost:8080/webhooks/cvv-change
Payload (JSON):
```json
   {
      "account_id": "processor account id",
      "card_id": 123,
      "next_cvv": 456,
      "expiration_date": "2025-12-31T23:59:59"
   }

```

## Testes Unitários
Os testes unitários do projeto estão localizados na pasta src/test/java/com/card-bank. Para executá-los, utilize:
```bash
    mvn test
```
## Tecnologias Utilizadas
- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL
- Maven
