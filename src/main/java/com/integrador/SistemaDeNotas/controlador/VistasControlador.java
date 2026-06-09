package com.integrador.SistemaDeNotas.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.integrador.SistemaDeNotas.modelo.entidades.Alumno;
import com.integrador.SistemaDeNotas.modelo.entidades.Nota;
import com.integrador.SistemaDeNotas.repositorio.AlumnoRepository;
import com.integrador.SistemaDeNotas.repositorio.CursoRepository;
import com.integrador.SistemaDeNotas.repositorio.NotaRepository;

@Controller
public class VistasControlador {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private NotaRepository notaRepository;

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

    @GetMapping("/admin/consultas")
    public String realizarConsultas(
            @RequestParam(required = false) String buscarNombre,
            @RequestParam(required = false) String buscarCodigo,
            Model model) {

        if (buscarNombre != null && !buscarNombre.isEmpty()) {
            List<Alumno> alumnosEncontrados = alumnoRepository.findByNombresContainingIgnoreCase(buscarNombre);
            model.addAttribute("alumnos", alumnosEncontrados);
        }

        if (buscarCodigo != null && !buscarCodigo.isEmpty()) {
            List<Nota> notasEncontradas = notaRepository.findNotasPorCodigoAlumno(buscarCodigo);
            model.addAttribute("notas", notasEncontradas);
        }

        return "admin/panel-consultas"; 
    }

}