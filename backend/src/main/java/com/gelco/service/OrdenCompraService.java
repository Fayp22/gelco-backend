package com.gelco.service;

import com.gelco.dto.OrdenCompraResponse;
import com.gelco.model.*;
import com.gelco.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final FacturaRepository     facturaRepository;
    private final PedidoRepository      pedidoRepository;
    private final ConsultoraRepository  consultoraRepository;

    // ── Obtener pedidos disponibles para generar OC ──────────────
    public List<OrdenCompraResponse> getPedidosDisponibles(Long usuarioId) {
        Consultora consultora = consultoraRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Consultora no encontrada"));

        return pedidoRepository.findByConsultoraId(consultora.getId())
                .stream()
                .filter(p -> p.getEstado().equals("Creado"))
                .map(p -> toResponse(p, false, null, null))
                .toList();
    }

    // ── Obtener órdenes ya generadas ─────────────────────────────
    public List<OrdenCompraResponse> getMisOrdenes(Long usuarioId) {
        return ordenCompraRepository.findByPedidoConsultoraUsuarioId(usuarioId)
                .stream()
                .map(oc -> {
                    Factura factura = facturaRepository
                            .findByPedidoConsultoraUsuarioId(usuarioId)
                            .stream()
                            .filter(f -> f.getPedido().getId().equals(oc.getPedido().getId()))
                            .findFirst()
                            .orElse(null);
                    return toResponse(
                            oc.getPedido(),
                            true,
                            oc,
                            factura
                    );
                })
                .toList();
    }

    // ── Generar OC para múltiples pedidos ────────────────────────
    @Transactional
    public List<OrdenCompraResponse> generarOrdenes(Long usuarioId, List<Long> pedidoIds) {
        Consultora consultora = consultoraRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Consultora no encontrada"));

        return pedidoIds.stream().map(pedidoId -> {

            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + pedidoId));

            // Validar que el pedido pertenece a la consultora
            if (!pedido.getConsultora().getId().equals(consultora.getId())) {
                throw new IllegalArgumentException("El pedido no pertenece a esta consultora");
            }

            // Validar que no tenga ya una OC
            if (ordenCompraRepository.existsByPedidoId(pedidoId)) {
                throw new IllegalArgumentException("El pedido #" + pedidoId + " ya tiene una orden de compra");
            }

            // Validar estado
            if (!pedido.getEstado().equals("Creado")) {
                throw new IllegalArgumentException("El pedido #" + pedidoId + " no está en estado Creado");
            }

            // 1. Cambiar estado del pedido
            pedido.setEstado("Enviado a Almacén");
            pedidoRepository.save(pedido);

            // 2. Crear Orden de Compra
            OrdenCompra oc = new OrdenCompra();
            oc.setPedido(pedido);
            oc.setFecha(LocalDateTime.now());
            oc.setTotal(pedido.getTotal());
            ordenCompraRepository.save(oc);

            // 3. Crear Factura en estado Pendiente de Pago
            Factura factura = new Factura();
            factura.setPedido(pedido);
            factura.setTotal(pedido.getTotal());
            factura.setFecha(LocalDateTime.now());
            factura.setEstado("Pendiente de Pago");
            facturaRepository.save(factura);

            return toResponse(pedido, true, oc, factura);

        }).toList();
    }

    // ── Helper ───────────────────────────────────────────────────
    private OrdenCompraResponse toResponse(
            Pedido pedido,
            boolean tieneOrden,
            OrdenCompra oc,
            Factura factura) {

        return new OrdenCompraResponse(
                oc != null ? oc.getId() : null,
                pedido.getId(),
                pedido.getCliente() != null ? pedido.getCliente().getNombre() : "Sin cliente",
                oc != null ? oc.getFecha() : pedido.getFecha(),
                pedido.getTotal(),
                factura != null ? factura.getEstado() : null,
                factura != null ? factura.getId() : null,
                tieneOrden
        );
    }
}