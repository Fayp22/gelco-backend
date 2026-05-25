package com.gelco.controller;

import com.gelco.dto.ErrorResponse;
import com.gelco.model.Usuario;
import com.gelco.repository.UsuarioRepository;
import com.gelco.service.AuthService;
import com.gelco.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    record UpdateUsuarioRequest(String nombre) {}

    record ChangePasswordRequest(String currentPassword, String newPassword) {}

    @PutMapping("/{id}/foto")
    public ResponseEntity<?> updateFoto(
            @PathVariable Long id,
            @RequestParam MultipartFile foto) {
        try {
            if (foto == null || foto.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(400, "Archivo requerido", "No se proporcionó ninguna imagen"));
            }
            String contentType = foto.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(400, "Tipo de archivo inválido", "Solo se permiten imágenes (JPG, PNG, GIF, WEBP)"));
            }
            if (foto.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse(400, "Archivo muy grande", "El tamaño máximo es 5MB"));
            }
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            String fotoUrl = authService.saveFoto(foto, usuario.getEmail());
            usuario.setFotoUrl(fotoUrl);
            usuarioRepository.save(usuario);

            String nuevoToken = jwtUtil.generateToken(
                    usuario.getEmail(),
                    usuario.getNombre(),
                    usuario.getPerfil().getNombre(),
                    usuario.getId()
            );

            return ResponseEntity.ok(Map.of(
                    "id", usuario.getId(),
                    "fotoUrl", fotoUrl,
                    "token", nuevoToken
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Usuario no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al subir foto", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(
            @PathVariable Long id,
            @RequestBody UpdateUsuarioRequest request) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (request.nombre() != null && !request.nombre().isBlank()) {
                usuario.setNombre(request.nombre());
            }

            usuarioRepository.save(usuario);

            String nuevoToken = jwtUtil.generateToken(
                    usuario.getEmail(),
                    usuario.getNombre(),
                    usuario.getPerfil().getNombre(),
                    usuario.getId()
            );

            return ResponseEntity.ok(Map.of(
                    "id", usuario.getId(),
                    "nombre", usuario.getNombre(),
                    "email", usuario.getEmail(),
                    "token", nuevoToken
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Usuario no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al actualizar usuario", e.getMessage()));
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        try {
            Map<String, Object> response = authService.changePassword(id, request.currentPassword(), request.newPassword());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error al cambiar contraseña", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al cambiar contraseña", e.getMessage()));
        }
    }
}
