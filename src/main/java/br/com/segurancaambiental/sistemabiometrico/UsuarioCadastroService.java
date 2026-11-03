
package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioCadastroService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioCadastroService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrar(
            String nome,
            String username,
            String senha,
            Integer nivelAcesso) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome.");
        }

        if (username == null || username.isBlank()
                || username.length() < 3
                || username.length() > 50) {
            throw new IllegalArgumentException(
                    "O usuário deve ter entre 3 e 50 caracteres.");
        }

        if (senha == null || senha.length() < 8) {
            throw new IllegalArgumentException(
                    "A senha deve ter pelo menos 8 caracteres.");
        }

        if (nivelAcesso == null
                || nivelAcesso < 1
                || nivelAcesso > 3) {
            throw new IllegalArgumentException(
                    "O nível deve ser 1, 2 ou 3.");
        }

        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Esse nome de usuário já existe.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome.trim());
        usuario.setUsername(username.trim());
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setNivelAcesso(nivelAcesso);

        return usuarioRepository.save(usuario);
    }

    // Redefinição de senha pelo administrador
    @Transactional
    public void redefinirSenha(
            String username,
            String novaSenha) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o nome de usuário."
            );
        }

        if (novaSenha == null || novaSenha.length() < 8) {
            throw new IllegalArgumentException(
                    "A senha deve ter pelo menos 8 caracteres."
            );
        }

        Usuario usuario = usuarioRepository
                .findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não encontrado."
                ));

        usuario.setSenha(passwordEncoder.encode(novaSenha));

        usuarioRepository.save(usuario);
    }
}
