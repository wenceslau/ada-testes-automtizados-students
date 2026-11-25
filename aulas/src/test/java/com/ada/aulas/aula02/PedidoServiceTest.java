package com.ada.aulas.aula02;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Aula 02 - Testes Unitários do PedidoService (Gabarito dos Exercícios)")
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private NotificacaoService notificacaoService;


    @Test
    @DisplayName("Deve criar pedido confirmado, abater estoque e enviar e-mail de confirmação")
    void deveCriarPedidoComSucesso() {
        // 1- Arrange (preparar o cenário e o comportamento do mock)
        Produto produto = new Produto(1L, "Notebook", "NOTE-001", 4500.0, 10, true);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // Any pedido.class usa a propria instancia criada do objet pedido dentro do metodo cadatrar
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(100L);
           /// p.setQuantidade(3);
            return p;
        });

        //2- Act (execução do método cadastrar da classe ProdutoServie)
       Pedido pedidoCriado =  pedidoService.criarPedido("cliente@ada.com", 1L, 1);

       // 3- Asserts
        assertNotNull(pedidoCriado.getId());
        assertEquals(100L, pedidoCriado.getId());
        assertEquals(StatusPedido.CONFIRMADO, pedidoCriado.getStatus());
        assertEquals(1, pedidoCriado.getQuantidade());
        assertEquals(9, produto.getQuantidadeEstoque());

        verify(notificacaoService).enviarConfirmacaoPedido("cliente@ada.com", pedidoCriado);

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(pedidoCaptor.capture());

        assertEquals("cliente@ada.com", pedidoCaptor.getValue().getEmailCliente());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando o produto do pedido não existir")
    void deveLancarExcecaoQuandoProdutoNaoExistir() {

        // Arrange
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () ->pedidoService.criarPedido("cliente@ada.com",99L, 1)
        );

        verifyNoInteractions(pedidoRepository);
        verifyNoInteractions(notificacaoService);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando o produto estiver inativo e não deve salvar nem notificar")
    void deveLancarExcecaoQuandoProdutoEstiverInativo() {

        Produto produtoInativo = new Produto(2L, "Monitor", "MON-001", 500.00, 5, false);
        when(produtoRepository.findById(2L)).thenReturn(Optional.of(produtoInativo));

        assertThrows(
                RegraNegocioException.class,
                () -> pedidoService.criarPedido("cliente@ada.com", 2L, 1)
        );

        verify(produtoRepository, never()).save(any());
        verifyNoInteractions(pedidoRepository);
        verifyNoInteractions(notificacaoService);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando o estoque for insuficiente e não deve enviar e-mail")
    void deveLancarExcecaoQuandoEstoqueForInsuficiente() {

    }

    @Test
    @DisplayName("Deve cancelar um pedido confirmado, devolver os itens ao estoque e enviar notificação de cancelamento")
    void deveCancelarPedidoConfirmadoEDevolverEstoque() {

    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar cancelar um pedido que já foi enviado")
    void naoDeveCancelarPedidoJaEnviado() {

    }
}
