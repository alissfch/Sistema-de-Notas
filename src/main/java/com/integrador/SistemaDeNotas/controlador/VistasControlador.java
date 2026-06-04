package com.integrador.SistemaDeNotas.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistasControlador {

    // 1. Esta ruta rompe el bucle infinito. 
    // Cuando Spring Security mande al usuario a "/login", este método le entregará tu diseño HTML.
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // Busca exactamente el archivo "login.html" en la carpeta templates
    }

    // 2. Redirección automática al abrir la raíz de la página (localhost:8080/)
    @GetMapping("/")
    public String paginaPrincipal() {
        return "redirect:/inicio";
    }

// Ruta para el panel principal del Administrador
    @GetMapping("/admin/panel")
    public String mostrarPanelAdmin() {
        // Por ahora, le decimos que muestre el dashboard principal (tu index.html)
        // Más adelante, si haces un HTML exclusivo para el admin, lo cambias a "admin/panel"
        return "index"; 
    }

    // Ruta para el reporte de Accesos (Protegida solo para ADMIN en tu SecurityConfig)
    @GetMapping("/admin/reportes/accesos")
    public String mostrarReporteAccesos() {
        return "admin/login-report"; // Busca templates/admin/reporte-accesos.html
    }

    // Ruta para el reporte de Registros/Matrículas
    @GetMapping("/admin/reportes/registros")
    public String mostrarReporteRegistros() {
        return "admin/registro-report"; // Busca templates/admin/reporte-registros.html
    }
}