
package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;

@Controller
public class InicioController {

    @GetMapping("/")
    @ResponseBody
    public String inicio(
            Authentication authentication,
            CsrfToken csrfToken) {

        boolean administrador = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String linkUsuarios = administrador
                ? "<a href='/admin/usuarios'>Usuários</a>"
                : "";

        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">
                <title>Sistema Biométrico</title>

                <style>
                    * { box-sizing: border-box; }

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

                    header h1 { margin: 0 0 8px; }

                    nav {
                        background: #1d4058;
                        padding: 14px 24px;
                    }

                    nav a {
                        color: white;
                        text-decoration: none;
                        margin-right: 24px;
                        font-weight: bold;
                    }

                    nav button {
                        background: #dc2626;
                        color: white;
                        border: none;
                        padding: 9px 16px;
                        border-radius: 6px;
                        font-weight: bold;
                        cursor: pointer;
                    }

                    main {
                        max-width: 1000px;
                        margin: 35px auto;
                        padding: 0 20px;
                    }

                    .painel {
                        background: white;
                        padding: 28px;
                        border-radius: 12px;
                        box-shadow: 0 3px 12px #00000010;
                    }

                    .cards {
                        display: grid;
                        grid-template-columns:
                            repeat(auto-fit, minmax(220px, 1fr));
                        gap: 18px;
                        margin-top: 24px;
                    }

                    .card {
                        background: white;
                        padding: 22px;
                        border-radius: 10px;
                        border: 1px solid #dbe3ea;
                    }

                    .card h3 { color: #123047; }

                    .card a {
                        color: #087e8b;
                        font-weight: bold;
                    }
                </style>
            </head>

            <body>
                <header>
                    <h1>Sistema Biométrico</h1>
                    <p>Sistema de Segurança Ambiental</p>
                </header>

                <nav>
                    <a href="/">Início</a>
                    __USUARIOS_LINK__

                    <form action="/logout" method="post"
                          style="display:inline;">
                        <input type="hidden"
                               name="_csrf"
                               value="__CSRF_TOKEN__">
                        <button type="submit">Sair</button>
                    </form>
                </nav>

                <main>
                    <section class="painel">
                        <h2>Painel inicial</h2>
                        <p>Aplicação iniciada com sucesso!</p>
                        <p>
                            Bem-vindo ao sistema de controle
                            e segurança ambiental.
                        </p>
                    </section>

                    <section class="cards">
                        <div class="card">
                            <h3>Usuários</h3>
                            <p>Gerencie os usuários do sistema.</p>
                            __USUARIOS_CARD__
                        </div>

                        <div class="card">
                            <h3>Controle biométrico</h3>
                            <p>
                                Área destinada ao controle
                                de identificação biométrica.
                            </p>
                        </div>

                        <div class="card">
                            <h3>Registros de acesso</h3>
                            <p>
                                Área destinada aos registros
                                de entradas e saídas.
                            </p>
                        </div>
                    </section>
                </main>
            </body>
            </html>
            """
            .replace("__USUARIOS_LINK__", linkUsuarios)
            .replace(
                "__USUARIOS_CARD__",
                administrador
                    ? "<a href='/admin/usuarios'>Acessar usuários</a>"
                    : "<p>Área restrita ao administrador.</p>"
            )
            .replace("__CSRF_TOKEN__", csrfToken.getToken());
    }
}
