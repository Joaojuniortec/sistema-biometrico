package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NivelAcessoController {

    @GetMapping("/publico/inicio")
    @ResponseBody
    public String nivel1() {
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <title>Teste - Nível 1</title>
            </head>
            <body>
                <h1>Sistema Biométrico</h1>
                <h2>Teste de Acesso - Nível 1</h2>
                <p>Acesso geral autorizado.</p>
                <a href="/logout">Sair do sistema</a>
            </body>
            </html>
            """;
    }

    @GetMapping("/diretoria/inicio")
    @ResponseBody
    public String nivel2() {
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <title>Teste - Nível 2</title>
            </head>
            <body>
                <h1>Sistema Biométrico</h1>
                <h2>Teste de Acesso - Nível 2</h2>
                <p>Acesso da diretoria autorizado.</p>
                <a href="/logout">Sair do sistema</a>
            </body>
            </html>
            """;
    }

    @GetMapping("/ministro/inicio")
    @ResponseBody
    public String nivel3() {
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <title>Teste - Nível 3</title>
            </head>
            <body>
                <h1>Sistema Biométrico</h1>
                <h2>Teste de Acesso - Nível 3</h2>
                <p>Acesso ministerial autorizado.</p>
                <a href="/logout">Sair do sistema</a>
            </body>
            </html>
            """;
    }
}