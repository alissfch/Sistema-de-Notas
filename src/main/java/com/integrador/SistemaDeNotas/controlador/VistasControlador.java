package com.integrador.SistemaDeNotas.controlador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.integrador.SistemaDeNotas.modelo.entidades.Alumno;
import com.integrador.SistemaDeNotas.modelo.entidades.Curso;
import com.integrador.SistemaDeNotas.modelo.entidades.EncuestaDocente;
import com.integrador.SistemaDeNotas.modelo.entidades.Evaluacion;
import com.integrador.SistemaDeNotas.modelo.entidades.Nota;
import com.integrador.SistemaDeNotas.repositorio.AlumnoRepository;
import com.integrador.SistemaDeNotas.repositorio.CursoRepository;
import com.integrador.SistemaDeNotas.repositorio.DocenteRepository;
import com.integrador.SistemaDeNotas.repositorio.EncuestaDocenteRepository;
import com.integrador.SistemaDeNotas.repositorio.EvaluacionRepository;
import com.integrador.SistemaDeNotas.repositorio.NotaRepository;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class VistasControlador {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private NotaRepository notaRepository;

    @Autowired
    private EvaluacionRepository evaluacionRepository;
    @Autowired
    private EncuestaDocenteRepository encuestaDocenteRepository;
    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private com.integrador.SistemaDeNotas.servicio.PdfReporteServicio pdfReporteServicio;

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

    @GetMapping("/admin/reportes/eliminados")
    public String mostrarReporteEliminados(@RequestParam(required = false) String formato, Model model,
            HttpServletResponse response) {
        List<Evaluacion> eliminados = evaluacionRepository.findByEstadoFalse();

        if ("pdf".equalsIgnoreCase(formato)) {
            try {
                pdfReporteServicio.exportarReporteEliminados(response, eliminados);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("eliminados", eliminados);
        return "admin/reporte-eliminados";
    }

    public static class FilaIndicadorCursoDTO {
        private final String nombreCurso;
        private final String seccion;
        private final int totalAlumnos;
        private final int aprobados;
        private final int desaprobados;
        private final BigDecimal porcentajeAprobados;
        private final String badgeClase;

        public FilaIndicadorCursoDTO(String nombreCurso, String seccion, int totalAlumnos, int aprobados,
                int desaprobados, BigDecimal porcentajeAprobados, String badgeClase) {
            this.nombreCurso = nombreCurso;
            this.seccion = seccion;
            this.totalAlumnos = totalAlumnos;
            this.aprobados = aprobados;
            this.desaprobados = desaprobados;
            this.porcentajeAprobados = porcentajeAprobados;
            this.badgeClase = badgeClase;
        }

        public String getNombreCurso() {
            return nombreCurso;
        }

        public String getSeccion() {
            return seccion;
        }

        public int getTotalAlumnos() {
            return totalAlumnos;
        }

        public int getAprobados() {
            return aprobados;
        }

        public int getDesaprobados() {
            return desaprobados;
        }

        public BigDecimal getPorcentajeAprobados() {
            return porcentajeAprobados;
        }

        public String getBadgeClase() {
            return badgeClase;
        }
    }

    @GetMapping("/admin/indicadores")
    public String mostrarIndicadores(Model model) {
        List<Nota> todasLasNotas = notaRepository.findAll();
        long totalDias = 0;
        int notasValidas = 0;
        for (Nota nota : todasLasNotas) {
            if (nota.getFechaRegistro() != null && nota.getEvaluacion() != null
                    && nota.getEvaluacion().getFecha() != null) {
                long diasDiferencia = ChronoUnit.DAYS.between(nota.getEvaluacion().getFecha(), nota.getFechaRegistro());
                totalDias += Math.max(0, diasDiferencia);
                notasValidas++;
            }
        }
        BigDecimal promedioTiempo = BigDecimal.ZERO;
        String colorBadge = "bg-secondary";
        String estadoTexto = "Sin datos";
        if (notasValidas > 0) {
            double promedio = (double) totalDias / notasValidas;
            promedioTiempo = new BigDecimal(promedio).setScale(2, RoundingMode.HALF_UP);
            if (promedioTiempo.compareTo(new BigDecimal("1")) < 0) {
                colorBadge = "bg-success";
                estadoTexto = "Óptimo";
            } else if (promedioTiempo.compareTo(new BigDecimal("2")) <= 0) {
                colorBadge = "bg-warning text-dark";
                estadoTexto = "Aceptable";
            } else {
                colorBadge = "bg-danger";
                estadoTexto = "Crítico";
            }
        }
        model.addAttribute("promedioTiempo", promedioTiempo);
        model.addAttribute("colorBadge", colorBadge);
        model.addAttribute("estadoTexto", estadoTexto);

        long totalNotas = notaRepository.count();
        long notasCorregidas = notaRepository.countByCorregidaTrue();
        BigDecimal porcentajeErrores = BigDecimal.ZERO;
        String colorBadgeErrores = "bg-secondary";
        String estadoTextoErrores = "Sin datos";
        if (totalNotas > 0) {
            double porcentaje = ((double) notasCorregidas / totalNotas) * 100;
            porcentajeErrores = new BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_UP);
            if (porcentajeErrores.compareTo(new BigDecimal("3")) < 0) {
                colorBadgeErrores = "bg-success";
                estadoTextoErrores = "Óptimo";
            } else if (porcentajeErrores.compareTo(new BigDecimal("10")) <= 0) {
                colorBadgeErrores = "bg-warning text-dark";
                estadoTextoErrores = "Aceptable";
            } else {
                colorBadgeErrores = "bg-danger";
                estadoTextoErrores = "Crítico";
            }
        }
        model.addAttribute("totalNotas", totalNotas);
        model.addAttribute("notasCorregidas", notasCorregidas);
        model.addAttribute("porcentajeErrores", porcentajeErrores);
        model.addAttribute("colorBadgeErrores", colorBadgeErrores);
        model.addAttribute("estadoTextoErrores", estadoTextoErrores);

        int reportesEsperados = 5;
        int reportesImplementados = 5;
        BigDecimal disponibilidadReportes = BigDecimal.ZERO;
        String colorBadgeReportes = "bg-secondary";
        String estadoTextoReportes = "Sin datos";
        if (reportesEsperados > 0) {
            double porcentajeDisp = ((double) reportesImplementados / reportesEsperados) * 100;
            disponibilidadReportes = new BigDecimal(porcentajeDisp).setScale(2, RoundingMode.HALF_UP);
            if (disponibilidadReportes.compareTo(new BigDecimal("100")) == 0) {
                colorBadgeReportes = "bg-success";
                estadoTextoReportes = "Óptimo";
            } else if (disponibilidadReportes.compareTo(new BigDecimal("80")) >= 0) {
                colorBadgeReportes = "bg-warning text-dark";
                estadoTextoReportes = "Aceptable";
            } else {
                colorBadgeReportes = "bg-danger";
                estadoTextoReportes = "Crítico";
            }
        }
        model.addAttribute("reportesEsperados", reportesEsperados);
        model.addAttribute("reportesImplementados", reportesImplementados);
        model.addAttribute("disponibilidadReportes", disponibilidadReportes);
        model.addAttribute("colorBadgeReportes", colorBadgeReportes);
        model.addAttribute("estadoTextoReportes", estadoTextoReportes);

        List<Curso> todosLosCursos = cursoRepository.findAll();
        List<FilaIndicadorCursoDTO> tablaCursos = new ArrayList<>();

        for (Curso curso : todosLosCursos) {
            List<Alumno> alumnosSeccion = alumnoRepository.findBySeccion(curso.getSeccion());
            int totalAlumnos = alumnosSeccion.size();
            int aprobados = 0;
            int desaprobados = 0;

            List<Evaluacion> evaluacionesActivas = new ArrayList<>();
            if (curso.getEvaluaciones() != null) {
                evaluacionesActivas = curso.getEvaluaciones().stream()
                        .filter(e -> e != null && e.isEstado())
                        .collect(Collectors.toList());
            }

            for (Alumno alumno : alumnosSeccion) {
                BigDecimal sumaPonderada = BigDecimal.ZERO;
                BigDecimal sumaPesos = BigDecimal.ZERO;

                for (Evaluacion ev : evaluacionesActivas) {
                    BigDecimal valorNota = null;
                    if (alumno.getNotas() != null) {
                        for (Nota n : alumno.getNotas()) {
                            if (n.getEvaluacion().getIdEvaluacion().equals(ev.getIdEvaluacion())) {
                                valorNota = n.getValor();
                                break;
                            }
                        }
                    }
                    if (valorNota != null && ev.getPesoPorcentual() != null) {
                        sumaPonderada = sumaPonderada.add(valorNota.multiply(ev.getPesoPorcentual()));
                        sumaPesos = sumaPesos.add(ev.getPesoPorcentual());
                    }
                }

                BigDecimal promedioFinal = null;
                if (sumaPesos.compareTo(BigDecimal.ZERO) > 0) {
                    promedioFinal = sumaPonderada.divide(sumaPesos, 2, RoundingMode.HALF_UP);
                }

                if (promedioFinal != null && promedioFinal.compareTo(new BigDecimal("11")) >= 0) {
                    aprobados++;
                } else {
                    desaprobados++;
                }
            }

            BigDecimal porcentajeAprobados = BigDecimal.ZERO;
            String badgeClase = "bg-success";

            if (totalAlumnos > 0) {
                double porcentaje = ((double) aprobados / totalAlumnos) * 100;
                porcentajeAprobados = new BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_UP);

                if (porcentajeAprobados.compareTo(new BigDecimal("80")) >= 0) {
                    badgeClase = "bg-success";
                } else if (porcentajeAprobados.compareTo(new BigDecimal("60")) >= 0) {
                    badgeClase = "bg-warning text-dark";
                } else {
                    badgeClase = "bg-danger";
                }
            }

            tablaCursos.add(new FilaIndicadorCursoDTO(
                    curso.getNombreCurso(),
                    curso.getSeccion().toUpperCase(),
                    totalAlumnos, aprobados, desaprobados, porcentajeAprobados, badgeClase));
        }

        model.addAttribute("tablaCursos", tablaCursos);

        long notasATiempo = 0;

        for (Nota nota : todasLasNotas) {
            if (nota.getFechaRegistro() != null &&
                    nota.getEvaluacion() != null &&
                    nota.getEvaluacion().getFecha() != null) {

                if (!nota.getFechaRegistro().isAfter(nota.getEvaluacion().getFecha())) {
                    notasATiempo++;
                }
            }
        }

        BigDecimal porcentajeAlDia = BigDecimal.ZERO;
        String colorBadgeAlDia = "bg-secondary";
        String estadoTextoAlDia = "Sin datos";

        if (totalNotas > 0) {
            double porcentaje = ((double) notasATiempo / totalNotas) * 100;
            porcentajeAlDia = new BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_UP);

            if (porcentajeAlDia.compareTo(new BigDecimal("95")) >= 0) {
                colorBadgeAlDia = "bg-success";
                estadoTextoAlDia = "Óptimo";
            } else if (porcentajeAlDia.compareTo(new BigDecimal("80")) >= 0) {
                colorBadgeAlDia = "bg-warning text-dark";
                estadoTextoAlDia = "Aceptable";
            } else {
                colorBadgeAlDia = "bg-danger";
                estadoTextoAlDia = "Crítico";
            }
        }

        model.addAttribute("notasATiempo", notasATiempo);
        model.addAttribute("porcentajeAlDia", porcentajeAlDia);
        model.addAttribute("colorBadgeAlDia", colorBadgeAlDia);
        model.addAttribute("estadoTextoAlDia", estadoTextoAlDia);

        List<EncuestaDocente> encuestas = encuestaDocenteRepository.findAll();
        int totalEncuestas = encuestas.size();

        BigDecimal promedioSatisfaccion = BigDecimal.ZERO;
        BigDecimal porcentajeSatisfaccion = BigDecimal.ZERO;
        String colorBadgeSatisfaccion = "bg-secondary";
        String estadoTextoSatisfaccion = "Sin datos";

        if (totalEncuestas > 0) {
            double sumaCalificaciones = 0;
            for (EncuestaDocente e : encuestas) {
                sumaCalificaciones += e.getCalificacion();
            }

            double promedioDocentes = sumaCalificaciones / totalEncuestas;
            promedioSatisfaccion = new BigDecimal(promedioDocentes).setScale(1, RoundingMode.HALF_UP);

            double porcentajeSat = (promedioDocentes / 5.0) * 100;
            porcentajeSatisfaccion = new BigDecimal(porcentajeSat).setScale(2, RoundingMode.HALF_UP);

            if (porcentajeSatisfaccion.compareTo(new BigDecimal("80")) >= 0) {
                colorBadgeSatisfaccion = "bg-success";
                estadoTextoSatisfaccion = "Óptimo";
            } else if (porcentajeSatisfaccion.compareTo(new BigDecimal("60")) >= 0) {
                colorBadgeSatisfaccion = "bg-warning text-dark";
                estadoTextoSatisfaccion = "Aceptable";
            } else {
                colorBadgeSatisfaccion = "bg-danger";
                estadoTextoSatisfaccion = "Crítico";
            }
        }

        model.addAttribute("totalEncuestas", totalEncuestas);
        model.addAttribute("promedioSatisfaccion", promedioSatisfaccion);
        model.addAttribute("porcentajeSatisfaccion", porcentajeSatisfaccion);
        model.addAttribute("colorBadgeSatisfaccion", colorBadgeSatisfaccion);
        model.addAttribute("estadoTextoSatisfaccion", estadoTextoSatisfaccion);

        return "admin/indicadores";
    }

    @GetMapping("/admin/reportes/estadisticas")
    public String mostrarEstadisticasAcademicas(Model model) {
        List<Nota> todasLasNotas = notaRepository.findAll();
        List<Curso> todosLosCursos = cursoRepository.findAll();
        List<Alumno> todosLosAlumnos = alumnoRepository.findAll();

        long totalNotasRegistradas = todasLasNotas.size();
        BigDecimal promedioGeneral = BigDecimal.ZERO;

        if (totalNotasRegistradas > 0) {
            BigDecimal sumaTotal = BigDecimal.ZERO;
            for (Nota n : todasLasNotas) {
                if (n.getValor() != null) {
                    sumaTotal = sumaTotal.add(n.getValor());
                }
            }
            promedioGeneral = sumaTotal.divide(new BigDecimal(totalNotasRegistradas), 2, RoundingMode.HALF_UP);
        }

        int cursosEvaluados = 0;
        int cursosSinEvaluaciones = 0;

        int alumnosAprobados = 0;
        int alumnosDesaprobados = 0;

        List<FilaIndicadorCursoDTO> tablaCursos = new ArrayList<>();

        for (Curso curso : todosLosCursos) {
            List<Evaluacion> evaluacionesActivas = new ArrayList<>();
            if (curso.getEvaluaciones() != null) {
                evaluacionesActivas = curso.getEvaluaciones().stream()
                        .filter(e -> e != null && e.isEstado())
                        .collect(Collectors.toList());
                if (!evaluacionesActivas.isEmpty()) {
                    cursosEvaluados++;
                } else {
                    cursosSinEvaluaciones++;
                }
            } else {
                cursosSinEvaluaciones++;
            }

            List<Alumno> alumnosSeccion = alumnoRepository.findBySeccion(curso.getSeccion());
            int totalAlumnosSeccion = alumnosSeccion.size();
            int aprobadosCurso = 0;
            int desaprobadosCurso = 0;

            for (Alumno alumno : alumnosSeccion) {
                BigDecimal sumaPonderada = BigDecimal.ZERO;
                BigDecimal sumaPesos = BigDecimal.ZERO;

                for (Evaluacion ev : evaluacionesActivas) {
                    BigDecimal valorNota = null;
                    if (alumno.getNotas() != null) {
                        for (Nota n : alumno.getNotas()) {
                            if (n.getEvaluacion().getIdEvaluacion().equals(ev.getIdEvaluacion())) {
                                valorNota = n.getValor();
                                break;
                            }
                        }
                    }
                    if (valorNota != null && ev.getPesoPorcentual() != null) {
                        sumaPonderada = sumaPonderada.add(valorNota.multiply(ev.getPesoPorcentual()));
                        sumaPesos = sumaPesos.add(ev.getPesoPorcentual());
                    }
                }

                BigDecimal promedioFinal = null;
                if (sumaPesos.compareTo(BigDecimal.ZERO) > 0) {
                    promedioFinal = sumaPonderada.divide(sumaPesos, 2, RoundingMode.HALF_UP);
                }

                if (promedioFinal != null) {
                    if (promedioFinal.compareTo(new BigDecimal("11")) >= 0) {
                        aprobadosCurso++;
                        alumnosAprobados++;
                    } else {
                        desaprobadosCurso++;
                        alumnosDesaprobados++;
                    }
                }
            }

            BigDecimal porcentajeAprobados = BigDecimal.ZERO;
            String badgeClase = "bg-success";

            if (totalAlumnosSeccion > 0) {
                double porcentaje = ((double) aprobadosCurso / totalAlumnosSeccion) * 100;
                porcentajeAprobados = new BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_UP);

                if (porcentajeAprobados.compareTo(new BigDecimal("80")) >= 0) {
                    badgeClase = "bg-success";
                } else if (porcentajeAprobados.compareTo(new BigDecimal("60")) >= 0) {
                    badgeClase = "bg-warning text-dark";
                } else {
                    badgeClase = "bg-danger";
                }
            }

            tablaCursos.add(new FilaIndicadorCursoDTO(
                    curso.getNombreCurso(),
                    curso.getSeccion().toUpperCase(),
                    totalAlumnosSeccion, aprobadosCurso, desaprobadosCurso, porcentajeAprobados, badgeClase));
        }

        int totalEvaluados = alumnosAprobados + alumnosDesaprobados;
        BigDecimal porcentajeAprobacion = BigDecimal.ZERO;
        BigDecimal porcentajeDesaprobacion = BigDecimal.ZERO;

        if (totalEvaluados > 0) {
            porcentajeAprobacion = new BigDecimal((double) alumnosAprobados / totalEvaluados * 100).setScale(2,
                    RoundingMode.HALF_UP);
            porcentajeDesaprobacion = new BigDecimal((double) alumnosDesaprobados / totalEvaluados * 100).setScale(2,
                    RoundingMode.HALF_UP);
        }

        long totalDocentes = docenteRepository.count();
        long totalAlumnos = todosLosAlumnos.size();
        long totalCursosRegistrados = todosLosCursos.size();

        model.addAttribute("promedioGeneral", promedioGeneral);
        model.addAttribute("cursosEvaluados", cursosEvaluados);
        model.addAttribute("totalNotasRegistradas", totalNotasRegistradas);

        model.addAttribute("alumnosAprobados", alumnosAprobados);
        model.addAttribute("alumnosDesaprobados", alumnosDesaprobados);
        model.addAttribute("porcentajeAprobacion", porcentajeAprobacion);
        model.addAttribute("porcentajeDesaprobacion", porcentajeDesaprobacion);

        model.addAttribute("totalCursosRegistrados", totalCursosRegistrados);
        model.addAttribute("cursosSinEvaluaciones", cursosSinEvaluaciones);
        model.addAttribute("totalDocentes", totalDocentes);
        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("tablaCursos", tablaCursos);

        return "admin/estadisticas-academicas";
    }
}