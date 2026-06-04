package com.integrador.SistemaDeNotas.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.integrador.SistemaDeNotas.modelo.entidades.Usuario;
import com.integrador.SistemaDeNotas.repositorio.UsuarioRepository;

@Controller
public class UsuarioControlador {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/admin/crear-usuario")
    public String crearNuevoUsuario(@RequestParam String nombre,
                                    @RequestParam String apellido,
                                    @RequestParam String correo,
                                    @RequestParam String contrasenaSinEncriptar,
                                    @RequestParam String rol,
                                    Model model) {


        String patronContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$";

        if (!contrasenaSinEncriptar.matches(patronContrasena)) {
            model.addAttribute("errorClave", "La contraseña no cumple con los requisitos de seguridad (Mín. 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial).");
            return "admin/formulario-crear";
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        
        nuevoUsuario.setContrasena(passwordEncoder.encode(contrasenaSinEncriptar));
        
        if (rol.equals("DOCENTE")) {
            nuevoUsuario.setRol(Usuario.RolUsuario.DOCENTE);
        } else {
            nuevoUsuario.setRol(Usuario.RolUsuario.ADMIN);
        }
        
        nuevoUsuario.setEstado(true);

        usuarioRepository.save(nuevoUsuario);

        return "redirect:/admin/panel?exito";
    }
}