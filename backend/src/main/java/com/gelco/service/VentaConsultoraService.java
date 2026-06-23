package com.gelco.service;

import com.gelco.model.Consultora;
import com.gelco.model.VentaConsultora;
import com.gelco.repository.ConsultoraRepository;
import com.gelco.repository.VentaConsultoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VentaConsultoraService {

    private final VentaConsultoraRepository ventaConsultoraRepository;
    private final ConsultoraRepository consultoraRepository;

    public List<VentaConsultora> getVentasByConsultora(Long consultoraId) {
        return ventaConsultoraRepository.findByConsultoraId(consultoraId);
    }

    public Map<String, Object> getVentasByConsultoraAndMonth(Long consultoraId, Integer mes, Integer anio) {
        List<VentaConsultora> ventas = ventaConsultoraRepository.buscarVentasPorConsultoraYMes(consultoraId, mes, anio);

        BigDecimal totalVentas = ventas.stream()
                .map(VentaConsultora::getTotalVentas)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> response = new HashMap<>();
        response.put("consultoraId", consultoraId);
        response.put("mes", mes);
        response.put("anio", anio);
        response.put("totalVentas", totalVentas);
        response.put("registros", ventas.size());
        response.put("ventas", ventas);
        return response;
    }

    public Map<String, Object> getCurrentMonthVentas(Long consultoraId) {
        LocalDate now = LocalDate.now();
        return getVentasByConsultoraAndMonth(consultoraId, now.getMonthValue(), now.getYear());
    }

    public Map<String, Object> getVentasResumen(Long consultoraId) {
        Consultora consultora = consultoraRepository.findById(consultoraId)
                .orElseThrow(() -> new IllegalArgumentException("Consultora no encontrada"));

        List<VentaConsultora> todasLasVentas = ventaConsultoraRepository.findByConsultoraId(consultoraId);

        BigDecimal totalGeneral = todasLasVentas.stream()
                .map(VentaConsultora::getTotalVentas)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate now = LocalDate.now();
        BigDecimal ventasMesActual = todasLasVentas.stream()
                .filter(v -> v.getMes().equals(now.getMonthValue()) && v.getAnio().equals(now.getYear()))
                .map(VentaConsultora::getTotalVentas)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int mesAnterior = now.getMonthValue() == 1 ? 12 : now.getMonthValue() - 1;
        int anioAnterior = now.getMonthValue() == 1 ? now.getYear() - 1 : now.getYear();
        BigDecimal ventasMesAnterior = todasLasVentas.stream()
                .filter(v -> v.getMes().equals(mesAnterior) && v.getAnio().equals(anioAnterior))
                .map(VentaConsultora::getTotalVentas)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal crecimiento = BigDecimal.ZERO;
        if (ventasMesAnterior.compareTo(BigDecimal.ZERO) > 0) {
            crecimiento = ventasMesActual.subtract(ventasMesAnterior)
                    .divide(ventasMesAnterior, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("consultoraId", consultoraId);
        response.put("nombre", consultora.getUsuario().getNombre());
        response.put("nivel", consultora.getNivel());
        response.put("ventasTotales", consultora.getVentasTotales());
        response.put("totalGeneralRegistrado", totalGeneral);
        response.put("ventasMesActual", ventasMesActual);
        response.put("ventasMesAnterior", ventasMesAnterior);
        response.put("crecimientoPorcentaje", crecimiento);
        response.put("historialVentas", todasLasVentas);
        return response;
    }

    public VentaConsultora registrarVenta(Long consultoraId, Integer mes, Integer anio, BigDecimal totalVentas) {
        Consultora consultora = consultoraRepository.findById(consultoraId)
                .orElseThrow(() -> new IllegalArgumentException("Consultora no encontrada"));

        VentaConsultora venta = new VentaConsultora();
        venta.setConsultora(consultora);
        venta.setMes(mes);
        venta.setAnio(anio);
        venta.setTotalVentas(totalVentas);

        return ventaConsultoraRepository.save(venta);
    }
}
