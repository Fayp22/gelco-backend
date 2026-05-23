package com.gelco.service;

import com.gelco.model.Consultora;
import com.gelco.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ConsultoraRepository consultoraRepository;
    private final VentaConsultoraRepository ventaConsultoraRepository;
    private final CapacitacionConsultoraRepository capacitacionConsultoraRepository;
    private final ClienteRepository clienteRepository;
    private final RutaRepository rutaRepository;
    private final UsuarioRepository usuarioRepository;

    public Map<String, Object> getPublicHome() {
        Map<String, Object> home = new HashMap<>();
        home.put("appName", "GELCO - Sistema de Gestión");
        home.put("version", "1.0.0");
        home.put("activeProducts", productoRepository.countByActivoTrue());
        home.put("totalClients", clienteRepository.count());
        return home;
    }

    public Map<String, Object> getConsultoraHome(Long usuarioId) {
        Map<String, Object> home = new HashMap<>();

        Consultora consultora = consultoraRepository.findByUsuarioId(usuarioId)
                .orElse(null);

        if (consultora == null) {
            home.put("error", "No se encontró perfil de consultora");
            return home;
        }

        Long consultoraId = consultora.getId();

        long pedidosPendientes = pedidoRepository.countByConsultoraIdAndEstado(consultoraId, "En proceso");
        long pedidosEnCamino = pedidoRepository.countByConsultoraIdAndEstado(consultoraId, "En camino");
        long pedidosEntregados = pedidoRepository.countByConsultoraIdAndEstado(consultoraId, "Entregado");
        long capacitacionesPendientes = capacitacionConsultoraRepository.countByConsultoraIdAndCompletado(consultoraId, false);
        long capacitacionesCompletadas = capacitacionConsultoraRepository.countByConsultoraIdAndCompletado(consultoraId, true);

        LocalDate now = LocalDate.now();
        List<com.gelco.model.VentaConsultora> ventasMes = ventaConsultoraRepository.findByConsultoraIdAndMesAndAnio(
                consultoraId, now.getMonthValue(), now.getYear());

        BigDecimal ventasDelMes = ventasMes.stream()
                .map(com.gelco.model.VentaConsultora::getTotalVentas)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        home.put("perfil", "CONSULTORA");
        home.put("consultoraId", consultoraId);
        home.put("nivel", consultora.getNivel());
        home.put("ventasTotales", consultora.getVentasTotales());
        home.put("pedidosPendientes", pedidosPendientes);
        home.put("pedidosEnCamino", pedidosEnCamino);
        home.put("pedidosEntregados", pedidosEntregados);
        home.put("ventasDelMes", ventasDelMes);
        home.put("capacitacionesPendientes", capacitacionesPendientes);
        home.put("capacitacionesCompletadas", capacitacionesCompletadas);

        return home;
    }

    public Map<String, Object> getAdminHome() {
        Map<String, Object> home = new HashMap<>();

        long totalUsuarios = usuarioRepository.count();
        long totalProductos = productoRepository.countByActivoTrue();
        long totalClientes = clienteRepository.count();
        long totalConsultoras = consultoraRepository.count();
        long pedidosEnProceso = pedidoRepository.countByEstado("En proceso");
        long pedidosEnCamino = pedidoRepository.countByEstado("En camino");
        long pedidosEntregados = pedidoRepository.countByEstado("Entregado");
        long totalRutas = rutaRepository.count();

        home.put("perfil", "ADMIN");
        home.put("totalUsuarios", totalUsuarios);
        home.put("totalProductos", totalProductos);
        home.put("totalClientes", totalClientes);
        home.put("totalConsultoras", totalConsultoras);
        home.put("pedidosEnProceso", pedidosEnProceso);
        home.put("pedidosEnCamino", pedidosEnCamino);
        home.put("pedidosEntregados", pedidosEntregados);
        home.put("totalRutas", totalRutas);

        return home;
    }

    public Map<String, Object> getDistribuidorHome() {
        Map<String, Object> home = new HashMap<>();

        long pedidosEnProceso = pedidoRepository.countByEstado("En proceso");
        long pedidosEnCamino = pedidoRepository.countByEstado("En camino");
        long pedidosEntregados = pedidoRepository.countByEstado("Entregado");
        long totalRutas = rutaRepository.count();
        long totalProductos = productoRepository.countByActivoTrue();

        home.put("perfil", "DISTRIBUIDOR");
        home.put("pedidosEnProceso", pedidosEnProceso);
        home.put("pedidosEnCamino", pedidosEnCamino);
        home.put("pedidosEntregados", pedidosEntregados);
        home.put("totalRutas", totalRutas);
        home.put("totalProductos", totalProductos);

        return home;
    }
}
