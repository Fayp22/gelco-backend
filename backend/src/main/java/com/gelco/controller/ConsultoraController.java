package com.gelco.controller;

import com.gelco.dto.ConsultoraResponse;
import com.gelco.dto.ErrorResponse;
import com.gelco.service.ConsultoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consultoras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConsultoraController {

    private final ConsultoraService consultoraService;

    @GetMapping
    public ResponseEntity<?> getAllConsultoras() {
        try {
            List<ConsultoraResponse> consultoras = consultoraService.getAllConsultoras();
            return ResponseEntity.ok(consultoras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener consultoras", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getConsultoraById(@PathVariable Long id) {
        try {
            ConsultoraResponse consultora = consultoraService.getConsultoraById(id);
            return ResponseEntity.ok(consultora);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Consultora no encontrada", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener consultora", e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getConsultoraByUsuario(@PathVariable Long usuarioId) {
        try {
            ConsultoraResponse consultora = consultoraService.getConsultoraByUsuario(usuarioId);
            return ResponseEntity.ok(consultora);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Consultora no encontrada", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener consultora", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createConsultora(
            @RequestParam Long usuarioId,
            @RequestParam String dni,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String nivel) {
        try {
            ConsultoraResponse consultora = consultoraService.createConsultora(usuarioId, dni, direccion, telefono, nivel);
            return ResponseEntity.status(HttpStatus.CREATED).body(consultora);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Datos inválidos", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al crear consultora", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateConsultora(
            @PathVariable Long id,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String nivel) {
        try {
            ConsultoraResponse consultora = consultoraService.updateConsultora(id, dni, direccion, telefono, nivel);
            return ResponseEntity.ok(consultora);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Consultora no encontrada", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al actualizar consultora", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConsultora(@PathVariable Long id) {
        try {
            consultoraService.deleteConsultora(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Consultora no encontrada", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al eliminar consultora", e.getMessage()));
        }
    }
}
