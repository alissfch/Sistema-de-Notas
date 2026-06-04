package com.integrador.SistemaDeNotas.modelo.entidades;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Alumno")
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno")
    private Integer idAlumno;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento; // Mapea perfectamente con el tipo DATE de MySQL

    @Column(nullable = false)
    private String grado;

    @Column(nullable = false)
    private String seccion;

    @Column(nullable = false)
    private boolean estado;

    // --- RELACIONES REVERSAS (One-To-Many) ---
    // Un alumno puede tener muchas matrículas, muchas notas y muchas asistencias.
    // El "mappedBy" indica el nombre del objeto 'alumno' dentro de las clases Matricula, Nota y Asistencia.

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<Matricula> matriculas;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<Nota> notas;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<Asistencia> asistencias;

    // Constructor vacío obligatorio para JPA
    public Alumno() {
    }

    // --- GETTERS Y SETTERS ---

    public Integer getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(Integer idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(List<Matricula> matriculas) {
        this.matriculas = matriculas;
    }

    public List<Nota> getNotas() {
        return notas;
    }

    public void setNotas(List<Nota> notas) {
        this.notas = notas;
    }

    public List<Asistencia> getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(List<Asistencia> asistencias) {
        this.asistencias = asistencias;
    }
}