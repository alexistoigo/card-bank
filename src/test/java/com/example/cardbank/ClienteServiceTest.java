package com.example.cardbank;

import com.example.cardbank.model.Cliente;
import com.example.cardbank.model.Endereco;
import com.example.cardbank.model.enums.StatusCliente;
import com.example.cardbank.repository.ClienteRepository;
import com.example.cardbank.service.ClienteService;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    public void testCriarCliente() {
        Cliente cliente = new Cliente();
        cliente.setCpf("12345678900");
        cliente.setEmail("teste@exemplo.com");
        cliente.setTelefone("11999999999");

        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua Exemplo");
        endereco.setNumero("100");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("01000000");
        cliente.setEndereco(endereco);

        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> {
            Cliente c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        Cliente result = clienteService.criarCliente(cliente);
        assertNotNull(result.getId());
        assertEquals("12345678900", result.getCpf());
    }

    @Test
    public void testListarClientes() {
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setCpf("12345678900");
        cliente1.setEmail("teste1@exemplo.com");
        cliente1.setTelefone("11999999999");

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setCpf("98765432100");
        cliente2.setEmail("teste2@exemplo.com");
        cliente2.setTelefone("11888888888");

        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente1, cliente2));

        List<Cliente> clientes = clienteService.listarClientes();
        assertEquals(2, clientes.size());
    }

    @Test
    public void testBuscarPorCpf() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCpf("12345678900");
        cliente.setEmail("teste@exemplo.com");
        cliente.setTelefone("11999999999");

        when(clienteRepository.findByCpf("12345678900")).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = clienteService.buscarPorCpf("12345678900");
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    public void testInativarCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCpf("12345678900");
        cliente.setEmail("teste@exemplo.com");
        cliente.setTelefone("11999999999");
        cliente.setStatus(StatusCliente.ATIVO);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        Cliente result = clienteService.inativarCliente(1L);
        assertEquals(StatusCliente.INATIVO, result.getStatus());
    }
}