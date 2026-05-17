package com.gelco.service;

import com.gelco.dto.RutaResponse;
import com.gelco.model.Ruta;
import com.gelco.model.Zona;
import com.gelco.model.Vehiculo;
import com.gelco.model.Chofer;
import com.gelco.repository.RutaRepository;
import com.gelco.repository.ZonaRepository;
import com.gelco.repository.VehiculoRepository;
import com.gelco.repository.ChoferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RutaService {

    private final RutaRepository rutaRepository;
    private final ZonaRepository zonaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ChoferRepository choferRepository;

    public List<RutaResponse> getAllRutas() {
        try {
            return rutaRepository.findAll()
                    .stream()
                    .map(RutaResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener rutas: " + e.getMessage());
        }
    }



    public List<RutaResponse> getRutasByChofer(Long choferId) {
        try {
            return rutaRepository.findByChoferId(choferId)
                    .stream()
                    .map(RutaResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener rutas por chofer: " + e.getMessage());
        }
    }

    public RutaResponse getRutaById(Long id) {
        try {
            Ruta ruta = rutaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada"));
            return RutaResponse.fromEntity(ruta);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener ruta: " + e.getMessage());
        }
    }

    public RutaResponse createRuta(Long zonaId, Long vehiculoId, Long choferId) {
        try {
            Zona zona = zonaRepository.findById(zonaId)
                    .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada"));
            
            Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                    .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
            
            Chofer chofer = choferRepository.findById(choferId)
                    .orElseThrow(() -> new IllegalArgumentException("Chofer no encontrado"));

            Ruta ruta = new Ruta();
            ruta.setZona(zona);
            ruta.setVehiculo(vehiculo);
            ruta.setChofer(chofer);
            ruta.setFecha(LocalDateTime.now());

            Ruta savedRuta = rutaRepository.save(ruta);
            return RutaResponse.fromEntity(savedRuta);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear ruta: " + e.getMessage());
        }
    }



    public void deleteRuta(Long id) {
        try {
            Ruta ruta = rutaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada"));
            rutaRepository.delete(ruta);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar ruta: " + e.getMessage());
        }
    }
}
