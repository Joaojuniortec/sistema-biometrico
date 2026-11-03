
package br.com.segurancaambiental.sistemabiometrico;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.FaceDetectorYN;
import org.opencv.objdetect.FaceRecognizerSF;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ServicoBiometriaFacial {

    private static final String MODELO_YUNET =
            "src/main/resources/models/face_detection_yunet_2023mar.onnx";

    private static final String MODELO_SFACE =
            "src/main/resources/models/face_recognition_sface_2021dec.onnx";

    public ServicoBiometriaFacial() {
        OpenCV.loadLocally();
    }

    public float[] extrairCaracteristicas(Path caminhoFoto) {

        if (caminhoFoto == null || !Files.isRegularFile(caminhoFoto)) {
            throw new IllegalArgumentException(
                    "Arquivo de imagem não encontrado."
            );
        }

        Mat imagem = Imgcodecs.imread(
                caminhoFoto.toAbsolutePath().toString()
        );

        if (imagem.empty()) {
            imagem.release();

            throw new IllegalArgumentException(
                    "Não foi possível abrir a imagem."
            );
        }

        Mat rostos = new Mat();
        Mat rostoAlinhado = new Mat();
        Mat caracteristicas = new Mat();

        try {
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

            detector.setInputSize(
                    new Size(imagem.cols(), imagem.rows())
            );

            int quantidade = detector.detect(imagem, rostos);

            if (quantidade != 1 || rostos.empty()) {
                throw new IllegalArgumentException(
                        "A imagem precisa conter exatamente um rosto detectável."
                );
            }

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

            if (caracteristicas.empty()
                    || caracteristicas.total() != 128) {
                throw new IllegalStateException(
                        "Não foi possível extrair as características faciais."
                );
            }

            float[] vetor = new float[128];

            caracteristicas.get(0, 0, vetor);

            return vetor;

        } finally {
            imagem.release();
            rostos.release();
            rostoAlinhado.release();
            caracteristicas.release();
        }
    }
}
