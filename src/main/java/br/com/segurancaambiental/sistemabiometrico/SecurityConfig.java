package br.com.segurancaambiental.sistemabiometrico;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**")
                    .permitAll()

                .requestMatchers("/publico/**")
                    .hasAnyRole("NIVEL_1", "NIVEL_2", "NIVEL_3")

                .requestMatchers("/diretoria/**")
                    .hasAnyRole("NIVEL_2", "NIVEL_3")

                .requestMatchers("/ministro/**")
                    .hasRole("NIVEL_3")

                .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {

                    String destino = "/";

                    boolean admin = authentication.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                    boolean nivel3 = authentication.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_NIVEL_3"));

                    boolean nivel2 = authentication.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_NIVEL_2"));

                    boolean nivel1 = authentication.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_NIVEL_1"));

                    if (admin) {
                        destino = "/admin/cadastro";
                    } else if (nivel3) {
                        destino = "/ministro/inicio";
                    } else if (nivel2) {
                        destino = "/diretoria/inicio";
                    } else if (nivel1) {
                        destino = "/publico/inicio";
                    }

                    response.sendRedirect(
                        request.getContextPath() + destino);
                })
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public org.springframework.security.authentication.AuthenticationManager
            authenticationManager(
                org.springframework.security.config.annotation.authentication.configuration
                    .AuthenticationConfiguration configuration)
                    throws Exception {

        return configuration.getAuthenticationManager();
    }
}