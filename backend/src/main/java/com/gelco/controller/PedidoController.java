package com.gelco.controller;

import com.gelco.dto.ErrorResponse;
import com.gelco.dto.PedidoResponse;
import com.gelco.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<?> getAllPedidos() {
        try {
            List<PedidoResponse> pedidos = pedidoService.getAllPedidos();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener pedidos", e.getMessage()));
        }
    }

    @GetMapping("/consultora/{consultoraId}")
    public ResponseEntity<?> getPedidosByConsultora(@PathVariable Long consultoraId) {
        try {
            List<PedidoResponse> pedidos = pedidoService.getPedidosByConsultora(consultoraId);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener pedidos", e.getMessage()));
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> getPedidosByCliente(@PathVariable Long clienteId) {
        try {
            List<PedidoResponse> pedidos = pedidoService.getPedidosByCliente(clienteId);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener pedidos", e.getMessage()));
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getPedidosByEstado(@PathVariable String estado) {
        try {
            List<PedidoResponse> pedidos = pedidoService.getPedidosByEstado(estado);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener pedidos", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPedidoById(@PathVariable Long id) {
        try {
            PedidoResponse pedido = pedidoService.getPedidoById(id);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Pedido no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al obtener pedido", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPedido(
            @RequestParam Long clienteId,
            @RequestParam Long consultoraId) {
        try {
            PedidoResponse pedido = pedidoService.createPedido(clienteId, consultoraId, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Datos inválidos", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al crear pedido", e.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> updatePedidoEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        try {
            PedidoResponse pedido = pedidoService.updatePedidoEstado(id, estado);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Pedido no encontrado", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al actualizar pedido", e.getMessage()));
        }
    }

    @PostMapping("/{pedidoId}/detalles")
    public ResponseEntity<?> addDetallePedido(
            @PathVariable Long pedidoId,
            @RequestParam Long productoId,
            @RequestParam Integer cantidad) {
        try {
            pedidoService.addDetallePedido(pedidoId, productoId, cantidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    java.util.Map.of("message", "Detalle agregado exitosamente")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Datos inválidos", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Error al agregar detalle", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
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
}
