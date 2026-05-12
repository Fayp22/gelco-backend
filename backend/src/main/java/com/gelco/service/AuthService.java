package com.gelco.service;

import com.gelco.model.Perfil;
import com.gelco.model.Usuario;
import com.gelco.repository.PerfilRepository;
import com.gelco.repository.UsuarioRepository;
import com.gelco.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, Object> register(String email, String password, String nombre, String perfilNombre) {
        try {
            if (usuarioRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email ya registrado");
            }

            Perfil perfil = perfilRepository.findByNombre(perfilNombre)
                    .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado"));

            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setPasswordHash(passwordEncoder.encode(password));
            usuario.setNombre(nombre);
            usuario.setPerfil(perfil);
            usuario.setEstado(true);

            Usuario savedUsuario = usuarioRepository.save(usuario);

            String token = jwtUtil.generateToken(savedUsuario.getEmail(), savedUsuario.getNombre(), perfil.getNombre(), savedUsuario.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");
            response.put("token", token);
            response.put("usuario", Map.of(
                    "id", savedUsuario.getId(),
                    "email", savedUsuario.getEmail(),
                    "nombre", savedUsuario.getNombre(),
                    "perfil", perfil.getNombre()
            ));
            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
        }
    }

    public Map<String, Object> login(String email, String password) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Email o contraseña incorrectos"));

            if (!usuario.getEstado()) {
                throw new IllegalArgumentException("Usuario inactivo");
            }

            if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
                throw new IllegalArgumentException("Email o contraseña incorrectos");
            }

            String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getNombre(), usuario.getPerfil().getNombre(), usuario.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", Map.of(
                    "id", usuario.getId(),
                    "email", usuario.getEmail(),
                    "nombre", usuario.getNombre(),
                    "perfil", usuario.getPerfil().getNombre()
            ));
            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al iniciar sesión: " + e.getMessage());
        }
    }
}
