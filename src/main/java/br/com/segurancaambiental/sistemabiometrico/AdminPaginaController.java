
package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaginaController {

    @GetMapping("/cadastro")
    @ResponseBody
    public String paginaCadastro(CsrfToken csrfToken) {

        String pagina = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">

                    <meta name="_csrf" content="CSRF_TOKEN_PLACEHOLDER">
                    <meta name="_csrf_header" content="CSRF_HEADER_PLACEHOLDER">

                    <title>Administração de Usuários</title>

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
                            max-width: 700px;
                            margin: 35px auto;
                            padding: 0 20px;
                        }

                        .painel {
                            background: white;
                            padding: 28px;
                            border-radius: 12px;
                            box-shadow: 0 3px 12px #00000010;
                            margin-bottom: 25px;
                        }

                        label {
                            display: block;
                            margin-top: 18px;
                            font-weight: bold;
                        }

                        input, select {
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

                        button:hover {
                            background: #066873;
                        }

                        .botaoSenha {
                            background: #b45309;
                        }

                        .botaoSenha:hover {
                            background: #92400e;
                        }

                        .voltar {
                            display: inline-block;
                            margin-top: 20px;
                            color: #087e8b;
                        }

                        .mensagem {
                            margin-top: 15px;
                            font-weight: bold;
                        }
                    </style>
                </head>

                <body>

                <header>
                    <h1>Sistema Biométrico</h1>
                    <p>Sistema de Segurança Ambiental</p>
                    <p>Área administrativa</p>
                </header>

                <main>

                    <section class="painel">

                        <h2>Cadastro de Usuários</h2>

                        <p>Preencha os dados do funcionário.</p>

                        <form id="formCadastro">

                            <label for="nome">Nome completo</label>
                            <input id="nome" name="nome"
                                   required maxlength="120">

                            <label for="username">Nome de usuário</label>
                            <input id="username" name="username"
                                   required maxlength="50"
                                   autocomplete="username">

                            <label for="senha">Senha provisória</label>
                            <input id="senha" name="senha"
                                   type="password"
                                   required minlength="8"
                                   autocomplete="new-password">

                            <label for="nivelAcesso">Nível de acesso</label>

                            <select id="nivelAcesso"
                                    name="nivelAcesso" required>

                                <option value="">Selecione um nível</option>
                                <option value="1">Nível 1 — Acesso geral</option>
                                <option value="2">Nível 2 — Diretoria</option>
                                <option value="3">Nível 3 — Ministro</option>

                            </select>

                            <button type="submit">
                                Cadastrar usuário
                            </button>

                        </form>

                        <p id="mensagemCadastro"
                           class="mensagem" role="status"></p>

                    </section>

                    <section class="painel">

                        <h2>Redefinição de Senha</h2>

                        <p>
                            Utilize esta opção para recuperar o acesso
                            de um usuário cadastrado.
                        </p>

                        <form id="formSenha">

                            <label for="usuarioSenha">
                                Nome de usuário
                            </label>

                            <input
                                id="usuarioSenha"
                                type="text"
                                placeholder="Digite o usuário"
                                required
                                maxlength="50">

                            <label for="novaSenha">
                                Nova senha
                            </label>

                            <input
                                id="novaSenha"
                                type="password"
                                placeholder="Digite a nova senha"
                                required
                                minlength="8"
                                autocomplete="new-password">

                            <button type="submit" class="botaoSenha">
                                Redefinir senha
                            </button>

                        </form>

                        <p id="mensagemSenha"
                           class="mensagem" role="status"></p>

                    </section>

                    <a class="voltar" href="/">
                        Voltar ao painel
                    </a>

                </main>

                <script>

                    const token =
                        document.querySelector('meta[name="_csrf"]').content;

                    const cabecalho =
                        document.querySelector('meta[name="_csrf_header"]').content;

                    // CADASTRO DE USUÁRIOS

                    const formulario =
                        document.getElementById('formCadastro');

                    const mensagemCadastro =
                        document.getElementById('mensagemCadastro');

                    formulario.addEventListener('submit', async function(evento) {

                        evento.preventDefault();

                        mensagemCadastro.textContent =
                            'Cadastrando usuário...';

                        const dados = {
                            nome: document.getElementById('nome').value.trim(),
                            username: document.getElementById('username').value.trim(),
                            senha: document.getElementById('senha').value,
                            nivelAcesso: Number(
                                document.getElementById('nivelAcesso').value
                            )
                        };

                        try {

                            const resposta = await fetch('/admin/usuarios', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json',
                                    [cabecalho]: token
                                },
                                body: JSON.stringify(dados)
                            });

                            const resultado = await resposta.text();

                            if (!resposta.ok) {
                                throw new Error(resultado);
                            }

                            mensagemCadastro.textContent =
                                'Usuário cadastrado com sucesso!';

                            formulario.reset();

                        } catch (erro) {

                            mensagemCadastro.textContent =
                                'Erro ao cadastrar: ' + erro.message;

                        }

                    });

                    // REDEFINIÇÃO DE SENHA

                    const formularioSenha =
                        document.getElementById('formSenha');

                    const mensagemSenha =
                        document.getElementById('mensagemSenha');

                    formularioSenha.addEventListener('submit', async function(evento) {

                        evento.preventDefault();

                        mensagemSenha.textContent =
                            'Redefinindo senha...';

                        const dados = {
                            username: document.getElementById('usuarioSenha').value.trim(),
                            novaSenha: document.getElementById('novaSenha').value
                        };

                        try {

                            const resposta = await fetch(
                                '/admin/usuarios/redefinir-senha',
                                {
                                    method: 'PUT',
                                    headers: {
                                        'Content-Type': 'application/json',
                                        [cabecalho]: token
                                    },
                                    body: JSON.stringify(dados)
                                }
                            );

                            const resultado = await resposta.text();

                            if (!resposta.ok) {
                                throw new Error(resultado);
                            }

                            mensagemSenha.textContent =
                                'Senha redefinida com sucesso!';

                            formularioSenha.reset();

                        } catch (erro) {

                            mensagemSenha.textContent =
                                'Erro ao redefinir senha: ' + erro.message;

                        }

                    });

                </script>

                </body>
                </html>
                """;

        return pagina
                .replace("CSRF_TOKEN_PLACEHOLDER", csrfToken.getToken())
                .replace("CSRF_HEADER_PLACEHOLDER", csrfToken.getHeaderName());
    }
}
