package com.gelco.service;

import com.gelco.model.Consultora;
import com.gelco.model.PasswordResetToken;
import com.gelco.model.Perfil;
import com.gelco.model.TokenBlacklist;
import com.gelco.model.Usuario;
import com.gelco.repository.ConsultoraRepository;
import com.gelco.repository.PasswordResetTokenRepository;
import com.gelco.repository.PerfilRepository;
import com.gelco.repository.TokenBlacklistRepository;
import com.gelco.repository.UsuarioRepository;
import com.gelco.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public boolean emailExists(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ConsultoraRepository consultoraRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${file.upload-dir:./uploads/fotos-perfil}")
    private String uploadDir;

    public void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La contraseña debe tener al menos una mayúscula");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("La contraseña debe tener al menos un número");
        }
        boolean tieneEspecial = false;
        for (char c : password.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) { tieneEspecial = true; break; }
        }
        if (!tieneEspecial) {
            throw new IllegalArgumentException("La contraseña debe tener al menos un carácter especial");
        }
    }

    public Map<String, Object> register(String email, String password, String nombre, String perfilNombre,
                                        String dni, String telefono, String direccion, String nivel, MultipartFile foto) {
        try {
            List<String> rolesPermitidos = List.of("CONSULTORA", "DISTRIBUIDOR");
            if (!rolesPermitidos.contains(perfilNombre)) {
                throw new IllegalArgumentException("No se puede registrar con ese perfil");
            }
            log.debug("=== REGISTER DEBUG ===");
            log.debug("email: {}, nombre: {}, perfil: {}", email, nombre, perfilNombre);
            log.debug("password length: {}", password != null ? password.length() : "null");

            validatePassword(password);
            log.debug("validatePassword PASSED");

            if (usuarioRepository.existsByEmail(email)) {
                log.debug("Email ya existe en BD: {}", email);
                throw new IllegalArgumentException("Email ya registrado");
            }
            log.debug("Email no existe, continuando...");

            Perfil perfil = perfilRepository.findByNombre(perfilNombre)
                    .orElseThrow(() -> {
                        log.error("Perfil no encontrado: {}", perfilNombre);
                        return new IllegalArgumentException("Perfil no encontrado");
                    });
            log.debug("Perfil encontrado: {}", perfil.getNombre());

            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            log.debug("Password a encodear (length): {}", password.length());
            String encodedPw = passwordEncoder.encode(password);
            log.debug("Password encodeado: {}", encodedPw.substring(0, Math.min(20, encodedPw.length())) + "...");
            usuario.setPasswordHash(encodedPw);
            usuario.setNombre(nombre);
            usuario.setPerfil(perfil);
            usuario.setEstado(true);
            log.debug("Usuario entity preparado, guardando...");

            if (foto != null && !foto.isEmpty()) {
                String fotoUrl = saveFoto(foto, email);
                log.debug("Foto guardada: {}", fotoUrl);
                usuario.setFotoUrl(fotoUrl);
            }

            Usuario savedUsuario = usuarioRepository.save(usuario);
            log.debug("Usuario guardado con ID: {}", savedUsuario.getId());

            String token = jwtUtil.generateToken(savedUsuario.getEmail(), savedUsuario.getNombre(), perfil.getNombre(), savedUsuario.getId());
            log.debug("Token generado para usuario: {}", savedUsuario.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");
            response.put("token", token);
            response.put("usuario", Map.of(
                    "id", savedUsuario.getId(),
                    "email", savedUsuario.getEmail(),
                    "nombre", savedUsuario.getNombre(),
                    "perfil", perfil.getNombre()
            ));

            if ("CONSULTORA".equals(perfilNombre)) {
                Consultora consultora = new Consultora();
                consultora.setUsuario(savedUsuario);
                consultora.setDni(dni != null && !dni.isBlank() ? dni : "00000000");
                consultora.setTelefono(telefono);
                consultora.setDireccion(direccion);
                consultora.setVentasTotales(java.math.BigDecimal.ZERO);
                String nivelFinal = (nivel != null && !nivel.isBlank()) ? nivel : "Bronce";
                nivelFinal = nivelFinal.substring(0, 1).toUpperCase() + nivelFinal.substring(1).toLowerCase();
                if (!List.of("Bronce", "Plata", "Oro").contains(nivelFinal)) {
                    nivelFinal = "Bronce";
                }
                consultora.setNivel(nivelFinal);
                consultoraRepository.save(consultora);
                log.debug("Consultora guardada con ID: {}", consultora.getId());
            }

            log.debug("=== REGISTER SUCCESS ===");
            return response;
        } catch (IllegalArgumentException e) {
            log.warn("IllegalArgumentException en register: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception en register: {} - {}", e.getClass().getName(), e.getMessage(), e);
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
        }
    }

    public String saveFoto(MultipartFile foto, String email) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String originalFilename = foto.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = email.replaceAll("[^a-zA-Z0-9]", "_") + "_" + UUID.randomUUID().toString() + extension;

        Path targetPath = uploadPath.resolve(filename);
        Files.copy(foto.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/fotos-perfil/" + filename;
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

    public Map<String, Object> logout(String token) {
        try {
            String jti = jwtUtil.getJtiFromToken(token);
            String email = jwtUtil.getUsernameFromToken(token);
            LocalDateTime expiracion = jwtUtil.getExpirationFromToken(token).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            TokenBlacklist blacklistEntry = TokenBlacklist.builder()
                    .tokenJti(jti)
                    .email(email)
                    .revocadoEn(LocalDateTime.now())
                    .expiracion(expiracion)
                    .build();

            tokenBlacklistRepository.save(blacklistEntry);

            return Map.of("message", "Sesión cerrada exitosamente");
        } catch (Exception e) {
            throw new RuntimeException("Error al cerrar sesión: " + e.getMessage());
        }
    }

    public Map<String, Object> refreshToken(String token) {
        try {
            if (!jwtUtil.isTokenValid(token)) {
                throw new IllegalArgumentException("Token inválido o expirado");
            }

            String jti = jwtUtil.getJtiFromToken(token);
            if (tokenBlacklistRepository.existsByTokenJti(jti)) {
                throw new IllegalArgumentException("Token revocado");
            }

            String email = jwtUtil.getUsernameFromToken(token);
            String nombre = jwtUtil.getNombreFromToken(token);
            String perfil = jwtUtil.getPerfilFromToken(token);
            Long usuarioId = jwtUtil.getUsuarioIdFromToken(token);

            TokenBlacklist blacklistEntry = TokenBlacklist.builder()
                    .tokenJti(jti)
                    .email(email)
                    .revocadoEn(LocalDateTime.now())
                    .expiracion(jwtUtil.getExpirationFromToken(token).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                    .build();
            tokenBlacklistRepository.save(blacklistEntry);

            String newToken = jwtUtil.generateToken(email, nombre, perfil, usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("token", newToken);
            response.put("message", "Token renovado exitosamente");
            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al renovar token: " + e.getMessage());
        }
    }

    public Map<String, Object> forgotPassword(String email) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Email no registrado"));

            String resetToken = UUID.randomUUID().toString();
            LocalDateTime expiracion = LocalDateTime.now().plusHours(1);

            PasswordResetToken tokenEntity = PasswordResetToken.builder()
                    .token(resetToken)
                    .usuario(usuario)
                    .expiracion(expiracion)
                    .usado(false)
                    .creadoEn(LocalDateTime.now())
                    .build();

            passwordResetTokenRepository.save(tokenEntity);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Se ha generado un token de recuperación de contraseña");
            response.put("resetToken", resetToken);
            response.put("expiresIn", "1 hora");
            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar token de recuperación: " + e.getMessage());
        }
    }

    public Map<String, Object> resetPassword(String token, String newPassword) {
        try {
            validatePassword(newPassword);

            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                    .orElseThrow(() -> new IllegalArgumentException("Token de recuperación inválido"));

            if (resetToken.getUsado()) {
                throw new IllegalArgumentException("Token de recuperación ya utilizado");
            }

            if (resetToken.getExpiracion().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Token de recuperación expirado");
            }

            Usuario usuario = resetToken.getUsuario();
            usuario.setPasswordHash(passwordEncoder.encode(newPassword));
            usuarioRepository.save(usuario);

            resetToken.setUsado(true);
            passwordResetTokenRepository.save(resetToken);

            return Map.of("message", "Contraseña actualizada exitosamente");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al resetear contraseña: " + e.getMessage());
        }
    }

    public Map<String, Object> changePassword(Long usuarioId, String currentPassword, String newPassword) {
        try {
            validatePassword(newPassword);

            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (!passwordEncoder.matches(currentPassword, usuario.getPasswordHash())) {
                throw new IllegalArgumentException("Contraseña actual incorrecta");
            }

            usuario.setPasswordHash(passwordEncoder.encode(newPassword));
            usuarioRepository.save(usuario);

            return Map.of("message", "Contraseña actualizada exitosamente");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al cambiar contraseña: " + e.getMessage());
        }
    }
}
