package com.ada.aulas.aula01;

/**
 * Estudo de Caso (Aula 01): Como costumamos testar manualmente sem um framework de testes.
 *
 * <p>Pontos para demonstrar em aula:
 * <ol>
 *   <li>Exige inspeção visual humana no console a cada execução.</li>
 *   <li>Não falha automaticamente no build caso alguém quebre uma regra.</li>
 *   <li>Casos de borda (como exatamente R$ 500,00) passam despercebidos facilmente.</li>
 * </ol>
 */
public class TesteManualMain {

    public static void main(String[] args) {
        CalculadoraDesconto calculadora = new CalculadoraDesconto();

        System.out.println("=== TESTES MANUAIS: CALCULADORA DE DESCONTO ===");

        // Cenário 1: Compra abaixo de R$ 100 (Cliente comum) -> Esperado: 80.0
        double resultado1 = calculadora.calcularValorFinal(80.0, false);
        System.out.println("Compra R$ 80,00 (Comum)  | Esperado: 80.0  | Obtido: " + resultado1);

        // Cenário 2: Compra entre R$ 100 e R$ 499,99 (Cliente comum, 5%) -> Esperado: 190.0
        double resultado2 = calculadora.calcularValorFinal(200.0, false);
        System.out.println("Compra R$ 200,00 (Comum) | Esperado: 190.0 | Obtido: " + resultado2);

        // Cenário 3: Compra acima de R$ 500 (Cliente comum, 10%) -> Esperado: 540.0
        double resultado3 = calculadora.calcularValorFinal(600.0, false);
        System.out.println("Compra R$ 600,00 (Comum) | Esperado: 540.0 | Obtido: " + resultado3);

        // Cenário 4: Compra acima de R$ 500 (Cliente VIP, 10% + 5% = 15%) -> Esperado: 510.0
        double resultado4 = calculadora.calcularValorFinal(600.0, true);
        System.out.println("Compra R$ 600,00 (VIP)   | Esperado: 510.0 | Obtido: " + resultado4);

        // Cenário 5 (Caso de borda onde o bug aparece): Compra de EXATAMENTE R$ 500,00 (Comum, 10%) -> Esperado: 450.0
        double resultado5 = calculadora.calcularValorFinal(500.0, false);
        System.out.println("Compra R$ 500,00 (Comum) | Esperado: 450.0 | Obtido: " + resultado5);
    }
}
