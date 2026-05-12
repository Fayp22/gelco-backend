package com.gelco.service;

import com.gelco.dto.PedidoResponse;
import com.gelco.model.*;
import com.gelco.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ConsultoraRepository consultoraRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    public List<PedidoResponse> getAllPedidos() {
        try {
            return pedidoRepository.findAll()
                    .stream()
                    .map(PedidoResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener pedidos: " + e.getMessage());
        }
    }

    public List<PedidoResponse> getPedidosByConsultora(Long consultoraId) {
        try {
            return pedidoRepository.findByConsultoraId(consultoraId)
                    .stream()
                    .map(PedidoResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener pedidos por consultora: " + e.getMessage());
        }
    }

    public List<PedidoResponse> getPedidosByCliente(Long clienteId) {
        try {
            return pedidoRepository.findByClienteId(clienteId)
                    .stream()
                    .map(PedidoResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener pedidos por cliente: " + e.getMessage());
        }
    }

    public List<PedidoResponse> getPedidosByEstado(String estado) {
        try {
            return pedidoRepository.findByEstado(estado)
                    .stream()
                    .map(PedidoResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener pedidos por estado: " + e.getMessage());
        }
    }

    public PedidoResponse getPedidoById(Long id) {
        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
            return PedidoResponse.fromEntity(pedido);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener pedido: " + e.getMessage());
        }
    }

    public PedidoResponse createPedido(Long clienteId, Long consultoraId, String estado) {
        try {
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            
            Consultora consultora = consultoraRepository.findById(consultoraId)
                    .orElseThrow(() -> new IllegalArgumentException("Consultora no encontrada"));

            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setConsultora(consultora);
            pedido.setFecha(java.time.LocalDateTime.now());
            pedido.setEstado(estado != null ? estado : "En proceso");
            pedido.setTotal(BigDecimal.ZERO);

            Pedido savedPedido = pedidoRepository.save(pedido);
            return PedidoResponse.fromEntity(savedPedido);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear pedido: " + e.getMessage());
        }
    }

    public PedidoResponse updatePedidoEstado(Long id, String estado) {
        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
            
            pedido.setEstado(estado);
            Pedido updatedPedido = pedidoRepository.save(pedido);
            return PedidoResponse.fromEntity(updatedPedido);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar estado del pedido: " + e.getMessage());
        }
    }



    public void addDetallePedido(Long pedidoId, Long productoId, Integer cantidad) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detallePedidoRepository.save(detalle);

            BigDecimal nuevoTotal = pedido.getTotal().add(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
            pedido.setTotal(nuevoTotal);
            pedidoRepository.save(pedido);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar detalle del pedido: " + e.getMessage());
        }
    }

    public void deletePedido(Long id) {
        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
            pedidoRepository.delete(pedido);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar pedido: " + e.getMessage());
        }
    }
}
