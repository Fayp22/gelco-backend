package com.gelco.dto;

import com.gelco.model.Pedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private Long consultoraId;
    private String consultoraDni;
    private LocalDateTime fecha;
    private String estado;
    private BigDecimal total;
    private LocalDateTime updatedAt;

    public static PedidoResponse fromEntity(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNombre(),
                pedido.getConsultora().getId(),
                pedido.getConsultora().getDni(),
                pedido.getFecha(),
                pedido.getEstado(),
                pedido.getTotal(),
                pedido.getUpdatedAt()
        );
    }
}
