
package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.Locale;

@Service
public class CadastroBiometriaService {

    private final UsuarioRepository usuarioRepository;
    private final ServicoBiometriaFacial servicoBiometriaFacial;

    public CadastroBiometriaService(
            UsuarioRepository usuarioRepository,
            ServicoBiometriaFacial servicoBiometriaFacial) {

        this.usuarioRepository = usuarioRepository;
        this.servicoBiometriaFacial = servicoBiometriaFacial;
    }

    @Transactional
    public String cadastrarBiometria(
            String username,
            Path caminhoFoto) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o nome de usuário."
            );
        }

        Usuario usuario = usuarioRepository
                .findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não encontrado."
                ));

        float[] caracteristicas =
                servicoBiometriaFacial.extrairCaracteristicas(
                        caminhoFoto
                );

        StringBuilder vetorFacial = new StringBuilder();

        for (int i = 0; i < caracteristicas.length; i++) {

            if (i > 0) {
                vetorFacial.append(",");
            }

            vetorFacial.append(
                    String.format(
                            Locale.ROOT,
                            "%.9f",
                            caracteristicas[i]
                    )
            );
        }

        usuario.setCaracteristicasFaciais(
                vetorFacial.toString()
        );

        usuarioRepository.save(usuario);

        return "Biometria facial cadastrada com sucesso para o usuário "
                + usuario.getUsername() + ".";
    }
}
