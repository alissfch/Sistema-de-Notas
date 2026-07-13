package com.integrador.SistemaDeNotas.controlador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.integrador.SistemaDeNotas.modelo.entidades.Alumno;
import com.integrador.SistemaDeNotas.modelo.entidades.Asistencia;
import com.integrador.SistemaDeNotas.modelo.entidades.Curso;
import com.integrador.SistemaDeNotas.modelo.entidades.Docente;
import com.integrador.SistemaDeNotas.modelo.entidades.Evaluacion;
import com.integrador.SistemaDeNotas.modelo.entidades.Evaluacion.TipoEvaluacion;
import com.integrador.SistemaDeNotas.modelo.entidades.Nota;
import com.integrador.SistemaDeNotas.modelo.entidades.Usuario;
import com.integrador.SistemaDeNotas.repositorio.AlumnoRepository;
import com.integrador.SistemaDeNotas.repositorio.AsistenciaRepository;
import com.integrador.SistemaDeNotas.repositorio.CursoRepository;
import com.integrador.SistemaDeNotas.repositorio.EvaluacionRepository;
import com.integrador.SistemaDeNotas.repositorio.NotaRepository;
import com.integrador.SistemaDeNotas.repositorio.UsuarioRepository;

@Controller
public class DocenteControlador {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EvaluacionRepository evaluacionRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private AlumnoRepository alumnoRepository;
    @Autowired private NotaRepository notaRepository;
    @Autowired private AsistenciaRepository asistenciaRepository;

    @GetMapping("/docente/panel")
    public String mostrarPanelDocente(Model model, Principal principal) {
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName()).get();
        Docente miDocente = usuario.getDocente();

        if (miDocente != null) {
             model.addAttribute("misCursos", miDocente.getCursosAsignados());
             model.addAttribute("nombreDocente", usuario.getNombre() + " " + usuario.getApellido());
        } else {
            return "redirect:/login?error=No se encontró el docente asociado a este usuario.";
        }
        return "docente/panel";
    }

    @GetMapping("/docente/curso/{id}")
    public String gestionarCurso(@PathVariable Integer id, 
                                  @RequestParam(required = false) Integer evaluacionId, 
                                  Model model, Principal principal) {
        
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName()).get();
        Docente miDocente = usuario.getDocente();
        Curso curso = cursoRepository.findById(id).orElse(null);

        if (curso == null) { return "redirect:/docente/panel?error=CursoNoEncontrado"; }
        if (!curso.getDocente().getId_docente().equals(miDocente.getId_docente())) {
            return "redirect:/docente/panel?error=AccesoDenegado"; 
        }

        List<Evaluacion> evaluacionesActivas = new ArrayList<>();
        if (curso.getEvaluaciones() != null) {
            evaluacionesActivas = curso.getEvaluaciones().stream()
                .filter(e -> e != null && e.isEstado())
                .collect(Collectors.toList());
        }

        Evaluacion evalSeleccionada = null;
        if (!evaluacionesActivas.isEmpty()) {
            if (evaluacionId != null) {
                evalSeleccionada = evaluacionRepository.findById(evaluacionId).orElse(null);
                if (evalSeleccionada != null && !evalSeleccionada.isEstado()) {
                    evalSeleccionada = null;
                }
            }
            if (evalSeleccionada == null) {
                evalSeleccionada = evaluacionesActivas.get(0);
            }
        }

        List<Alumno> alumnosDeLaSeccion = alumnoRepository.findBySeccion(curso.getSeccion());
        List<FilaAlumnoDTO> filasAlumnos = new ArrayList<>();

        for (Alumno alumno : alumnosDeLaSeccion) {
            Nota notaAlumno = null;
            Asistencia asistenciaAlumno = null;

            if (evalSeleccionada != null) {
                if (alumno.getNotas() != null) {
                    for (Nota n : alumno.getNotas()) {
                        if (n.getEvaluacion().getIdEvaluacion().equals(evalSeleccionada.getIdEvaluacion())) {
                            notaAlumno = n;
                            break;
                        }
                    }
                }
            }
            filasAlumnos.add(new FilaAlumnoDTO(alumno, notaAlumno, asistenciaAlumno));
        }

        filasAlumnos.sort((f1, f2) -> f1.getAlumno().getApellidos().compareToIgnoreCase(f2.getAlumno().getApellidos()));

        model.addAttribute("curso", curso);
        model.addAttribute("evaluacionesActivas", evaluacionesActivas); 
        model.addAttribute("evaluacionSeleccionada", evalSeleccionada);
        model.addAttribute("filasAlumnos", filasAlumnos); 
        model.addAttribute("nombreDocente", usuario.getNombre() + " " + usuario.getApellido());

        return "docente/curso-detalle"; 
    }

    @PostMapping("/docente/curso/{id}/evaluacion")
    public String crearEvaluacion(@PathVariable Integer id, @RequestParam String nombre,
                                  @RequestParam String tipo, @RequestParam String fecha,
                                  @RequestParam BigDecimal pesoPorcentual) {
        Curso curso = cursoRepository.findById(id).orElse(null);
        if (curso == null) { return "redirect:/docente/panel?error=CursoNoEncontrado"; }
        
        Evaluacion nuevaEvaluacion = new Evaluacion();
        nuevaEvaluacion.setNombre(nombre);
        nuevaEvaluacion.setTipo(TipoEvaluacion.valueOf(tipo));
        nuevaEvaluacion.setFecha(LocalDate.parse(fecha));
        nuevaEvaluacion.setPesoPorcentual(pesoPorcentual);
        nuevaEvaluacion.setEstado(true);
        nuevaEvaluacion.setCurso(curso);
        evaluacionRepository.save(nuevaEvaluacion);

        return "redirect:/docente/curso/" + id + "?evaluacionId=" + nuevaEvaluacion.getIdEvaluacion() + "&exito";
    }

    @PostMapping("/docente/curso/{id}/evaluacion/{idEval}/desactivar")
    public String desactivarEvaluacion(@PathVariable Integer id, @PathVariable Integer idEval) {
        Evaluacion eval = evaluacionRepository.findById(idEval).orElse(null);
        if (eval != null) {
            eval.setEstado(false); 
            evaluacionRepository.save(eval);
        }
        return "redirect:/docente/curso/" + id + "?eliminado";
    }

    @PostMapping("/docente/curso/{id}/evaluacion/{idEval}/eliminar")
    public String eliminarEvaluacionFisica(@PathVariable Integer id, @PathVariable Integer idEval) {
        evaluacionRepository.deleteById(idEval); 
        return "redirect:/docente/curso/" + id + "?eliminado";
    }

    @PostMapping("/docente/curso/{id}/nota")
    public String registrarNota(@PathVariable Integer id, 
                                @RequestParam Integer idAlumno, 
                                @RequestParam Integer idEvaluacion, 
                                @RequestParam(required = false) BigDecimal valor) {
        
        Alumno alumno = alumnoRepository.findById(idAlumno).orElse(null);
        Evaluacion evaluacion = evaluacionRepository.findById(idEvaluacion).orElse(null);

        if(alumno != null && evaluacion != null) {
            Nota notaExistente = null;
            if (alumno.getNotas() != null) {
                for (Nota n : alumno.getNotas()) {
                    if (n.getEvaluacion().getIdEvaluacion().equals(evaluacion.getIdEvaluacion())) {
                        notaExistente = n;
                        break;
                    }
                }
            }

            if (valor != null) {
                if (notaExistente == null) {
                    notaExistente = new Nota();
                    notaExistente.setAlumno(alumno);
                    notaExistente.setEvaluacion(evaluacion);
                    notaExistente.setFechaRegistro(LocalDate.now());
                }
                notaExistente.setValor(valor);
                notaRepository.save(notaExistente);
            }
        }
        return "redirect:/docente/curso/" + id + "?evaluacionId=" + idEvaluacion + "&exito";
    }

    @PostMapping("/docente/curso/{id}/asistencia")
    public String gestionarAsistencia(@PathVariable Integer id,
                                      @RequestParam Integer idAlumno,
                                      @RequestParam String fecha,
                                      @RequestParam String estado) {
        
        LocalDate fechaAsistencia = LocalDate.parse(fecha);
        Curso curso = cursoRepository.findById(id).orElse(null);
        Alumno alumno = alumnoRepository.findById(idAlumno).orElse(null);

        if (curso != null && alumno != null) {
            
            Asistencia asistenciaExistente = null;
            if (alumno.getAsistencias() != null) {
                for (Asistencia a : alumno.getAsistencias()) {
                    if (a.getCurso().getIdCurso().equals(curso.getIdCurso()) && a.getFecha().equals(fechaAsistencia)) {
                        asistenciaExistente = a;
                        break;
                    }
                }
            }

            if (estado.equals("ELIMINAR")) {
                if (asistenciaExistente != null) {
                    asistenciaRepository.delete(asistenciaExistente);
                }
            } else {
                if (asistenciaExistente == null) {
                    asistenciaExistente = new Asistencia();
                    asistenciaExistente.setAlumno(alumno);
                    asistenciaExistente.setCurso(curso);
                    asistenciaExistente.setFecha(fechaAsistencia);
                }
                asistenciaExistente.setEstado(Asistencia.EstadoAsistencia.valueOf(estado));
                asistenciaRepository.save(asistenciaExistente);
            }
        }
        return "redirect:/docente/curso/" + id + "?exito";
    }

    @GetMapping("/docente/reportes/notas")
    public String reporteNotas(@RequestParam(required = false) Integer cursoId, Model model, Principal principal) {
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName()).get();
        Docente miDocente = usuario.getDocente();

        List<Curso> misCursos = (miDocente != null && miDocente.getCursosAsignados() != null)
                ? miDocente.getCursosAsignados() : new ArrayList<>();

        Curso cursoSeleccionado = null;
        if (cursoId != null) {
            for (Curso c : misCursos) {
                if (c.getIdCurso().equals(cursoId)) { cursoSeleccionado = c; break; }
            }
        }
        if (cursoSeleccionado == null && !misCursos.isEmpty()) {
            cursoSeleccionado = misCursos.get(0);
        }

        List<Evaluacion> evaluacionesActivas = new ArrayList<>();
        List<FilaReporteNotaDTO> filas = new ArrayList<>();

        if (cursoSeleccionado != null) {
            if (cursoSeleccionado.getEvaluaciones() != null) {
                evaluacionesActivas = cursoSeleccionado.getEvaluaciones().stream()
                        .filter(e -> e != null && e.isEstado())
                        .sorted((a, b) -> a.getFecha().compareTo(b.getFecha()))
                        .collect(Collectors.toList());
            }

            List<Alumno> alumnos = alumnoRepository.findBySeccion(cursoSeleccionado.getSeccion());
            alumnos.sort((a1, a2) -> a1.getApellidos().compareToIgnoreCase(a2.getApellidos()));

            for (Alumno alumno : alumnos) {
                List<BigDecimal> valores = new ArrayList<>();
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
                    valores.add(valorNota);
                    if (valorNota != null && ev.getPesoPorcentual() != null) {
                        sumaPonderada = sumaPonderada.add(valorNota.multiply(ev.getPesoPorcentual()));
                        sumaPesos = sumaPesos.add(ev.getPesoPorcentual());
                    }
                }

                BigDecimal promedio = null;
                String estado = "Sin notas";
                if (sumaPesos.compareTo(BigDecimal.ZERO) > 0) {
                    promedio = sumaPonderada.divide(sumaPesos, 2, RoundingMode.HALF_UP);
                    if (promedio.compareTo(new BigDecimal("14")) >= 0) {
                        estado = "Aprobado";
                    } else if (promedio.compareTo(new BigDecimal("11")) >= 0) {
                        estado = "En proceso";
                    } else {
                        estado = "En inicio";
                    }
                }

                filas.add(new FilaReporteNotaDTO(alumno, valores, promedio, estado));
            }
        }

        model.addAttribute("misCursos", misCursos);
        model.addAttribute("cursoSeleccionado", cursoSeleccionado);
        model.addAttribute("evaluacionesActivas", evaluacionesActivas);
        model.addAttribute("filas", filas);
        model.addAttribute("nombreDocente", usuario.getNombre() + " " + usuario.getApellido());

        return "docente/reporte-notas";
    }

    @GetMapping("/docente/reportes/asistencia")
    public String reporteAsistencia(@RequestParam(required = false) Integer cursoId, Model model, Principal principal) {
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName()).get();
        Docente miDocente = usuario.getDocente();

        List<Curso> misCursos = (miDocente != null && miDocente.getCursosAsignados() != null)
                ? miDocente.getCursosAsignados() : new ArrayList<>();

        Curso cursoSeleccionado = null;
        if (cursoId != null) {
            for (Curso c : misCursos) {
                if (c.getIdCurso().equals(cursoId)) { cursoSeleccionado = c; break; }
            }
        }
        if (cursoSeleccionado == null && !misCursos.isEmpty()) {
            cursoSeleccionado = misCursos.get(0);
        }

        List<FilaReporteAsistenciaDTO> filas = new ArrayList<>();

        if (cursoSeleccionado != null) {
            List<Alumno> alumnos = alumnoRepository.findBySeccion(cursoSeleccionado.getSeccion());
            alumnos.sort((a1, a2) -> a1.getApellidos().compareToIgnoreCase(a2.getApellidos()));

            for (Alumno alumno : alumnos) {
                int asistio = 0, tardanza = 0, falta = 0, justificada = 0;
                if (alumno.getAsistencias() != null) {
                    for (Asistencia a : alumno.getAsistencias()) {
                        if (a.getCurso() == null || !a.getCurso().getIdCurso().equals(cursoSeleccionado.getIdCurso())) {
                            continue;
                        }
                        switch (a.getEstado()) {
                            case ASISTIO -> asistio++;
                            case TARDANZA -> tardanza++;
                            case FALTA -> falta++;
                            case FALTA_JUSTIFICADA -> justificada++;
                        }
                    }
                }

                int total = asistio + tardanza + falta + justificada;
                BigDecimal porcentaje = null;
                String estado = "Sin registros";
                if (total > 0) {
                    BigDecimal efectivas = BigDecimal.valueOf(asistio + tardanza);
                    porcentaje = efectivas.multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
                    if (porcentaje.compareTo(new BigDecimal("90")) >= 0) {
                        estado = "Bueno";
                    } else if (porcentaje.compareTo(new BigDecimal("80")) >= 0) {
                        estado = "Regular";
                    } else {
                        estado = "Riesgo";
                    }
                }

                filas.add(new FilaReporteAsistenciaDTO(alumno, asistio, tardanza, falta, justificada, total, porcentaje, estado));
            }
        }

        model.addAttribute("misCursos", misCursos);
        model.addAttribute("cursoSeleccionado", cursoSeleccionado);
        model.addAttribute("filas", filas);
        model.addAttribute("nombreDocente", usuario.getNombre() + " " + usuario.getApellido());

        return "docente/reporte-asistencia";
    }

    public static class FilaReporteNotaDTO {
        private final Alumno alumno;
        private final List<BigDecimal> valores;
        private final BigDecimal promedio;
        private final String estado;

        public FilaReporteNotaDTO(Alumno alumno, List<BigDecimal> valores, BigDecimal promedio, String estado) {
            this.alumno = alumno;
            this.valores = valores;
            this.promedio = promedio;
            this.estado = estado;
        }
        public Alumno getAlumno() { return alumno; }
        public List<BigDecimal> getValores() { return valores; }
        public BigDecimal getPromedio() { return promedio; }
        public String getEstado() { return estado; }
    }

    public static class FilaReporteAsistenciaDTO {
        private final Alumno alumno;
        private final int asistio;
        private final int tardanza;
        private final int falta;
        private final int justificada;
        private final int total;
        private final BigDecimal porcentaje;
        private final String estado;

        public FilaReporteAsistenciaDTO(Alumno alumno, int asistio, int tardanza, int falta, int justificada,
                                         int total, BigDecimal porcentaje, String estado) {
            this.alumno = alumno;
            this.asistio = asistio;
            this.tardanza = tardanza;
            this.falta = falta;
            this.justificada = justificada;
            this.total = total;
            this.porcentaje = porcentaje;
            this.estado = estado;
        }
        public Alumno getAlumno() { return alumno; }
        public int getAsistio() { return asistio; }
        public int getTardanza() { return tardanza; }
        public int getFalta() { return falta; }
        public int getJustificada() { return justificada; }
        public int getTotal() { return total; }
        public BigDecimal getPorcentaje() { return porcentaje; }
        public String getEstado() { return estado; }
    }

    public static class FilaAlumnoDTO {
        private final Alumno alumno;
        private final Nota nota;
        private final Asistencia asistencia;

        public FilaAlumnoDTO(Alumno alumno, Nota nota, Asistencia asistencia) {
            this.alumno = alumno;
            this.nota = nota;
            this.asistencia = asistencia;
        }
        public Alumno getAlumno() { return alumno; }
        public Nota getNota() { return nota; }
        public Asistencia getAsistencia() { return asistencia; }
    }
}