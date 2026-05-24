package com.gelco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraResponse {
    private Long id;
    private Long pedidoId;
    private String clienteNombre;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String estadoFactura;
    private Long facturaId;
    private boolean tieneOrden;
}