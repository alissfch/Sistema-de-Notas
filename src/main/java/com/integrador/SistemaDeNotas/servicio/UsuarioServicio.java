package com.integrador.SistemaDeNotas.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.integrador.SistemaDeNotas.modelo.entidades.Alumno;
import com.integrador.SistemaDeNotas.modelo.entidades.Docente;
import com.integrador.SistemaDeNotas.modelo.entidades.Usuario;
import com.integrador.SistemaDeNotas.modelo.entidades.Curso;
import com.integrador.SistemaDeNotas.repositorio.CursoRepository;
import com.integrador.SistemaDeNotas.repositorio.AlumnoRepository;
import com.integrador.SistemaDeNotas.repositorio.DocenteRepository;
import com.integrador.SistemaDeNotas.repositorio.UsuarioRepository;

@Service
public class UsuarioServicio {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private AlumnoRepository alumnoRepo;
    @Autowired private DocenteRepository docenteRepo;
    @Autowired private CursoRepository cursoRepo;
    @Autowired private PasswordEncoder passwordEncoder;

@Transactional
public void registrarNuevoUsuario(String correo, String passwordRaw, String rol, 
                                  String nombres, String apellidos, String codigo, String grado, String seccion, List<Integer> cursoIds) {
    
    Usuario nuevoUsuario = new Usuario();
    nuevoUsuario.setNombre(nombres);
    nuevoUsuario.setApellido(apellidos);
    nuevoUsuario.setCorreo(correo);
    nuevoUsuario.setContrasena(passwordEncoder.encode(passwordRaw)); 
    nuevoUsuario.setRol(Usuario.RolUsuario.valueOf(rol.toUpperCase()));
    nuevoUsuario.setEstado(true);

    if (rol.equalsIgnoreCase("DOCENTE")) {
        Docente docente = new Docente();
        
        docente.setUsuario(nuevoUsuario);
        nuevoUsuario.setDocente(docente); 
        docente.setCodigo(codigo);
        
        usuarioRepo.save(nuevoUsuario);
        docenteRepo.save(docente);

        if (cursoIds != null && !cursoIds.isEmpty()) {
           for (Integer idCurso : cursoIds) {
                    Curso curso = cursoRepo.findById(idCurso).orElse(null);
                    
                    if (curso != null) {
                        curso.setDocente(docente);
                        cursoRepo.save(curso);
                    }
                }
        }
        
    } else if (rol.equalsIgnoreCase("ALUMNO")) {
        Alumno alumno = new Alumno();
        alumno.setNombres(nombres);
        alumno.setApellidos(apellidos);
        alumno.setEstado(true);
        alumno.setFechaNacimiento(LocalDate.now().minusYears(15));
        alumno.setGrado(grado);
        alumno.setSeccion(seccion);
        alumno.setCodigo(codigo);

        alumno.setUsuario(nuevoUsuario);
        nuevoUsuario.setAlumno(alumno);
        
        usuarioRepo.save(nuevoUsuario);
        alumnoRepo.save(alumno);
    }
}
}