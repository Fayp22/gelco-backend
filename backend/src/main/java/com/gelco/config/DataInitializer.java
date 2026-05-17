package com.gelco.config;

import com.gelco.model.Perfil;
import com.gelco.model.Usuario;
import com.gelco.repository.PerfilRepository;
import com.gelco.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only create demo users if they don't exist
        // The database already has perfiles, productos, consultoras, choferes, vehiculos, zonas
        
        if (!usuarioRepository.existsByEmail("admin@gelco.com")) {
            Perfil perfilAdmin = perfilRepository.findByNombre("ADMIN").orElse(null);
            
            if (perfilAdmin == null) {
                System.out.println("WARNING: ADMIN perfil not found, skipping admin user creation");
            } else {
                Usuario admin = Usuario.builder()
                        .email("admin@gelco.com")
                        .passwordHash(passwordEncoder.encode("123456"))
                        .nombre("Admin User")
                        .perfil(perfilAdmin)
                        .estado(true)
                        .build();
                usuarioRepository.save(admin);
            }
        }

        if (!usuarioRepository.existsByEmail("consultora@gelco.com")) {
            Perfil perfilConsultora = perfilRepository.findByNombre("CONSULTORA").orElse(null);
            
            if (perfilConsultora == null) {
                System.out.println("WARNING: CONSULTORA perfil not found, skipping consultora user creation");
            } else {
                Usuario consultora = Usuario.builder()
                        .email("consultora@gelco.com")
                        .passwordHash(passwordEncoder.encode("123456"))
                        .nombre("Consultora Demo")
                        .perfil(perfilConsultora)
                        .estado(true)
                        .build();
                usuarioRepository.save(consultora);
            }
        }

        if (!usuarioRepository.existsByEmail("distribuidor@gelco.com")) {
            Perfil perfilDistribuidor = perfilRepository.findByNombre("DISTRIBUIDOR").orElse(null);
            
            if (perfilDistribuidor == null) {
                System.out.println("WARNING: DISTRIBUIDOR perfil not found, skipping distribuidor user creation");
            } else {
                Usuario distribuidor = Usuario.builder()
                        .email("distribuidor@gelco.com")
                        .passwordHash(passwordEncoder.encode("123456"))
                        .nombre("Distribuidor Demo")
                        .perfil(perfilDistribuidor)
                        .estado(true)
                        .build();
                usuarioRepository.save(distribuidor);
            }
        }
    }
}
