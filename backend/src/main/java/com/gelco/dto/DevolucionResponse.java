package com.gelco.dto;

import com.gelco.model.Devolucion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionResponse {

    private Integer id;
    private Integer detallePedidoId;
    private Long pedidoId;       // Queda en Long ya que el repositorio de Pedido lo usa internamente
    private String clienteNombre;
    private Integer productoId;
    private String productoNombre;
    private Integer cantidad;
    private String tipo;
    private String motivo;
    private String condicionProducto;
    private String estado;
    private String observaciones;
    private LocalDateTime fechaSolicitud;
    private String recepcionistaNombre;

    public static DevolucionResponse fromEntity(Devolucion d) {
        DevolucionResponse r = new DevolucionResponse();
        r.setId(d.getId());
        r.setDetallePedidoId(d.getDetallePedido().getId().intValue());
        r.setPedidoId(d.getDetallePedido().getPedido().getId());
        r.setClienteNombre(d.getDetallePedido().getPedido().getCliente().getNombre());
        r.setProductoId(d.getDetallePedido().getProducto().getId().intValue());
        r.setProductoNombre(d.getDetallePedido().getProducto().getNombre());
        r.setCantidad(d.getCantidad());
        r.setTipo(d.getTipo());
        r.setMotivo(d.getMotivo());
        r.setCondicionProducto(d.getCondicionProducto());
        r.setEstado(d.getEstado());
        r.setObservaciones(d.getObservaciones());
        r.setFechaSolicitud(d.getFechaSolicitud());
        r.setRecepcionistaNombre(d.getRecepcionista().getNombre());
        return r;
    }
}