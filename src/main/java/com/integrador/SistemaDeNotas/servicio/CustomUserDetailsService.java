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
        Usuario usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + username));
        if (!usuario.isEstado()) {
            throw new UsernameNotFoundException("La cuenta del usuario está inactiva.");
        }
        String nombreRol = "ROLE_" + usuario.getRol().name();
        SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority(nombreRol);

        return new User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                Collections.singletonList(autoridad)
        );
    }
}