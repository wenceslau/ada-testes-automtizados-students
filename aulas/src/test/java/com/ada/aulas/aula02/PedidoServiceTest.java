package com.ada.aulas.aula02;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Aula 02 - Testes Unitários do PedidoService (Gabarito dos Exercícios)")
class PedidoServiceTest {


    @Test
    @DisplayName("Deve criar pedido confirmado, abater estoque e enviar e-mail de confirmação")
    void deveCriarPedidoComSucesso() {

    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando o produto do pedido não existir")
    void deveLancarExcecaoQuandoProdutoNaoExistir() {

    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando o produto estiver inativo e não deve salvar nem notificar")
    void deveLancarExcecaoQuandoProdutoEstiverInativo() {

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
