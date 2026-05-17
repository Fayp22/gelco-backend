package com.gelco.controller;

import com.gelco.dto.ErrorResponse;
import com.gelco.model.Usuario;
import com.gelco.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    record UpdateUsuarioRequest(String nombre) {}

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody UpdateUsuarioRequest request) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (request.nombre() != null && !request.nombre().isBlank()) {
                usuario.setNombre(request.nombre());
            }

            usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of(
                    "id", usuario.getId(),
                    "nombre", usuario.getNombre(),
                    "email", usuario.getEmail()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Usuario no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            Remove-Item -Recurse -Force .git.body(new ErrorResponse(500, "Error al actualizar usuario", e.getMessage()));
        }
    }
}