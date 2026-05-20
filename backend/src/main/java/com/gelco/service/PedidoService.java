package com.gelco.service;

import com.gelco.dto.CrearPedidoRequest;
import com.gelco.dto.PedidoResponse;
import com.gelco.model.*;
import com.gelco.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository          pedidoRepository;
    private final ClienteRepository         clienteRepository;
    private final ConsultoraRepository      consultoraRepository;
    private final DetallePedidoRepository   detallePedidoRepository;
    private final ProductoRepository        productoRepository;

    // ── Listar todos ──────────────────────────────────────────────
    public List<PedidoResponse> getAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(p -> PedidoResponse.fromEntity(p, detallePedidoRepository.findByPedidoId(p.getId())))
                .collect(Collectors.toList());
    }

    public List<PedidoResponse> getPedidosByConsultora(Long consultoraId) {
        return pedidoRepository.findByConsultoraId(consultoraId).stream()
                .map(p -> PedidoResponse.fromEntity(p, detallePedidoRepository.findByPedidoId(p.getId())))
                .collect(Collectors.toList());
    }

    public List<PedidoResponse> getPedidosByCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(p -> PedidoResponse.fromEntity(p, detallePedidoRepository.findByPedidoId(p.getId())))
                .collect(Collectors.toList());
    }

    public List<PedidoResponse> getPedidosByEstado(String estado) {
        return pedidoRepository.findByEstado(estado).stream()
                .map(p -> PedidoResponse.fromEntity(p, detallePedidoRepository.findByPedidoId(p.getId())))
                .collect(Collectors.toList());
    }

    public PedidoResponse getPedidoById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con id: " + id));
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(id);
        return PedidoResponse.fromEntity(pedido, detalles);
    }

    // ── Crear pedido completo (HU06) ──────────────────────────────
    /**
     * Crea un pedido completo en una sola transacción:
     * 1. Valida que el cliente exista
     * 2. Resuelve la consultora a partir del usuarioId del JWT
     * 3. Valida stock de cada producto ANTES de descontar
     * 4. Guarda el pedido, los detalles y descuenta el stock
     * 5. Si algo falla, hace rollback completo
     */
    @Transactional
    public PedidoResponse crearPedidoCompleto(Long usuarioId, CrearPedidoRequest request) {

        // 1. Resolver consultora por usuario autenticado
        Consultora consultora = consultoraRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró una consultora asociada al usuario autenticado"));

        // 2. Validar cliente
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente no encontrado con id: " + request.getClienteId()));

        // 3. Cargar y validar productos (ANTES de guardar nada)
        List<ItemValidado> itemsValidados = request.getItems().stream()
                .map(item -> {
                    Producto producto = productoRepository.findById(item.getProductoId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Producto no encontrado con id: " + item.getProductoId()));

                    if (!producto.isActivo()) {
                        throw new IllegalArgumentException(
                                "El producto '" + producto.getNombre() + "' no está disponible");
                    }
                    if (producto.getStock() < item.getCantidad()) {
                        throw new IllegalArgumentException(
                                "Stock insuficiente para '" + producto.getNombre() +
                                        "'. Disponible: " + producto.getStock() +
                                        ", solicitado: " + item.getCantidad());
                    }
                    return new ItemValidado(producto, item.getCantidad());
                })
                .collect(Collectors.toList());

        // 4. Calcular total
        BigDecimal total = itemsValidados.stream()
                .map(i -> i.producto.getPrecio().multiply(BigDecimal.valueOf(i.cantidad)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Guardar pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setConsultora(consultora);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("En proceso");
        pedido.setTotal(total);
        pedidoRepository.save(pedido);

        // 6. Guardar detalles y descontar stock
        List<DetallePedido> detallesGuardados = itemsValidados.stream().map(item -> {
            // Descontar stock
            item.producto.setStock(item.producto.getStock() - item.cantidad);
            productoRepository.save(item.producto);

            // Guardar detalle
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(item.producto);
            detalle.setCantidad(item.cantidad);
            detalle.setPrecioUnitario(item.producto.getPrecio());
            return detallePedidoRepository.save(detalle);
        }).collect(Collectors.toList());

        return PedidoResponse.fromEntity(pedido, detallesGuardados);
    }

    // ── Actualizar estado ─────────────────────────────────────────
    @Transactional
    public PedidoResponse updatePedidoEstado(Long id, String estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con id: " + id));

        List<String> estadosValidos = List.of("En proceso", "En camino", "Entregado", "Cancelado");
        if (!estadosValidos.contains(estado)) {
            throw new IllegalArgumentException(
                    "Estado inválido. Valores permitidos: " + estadosValidos);
        }

        pedido.setEstado(estado);
        Pedido actualizado = pedidoRepository.save(pedido);
        return PedidoResponse.fromEntity(actualizado, detallePedidoRepository.findByPedidoId(id));
    }

    // ── Eliminar pedido ───────────────────────────────────────────
    @Transactional
    public void deletePedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con id: " + id));
        pedidoRepository.delete(pedido);
    }

    // ── Record interno de validación ──────────────────────────────
    private record ItemValidado(Producto producto, Integer cantidad) {}
}