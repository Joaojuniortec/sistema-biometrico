
package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class ComparacaoBiometriaService {

    private final UsuarioRepository usuarioRepository;
    private final ServicoBiometriaFacial servicoBiometriaFacial;

    public ComparacaoBiometriaService(
            UsuarioRepository usuarioRepository,
            ServicoBiometriaFacial servicoBiometriaFacial) {

        this.usuarioRepository = usuarioRepository;
        this.servicoBiometriaFacial = servicoBiometriaFacial;
    }

    public double compararBiometria(
            String username,
            Path caminhoFoto) {

        Usuario usuario = usuarioRepository
                .findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não encontrado."
                ));

        String vetorCadastrado = usuario.getCaracteristicasFaciais();

        if (vetorCadastrado == null || vetorCadastrado.isBlank()) {
            throw new IllegalArgumentException(
                    "Este usuário não possui biometria cadastrada."
            );
        }

        String[] valores = vetorCadastrado.split(",");

        if (valores.length != 128) {
            throw new IllegalStateException(
                    "O vetor facial cadastrado está inválido."
            );
        }

        float[] vetorOriginal = new float[128];

        for (int i = 0; i < 128; i++) {
            vetorOriginal[i] = Float.parseFloat(valores[i]);
        }

        float[] vetorNovo =
                servicoBiometriaFacial.extrairCaracteristicas(caminhoFoto);

        double produto = 0;
        double normaOriginal = 0;
        double normaNovo = 0;

        for (int i = 0; i < 128; i++) {
            produto += vetorOriginal[i] * vetorNovo[i];

            normaOriginal += vetorOriginal[i] * vetorOriginal[i];
            normaNovo += vetorNovo[i] * vetorNovo[i];
        }

        if (normaOriginal == 0 || normaNovo == 0) {
            throw new IllegalStateException(
                    "Não foi possível comparar os vetores faciais."
            );
        }

        return produto /
                (Math.sqrt(normaOriginal) * Math.sqrt(normaNovo));
    }
}
