package com.integrador.SistemaDeNotas.modelo.entidades;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Asistencia")
public class Asistencia {

    public enum EstadoAsistencia {
    ASISTIO, FALTA, TARDANZA, FALTA_JUSTIFICADA
}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Integer idAsistencia;

    // Relación: Una asistencia le pertenece a un alumno
    @ManyToOne
    @JoinColumn(name = "id_alumno", nullable = false)
    private Alumno alumno;

    // Relación: Una asistencia ocurre dentro de un curso específico
    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING) // Guarda el texto, no el orden numérico
    @Column(nullable = false)
    private EstadoAsistencia estado;

    private String observacion;

    public Asistencia() {}

    // --- GETTERS Y SETTERS ---
    public Integer getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(Integer idAsistencia) { this.idAsistencia = idAsistencia; }

    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public EstadoAsistencia getEstado() { return estado; }
    public void setEstado(EstadoAsistencia estado) { this.estado = estado; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}