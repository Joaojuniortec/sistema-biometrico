
package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioCadastroService cadastroService;

    public AdminController(UsuarioCadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    public record CadastroUsuario(
            String nome,
            String username,
            String senha,
            Integer nivelAcesso) {
    }

    public record RedefinicaoSenha(
            String username,
            String novaSenha) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String cadastrar(@RequestBody CadastroUsuario dados) {

        cadastroService.cadastrar(
                dados.nome(),
                dados.username(),
                dados.senha(),
                dados.nivelAcesso());

        return "Usuário cadastrado com sucesso!";
    }

    @PutMapping("/redefinir-senha")
    @ResponseStatus(HttpStatus.OK)
    public String redefinirSenha(
            @RequestBody RedefinicaoSenha dados) {

        cadastroService.redefinirSenha(
                dados.username(),
                dados.novaSenha());

        return "Senha redefinida com sucesso!";
    }
}
