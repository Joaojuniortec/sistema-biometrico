
package br.com.segurancaambiental.sistemabiometrico;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reconhecimento-facial")
@PreAuthorize("isAuthenticated()")
public class ReconhecimentoFacialController {

    private final ComparacaoBiometriaService comparacaoService;
    private final IdentificacaoBiometricaService identificacaoService;

    public ReconhecimentoFacialController(
            ComparacaoBiometriaService comparacaoService,
            IdentificacaoBiometricaService identificacaoService) {

        this.comparacaoService = comparacaoService;
        this.identificacaoService = identificacaoService;
    }

    @PostMapping(
        value = "/identificar",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResultadoTesteIdentificacao identificar(
            @RequestParam("foto") MultipartFile foto) throws IOException {

        if (foto.isEmpty()) {
            throw new IllegalArgumentException(
                    "Nenhuma fotografia foi enviada."
            );
        }

        String tipo = foto.getContentType();

        if (tipo == null ||
            !(tipo.equals("image/jpeg") || tipo.equals("image/png"))) {

            throw new IllegalArgumentException(
                    "Envie uma fotografia JPG ou PNG."
            );
        }

        Path arquivoTemporario = Files.createTempFile(
                "biometria-",
                tipo.equals("image/png") ? ".png" : ".jpg"
        );

        try {
            Files.copy(
                    foto.getInputStream(),
                    arquivoTemporario,
                    StandardCopyOption.REPLACE_EXISTING
            );

            var resultado =
                    identificacaoService.identificar(arquivoTemporario);

            if (resultado == null) {
                return new ResultadoTesteIdentificacao(
                        false,
                        null,
                        null,
                        -1,
                        "Nenhuma biometria cadastrada foi encontrada."
                );
            }

            return new ResultadoTesteIdentificacao(
                    true,
                    resultado.nome(),
                    resultado.username(),
                    resultado.similaridade(),
                    "Possível usuário identificado. Acesso ainda não liberado."
            );

        } finally {
            Files.deleteIfExists(arquivoTemporario);
        }
    }

    @PostMapping(
        value = "/comparar",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ComparacaoBiometriaResultado comparar(
            @RequestParam("foto") MultipartFile foto,
            java.security.Principal principal) throws IOException {

        if (foto.isEmpty()) {
            throw new IllegalArgumentException(
                    "Nenhuma fotografia foi enviada."
            );
        }

        String tipo = foto.getContentType();

        if (tipo == null ||
            !(tipo.equals("image/jpeg") || tipo.equals("image/png"))) {

            throw new IllegalArgumentException(
                    "Envie uma fotografia JPG ou PNG."
            );
        }

        Path arquivoTemporario = Files.createTempFile(
                "biometria-",
                tipo.equals("image/png") ? ".png" : ".jpg"
        );

        try {
            Files.copy(
                    foto.getInputStream(),
                    arquivoTemporario,
                    StandardCopyOption.REPLACE_EXISTING
            );

            double similaridade = comparacaoService.compararBiometria(
                    principal.getName(),
                    arquivoTemporario
            );

            return new ComparacaoBiometriaResultado(
                    similaridade,
                    "Comparação facial realizada com sucesso!"
            );

        } finally {
            Files.deleteIfExists(arquivoTemporario);
        }
    }

    public record ResultadoTesteIdentificacao(
            boolean identificado,
            String nome,
            String username,
            double similaridade,
            String mensagem
    ) {}

    public record ComparacaoBiometriaResultado(
            double similaridade,
            String mensagem
    ) {}
}
