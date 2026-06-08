package com.integrador.SistemaDeNotas.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integrador.SistemaDeNotas.modelo.entidades.Alumno;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
    List<Alumno> findBySeccion(String seccion);
}
