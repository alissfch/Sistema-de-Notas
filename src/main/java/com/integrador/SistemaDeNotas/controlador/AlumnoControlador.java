package com.integrador.SistemaDeNotas.controlador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.integrador.SistemaDeNotas.modelo.entidades.Alumno;
import com.integrador.SistemaDeNotas.modelo.entidades.Curso;
import com.integrador.SistemaDeNotas.modelo.entidades.Evaluacion;
import com.integrador.SistemaDeNotas.modelo.entidades.Nota;
import com.integrador.SistemaDeNotas.modelo.entidades.Usuario;
import com.integrador.SistemaDeNotas.repositorio.CursoRepository;
import com.integrador.SistemaDeNotas.repositorio.UsuarioRepository;

@Controller
public class AlumnoControlador {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping("/alumno/dashboard")
    public String mostrarDashboard(Model model, Principal principal) {
        // 1. Obtener alumno autenticado (Seguridad)
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName()).get();
        Alumno miAlumno = usuario.getAlumno();

        if (miAlumno == null) {
            return "redirect:/login?error=No se encontró el perfil de alumno.";
        }

        // 2. Obtener los cursos de su sección
        List<Curso> misCursos = cursoRepository.findBySeccion(miAlumno.getSeccion());
        List<FilaCursoAlumnoDTO> filasCursos = new ArrayList<>();

        // Variables para las tarjetas de indicadores generales
        BigDecimal sumaPromediosGlobal = BigDecimal.ZERO;
        int cursosConPromedio = 0;
        BigDecimal notaMaxGlobal = null;
        BigDecimal notaMinGlobal = null;

        // 3. Lógica idéntica al Docente para promedios ponderados
        for (Curso curso : misCursos) {
            List<Evaluacion> evaluacionesActivas = new ArrayList<>();
            if (curso.getEvaluaciones() != null) {
                evaluacionesActivas = curso.getEvaluaciones().stream()
                        .filter(e -> e != null && e.isEstado())
                        .collect(Collectors.toList());
            }

            BigDecimal sumaPonderada = BigDecimal.ZERO;
            BigDecimal sumaPesos = BigDecimal.ZERO;
            BigDecimal maxCurso = null;
            BigDecimal minCurso = null;

            for (Evaluacion ev : evaluacionesActivas) {
                BigDecimal valorNota = null;
                if (miAlumno.getNotas() != null) {
                    for (Nota n : miAlumno.getNotas()) {
                        if (n.getEvaluacion().getIdEvaluacion().equals(ev.getIdEvaluacion())) {
                            valorNota = n.getValor();
                            break;
                        }
                    }
                }

                if (valorNota != null) {
                    // Calculamos máximo y mínimo del curso
                    if (maxCurso == null || valorNota.compareTo(maxCurso) > 0)
                        maxCurso = valorNota;
                    if (minCurso == null || valorNota.compareTo(minCurso) < 0)
                        minCurso = valorNota;

                    if (ev.getPesoPorcentual() != null) {
                        sumaPonderada = sumaPonderada.add(valorNota.multiply(ev.getPesoPorcentual()));
                        sumaPesos = sumaPesos.add(ev.getPesoPorcentual());
                    }
                }
            }

            BigDecimal promedioFinal = null;
            String estado = "Sin notas";
            String badgeClase = "bg-light text-muted border";

            if (sumaPesos.compareTo(BigDecimal.ZERO) > 0) {
                promedioFinal = sumaPonderada.divide(sumaPesos, 2, RoundingMode.HALF_UP);
                sumaPromediosGlobal = sumaPromediosGlobal.add(promedioFinal);
                cursosConPromedio++;

                if (promedioFinal.compareTo(new BigDecimal("11")) >= 0) {
                    estado = "Aprobado";
                    badgeClase = "bg-success";
                } else {
                    estado = "Desaprobado";
                    badgeClase = "bg-danger";
                }
            }

            // Calculamos máximo y mínimo global del alumno
            if (maxCurso != null && (notaMaxGlobal == null || maxCurso.compareTo(notaMaxGlobal) > 0))
                notaMaxGlobal = maxCurso;
            if (minCurso != null && (notaMinGlobal == null || minCurso.compareTo(notaMinGlobal) < 0))
                notaMinGlobal = minCurso;

            filasCursos.add(new FilaCursoAlumnoDTO(curso.getNombreCurso(), promedioFinal, maxCurso, minCurso, estado,
                    badgeClase));
        }

        BigDecimal promedioGeneral = null;
        if (cursosConPromedio > 0) {
            promedioGeneral = sumaPromediosGlobal.divide(new BigDecimal(cursosConPromedio), 2, RoundingMode.HALF_UP);
        }

        // 4. Enviar a la vista
        model.addAttribute("filasCursos", filasCursos);
        model.addAttribute("promedioGeneral", promedioGeneral);
        model.addAttribute("notaMaxGlobal", notaMaxGlobal);
        model.addAttribute("notaMinGlobal", notaMinGlobal);
        model.addAttribute("totalCursos", misCursos.size());
        model.addAttribute("nombreAlumno", usuario.getNombre() + " " + usuario.getApellido());
        model.addAttribute("seccionAlumno", miAlumno.getSeccion());

        return "alumno/dashboard";
    }

    // DTO Interno para enviar datos estructurados a la tabla
    public static class FilaCursoAlumnoDTO {
        private final String curso;
        private final BigDecimal promedio;
        private final BigDecimal notaMaxima;
        private final BigDecimal notaMinima;
        private final String estado;
        private final String badgeClase;

        public FilaCursoAlumnoDTO(String curso, BigDecimal promedio, BigDecimal notaMaxima, BigDecimal notaMinima,
                String estado, String badgeClase) {
            this.curso = curso;
            this.promedio = promedio;
            this.notaMaxima = notaMaxima;
            this.notaMinima = notaMinima;
            this.estado = estado;
            this.badgeClase = badgeClase;
        }

        public String getCurso() {
            return curso;
        }

        public BigDecimal getPromedio() {
            return promedio;
        }

        public BigDecimal getNotaMaxima() {
            return notaMaxima;
        }

        public BigDecimal getNotaMinima() {
            return notaMinima;
        }

        public String getEstado() {
            return estado;
        }

        public String getBadgeClase() {
            return badgeClase;
        }
    }
}