package com.ada.aulas.aula02;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Suíte de testes unitários da camada de serviço para {@link ProdutoService} (Live Coding do Professor).
 *
 * <p>Observe o uso de {@code @ExtendWith(MockitoExtension.class)} em vez de {@code @SpringBootTest}:
 * não subimos o contexto completo do Spring nem o banco de dados, mantendo os testes na casa dos milissegundos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Aula 02 - Testes Unitários do ProdutoService com Mockito")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    @DisplayName("Deve cadastrar um produto com sucesso quando os dados forem válidos e o SKU for inédito")
    void deveCadastrarProdutoComSucesso() {
        // 1. Arrange (Preparar o cenário e o comportamento do Mock)
        Produto novoProduto = new Produto(null, "Notebook Gamer", "NOTE-001", 4500.0, 10, true);
        Produto produtoSalvoNoBanco = new Produto(1L, "Notebook Gamer", "NOTE-001", 4500.0, 10, true);

        when(produtoRepository.existsBySku("NOTE-001")).thenReturn(false);
        when(produtoRepository.save(novoProduto)).thenReturn(produtoSalvoNoBanco);

        // 2. Act (Executar a ação no Service)
        Produto resultado = produtoService.cadastrar(novoProduto);

        // 3. Assert (Verificar o retorno e o comportamento no Mock)
        assertNotNull(resultado.getId());
        assertEquals(1L, resultado.getId());
        assertEquals("NOTE-001", resultado.getSku());

        verify(produtoRepository).existsBySku("NOTE-001");
        verify(produtoRepository).save(novoProduto);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar cadastrar produto com SKU já existente e não deve salvar")
    void deveLancarExcecaoQuandoSkuJaExistir() {
        // Arrange
        Produto produtoDuplicado = new Produto(null, "Mouse Sem Fio", "MOUSE-99", 150.0, 5, true);
        when(produtoRepository.existsBySku("MOUSE-99")).thenReturn(true);

        // Act & Assert
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> produtoService.cadastrar(produtoDuplicado)
        );

        assertEquals("Já existe um produto cadastrado com o SKU: MOUSE-99", exception.getMessage());

        // Garantimos que o método save() JAMAIS foi chamado no banco após a falha!
        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o preço do produto for menor ou igual a zero")
    void deveLancarExcecaoQuandoPrecoForInvalido() {
        // Arrange
        Produto produtoPrecoZero = new Produto(null, "Teclado Mecânico", "TEC-01", 0.0, 10, true);

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> produtoService.cadastrar(produtoPrecoZero)
        );

        verify(produtoRepository, never()).existsBySku(any());
        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar o produto quando buscar por um ID existente")
    void deveBuscarProdutoPorIdExistente() {
        // Arrange
        Produto produtoExistente = new Produto(10L, "Monitor 27\"", "MON-27", 1200.0, 8, true);
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produtoExistente));

        // Act
        Produto encontrado = produtoService.buscarPorId(10L);

        // Assert
        assertEquals(10L, encontrado.getId());
        assertEquals("Monitor 27\"", encontrado.getNome());
        verify(produtoRepository).findById(10L);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando buscar por um ID inexistente")
    void deveLancarExcecaoQuandoBuscarPorIdInexistente() {
        // Arrange
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> produtoService.buscarPorId(999L)
        );

        assertEquals("Produto não encontrado com ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Deve baixar o estoque e salvar o produto quando houver saldo suficiente e produto estiver ativo")
    void deveBaixarEstoqueComSucesso() {
        // Arrange
        Produto produto = new Produto(5L, "Headset USB", "HEAD-05", 300.0, 10, true);
        when(produtoRepository.findById(5L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(produto)).thenReturn(produto);

        // Act
        Produto atualizado = produtoService.baixarEstoque(5L, 3);

        // Assert
        assertEquals(7, atualizado.getQuantidadeEstoque());
        verify(produtoRepository).save(produto);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar baixar estoque acima do saldo disponível")
    void deveLancarExcecaoAoBaixarEstoqueInsuficiente() {
        // Arrange
        Produto produto = new Produto(5L, "Headset USB", "HEAD-05", 300.0, 2, true);
        when(produtoRepository.findById(5L)).thenReturn(Optional.of(produto));

        // Act & Assert
        assertThrows(
                RegraNegocioException.class,
                () -> produtoService.baixarEstoque(5L, 5)
        );

        assertEquals(2, produto.getQuantidadeEstoque());
        verify(produtoRepository, never()).save(any());
    }
}
