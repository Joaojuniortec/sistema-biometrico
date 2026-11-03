
package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/biometria")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBiometriaPaginaController {

    @GetMapping("/cadastro")
    @ResponseBody
    public String paginaBiometria(CsrfToken csrfToken) {

        String pagina = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <meta name="_csrf" content="CSRF_TOKEN">
                    <meta name="_csrf_header" content="CSRF_HEADER">

                    <title>Cadastro Biométrico</title>

                    <style>
                        * {
                            box-sizing: border-box;
                        }

                        body {
                            margin: 0;
                            font-family: Arial, sans-serif;
                            background: #f1f5f9;
                            color: #1e293b;
                        }

                        header {
                            background: #123047;
                            color: white;
                            padding: 24px;
                        }

                        main {
                            max-width: 650px;
                            margin: 35px auto;
                            padding: 0 20px;
                        }

                        .painel {
                            background: white;
                            padding: 28px;
                            border-radius: 12px;
                            box-shadow: 0 3px 12px #00000010;
                        }

                        label {
                            display: block;
                            margin-top: 18px;
                            font-weight: bold;
                        }

                        input {
                            width: 100%;
                            padding: 12px;
                            margin-top: 7px;
                            border: 1px solid #cbd5e1;
                            border-radius: 6px;
                            font-size: 16px;
                        }

                        button {
                            width: 100%;
                            margin-top: 24px;
                            padding: 14px;
                            background: #087e8b;
                            color: white;
                            border: 0;
                            border-radius: 6px;
                            font-size: 16px;
                            font-weight: bold;
                            cursor: pointer;
                        }

                        .voltar {
                            display: inline-block;
                            margin-top: 20px;
                            color: #087e8b;
                        }
                    </style>
                </head>

                <body>

                    <header>
                        <h1>Sistema Biométrico</h1>
                        <p>Sistema de Segurança Ambiental</p>
                    </header>

                    <main>
                        <section class="painel">

                            <h2>Cadastro de Biometria Facial</h2>

                            <p>
                                Informe o usuário e selecione uma
                                fotografia para registrar sua biometria.
                            </p>

                            <form id="formBiometria">

                                <label for="username">
                                    Nome de usuário
                                </label>

                                <input
                                    type="text"
                                    id="username"
                                    required
                                    placeholder="Ex.: joaocassioj@gmail.com">

                                <label for="foto">
                                    Fotografia facial
                                </label>

                                <input
                                    type="file"
                                    id="foto"
                                    accept="image/jpeg,image/png"
                                    required>

                                <button type="submit">
                                    Cadastrar biometria
                                </button>

                            </form>

                            <p id="mensagem" role="status"></p>

                            <a class="voltar" href="/admin/cadastro">
                                Voltar ao cadastro de usuários
                            </a>

                        </section>
                    </main>

                    <script>
                        const formulario =
                            document.getElementById('formBiometria');

                        const mensagem =
                            document.getElementById('mensagem');

                        formulario.addEventListener(
                            'submit',
                            async function(evento) {

                                evento.preventDefault();

                                mensagem.textContent =
                                    'Preparando cadastro biométrico...';

                                const token =
                                    document.querySelector(
                                        'meta[name="_csrf"]'
                                    ).content;

                                const cabecalho =
                                    document.querySelector(
                                        'meta[name="_csrf_header"]'
                                    ).content;

                                const dados = new FormData();

                                dados.append(
                                    'username',
                                    document.getElementById(
                                        'username'
                                    ).value.trim()
                                );

                                dados.append(
                                    'foto',
                                    document.getElementById(
                                        'foto'
                                    ).files[0]
                                );

                                try {
                                    const resposta = await fetch(
                                        '/admin/biometria',
                                        {
                                            method: 'POST',
                                            headers: {
                                                [cabecalho]: token
                                            },
                                            body: dados
                                        }
                                    );

                                    const resultado =
                                        await resposta.text();

                                    if (!resposta.ok) {
                                        throw new Error(
                                            resultado ||
                                            'Erro HTTP ' + resposta.status
                                        );
                                    }

                                    mensagem.textContent = resultado;
                                    formulario.reset();

                                } catch (erro) {
                                    mensagem.textContent =
                                        'Erro: ' + erro.message;
                                }
                            }
                        );
                    </script>

                </body>
                </html>
                """;

        return pagina
                .replace("CSRF_TOKEN", csrfToken.getToken())
                .replace("CSRF_HEADER", csrfToken.getHeaderName());
    }
}
