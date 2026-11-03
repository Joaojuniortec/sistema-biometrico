
package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/admin/biometria")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBiometriaController {

    private final CadastroBiometriaService cadastroBiometriaService;

    public AdminBiometriaController(
            CadastroBiometriaService cadastroBiometriaService) {

        this.cadastroBiometriaService = cadastroBiometriaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String cadastrar(
            @RequestParam("username") String username,
            @RequestParam("foto") MultipartFile foto)
            throws IOException {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o nome de usuário."
            );
        }

        if (foto == null || foto.isEmpty()) {
            throw new IllegalArgumentException(
                    "Selecione uma fotografia."
            );
        }

        String tipo = foto.getContentType();

        if (!"image/jpeg".equals(tipo)
                && !"image/png".equals(tipo)) {

            throw new IllegalArgumentException(
                    "Envie uma imagem JPG ou PNG."
            );
        }

        String extensao = "image/png".equals(tipo)
                ? ".png"
                : ".jpg";

        Path arquivoTemporario = Files.createTempFile(
                "biometria-",
                extensao
        );

        try {
            foto.transferTo(arquivoTemporario);

            return cadastroBiometriaService.cadastrarBiometria(
                    username,
                    arquivoTemporario
            );

        } finally {
            Files.deleteIfExists(arquivoTemporario);
        }
    }
}
