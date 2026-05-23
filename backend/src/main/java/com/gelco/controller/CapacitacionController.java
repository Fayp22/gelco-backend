package com.gelco.controller;

import com.gelco.dto.CapacitacionConsultoraResponse;
import com.gelco.dto.ErrorResponse;
import com.gelco.service.CapacitacionService;
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

    @DeleteMapping("/{id}")
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
}
