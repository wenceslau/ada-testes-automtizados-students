package com.ada.aulas.aula01;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Aula 01 - Testes da Calculadora de Desconto")
class CalculadoraDescontoTest {

    private CalculadoraDesconto calculadora;

    @BeforeEach
    void setUp() {
        // Executado antes de CADA método @Test para garantir isolamento entre os testes
        calculadora = new CalculadoraDesconto();
    }

    @Test
    @DisplayName("Não deve aplicar desconto para compra abaixo de R$ 100 de cliente comum")
    void naoDeveAplicarDescontoParaCompraAbaixoDeCemClienteComum() {
        // 1. Arrange (Preparar)
        double valorCompra = 80.0;
        boolean clienteVip = false;

        // 2. Act (Agir)
        double valorFinal = calculadora.calcularValorFinal(valorCompra, clienteVip);

        // 3. Assert (Verificar)
        assertEquals(80.0, valorFinal, 0.001);
    }

    @Test
    @DisplayName("Deve aplicar 5% de desconto para compra a partir de R$ 100 até R$ 499,99 de cliente comum")
    void deveAplicarCincoPorCentoParaCompraEntreCemEQuatrocentosENoventaENove() {
        // Arrange
        double valorCompra = 200.0;

        // Act
        double valorFinal = calculadora.calcularValorFinal(valorCompra, false);

        // Assert
        assertEquals(190.0, valorFinal, 0.001);
    }

    @Test
    @DisplayName("Deve aplicar 5% de desconto no limite exato de R$ 100,00 para cliente comum")
    void deveAplicarCincoPorCentoNoLimiteExatoDeCemReais() {
        // Arrange & Act
        double valorFinal = calculadora.calcularValorFinal(100.0, false);

        // Assert
        assertEquals(95.0, valorFinal, 0.001);
    }

    @Test
    @DisplayName("Deve aplicar 10% de desconto para compra acima de R$ 500 de cliente comum")
    void deveAplicarDezPorCentoParaCompraAcimaDeQuinhentosClienteComum() {
        // Arrange & Act
        double valorFinal = calculadora.calcularValorFinal(600.0, false);

        // Assert
        assertEquals(540.0, valorFinal, 0.001);
    }

    @Test
    @DisplayName("Deve aplicar 10% de desconto no limite exato de R$ 500,00 para cliente comum")
    void deveAplicarDezPorCentoNoLimiteExatoDeQuinhentosReais() {
        // Arrange & Act
        // Dica para o live coding: se na CalculadoraDesconto estiver 'valorCompra > 500.0', este teste FALHA (retorna 475.0)!
        double valorFinal = calculadora.calcularValorFinal(500.0, false);

        // Assert
        assertEquals(450.0, valorFinal, 0.001);
    }

    @Test
    @DisplayName("Deve somar 5% de bônus VIP em compra abaixo de R$ 100 (total 5%)")
    void deveAplicarCincoPorCentoParaClienteVipEmCompraAbaixoDeCem() {
        // Arrange & Act
        double valorFinal = calculadora.calcularValorFinal(80.0, true);

        // Assert
        assertEquals(76.0, valorFinal, 0.001);
    }

    @Test
    @DisplayName("Deve somar 5% de bônus VIP em compra a partir de R$ 500 (total 15%)")
    void deveAplicarQuinzePorCentoParaClienteVipEmCompraAcimaDeQuinhentos() {
        // Arrange & Act
        double valorFinal = calculadora.calcularValorFinal(600.0, true);

        // Assert
        assertEquals(510.0, valorFinal, 0.001);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o valor da compra for zero")
    void deveLancarExcecaoQuandoValorCompraForZero() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculadora.calcularValorFinal(0.0, false)
        );

        assertEquals("O valor da compra deve ser maior que zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o valor da compra for negativo")
    void deveLancarExcecaoQuandoValorCompraForNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculadora.calcularValorFinal(-50.0, true)
        );
    }
}
