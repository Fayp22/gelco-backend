package com.gelco.controller;

import com.gelco.dto.CrearPedidoRequest;
import com.gelco.dto.ErrorResponse;
import com.gelco.dto.PedidoResponse;
import com.gelco.service.PedidoService;
import com.gelco.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Gestión de pedidos - HU06")
public class PedidoController {

    private final PedidoService pedidoService;
    private final JwtUtil       jwtUtil;

    // ── GET /pedidos ──────────────────────────────────────────────
    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    public ResponseEntity<?> getAllPedidos() {
        try {
            List<PedidoResponse> pedidos = pedidoService.getAllPedidos();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener pedidos", e.getMessage()));
        }
    }

    // ── GET /pedidos/mis-pedidos ──────────────────────────────────
    @GetMapping("/mis-pedidos")
    @Operation(summary = "Obtener pedidos de la consultora autenticada")
    public ResponseEntity<?> getMisPedidos(HttpServletRequest httpRequest) {
        try {
            Long consultoraId = getConsultoraIdFromJwt(httpRequest);
            List<PedidoResponse> pedidos = pedidoService.getPedidosByConsultora(consultoraId);
            return ResponseEntity.ok(pedidos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error de autenticación", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener pedidos", e.getMessage()));
        }
    }

    // ── GET /pedidos/consultora/{id} ──────────────────────────────
    @GetMapping("/consultora/{consultoraId}")
    @Operation(summary = "Obtener pedidos por consultora (admin)")
    public ResponseEntity<?> getPedidosByConsultora(@PathVariable Long consultoraId) {
        try {
            return ResponseEntity.ok(pedidoService.getPedidosByConsultora(consultoraId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener pedidos", e.getMessage()));
        }
    }

    // ── GET /pedidos/cliente/{id} ─────────────────────────────────
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener pedidos por cliente")
    public ResponseEntity<?> getPedidosByCliente(@PathVariable Long clienteId) {
        try {
            return ResponseEntity.ok(pedidoService.getPedidosByCliente(clienteId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener pedidos", e.getMessage()));
        }
    }

    // ── GET /pedidos/{id} ─────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pedido por ID (incluye detalles)")
    public ResponseEntity<?> getPedidoById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pedidoService.getPedidoById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Pedido no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener pedido", e.getMessage()));
        }
    }

    // ── POST /pedidos ─────────────────────────────────────────────
    @PostMapping
    @Operation(summary = "Registrar un nuevo pedido completo (HU06)",
            description = "Valida stock, descuenta inventario y guarda pedido + detalles en una transacción.")
    public ResponseEntity<?> crearPedido(
            @Valid @RequestBody CrearPedidoRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long usuarioId = getUsuarioIdFromJwt(httpRequest);
            PedidoResponse pedido = pedidoService.crearPedidoCompleto(usuarioId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Error al crear pedido", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error interno", e.getMessage()));
        }
    }

    // ── PUT /pedidos/{id}/estado ──────────────────────────────────
    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar el estado de un pedido",
            description = "Estados válidos: En proceso | En camino | Entregado | Cancelado")
    public ResponseEntity<?> updateEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        try {
            return ResponseEntity.ok(pedidoService.updatePedidoEstado(id, estado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Datos inválidos", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al actualizar estado", e.getMessage()));
        }
    }

    // ── DELETE /pedidos/{id} ──────────────────────────────────────
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pedido")
    public ResponseEntity<?> deletePedido(@PathVariable Long id) {
        try {
            pedidoService.deletePedido(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Pedido no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al eliminar pedido", e.getMessage()));
        }
    }

    // ── Helpers JWT ───────────────────────────────────────────────
    private Long getUsuarioIdFromJwt(HttpServletRequest request) {
        String token = extraerToken(request);
        Long usuarioId = jwtUtil.getUsuarioIdFromToken(token);
        if (usuarioId == null) throw new IllegalArgumentException("No se pudo obtener el usuario del token");
        return usuarioId;
    }

    private Long getConsultoraIdFromJwt(HttpServletRequest request) {
        // Reutiliza getUsuarioId — el service resuelve la consultora por usuarioId
        return getUsuarioIdFromJwt(request);
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token de autorización no proporcionado");
        }
        return header.substring(7);
    }
}