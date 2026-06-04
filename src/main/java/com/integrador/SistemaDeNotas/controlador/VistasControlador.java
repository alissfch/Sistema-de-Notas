package com.integrador.SistemaDeNotas.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistasControlador {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; 
    }

    @GetMapping("/")
    public String paginaPrincipal() {
        return "redirect:/inicio";
    }

    @GetMapping("/admin/panel")
    public String mostrarPanelAdmin() {
        return "index"; 
    }

    @GetMapping("/admin/reportes/accesos")
    public String mostrarReporteAccesos() {
        return "admin/login-report";
    }

    @GetMapping("/admin/reportes/registros")
    public String mostrarReporteRegistros() {
        return "admin/registro-report"; 
    }

    @GetMapping("/admin/crear-usuario")
    public String mostrarFormularioCrearUsuario() {
        return "admin/formulario-crear"; 
    }
}