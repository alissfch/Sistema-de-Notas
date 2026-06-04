package com.integrador.SistemaDeNotas.controlador;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.integrador.SistemaDeNotas.modelo.entidades.Docente;
import com.integrador.SistemaDeNotas.modelo.entidades.Usuario;
import com.integrador.SistemaDeNotas.repositorio.UsuarioRepository;

@Controller
public class DocenteControlador {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/docente/panel")
    public String mostrarPanelDocente(Model model, Principal principal) {

        Usuario usuario = usuarioRepository.findByCorreo(principal.getName()).get();
        Docente miDocente = usuario.getDocente();

        if (miDocente != null) {
             model.addAttribute("misCursos", miDocente.getCursosAsignados());
        }
        else {
            return "redirect:/login?error=No se encontró el docente asociado a este usuario.";
        }

        return "docente/panel";
    }
}
