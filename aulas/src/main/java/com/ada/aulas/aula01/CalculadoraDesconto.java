package com.ada.aulas.aula01;

/**
 * Classe responsável pelo cálculo do valor final de uma compra após aplicação de descontos.
 *
 * <p>Regras de negócio especificadas:
 * <ul>
 *   <li>Valores menores ou iguais a zero devem lançar {@link IllegalArgumentException}.</li>
 *   <li>Compras abaixo de R$ 100,00: 0% de desconto base.</li>
 *   <li>Compras de R$ 100,00 até R$ 499,99: 5% de desconto base.</li>
 *   <li>Compras a partir de R$ 500,00: 10% de desconto base.</li>
 *   <li>Clientes VIP recebem +5% de desconto adicional sobre o valor da compra.</li>
 * </ul>
 */
public class CalculadoraDesconto {

    public double calcularValorFinal(double valorCompra, boolean clienteVip) {
        if (valorCompra <= 0) {
            throw new IllegalArgumentException("O valor da compra deve ser maior que zero.");
        }

        double percentualDesconto = 0.0;

        // Dica para o live coding: comece com 'valorCompra > 500.0' para mostrar o teste de R$ 500,00 falhando,
        // e depois corrija para 'valorCompra >= 500.0' diante da turma!
        if (valorCompra >= 500.0) {
            percentualDesconto = 0.10;
        } else if (valorCompra >= 100.0) {
            percentualDesconto = 0.05;
        }

        if (clienteVip) {
            percentualDesconto += 0.05;
        }

        return valorCompra * (1.0 - percentualDesconto);
    }
}
