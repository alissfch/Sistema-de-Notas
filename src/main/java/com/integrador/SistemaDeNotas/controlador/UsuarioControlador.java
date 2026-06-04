package com.integrador.SistemaDeNotas.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.integrador.SistemaDeNotas.servicio.UsuarioServicio;

@Controller
public class UsuarioControlador {

    // Cambiamos el Repositorio y el PasswordEncoder por el Servicio que centraliza
    // la lógica
    @Autowired
    private UsuarioServicio usuarioServicio;

    @PostMapping("/admin/crear-usuario")
    public String crearNuevoUsuario(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String contrasenaSinEncriptar,
            @RequestParam String rol,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String grado,
            @RequestParam(required = false) String seccion,
            Model model) {

        String patronContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$";
        if (!contrasenaSinEncriptar.matches(patronContrasena)) {
            model.addAttribute("errorClave",
                    "La contraseña no cumple con los requisitos de seguridad (Mín. 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial).");
            return "admin/formulario-crear";
        }

        if (!correo.toLowerCase().endsWith("@jcm.edu.pe")) {
            model.addAttribute("errorClave", "El correo debe pertenecer al dominio @jcm.edu.pe");
            return "admin/formulario-crear";
        }

        try {
            usuarioServicio.registrarNuevoUsuario(correo, contrasenaSinEncriptar, rol, nombre, apellido, codigo, grado,
                    seccion);
            return "redirect:/admin/panel?exito";

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("errorClave", "Ocurrió un error al registrar el usuario en la base de datos.");
            return "admin/formulario-crear";
        }
    }
}