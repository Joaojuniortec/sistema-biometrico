package br.com.segurancaambiental.sistemabiometrico;

import jakarta.annotation.PostConstruct;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ReconhecimentoFacialService {

    @PostConstruct
    public void verificarOpenCV() {
        try {
            OpenCV.loadLocally();
            System.out.println(
                "[BIOMETRIA] OpenCV carregado com sucesso!"
            );
        } catch (Exception | LinkageError erro) {
            System.err.println(
                "[BIOMETRIA] Falha ao carregar OpenCV: "
                + erro.getMessage()
            );
        }
    }

    public void testarFotoReferencia() {
        Path caminhoFoto = Path.of(
            "imagens-teste",
            "rosto-referencia.jpg"
        );

        if (!Files.exists(caminhoFoto)) {
            System.err.println(
                "[BIOMETRIA] Foto não encontrada: "
                + caminhoFoto.toAbsolutePath()
            );
            return;
        }

        Mat imagem = Imgcodecs.imread(
            caminhoFoto.toAbsolutePath().toString()
        );

        try {
            if (imagem.empty()) {
                System.err.println(
                    "[BIOMETRIA] Não foi possível abrir a foto."
                );
                return;
            }

            System.out.println(
                "[BIOMETRIA] Foto aberta com sucesso! "
                + "Largura: " + imagem.cols()
                + ", altura: " + imagem.rows()
            );

            System.out.println(
                "[BIOMETRIA] Próximo passo: detectar "
                + "o rosto usando o modelo YuNet."
            );

        } finally {
            imagem.release();
        }
    }
}