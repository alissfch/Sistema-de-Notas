package com.integrador.SistemaDeNotas.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.integrador.SistemaDeNotas.repositorio.CursoRepository;

@Controller
public class VistasControlador {

    @Autowired
    private CursoRepository cursoRepository;

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
    public String mostrarFormularioCrearUsuario(Model model) {
        model.addAttribute("todosLosCursos", cursoRepository.findAll());
        return "admin/formulario-crear"; 
    }
}