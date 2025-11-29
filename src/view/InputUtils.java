package view;

import java.math.BigDecimal;
import java.util.Scanner;

// alguns tratamentos de erro

public class InputUtils {

    // Método para ler inteiros com segurança
    public static int lerInt(Scanner sc, String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                String entrada = sc.nextLine();
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Por favor, digite um número inteiro válido.");
            }
        }
    }

    // Método para ler Long (IDs) com segurança
    public static Long lerLong(Scanner sc, String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                String entrada = sc.nextLine();
                return Long.parseLong(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Por favor, digite um ID numérico válido.");
            }
        }
    }

    // Método para ler BigDecimal (Preços) com segurança
    public static BigDecimal lerBigDecimal(Scanner sc, String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                String entrada = sc.nextLine().replace(",", "."); // Aceita vírgula ou ponto
                return new BigDecimal(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Digite um valor monetário válido (ex: 29.90).");
            }
        }
    }
}
