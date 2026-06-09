package com.integrador.SistemaDeNotas;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.integrador.SistemaDeNotas.modelo.entidades.Administrador;
import com.integrador.SistemaDeNotas.modelo.entidades.Usuario;
import com.integrador.SistemaDeNotas.repositorio.UsuarioRepository;

@SpringBootApplication
public class SistemaDeNotasApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaDeNotasApplication.class, args);
	}

    @Bean
    public CommandLineRunner inicializarDatos(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepo.count() == 0) {
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setApellido("Principal");
                admin.setCorreo("admin@jcm.edu.pe");
                
                admin.setContrasena(passwordEncoder.encode("Admin123!")); 
                
                admin.setRol(Usuario.RolUsuario.ADMIN);
                admin.setEstado(true);

                Administrador detalleAdmin = new Administrador();
                detalleAdmin.setUsuario(admin);
                admin.setAdministrador(detalleAdmin);

                usuarioRepo.save(admin);
            }
        };
    }
}
