package com.integrador.SistemaDeNotas.servicio;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.integrador.SistemaDeNotas.modelo.entidades.Usuario;
import com.integrador.SistemaDeNotas.repositorio.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscamos al usuario por su correo (username)
        Usuario usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + username));

        // 2. Verificamos si la cuenta está activa
        if (!usuario.isEstado()) {
            throw new UsernameNotFoundException("La cuenta del usuario está inactiva.");
        }

        // 3. Convertimos nuestro 'RolUsuario' (ej. ADMIN) al formato que entiende Spring Security (ROLE_ADMIN)
        String nombreRol = "ROLE_" + usuario.getRol().name();
        SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority(nombreRol);

        // 4. Devolvemos el objeto User nativo de Spring Security
        return new User(
                usuario.getCorreo(),
                usuario.getContrasena(), // Spring Security comparará esta contraseña encriptada automáticamente
                Collections.singletonList(autoridad)
        );
    }
}