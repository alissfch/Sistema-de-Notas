package com.integrador.SistemaDeNotas.controlador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
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
import com.integrador.SistemaDeNotas.modelo.entidades.Usuario;
import com.integrador.SistemaDeNotas.repositorio.AlumnoRepository;
import com.integrador.SistemaDeNotas.repositorio.CursoRepository;
import com.integrador.SistemaDeNotas.repositorio.DocenteRepository;
import com.integrador.SistemaDeNotas.repositorio.EncuestaDocenteRepository;
import com.integrador.SistemaDeNotas.repositorio.EvaluacionRepository;
import com.integrador.SistemaDeNotas.repositorio.NotaRepository;
import com.integrador.SistemaDeNotas.repositorio.UsuarioRepository;

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
    private UsuarioRepository usuarioRepository;
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

    // --- NUEVO ENDPOINT PARA PDF DE INDICADORES ---
    @GetMapping("/admin/indicadores/pdf")
    public String descargarReporteIndicadoresPdf(Principal principal, HttpServletResponse response) {
        Usuario usuario = null;
        if (principal != null) {
            usuario = usuarioRepository.findByCorreo(principal.getName()).orElse(null);
        }
        String nombreAdmin = usuario != null ? usuario.getNombre() + " " + usuario.getApellido() : "Administrador";

        IndicadoresDTO datos = calcularDatosIndicadores();

        try {
            pdfReporteServicio.exportarReporteIndicadores(response, datos, nombreAdmin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Evita buscar vista HTML
    }

    @GetMapping("/admin/indicadores")
    public String mostrarIndicadores(Model model) {
        IndicadoresDTO datos = calcularDatosIndicadores();

        model.addAttribute("promedioTiempo", datos.promedioTiempo);
        model.addAttribute("colorBadge", datos.colorBadgeTiempo);
        model.addAttribute("estadoTexto", datos.estadoTextoTiempo);

        model.addAttribute("totalNotas", datos.totalNotas);
        model.addAttribute("notasCorregidas", datos.notasCorregidas);
        model.addAttribute("porcentajeErrores", datos.porcentajeErrores);
        model.addAttribute("colorBadgeErrores", datos.colorBadgeErrores);
        model.addAttribute("estadoTextoErrores", datos.estadoTextoErrores);

        model.addAttribute("reportesEsperados", datos.reportesEsperados);
        model.addAttribute("reportesImplementados", datos.reportesImplementados);
        model.addAttribute("disponibilidadReportes", datos.disponibilidadReportes);
        model.addAttribute("colorBadgeReportes", datos.colorBadgeReportes);
        model.addAttribute("estadoTextoReportes", datos.estadoTextoReportes);

        model.addAttribute("tablaCursos", datos.tablaCursos);

        model.addAttribute("notasATiempo", datos.notasATiempo);
        model.addAttribute("porcentajeAlDia", datos.porcentajeAlDia);
        model.addAttribute("colorBadgeAlDia", datos.colorBadgeAlDia);
        model.addAttribute("estadoTextoAlDia", datos.estadoTextoAlDia);

        model.addAttribute("totalEncuestas", datos.totalEncuestas);
        model.addAttribute("promedioSatisfaccion", datos.promedioSatisfaccion);
        model.addAttribute("porcentajeSatisfaccion", datos.porcentajeSatisfaccion);
        model.addAttribute("colorBadgeSatisfaccion", datos.colorBadgeSatisfaccion);
        model.addAttribute("estadoTextoSatisfaccion", datos.estadoTextoSatisfaccion);

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

    // --- LÓGICA CENTRALIZADA PARA INDICADORES ---
    private IndicadoresDTO calcularDatosIndicadores() {
        IndicadoresDTO dto = new IndicadoresDTO();
        List<Nota> todasLasNotas = notaRepository.findAll();

        // 1. Tiempo Promedio
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
        dto.promedioTiempo = BigDecimal.ZERO;
        dto.colorBadgeTiempo = "bg-secondary";
        dto.estadoTextoTiempo = "Sin datos";
        if (notasValidas > 0) {
            double promedio = (double) totalDias / notasValidas;
            dto.promedioTiempo = new BigDecimal(promedio).setScale(2, RoundingMode.HALF_UP);
            if (dto.promedioTiempo.compareTo(new BigDecimal("1")) < 0) {
                dto.colorBadgeTiempo = "bg-success";
                dto.estadoTextoTiempo = "Óptimo";
            } else if (dto.promedioTiempo.compareTo(new BigDecimal("2")) <= 0) {
                dto.colorBadgeTiempo = "bg-warning text-dark";
                dto.estadoTextoTiempo = "Aceptable";
            } else {
                dto.colorBadgeTiempo = "bg-danger";
                dto.estadoTextoTiempo = "Crítico";
            }
        }

        // 2. Errores Consolidación
        dto.totalNotas = notaRepository.count();
        dto.notasCorregidas = notaRepository.countByCorregidaTrue();
        dto.porcentajeErrores = BigDecimal.ZERO;
        dto.colorBadgeErrores = "bg-secondary";
        dto.estadoTextoErrores = "Sin datos";
        if (dto.totalNotas > 0) {
            double porcentaje = ((double) dto.notasCorregidas / dto.totalNotas) * 100;
            dto.porcentajeErrores = new BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_UP);
            if (dto.porcentajeErrores.compareTo(new BigDecimal("3")) < 0) {
                dto.colorBadgeErrores = "bg-success";
                dto.estadoTextoErrores = "Óptimo";
            } else if (dto.porcentajeErrores.compareTo(new BigDecimal("10")) <= 0) {
                dto.colorBadgeErrores = "bg-warning text-dark";
                dto.estadoTextoErrores = "Aceptable";
            } else {
                dto.colorBadgeErrores = "bg-danger";
                dto.estadoTextoErrores = "Crítico";
            }
        }

        // 3. Disponibilidad
        dto.reportesEsperados = 5;
        dto.reportesImplementados = 5;
        dto.disponibilidadReportes = BigDecimal.ZERO;
        dto.colorBadgeReportes = "bg-secondary";
        dto.estadoTextoReportes = "Sin datos";
        if (dto.reportesEsperados > 0) {
            double porcentajeDisp = ((double) dto.reportesImplementados / dto.reportesEsperados) * 100;
            dto.disponibilidadReportes = new BigDecimal(porcentajeDisp).setScale(2, RoundingMode.HALF_UP);
            if (dto.disponibilidadReportes.compareTo(new BigDecimal("100")) == 0) {
                dto.colorBadgeReportes = "bg-success";
                dto.estadoTextoReportes = "Óptimo";
            } else if (dto.disponibilidadReportes.compareTo(new BigDecimal("80")) >= 0) {
                dto.colorBadgeReportes = "bg-warning text-dark";
                dto.estadoTextoReportes = "Aceptable";
            } else {
                dto.colorBadgeReportes = "bg-danger";
                dto.estadoTextoReportes = "Crítico";
            }
        }

        // 4. Aprobación Curso
        List<Curso> todosLosCursos = cursoRepository.findAll();
        dto.tablaCursos = new ArrayList<>();
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
            dto.tablaCursos.add(new FilaIndicadorCursoDTO(
                    curso.getNombreCurso(), curso.getSeccion().toUpperCase(),
                    totalAlumnos, aprobados, desaprobados, porcentajeAprobados, badgeClase));
        }

        // 5. Notas a tiempo
        dto.notasATiempo = 0;
        for (Nota nota : todasLasNotas) {
            if (nota.getFechaRegistro() != null && nota.getEvaluacion() != null
                    && nota.getEvaluacion().getFecha() != null) {
                if (!nota.getFechaRegistro().isAfter(nota.getEvaluacion().getFecha())) {
                    dto.notasATiempo++;
                }
            }
        }
        dto.porcentajeAlDia = BigDecimal.ZERO;
        dto.colorBadgeAlDia = "bg-secondary";
        dto.estadoTextoAlDia = "Sin datos";
        if (dto.totalNotas > 0) {
            double porcentaje = ((double) dto.notasATiempo / dto.totalNotas) * 100;
            dto.porcentajeAlDia = new BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_UP);
            if (dto.porcentajeAlDia.compareTo(new BigDecimal("95")) >= 0) {
                dto.colorBadgeAlDia = "bg-success";
                dto.estadoTextoAlDia = "Óptimo";
            } else if (dto.porcentajeAlDia.compareTo(new BigDecimal("80")) >= 0) {
                dto.colorBadgeAlDia = "bg-warning text-dark";
                dto.estadoTextoAlDia = "Aceptable";
            } else {
                dto.colorBadgeAlDia = "bg-danger";
                dto.estadoTextoAlDia = "Crítico";
            }
        }

        // 6. Satisfacción Docente
        List<EncuestaDocente> encuestas = encuestaDocenteRepository.findAll();
        dto.totalEncuestas = encuestas.size();
        dto.promedioSatisfaccion = BigDecimal.ZERO;
        dto.porcentajeSatisfaccion = BigDecimal.ZERO;
        dto.colorBadgeSatisfaccion = "bg-secondary";
        dto.estadoTextoSatisfaccion = "Sin datos";
        if (dto.totalEncuestas > 0) {
            double sumaCalificaciones = 0;
            for (EncuestaDocente e : encuestas) {
                sumaCalificaciones += e.getCalificacion();
            }
            double promedioDocentes = sumaCalificaciones / dto.totalEncuestas;
            dto.promedioSatisfaccion = new BigDecimal(promedioDocentes).setScale(1, RoundingMode.HALF_UP);
            double porcentajeSat = (promedioDocentes / 5.0) * 100;
            dto.porcentajeSatisfaccion = new BigDecimal(porcentajeSat).setScale(2, RoundingMode.HALF_UP);

            if (dto.porcentajeSatisfaccion.compareTo(new BigDecimal("80")) >= 0) {
                dto.colorBadgeSatisfaccion = "bg-success";
                dto.estadoTextoSatisfaccion = "Óptimo";
            } else if (dto.porcentajeSatisfaccion.compareTo(new BigDecimal("60")) >= 0) {
                dto.colorBadgeSatisfaccion = "bg-warning text-dark";
                dto.estadoTextoSatisfaccion = "Aceptable";
            } else {
                dto.colorBadgeSatisfaccion = "bg-danger";
                dto.estadoTextoSatisfaccion = "Crítico";
            }
        }

        return dto;
    }

    // --- DTOs Internos ---
    public static class IndicadoresDTO {
        public BigDecimal promedioTiempo;
        public String estadoTextoTiempo;
        public String colorBadgeTiempo;
        public long totalNotas;
        public long notasCorregidas;
        public BigDecimal porcentajeErrores;
        public String estadoTextoErrores;
        public String colorBadgeErrores;
        public int reportesEsperados;
        public int reportesImplementados;
        public BigDecimal disponibilidadReportes;
        public String estadoTextoReportes;
        public String colorBadgeReportes;
        public List<FilaIndicadorCursoDTO> tablaCursos;
        public long notasATiempo;
        public BigDecimal porcentajeAlDia;
        public String estadoTextoAlDia;
        public String colorBadgeAlDia;
        public int totalEncuestas;
        public BigDecimal promedioSatisfaccion;
        public BigDecimal porcentajeSatisfaccion;
        public String estadoTextoSatisfaccion;
        public String colorBadgeSatisfaccion;
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
}