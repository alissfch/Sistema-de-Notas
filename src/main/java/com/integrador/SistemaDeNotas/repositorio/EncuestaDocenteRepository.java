package com.integrador.SistemaDeNotas.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integrador.SistemaDeNotas.modelo.entidades.Docente;
import com.integrador.SistemaDeNotas.modelo.entidades.EncuestaDocente;

@Repository
public interface EncuestaDocenteRepository extends JpaRepository<EncuestaDocente, Integer> {
    boolean existsByDocente(Docente docente);
}