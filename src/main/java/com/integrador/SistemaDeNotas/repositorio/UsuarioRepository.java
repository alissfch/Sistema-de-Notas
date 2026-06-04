package com.integrador.SistemaDeNotas.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integrador.SistemaDeNotas.modelo.entidades.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Este método es crucial para el Login.
    // Spring leerá el nombre "findByCorreo" y automáticamente creará la consulta SQL: 
    // SELECT * FROM usuario WHERE correo = ?
    Optional<Usuario> findByCorreo(String correo);
    
}