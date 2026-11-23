package com.ada.aulas.aula01;

/**
 * Exercício 2 (Aula 01): Cálculo de frete por região e regra de frete grátis.
 *
 * <p>Regras de negócio:
 * <ul>
 *   <li>Valor da compra ou peso menores ou iguais a zero devem lançar {@link IllegalArgumentException}.</li>
 *   <li>Região nula ou desconhecida deve lançar {@link IllegalArgumentException}.</li>
 *   <li>Compras a partir de R$ 300,00 têm <b>frete grátis (R$ 0,00)</b> para qualquer região.</li>
 *   <li>Taxa fixa por região + R$ 2,00 por kg:
 *     <ul>
 *       <li>SUDESTE: R$ 15,00 + (R$ 2,00 * pesoKg)</li>
 *       <li>SUL / CENTRO_OESTE: R$ 25,00 + (R$ 2,00 * pesoKg)</li>
 *       <li>NORDESTE / NORTE: R$ 35,00 + (R$ 2,00 * pesoKg)</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public class CalculadoraFrete {

    public double calcularFrete(double valorCompra, double pesoKg, String regiao) {
        if (valorCompra <= 0) {
            throw new IllegalArgumentException("O valor da compra deve ser maior que zero.");
        }
        if (pesoKg <= 0) {
            throw new IllegalArgumentException("O peso deve ser maior que zero.");
        }
        if (regiao == null || regiao.isBlank()) {
            throw new IllegalArgumentException("A região deve ser informada.");
        }

        if (valorCompra >= 300.0) {
            return 0.0;
        }

        double taxaBase = switch (regiao.trim().toUpperCase()) {
            case "SUDESTE" -> 15.0;
            case "SUL", "CENTRO_OESTE" -> 25.0;
            case "NORDESTE", "NORTE" -> 35.0;
            default -> throw new IllegalArgumentException("Região inválida: " + regiao);
        };

        return taxaBase + (2.0 * pesoKg);
    }
}
