
package br.com.segurancaambiental.sistemabiometrico;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.FaceDetectorYN;
import org.opencv.objdetect.FaceRecognizerSF;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class TesteModelosFaciais implements CommandLineRunner {

    private static final String MODELO_YUNET =
        "src/main/resources/models/face_detection_yunet_2023mar.onnx";

    private static final String MODELO_SFACE =
        "src/main/resources/models/face_recognition_sface_2021dec.onnx";

    private static final String FOTO_REFERENCIA =
        "imagens-teste/rosto-referencia.jpg";

    private static final String FOTO_COMPARACAO =
        "imagens-teste/rosto-comparacao.jpg";

    @Override
    public void run(String... args) {
        System.out.println("[BIOMETRIA] Iniciando testes faciais...");

        try {
            OpenCV.loadLocally();

            testarModelo("YuNet", MODELO_YUNET);
            testarModelo("SFace", MODELO_SFACE);

            FaceDetectorYN detector = FaceDetectorYN.create(
                Path.of(MODELO_YUNET).toAbsolutePath().toString(),
                "",
                new Size(320, 320),
                0.8f,
                0.3f,
                5000
            );

            FaceRecognizerSF reconhecedor = FaceRecognizerSF.create(
                Path.of(MODELO_SFACE).toAbsolutePath().toString(),
                ""
            );

            compararFotos(detector, reconhecedor);

        } catch (Exception | LinkageError erro) {
            System.err.println(
                "[BIOMETRIA] Erro: " + erro.getMessage()
            );
            erro.printStackTrace();
        }
    }

    private void testarModelo(String nome, String caminho) {
        Path arquivo = Path.of(caminho).toAbsolutePath();

        try {
            if (!Files.exists(arquivo)) {
                System.err.println(
                    "[BIOMETRIA] Modelo " + nome
                    + " não encontrado: " + arquivo
                );
                return;
            }

            Net rede = Dnn.readNetFromONNX(arquivo.toString());

            if (rede.empty()) {
                System.err.println(
                    "[BIOMETRIA] Modelo " + nome + " vazio."
                );
                return;
            }

            System.out.println(
                "[BIOMETRIA] Modelo " + nome
                + " carregado com sucesso!"
            );

        } catch (Exception | LinkageError erro) {
            System.err.println(
                "[BIOMETRIA] Erro ao testar " + nome
                + ": " + erro.getMessage()
            );
        }
    }

    private Mat extrairCaracteristicas(
        Path caminho,
        FaceDetectorYN detector,
        FaceRecognizerSF reconhecedor
    ) {
        if (!Files.exists(caminho)) {
            System.err.println(
                "[BIOMETRIA] Foto não encontrada: "
                + caminho.toAbsolutePath()
            );
            return null;
        }

        Mat imagem = Imgcodecs.imread(
            caminho.toAbsolutePath().toString()
        );

        Mat rostos = new Mat();
        Mat rostoAlinhado = new Mat();
        Mat caracteristicas = new Mat();

        try {
            if (imagem.empty()) {
                System.err.println(
                    "[BIOMETRIA] Não foi possível abrir: " + caminho
                );
                return null;
            }

            detector.setInputSize(
                new Size(imagem.cols(), imagem.rows())
            );

            int quantidade = detector.detect(imagem, rostos);

            if (quantidade <= 0 || rostos.empty()) {
                System.err.println(
                    "[BIOMETRIA] Nenhum rosto detectado em: "
                    + caminho.getFileName()
                );
                return null;
            }

            System.out.println(
                "[BIOMETRIA] Rosto detectado em: "
                + caminho.getFileName()
            );

            Mat primeiroRosto = rostos.row(0);

            try {
                reconhecedor.alignCrop(
                    imagem,
                    primeiroRosto,
                    rostoAlinhado
                );
            } finally {
                primeiroRosto.release();
            }

            reconhecedor.feature(
                rostoAlinhado,
                caracteristicas
            );
            System.out.println(
    "[BIOMETRIA] Tamanho das características de "
    + caminho.getFileName()
    + ": " + caracteristicas.rows()
    + " x " + caracteristicas.cols()
    + " | canais: " + caracteristicas.channels()
);

System.out.println(
    "[BIOMETRIA] Tipo das características: "
    + caracteristicas.type()
);

            if (caracteristicas.empty()) {
                System.err.println(
                    "[BIOMETRIA] Não foi possível extrair "
                    + "as características faciais."
                );
                return null;
            }

     return caracteristicas.clone();
        } finally {
            imagem.release();
            rostos.release();
            rostoAlinhado.release();
            caracteristicas.release();
        }
    }

    private void compararFotos(
        FaceDetectorYN detector,
        FaceRecognizerSF reconhecedor
    ) {
        System.out.println(
            "[BIOMETRIA] Iniciando comparação das duas fotos..."
        );

        Mat caracteristicas1 = extrairCaracteristicas(
            Path.of(FOTO_REFERENCIA),
            detector,
            reconhecedor
        );

        if (caracteristicas1 == null) {
            System.err.println(
                "[BIOMETRIA] Não foi possível processar "
                + "a foto de referência."
            );
            return;
        }

        try {
            Mat caracteristicas2 = extrairCaracteristicas(
                Path.of(FOTO_COMPARACAO),
                detector,
                reconhecedor
            );

            if (caracteristicas2 == null) {
                System.err.println(
                    "[BIOMETRIA] Não foi possível processar "
                    + "a segunda foto."
                );
                return;
            }

            try {
                double similaridade = reconhecedor.match(
                    caracteristicas1,
                    caracteristicas2,
                    FaceRecognizerSF.FR_COSINE
                );

                System.out.println(
                    "[BIOMETRIA] Similaridade facial: "
                    + similaridade
                );

                System.out.println(
                    "[BIOMETRIA] Teste de comparação concluído. "
                    + "O valor precisa ser avaliado com imagens "
                    + "de teste antes de definir uma regra de acesso."
                );

            } finally {
                caracteristicas2.release();
            }

        } finally {
            caracteristicas1.release();
        }
    }
}