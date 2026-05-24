package com.gelco.controller;

import com.gelco.dto.ErrorResponse;
import com.gelco.dto.OrdenCompraResponse;
import com.gelco.service.OrdenCompraService;
import com.gelco.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes-compra")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;
    private final JwtUtil            jwtUtil;

    // ── GET /ordenes-compra/disponibles ──────────────────────────
    // Pedidos en estado "Creado" listos para generar OC
    @GetMapping("/disponibles")
    public ResponseEntity<?> getDisponibles(HttpServletRequest request) {
        try {
            Long usuarioId = getUsuarioId(request);
            return ResponseEntity.ok(ordenCompraService.getPedidosDisponibles(usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error interno", e.getMessage()));
        }
    }

    // ── GET /ordenes-compra/mis-ordenes ──────────────────────────
    // Órdenes ya generadas por la consultora
    @GetMapping("/mis-ordenes")
    public ResponseEntity<?> getMisOrdenes(HttpServletRequest request) {
        try {
            Long usuarioId = getUsuarioId(request);
            return ResponseEntity.ok(ordenCompraService.getMisOrdenes(usuarioId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error interno", e.getMessage()));
        }
    }

    // ── POST /ordenes-compra/generar ─────────────────────────────
    // Genera OC para los pedidos seleccionados
    @PostMapping("/generar")
    public ResponseEntity<?> generarOrdenes(
            @RequestBody List<Long> pedidoIds,
            HttpServletRequest request) {
        try {
            Long usuarioId = getUsuarioId(request);
            List<OrdenCompraResponse> resultado =
                    ordenCompraService.generarOrdenes(usuarioId, pedidoIds);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error al generar orden", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error interno", e.getMessage()));
        }
    }

    // ── Helper JWT ───────────────────────────────────────────────
    private Long getUsuarioId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
            throw new IllegalArgumentException("Token no proporcionado");
        return jwtUtil.getUsuarioIdFromToken(header.substring(7));
    }
}