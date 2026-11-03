
package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;

@Controller
public class ReconhecimentoFacialPaginaController {

    @GetMapping("/reconhecimento-facial")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public String paginaReconhecimento(CsrfToken csrfToken) {

        return """
                <!DOCTYPE html>
                <html lang="pt-BR">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>Reconhecimento Facial</title>

                    <style>
                        body {
                            margin: 0;
                            font-family: Arial, sans-serif;
                            background: #101827;
                            color: white;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            min-height: 100vh;
                        }

                        .container {
                            background: #1e293b;
                            padding: 30px;
                            border-radius: 15px;
                            width: 90%;
                            max-width: 500px;
                            text-align: center;
                        }

                        h1 {
                            color: #38bdf8;
                        }

                        button {
                            width: 100%;
                            padding: 12px;
                            margin-top: 12px;
                            border-radius: 7px;
                            border: none;
                            box-sizing: border-box;
                            background: #0284c7;
                            color: white;
                            font-weight: bold;
                            cursor: pointer;
                        }

                        button:hover {
                            background: #0369a1;
                        }

                        button:disabled {
                            background: #64748b;
                            cursor: not-allowed;
                        }

                        video {
                            width: 100%;
                            margin-top: 20px;
                            border-radius: 10px;
                            background: black;
                        }

                        #resultado {
                            margin-top: 20px;
                            color: #38bdf8;
                            line-height: 1.6;
                            overflow-wrap: anywhere;
                        }

                        a {
                            color: #38bdf8;
                        }
                    </style>
                </head>

                <body>

                <div class="container">

                    <h1>Sistema Biométrico</h1>

                    <p>Reconhecimento Facial</p>

                    <video id="camera"
                           autoplay
                           playsinline></video>

                    <button id="botaoCamera"
                            onclick="iniciarCamera()">
                        Ligar câmera
                    </button>

                    <button id="botaoCaptura"
                            onclick="capturarFoto()">
                        Identificar meu rosto
                    </button>

                    <canvas id="canvas" hidden></canvas>

                    <p id="resultado">
                        Aguardando inicialização da câmera...
                    </p>

                    <a href="/">
                        Voltar ao início
                    </a>

                </div>

                <script>

                    const camera =
                        document.getElementById("camera");

                    const canvas =
                        document.getElementById("canvas");

                    const resultado =
                        document.getElementById("resultado");

                    const botaoCaptura =
                        document.getElementById("botaoCaptura");

                    const csrfToken = "__CSRF_TOKEN__";
                    const csrfHeaderName = "__CSRF_HEADER__";

                    async function iniciarCamera() {

                        try {

                            const stream =
                                await navigator.mediaDevices.getUserMedia({
                                    video: true
                                });

                            camera.srcObject = stream;

                            resultado.textContent =
                                "Câmera iniciada com sucesso!";

                        } catch (erro) {

                            resultado.textContent =
                                "Erro ao acessar a câmera: " +
                                erro.message;
                        }
                    }

                    async function capturarFoto() {

                        if (!camera.videoWidth) {

                            resultado.textContent =
                                "Ligue a câmera antes de continuar.";

                            return;
                        }

                        botaoCaptura.disabled = true;

                        resultado.textContent =
                            "Capturando e analisando o rosto...";

                        canvas.width = camera.videoWidth;
                        canvas.height = camera.videoHeight;

                        const contexto = canvas.getContext("2d");

                        contexto.drawImage(
                            camera,
                            0,
                            0,
                            canvas.width,
                            canvas.height
                        );

                        try {

                            const blob = await new Promise(
                                (resolve, reject) => {

                                    canvas.toBlob(
                                        imagem => {
                                            if (imagem) {
                                                resolve(imagem);
                                            } else {
                                                reject(
                                                    new Error(
                                                        "Falha ao gerar fotografia."
                                                    )
                                                );
                                            }
                                        },
                                        "image/jpeg",
                                        0.90
                                    );
                                }
                            );

                            const dados = new FormData();

                            dados.append(
                                "foto",
                                blob,
                                "captura.jpg"
                            );

                            const resposta = await fetch(
                                "/api/reconhecimento-facial/identificar",
                                {
                                    method: "POST",
                                    headers: {
                                        [csrfHeaderName]: csrfToken
                                    },
                                    body: dados,
                                    credentials: "same-origin"
                                }
                            );

                            const resultadoApi =
                                await resposta.json();

                            if (!resposta.ok) {
                                throw new Error(
                                    resultadoApi.message ||
                                    resultadoApi.mensagem ||
                                    "Erro ao processar a fotografia."
                                );
                            }

                            if (resultadoApi.identificado) {

                                resultado.innerHTML =
                                    "<strong>Possível usuário identificado!</strong>" +
                                    "<br>Nome: " +
                                    resultadoApi.nome +
                                    "<br>Usuário: " +
                                    resultadoApi.username +
                                    "<br>Similaridade: " +
                                    resultadoApi.similaridade.toFixed(4) +
                                    "<br><br>" +
                                    resultadoApi.mensagem;

                            } else {

                                resultado.textContent =
                                    resultadoApi.mensagem;
                            }

                        } catch (erro) {

                            resultado.textContent =
                                "Erro no reconhecimento: " +
                                erro.message;

                        } finally {

                            botaoCaptura.disabled = false;
                        }
                    }

                </script>

                </body>
                </html>
                """
                .replace("__CSRF_TOKEN__", csrfToken.getToken())
                .replace("__CSRF_HEADER__", csrfToken.getHeaderName());
    }
}
