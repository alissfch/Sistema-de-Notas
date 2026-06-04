package com.integrador.SistemaDeNotas.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integrador.SistemaDeNotas.modelo.entidades.Docente;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
}