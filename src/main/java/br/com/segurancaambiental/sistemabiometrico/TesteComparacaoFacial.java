
package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class TesteComparacaoFacial implements CommandLineRunner {

    private final ComparacaoBiometriaService comparacaoBiometriaService;

    public TesteComparacaoFacial(
            ComparacaoBiometriaService comparacaoBiometriaService) {
        this.comparacaoBiometriaService = comparacaoBiometriaService;
    }

    @Override
    public void run(String... args) {

        System.out.println("\n==================================");
        System.out.println(" TESTE DE COMPARAÇÃO FACIAL");
        System.out.println("==================================");

        String username = "joaocassioj@gmail.com";

        String[] fotografias = {
                "rosto-comparacao.jpg",
                "rosto-comparacao (2).jpg",
                "rosto-comparacao(3).jpg",
                "rosto-pessoa-diferente.jpg"
        };

        for (String fotografia : fotografias) {

            try {
                System.out.println("\nAnalisando: " + fotografia);

                double resultado =
                        comparacaoBiometriaService.compararBiometria(
                                username,
                                Path.of("imagens-teste", fotografia)
                        );

                System.out.println(
                        "Similaridade facial: " + resultado
                );

            } catch (Exception e) {
                System.err.println(
                        "Erro ao analisar " + fotografia + ": "
                                + e.getMessage()
                );
            }
        }

        System.out.println("\n==================================");
        System.out.println(" TESTES CONCLUÍDOS");
        System.out.println("==================================");
    }
}
