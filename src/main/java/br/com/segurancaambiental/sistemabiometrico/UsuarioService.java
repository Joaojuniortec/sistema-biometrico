package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado"));

        List<SimpleGrantedAuthority> autoridades = new ArrayList<>();

        autoridades.add(new SimpleGrantedAuthority(
                "ROLE_NIVEL_" + usuario.getNivelAcesso()));

        if (usuario.isAdministrador()) {
            autoridades.add(
                    new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getUsername())
                .password(usuario.getSenha())
                .authorities(autoridades)
                .build();
    }
}