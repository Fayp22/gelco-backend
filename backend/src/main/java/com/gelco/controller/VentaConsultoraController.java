package com.gelco.controller;

import com.gelco.dto.ErrorResponse;
import com.gelco.model.VentaConsultora;
import com.gelco.service.VentaConsultoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VentaConsultoraController {

    private final VentaConsultoraService ventaConsultoraService;

    @GetMapping("/consultora/{consultoraId}")
    public ResponseEntity<?> getVentasByConsultora(@PathVariable Long consultoraId) {
        try {
            var ventas = ventaConsultoraService.getVentasByConsultora(consultoraId);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener ventas", e.getMessage()));
        }
    }

    @GetMapping("/consultora/{consultoraId}/resumen")
    public ResponseEntity<?> getVentasResumen(@PathVariable Long consultoraId) {
        try {
            var resumen = ventaConsultoraService.getVentasResumen(consultoraId);
            return ResponseEntity.ok(resumen);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error al obtener resumen", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener resumen de ventas", e.getMessage()));
        }
    }

    @GetMapping("/consultora/{consultoraId}/mes/{mes}/anio/{anio}")
    public ResponseEntity<?> getVentasByMonth(
            @PathVariable Long consultoraId,
            @PathVariable Integer mes,
            @PathVariable Integer anio) {
        try {
            var ventas = ventaConsultoraService.getVentasByConsultoraAndMonth(consultoraId, mes, anio);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener ventas del mes", e.getMessage()));
        }
    }

    @GetMapping("/consultora/{consultoraId}/mes-actual")
    public ResponseEntity<?> getVentasMesActual(@PathVariable Long consultoraId) {
        try {
            var ventas = ventaConsultoraService.getCurrentMonthVentas(consultoraId);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener ventas del mes actual", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> registrarVenta(
            @RequestParam Long consultoraId,
            @RequestParam Integer mes,
            @RequestParam Integer anio,
            @RequestParam BigDecimal totalVentas) {
        try {
            VentaConsultora venta = ventaConsultoraService.registrarVenta(consultoraId, mes, anio, totalVentas);
            return ResponseEntity.status(HttpStatus.CREATED).body(venta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error al registrar venta", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al registrar venta", e.getMessage()));
        }
    }
}
