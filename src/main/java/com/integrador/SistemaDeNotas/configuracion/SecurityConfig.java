package com.integrador.SistemaDeNotas.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Dejamos públicos los archivos CSS/JS y el login
                .requestMatchers("/css/**", "/js/**", "/login").permitAll()
                // Protegemos las rutas según el rol
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/docente/**").hasRole("DOCENTE")
                .requestMatchers("/alumno/**").hasRole("ALUMNO")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Le decimos a Spring que use nuestro HTML
                .successHandler(customSuccessHandler()) // Controlador inteligente de redirección
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
            
        return http.build();
    }

    // Esta es la pieza que decide a dónde va cada usuario
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            var roles = authentication.getAuthorities();
            
            if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
                response.sendRedirect("/admin/panel");
            } else if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_DOCENTE"))) {
                response.sendRedirect("/docente/inicio");
            } else {
                response.sendRedirect("/alumno/mis-notas");
            }
        };
    }

    // Encriptador de contraseñas requerido por Spring (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}