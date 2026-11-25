package com.ada.aulas.aula01;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Aula 01 - Gabarito: Testes da Conta Bancária")
class ContaBancariaTest {

    private ContaBancaria conta;

    @BeforeEach
    void setUp() {
        conta = new ContaBancaria("Maria Silva", 1000.0);
    }

    @Test
    @DisplayName("Deve aumentar o saldo ao realizar um depósito válido")
    void deveAumentarSaldoAoDepositarValorValido() {
        // Act
        conta.depositar(250.0);

        // Assert
        assertEquals(1250.0, conta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Deve diminuir o saldo ao realizar um saque dentro do limite disponível")
    void deveDiminuirSaldoAoSacarValorDentroDoSaldo() {
        // Act
        conta.sacar(400.0);

        // Assert
        assertEquals(600.0, conta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Deve permitir sacar a totalidade do saldo zerando a conta")
    void devePermitirSacarTodoOSaldo() {
        // Act
        conta.sacar(1000.0);

        // Assert
        assertEquals(0.0, conta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao tentar sacar valor maior que o saldo")
    void deveLancarExcecaoAoSacarValorAcimaDoSaldo() {
        assertThrows(IllegalStateException.class, () -> conta.sacar(1000.01));
        assertEquals(1000.0, conta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para depósito ou saque com valor zero ou negativo")
    void deveLancarExcecaoParaDepositoOuSaqueInvalido() {
        assertThrows(IllegalArgumentException.class, () -> conta.depositar(0.0));
        assertThrows(IllegalArgumentException.class, () -> conta.depositar(-10.0));
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(0.0));
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(-50.0));
    }
}
