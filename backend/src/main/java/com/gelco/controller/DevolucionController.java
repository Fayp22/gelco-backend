package com.gelco.controller;

import com.gelco.dto.CrearDevolucionRequest;
import com.gelco.dto.DevolucionResponse;
import com.gelco.dto.ErrorResponse;
import com.gelco.service.DevolucionService;
import com.gelco.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/devoluciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Devoluciones", description = "Validar devoluciones y cambios - HU14")
public class DevolucionController {

    private final DevolucionService devolucionService;
    private final JwtUtil jwtUtil;

    // ── GET /devoluciones (historial) ─────────────────────────────
    @GetMapping
    @Operation(summary = "Listar todas las devoluciones procesadas")
    public ResponseEntity<?> getAllDevoluciones() {
        try {
            return ResponseEntity.ok(devolucionService.getAllDevoluciones());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener devoluciones", e.getMessage()));
        }
    }

    // ── GET /devoluciones/detalle/{detallePedidoId} ───────────────
    @GetMapping("/detalle/{detallePedidoId}")
    @Operation(summary = "Devoluciones ya registradas para un ítem de pedido")
    public ResponseEntity<?> getDevolucionesByDetalle(@PathVariable Integer detallePedidoId) {
        try {
            return ResponseEntity.ok(devolucionService.getDevolucionesByDetalle(detallePedidoId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener devoluciones", e.getMessage()));
        }
    }

    // ── GET /devoluciones/detalle/{id}/disponible ─────────────────
    @GetMapping("/detalle/{detallePedidoId}/disponible")
    @Operation(summary = "Cantidad aún disponible para devolver de un ítem")
    public ResponseEntity<?> getCantidadDisponible(@PathVariable Integer detallePedidoId) {
        try {
            Integer disponible = devolucionService.getCantidadDisponibleParaDevolucion(detallePedidoId);
            return ResponseEntity.ok(Map.of("disponible", disponible));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Detalle no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al calcular disponible", e.getMessage()));
        }
    }

    // ── POST /devoluciones ─────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('RECEPCIONISTA')")
    @Operation(summary = "Registrar una devolución o cambio (HU14)")
    public ResponseEntity<?> crearDevolucion(
            @Valid @RequestBody CrearDevolucionRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long usuarioId = getUsuarioIdFromJwt(httpRequest);
            DevolucionResponse devolucion = devolucionService.crearDevolucion(usuarioId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(devolucion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error al registrar devolución", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error interno", e.getMessage()));
        }
    }

    private Long getUsuarioIdFromJwt(HttpServletRequest request) {
        String token = extraerToken(request);
        Long usuarioId = jwtUtil.getUsuarioIdFromToken(token);
        if (usuarioId == null) throw new IllegalArgumentException("No se pudo obtener el usuario del token");
        return usuarioId;
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token de autorización no proporcionado");
        }
        return header.substring(7);
    }
}