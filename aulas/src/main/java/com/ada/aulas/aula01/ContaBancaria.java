package com.ada.aulas.aula01;

/**
 * Exercício 3 (Aula 01): Operações básicas de uma Conta Bancária com estado.
 */
public class ContaBancaria {

    private final String titular;
    private double saldo;

    public ContaBancaria(String titular, double saldoInicial) {
        if (titular == null || titular.isBlank()) {
            throw new IllegalArgumentException("O titular da conta é obrigatório.");
        }
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
        }
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        }
        this.saldo += valor;
    }

    public void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser maior que zero.");
        }
        if (valor > this.saldo) {
            throw new IllegalStateException("Saldo insuficiente para realizar o saque.");
        }
        this.saldo -= valor;
    }

    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }
}
