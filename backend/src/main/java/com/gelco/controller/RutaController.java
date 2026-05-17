package com.gelco.controller;

import com.gelco.dto.ErrorResponse;
import com.gelco.dto.RutaResponse;
import com.gelco.service.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rutas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RutaController {

    private final RutaService rutaService;

    @GetMapping
    public ResponseEntity<?> getAllRutas() {
        try {
            List<RutaResponse> rutas = rutaService.getAllRutas();
            return ResponseEntity.ok(rutas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener rutas", e.getMessage()));
        }
    }

    @GetMapping("/chofer/{choferId}")
    public ResponseEntity<?> getRutasByChofer(@PathVariable Long choferId) {
        try {
            List<RutaResponse> rutas = rutaService.getRutasByChofer(choferId);
            return ResponseEntity.ok(rutas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener rutas", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRutaById(@PathVariable Long id) {
        try {
            RutaResponse ruta = rutaService.getRutaById(id);
            return ResponseEntity.ok(ruta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Ruta no encontrada", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener ruta", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createRuta(
            @RequestParam Long zonaId,
            @RequestParam Long vehiculoId,
            @RequestParam Long choferId) {
        try {
            RutaResponse ruta = rutaService.createRuta(zonaId, vehiculoId, choferId);
            return ResponseEntity.status(HttpStatus.CREATED).body(ruta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Datos inválidos", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al crear ruta", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRuta(@PathVariable Long id) {
        try {
            rutaService.deleteRuta(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Ruta no encontrada", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al eliminar ruta", e.getMessage()));
        }
    }
}
