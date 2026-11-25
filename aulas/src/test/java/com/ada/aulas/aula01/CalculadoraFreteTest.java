package com.ada.aulas.aula01;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Aula 01 - Gabarito: Testes da Calculadora de Frete")
class CalculadoraFreteTest {

    private CalculadoraFrete calculadora;

    @BeforeEach
    void setUp() {
        calculadora = new CalculadoraFrete();
    }

    @Test
    @DisplayName("Deve retornar frete grátis (R$ 0,00) quando a compra for a partir de R$ 300,00")
    void deveRetornarFreteGratisQuandoCompraForAPartirDeTrezentosReais() {
        double freteNoLimite = calculadora.calcularFrete(300.0, 5.0, "NORDESTE");
        double freteAcima = calculadora.calcularFrete(450.0, 10.0, "NORTE");

        assertEquals(0.0, freteNoLimite, 0.001);
        assertEquals(0.0, freteAcima, 0.001);
    }

    @Test
    @DisplayName("Deve calcular frete corretamente para a região SUDESTE (R$ 15 + R$ 2/kg)")
    void deveCalcularFreteParaRegiaoSudeste() {
        double frete = calculadora.calcularFrete(150.0, 3.0, "SUDESTE");

        assertEquals(21.0, frete, 0.001);
    }

    @Test
    @DisplayName("Deve calcular frete corretamente para SUL e CENTRO_OESTE (R$ 25 + R$ 2/kg)")
    void deveCalcularFreteParaRegiaoSulECentroOeste() {
        assertEquals(29.0, calculadora.calcularFrete(100.0, 2.0, "SUL"), 0.001);
        assertEquals(29.0, calculadora.calcularFrete(100.0, 2.0, "CENTRO_OESTE"), 0.001);
    }

    @Test
    @DisplayName("Deve calcular frete corretamente para NORDESTE e NORTE (R$ 35 + R$ 2/kg)")
    void deveCalcularFreteParaRegiaoNordesteENorte() {
        assertEquals(45.0, calculadora.calcularFrete(299.99, 5.0, "NORDESTE"), 0.001);
        assertEquals(45.0, calculadora.calcularFrete(299.99, 5.0, "NORTE"), 0.001);
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor da compra ou peso forem inválidos")
    void deveLancarExcecaoParaValorCompraOuPesoInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularFrete(0.0, 2.0, "SUDESTE"));
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularFrete(100.0, -1.0, "SUDESTE"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a região for nula, vazia ou inexistente")
    void deveLancarExcecaoParaRegiaoInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularFrete(100.0, 2.0, null));
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularFrete(100.0, 2.0, "   "));
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularFrete(100.0, 2.0, "EXTERIOR"));
    }
}
