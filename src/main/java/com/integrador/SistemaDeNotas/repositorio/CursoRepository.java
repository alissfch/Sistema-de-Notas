package com.integrador.SistemaDeNotas.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integrador.SistemaDeNotas.modelo.entidades.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {
    
}
