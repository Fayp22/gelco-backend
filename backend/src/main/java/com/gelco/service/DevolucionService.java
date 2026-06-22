package com.gelco.service;

import com.gelco.dto.CrearDevolucionRequest;
import com.gelco.dto.DevolucionResponse;
import com.gelco.model.DetallePedido;
import com.gelco.model.Devolucion;
import com.gelco.model.Producto;
import com.gelco.model.Usuario;
import com.gelco.repository.DetallePedidoRepository;
import com.gelco.repository.DevolucionRepository;
import com.gelco.repository.ProductoRepository;
import com.gelco.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevolucionService {

    private static final List<String> TIPOS_VALIDOS = List.of("Cambio", "Devolucion");
    private static final List<String> CONDICIONES_VALIDAS = List.of("Apto", "No apto");

    private final DevolucionRepository devolucionRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public DevolucionResponse crearDevolucion(Long usuarioId, CrearDevolucionRequest request) {

        if (!TIPOS_VALIDOS.contains(request.getTipo())) {
            throw new IllegalArgumentException("Tipo inválido. Valores permitidos: " + TIPOS_VALIDOS);
        }
        if (!CONDICIONES_VALIDAS.contains(request.getCondicionProducto())) {
            throw new IllegalArgumentException("Condición inválida. Valores permitidos: " + CONDICIONES_VALIDAS);
        }

        DetallePedido detalle = detallePedidoRepository.findById(request.getDetallePedidoId().longValue())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Detalle de pedido no encontrado con id: " + request.getDetallePedidoId()));

        String estadoPedido = detalle.getPedido().getEstado();
        if (!"Entregado".equals(estadoPedido)) {
            throw new IllegalArgumentException(
                    "Solo se pueden procesar devoluciones de pedidos en estado 'Entregado'. Estado actual: " + estadoPedido);
        }

        Integer yaDevuelto = devolucionRepository.sumCantidadDevueltaByDetalleId(request.getDetallePedidoId().longValue());
        int disponible = detalle.getCantidad() - (yaDevuelto != null ? yaDevuelto : 0);
        if (request.getCantidad() > disponible) {
            throw new IllegalArgumentException(
                    "Cantidad solicitada excede lo disponible para devolver. Disponible: " + disponible +
                            ", solicitado: " + request.getCantidad());
        }

        Usuario recepcionista = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + usuarioId));

        if ("Apto".equals(request.getCondicionProducto())) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + request.getCantidad());
            productoRepository.save(producto);
        }
        // Si es "No apto", no se repone — el producto se pierde, pero el registro
        // queda igual para que el facturador ajuste la factura en otra HU.

        Devolucion devolucion = new Devolucion();
        devolucion.setDetallePedido(detalle);
        devolucion.setCantidad(request.getCantidad());
        devolucion.setTipo(request.getTipo());
        devolucion.setMotivo(request.getMotivo());
        devolucion.setCondicionProducto(request.getCondicionProducto());
        devolucion.setEstado("Procesada");
        devolucion.setObservaciones(request.getObservaciones());
        devolucion.setFechaSolicitud(LocalDateTime.now());
        devolucion.setRecepcionista(recepcionista);

        Devolucion guardada = devolucionRepository.save(devolucion);

        Devolucion conRelaciones = devolucionRepository.findByIdWithRelations(guardada.getId().longValue())
                .orElseThrow(() -> new IllegalStateException("Error al recuperar la devolución guardada"));
        return DevolucionResponse.fromEntity(conRelaciones);
    }

    @Transactional(readOnly = true)
    public List<DevolucionResponse> getAllDevoluciones() {
        return devolucionRepository.findAllWithRelations().stream()
                .map(DevolucionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DevolucionResponse> getDevolucionesByDetalle(Integer detallePedidoId) {
        // CORRECCIÓN: Agregado .longValue() para el repositorio
        return devolucionRepository.findByDetallePedidoId(detallePedidoId.longValue()).stream()
                .map(DevolucionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Integer getCantidadDisponibleParaDevolucion(Integer detallePedidoId) {
        DetallePedido detalle = detallePedidoRepository.findById(detallePedidoId.longValue())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Detalle de pedido no encontrado con id: " + detallePedidoId));
        Integer yaDevuelto = devolucionRepository.sumCantidadDevueltaByDetalleId(detallePedidoId.longValue());
        return detalle.getCantidad() - (yaDevuelto != null ? yaDevuelto : 0);
    }
}