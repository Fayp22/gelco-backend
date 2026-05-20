package com.gelco.dto;

import com.gelco.model.DetallePedido;
import com.gelco.model.Pedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private Long consultoraId;
    private String consultoraUsuarioNombre;
    private LocalDateTime fecha;
    private String estado;
    private BigDecimal total;
    private LocalDateTime updatedAt;
    private List<DetallePedidoResponse> detalles;

    // ── Detalle de cada línea ─────────────────────────────────────
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetallePedidoResponse {
        private Long id;
        private Long productoId;
        private String productoNombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;

        public static DetallePedidoResponse fromEntity(DetallePedido d) {
            return new DetallePedidoResponse(
                    d.getId(),
                    d.getProducto().getId(),
                    d.getProducto().getNombre(),
                    d.getCantidad(),
                    d.getPrecioUnitario(),
                    d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()))
            );
        }
    }

    // ── Constructor sin detalles (para listados simples) ──────────
    public static PedidoResponse fromEntity(Pedido pedido) {
        return fromEntity(pedido, List.of());
    }

    public static PedidoResponse fromEntity(Pedido pedido, List<DetallePedido> detalles) {
        PedidoResponse r = new PedidoResponse();
        r.setId(pedido.getId());
        r.setClienteId(pedido.getCliente().getId());
        r.setClienteNombre(pedido.getCliente().getNombre());
        r.setConsultoraId(pedido.getConsultora().getId());
        r.setConsultoraUsuarioNombre(pedido.getConsultora().getUsuario().getNombre());
        r.setFecha(pedido.getFecha());
        r.setEstado(pedido.getEstado());
        r.setTotal(pedido.getTotal());
        r.setUpdatedAt(pedido.getUpdatedAt());
        r.setDetalles(detalles.stream()
                .map(DetallePedidoResponse::fromEntity)
                .collect(Collectors.toList()));
        return r;
    }
}