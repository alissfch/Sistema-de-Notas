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

    // Esta ruta recibe los datos del formulario de "Crear Usuario"
    @PostMapping("/admin/crear-usuario")
    public String crearNuevoUsuario(@RequestParam String nombre,
                                    @RequestParam String apellido,
                                    @RequestParam String correo,
                                    @RequestParam String contrasenaSinEncriptar,
                                    @RequestParam String rol,
                                    Model model) {

        // 1. DEFINIMOS LA REGLA ESTRICTA (Regex)
        // (?=.*[0-9]) : Al menos un número
        // (?=.*[a-z]) : Al menos una letra minúscula
        // (?=.*[A-Z]) : Al menos una letra mayúscula
        // (?=.*[\W_]) : Al menos un carácter especial
        // .{8,}       : Mínimo 8 caracteres en total
        String patronContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$";

        // 2. VERIFICAMOS SI CUMPLE LA REGLA
        if (!contrasenaSinEncriptar.matches(patronContrasena)) {
            // Si falla, enviamos un mensaje de error de vuelta al HTML
            model.addAttribute("errorClave", "La contraseña no cumple con los requisitos de seguridad (Mín. 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial).");
            return "admin/formulario-crear"; // Regresa a la página del formulario
        }

        // 3. SI PASA LA PRUEBA, CONTINUAMOS CON LA CREACIÓN
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        
        // ¡Importante! Aquí la encriptamos ANTES de guardarla
        nuevoUsuario.setContrasena(passwordEncoder.encode(contrasenaSinEncriptar));
        
        if (rol.equals("DOCENTE")) {
            nuevoUsuario.setRol(Usuario.RolUsuario.DOCENTE);
        } else {
            nuevoUsuario.setRol(Usuario.RolUsuario.ADMIN);
        }
        
        nuevoUsuario.setEstado(true);

        // 4. GUARDAMOS EN MYSQL
        usuarioRepository.save(nuevoUsuario);

        // Redirigimos al panel con un mensaje de éxito
        return "redirect:/admin/panel?exito";
    }
}