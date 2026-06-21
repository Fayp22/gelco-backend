package com.gelco.controller;

import com.gelco.dto.CapacitacionConsultoraResponse;
import com.gelco.dto.CapacitacionRequest;
import com.gelco.dto.CapacitacionResponse;
import com.gelco.dto.ErrorResponse;
import com.gelco.dto.PreguntaResponse;
import com.gelco.model.Consultora;
import com.gelco.repository.ConsultoraRepository;
import com.gelco.service.CapacitacionService;
import com.gelco.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/capacitaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CapacitacionController {

    private final CapacitacionService capacitacionService;
    private final ConsultoraRepository consultoraRepository;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getAllCapacitaciones() {
        try {
            List<CapacitacionResponse> capacitaciones = capacitacionService.getAllCapacitaciones();
            return ResponseEntity.ok(capacitaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener capacitaciones", e.getMessage()));
        }
    }

    @GetMapping("/consultora/{consultoraId}")
    public ResponseEntity<?> getCapacitacionesByConsultora(@PathVariable Long consultoraId) {
        try {
            List<CapacitacionConsultoraResponse> capacitaciones = capacitacionService.getCapacitacionesByConsultora(consultoraId);
            return ResponseEntity.ok(capacitaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener capacitaciones", e.getMessage()));
        }
    }

    @GetMapping("/mis-capacitaciones")
    public ResponseEntity<?> getMisCapacitaciones(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long usuarioId = jwtUtil.getUsuarioIdFromToken(token);
            Consultora consultora = consultoraRepository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró consultora para este usuario"));
            List<CapacitacionConsultoraResponse> capacitaciones = capacitacionService.getCapacitacionesByConsultora(consultora.getId());
            return ResponseEntity.ok(capacitaciones);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error al obtener mis capacitaciones", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener mis capacitaciones", e.getMessage()));
        }
    }

    @GetMapping("/{capacitacionId}/consultoras")
    public ResponseEntity<?> getConsultorasByCapacitacion(@PathVariable Long capacitacionId) {
        try {
            List<CapacitacionConsultoraResponse> consultoras = capacitacionService.getCapacitacionesByCapacitacion(capacitacionId);
            return ResponseEntity.ok(consultoras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener consultoras", e.getMessage()));
        }
    }

    @GetMapping("/{capacitacionId}/preguntas")
    public ResponseEntity<?> getPreguntasByCapacitacion(@PathVariable Long capacitacionId) {
        try {
            List<PreguntaResponse> preguntas = capacitacionService.getPreguntasByCapacitacion(capacitacionId);
            return ResponseEntity.ok(preguntas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener preguntas", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCapacitacion(@Valid @RequestBody CapacitacionRequest request) {
        try {
            CapacitacionResponse capacitacion = capacitacionService.createCapacitacion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(capacitacion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al crear capacitación", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCapacitacion(
            @PathVariable Long id,
            @RequestBody CapacitacionRequest request) {
        try {
            CapacitacionResponse capacitacion = capacitacionService.updateCapacitacion(id, request);
            return ResponseEntity.ok(capacitacion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Capacitación no encontrada", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al actualizar capacitación", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCapacitacion(@PathVariable Long id) {
        try {
            capacitacionService.deleteCapacitacion(id);
            return ResponseEntity.ok(Map.of("message", "Capacitación eliminada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Capacitación no encontrada", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al eliminar capacitación", e.getMessage()));
        }
    }

    @PostMapping("/inscribir")
    public ResponseEntity<?> inscribirConsultora(
            @RequestParam Long capacitacionId,
            @RequestParam Long consultoraId) {
        try {
            CapacitacionConsultoraResponse inscripcion = capacitacionService.inscribirConsultora(capacitacionId, consultoraId);
            return ResponseEntity.status(HttpStatus.CREATED).body(inscripcion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error al inscribir", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al inscribir consultora", e.getMessage()));
        }
    }

    @PostMapping("/{capacitacionId}/inscribirse")
    public ResponseEntity<?> inscribirse(
            @PathVariable Long capacitacionId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long usuarioId = jwtUtil.getUsuarioIdFromToken(token);
            Consultora consultora = consultoraRepository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró consultora para este usuario"));
            CapacitacionConsultoraResponse inscripcion = capacitacionService.inscribirConsultora(capacitacionId, consultora.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(inscripcion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error al inscribirse", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al inscribirse", e.getMessage()));
        }
    }

    @PutMapping("/{id}/completar")
    public ResponseEntity<?> completarCapacitacion(
            @PathVariable Long id,
            @RequestParam BigDecimal puntaje) {
        try {
            CapacitacionConsultoraResponse inscripcion = capacitacionService.completarCapacitacion(id, puntaje);
            return ResponseEntity.ok(inscripcion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error al completar", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al completar capacitación", e.getMessage()));
        }
    }

    @DeleteMapping("/inscripcion/{id}")
    public ResponseEntity<?> eliminarInscripcion(@PathVariable Long id) {
        try {
            capacitacionService.deleteCapacitacionConsultora(id);
            return ResponseEntity.ok().body(Map.of("message", "Inscripción eliminada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Inscripción no encontrada", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al eliminar inscripción", e.getMessage()));
        }
    }

    @DeleteMapping("/{capacitacionId}/cancelar")
    public ResponseEntity<?> cancelarMiInscripcion(
            @PathVariable Long capacitacionId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long usuarioId = jwtUtil.getUsuarioIdFromToken(token);
            Consultora consultora = consultoraRepository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró consultora para este usuario"));
            capacitacionService.cancelarInscripcion(capacitacionId, consultora.getId());
            return ResponseEntity.ok().body(Map.of("message", "Inscripción cancelada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error al cancelar", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al cancelar inscripción", e.getMessage()));
        }
    }
}
