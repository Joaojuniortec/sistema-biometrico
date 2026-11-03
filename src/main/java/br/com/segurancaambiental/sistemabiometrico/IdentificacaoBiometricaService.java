
package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class IdentificacaoBiometricaService {

    private final UsuarioRepository usuarioRepository;
    private final ServicoBiometriaFacial servicoBiometriaFacial;

    public IdentificacaoBiometricaService(
            UsuarioRepository usuarioRepository,
            ServicoBiometriaFacial servicoBiometriaFacial) {

        this.usuarioRepository = usuarioRepository;
        this.servicoBiometriaFacial = servicoBiometriaFacial;
    }

    public ResultadoIdentificacao identificar(Path caminhoFoto) {

        float[] vetorCapturado =
                servicoBiometriaFacial.extrairCaracteristicas(caminhoFoto);

        List<Usuario> usuarios =
                usuarioRepository.findByCaracteristicasFaciaisIsNotNull();

        Usuario melhorUsuario = null;
        double maiorSimilaridade = -1;

        for (Usuario usuario : usuarios) {

            String dadosFaciais = usuario.getCaracteristicasFaciais();

            if (dadosFaciais == null || dadosFaciais.isBlank()) {
                continue;
            }

            try {

                String[] valores = dadosFaciais.split(",");

                if (valores.length != 128) {
                    continue;
                }

                float[] vetorCadastrado = new float[128];

                for (int i = 0; i < 128; i++) {
                    vetorCadastrado[i] = Float.parseFloat(valores[i]);
                }

                double similaridade = calcularSimilaridade(
                        vetorCadastrado,
                        vetorCapturado
                );

                if (similaridade > maiorSimilaridade) {
                    maiorSimilaridade = similaridade;
                    melhorUsuario = usuario;
                }

            } catch (NumberFormatException e) {
                continue;
            }
        }

        if (melhorUsuario == null) {
            return null;
        }

        return new ResultadoIdentificacao(
                melhorUsuario.getId(),
                melhorUsuario.getNome(),
                melhorUsuario.getUsername(),
                maiorSimilaridade
        );
    }

    private double calcularSimilaridade(
            float[] original,
            float[] capturado) {

        double produto = 0;
        double normaOriginal = 0;
        double normaCapturado = 0;

        for (int i = 0; i < 128; i++) {

            produto += original[i] * capturado[i];

            normaOriginal += original[i] * original[i];
            normaCapturado += capturado[i] * capturado[i];
        }

        if (normaOriginal == 0 || normaCapturado == 0) {
            return -1;
        }

        return produto /
                (Math.sqrt(normaOriginal) *
                 Math.sqrt(normaCapturado));
    }

    public record ResultadoIdentificacao(
            Long id,
            String nome,
            String username,
            double similaridade
    ) {}
}
