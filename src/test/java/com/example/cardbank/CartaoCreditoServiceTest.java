package com.example.cardbank;

import com.example.cardbank.model.CartaoCredito;
import com.example.cardbank.model.Cliente;
import com.example.cardbank.model.enums.StatusCartao;
import com.example.cardbank.repository.CartaoCreditoRepository;
import com.example.cardbank.repository.ClienteRepository;
import com.example.cardbank.service.CartaoCreditoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartaoCreditoServiceTest {

    @Mock
    private CartaoCreditoRepository cartaoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private CartaoCreditoService cartaoCreditoService;

    private Cliente cliente;
    private CartaoCredito cartaoFisicoAtivado;
    private CartaoCredito cartaoFisicoPendente;

    @BeforeEach
    public void setup() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCpf("12345678900");
        cliente.setEmail("teste@exemplo.com");
        cliente.setTelefone("11999999999");

        cartaoFisicoAtivado = new CartaoCredito();
        cartaoFisicoAtivado.setId(100L);
        cartaoFisicoAtivado.setTipo("FISICO");
        cartaoFisicoAtivado.setStatus(StatusCartao.ATIVADO);
        cartaoFisicoAtivado.setNomeTitular("João Silva");
        cartaoFisicoAtivado.setCliente(cliente);

        cartaoFisicoPendente = new CartaoCredito();
        cartaoFisicoPendente.setId(101L);
        cartaoFisicoPendente.setTipo("FISICO");
        cartaoFisicoPendente.setStatus(StatusCartao.PENDENTE);
        cartaoFisicoPendente.setNomeTitular("João Silva");
        cartaoFisicoPendente.setCliente(cliente);
    }

    @Test
    public void testCriarCartaoFisico() {
        CartaoCredito novoCartaoFisico = new CartaoCredito();
        novoCartaoFisico.setTipo("FISICO");
        novoCartaoFisico.setNomeTitular("João Silva");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cartaoRepository.save(any(CartaoCredito.class))).thenAnswer(i -> {
            CartaoCredito cc = i.getArgument(0);
            cc.setId(300L);
            return cc;
        });

        CartaoCredito result = cartaoCreditoService.criarCartao(1L, novoCartaoFisico);
        assertNotNull(result);
        assertEquals(300L, result.getId());
        verify(cartaoRepository, times(1)).save(novoCartaoFisico);
    }

    @Test
    public void testCriarCartaoVirtualSemCartaoFisicoAtivado() {
        CartaoCredito novoCartaoVirtual = new CartaoCredito();
        novoCartaoVirtual.setTipo("VIRTUAL");
        novoCartaoVirtual.setNomeTitular("João Silva");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cartaoRepository.findByCliente(cliente)).thenReturn(Arrays.asList(cartaoFisicoPendente));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartaoCreditoService.criarCartao(1L, novoCartaoVirtual);
        });
        String expectedMessage = "Não é possível criar cartão virtual, pois nenhum cartão físico foi validado.";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testCriarCartaoVirtualComCartaoFisicoAtivado() {
        CartaoCredito novoCartaoVirtual = new CartaoCredito();
        novoCartaoVirtual.setTipo("VIRTUAL");
        novoCartaoVirtual.setNomeTitular("João Silva");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cartaoRepository.findByCliente(cliente)).thenReturn(Arrays.asList(cartaoFisicoAtivado, cartaoFisicoPendente));
        when(cartaoRepository.save(any(CartaoCredito.class))).thenAnswer(i -> {
            CartaoCredito cc = i.getArgument(0);
            cc.setId(400L);
            return cc;
        });

        CartaoCredito result = cartaoCreditoService.criarCartao(1L, novoCartaoVirtual);
        assertNotNull(result);
        assertEquals(400L, result.getId());
        verify(cartaoRepository, times(1)).save(novoCartaoVirtual);
    }

    @Test
    public void testAtivarCartao() {
        when(cartaoRepository.findById(100L)).thenReturn(Optional.of(cartaoFisicoPendente));
        when(cartaoRepository.save(any(CartaoCredito.class))).thenAnswer(i -> i.getArgument(0));

        CartaoCredito result = cartaoCreditoService.ativarCartao(100L);
        assertEquals(StatusCartao.ATIVADO, result.getStatus());
    }

    @Test
    public void testBloquearCartao() {
        when(cartaoRepository.findById(100L)).thenReturn(Optional.of(cartaoFisicoAtivado));
        when(cartaoRepository.save(any(CartaoCredito.class))).thenAnswer(i -> i.getArgument(0));

        CartaoCredito result = cartaoCreditoService.bloquearCartao(100L);
        assertEquals(StatusCartao.BLOQUEADO, result.getStatus());
    }

    @Test
    public void testReemitirCartaoFisicoFurto() {
        CartaoCredito cartaoExistente = new CartaoCredito();
        cartaoExistente.setId(500L);
        cartaoExistente.setTipo("FISICO");
        cartaoExistente.setStatus(StatusCartao.ATIVADO);
        cartaoExistente.setNomeTitular("João Silva");
        cartaoExistente.setCliente(cliente);

        when(cartaoRepository.findById(500L)).thenReturn(Optional.of(cartaoExistente));
        when(cartaoRepository.save(any(CartaoCredito.class))).thenAnswer(i -> {
            CartaoCredito cc = i.getArgument(0);
            if (cc.getId() == null) {
                cc.setId(600L);
            }
            return cc;
        });

        CartaoCredito novoCartao = cartaoCreditoService.reemitirCartao(500L, "furto");
        assertEquals(StatusCartao.PENDENTE, novoCartao.getStatus());
        assertEquals("FISICO", novoCartao.getTipo());
        assertNotNull(novoCartao.getNumero());
        assertEquals(16, novoCartao.getNumero().length());
    }

    @Test
    public void testReemitirCartaoVirtual() {
        CartaoCredito cartaoExistente = new CartaoCredito();
        cartaoExistente.setId(700L);
        cartaoExistente.setTipo("VIRTUAL");
        cartaoExistente.setStatus(StatusCartao.BLOQUEADO);
        cartaoExistente.setNomeTitular("João Silva");
        cartaoExistente.setCliente(cliente);

        when(cartaoRepository.findById(700L)).thenReturn(Optional.of(cartaoExistente));
        when(cartaoRepository.save(any(CartaoCredito.class))).thenAnswer(i -> i.getArgument(0));

        CartaoCredito result = cartaoCreditoService.reemitirCartao(700L, "qualquer");
        assertEquals(StatusCartao.PENDENTE, result.getStatus());
    }
}
