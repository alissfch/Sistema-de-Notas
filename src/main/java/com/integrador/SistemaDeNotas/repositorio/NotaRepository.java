package com.integrador.SistemaDeNotas.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integrador.SistemaDeNotas.modelo.entidades.Nota;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Integer> {
    @Query("SELECT n FROM Nota n JOIN n.alumno a WHERE a.codigo = :codigoAlumno")
    List<Nota> findNotasPorCodigoAlumno(@Param("codigoAlumno") String codigoAlumno);

    long countByCorregidaTrue();
}