package com.integrador.SistemaDeNotas.controlador;

import java.math.BigDecimal;
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