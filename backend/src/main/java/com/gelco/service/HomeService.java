package com.gelco.service;

import com.gelco.model.Consultora;
import com.gelco.model.Usuario;
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
    private final PerfilRepository perfilRepository;

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

    public Map<String, Object> getRrhhHome() {
        Map<String, Object> home = new HashMap<>();

        List<Usuario> consultoras = usuarioRepository.findByPerfilNombre("CONSULTORA");
        long totalConsultoras = consultoras.size();
        long activas = consultoras.stream().filter(u -> u.getEstado() == null || u.getEstado()).count();
        long inactivas = totalConsultoras - activas;

        long capacitacionesPendientes = 0;
        long capacitacionesCompletadas = 0;
        for (Usuario u : consultoras) {
            try {
                Consultora c = consultoraRepository.findByUsuarioId(u.getId()).orElse(null);
                if (c != null) {
                    capacitacionesPendientes += capacitacionConsultoraRepository.countByConsultoraIdAndCompletado(c.getId(), false);
                    capacitacionesCompletadas += capacitacionConsultoraRepository.countByConsultoraIdAndCompletado(c.getId(), true);
                }
            } catch (Exception ignored) {}
        }

        BigDecimal ventasTotales = consultoras.stream()
                .map(u -> {
                    try {
                        Consultora c = consultoraRepository.findByUsuarioId(u.getId()).orElse(null);
                        return c != null ? c.getVentasTotales() : BigDecimal.ZERO;
                    } catch (Exception e) {
                        return BigDecimal.ZERO;
                    }
                })
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasPromedio = totalConsultoras > 0
                ? ventasTotales.divide(BigDecimal.valueOf(totalConsultoras), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        home.put("perfil", "RECURSOS_HUMANOS");
        home.put("totalConsultoras", totalConsultoras);
        home.put("activas", activas);
        home.put("inactivas", inactivas);
        home.put("totalVentas", ventasTotales);
        home.put("ventasPromedio", ventasPromedio);
        home.put("capacitacionesPendientes", capacitacionesPendientes);
        home.put("capacitacionesCompletadas", capacitacionesCompletadas);

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
