package com.gelco.service;

import com.gelco.dto.CapacitacionConsultoraResponse;
import com.gelco.dto.CapacitacionResponse;
import com.gelco.dto.CapacitacionRequest;
import com.gelco.model.Capacitacion;
import com.gelco.model.CapacitacionConsultora;
import com.gelco.model.Consultora;
import com.gelco.repository.CapacitacionConsultoraRepository;
import com.gelco.repository.CapacitacionRepository;
import com.gelco.repository.ConsultoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapacitacionService {

    private final CapacitacionRepository capacitacionRepository;
    private final CapacitacionConsultoraRepository capacitacionConsultoraRepository;
    private final ConsultoraRepository consultoraRepository;

    public List<CapacitacionResponse> getAllCapacitaciones() {
        try {
            return capacitacionRepository.findAll()
                    .stream()
                    .map(CapacitacionResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener capacitaciones: " + e.getMessage());
        }
    }

    public List<CapacitacionConsultoraResponse> getCapacitacionesByConsultora(Long consultoraId) {
        try {
            return capacitacionConsultoraRepository.findByConsultoraId(consultoraId)
                    .stream()
                    .map(CapacitacionConsultoraResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener capacitaciones por consultora: " + e.getMessage());
        }
    }

    public List<CapacitacionConsultoraResponse> getCapacitacionesByCapacitacion(Long capacitacionId) {
        try {
            return capacitacionConsultoraRepository.findByCapacitacionId(capacitacionId)
                    .stream()
                    .map(CapacitacionConsultoraResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener consultoras por capacitación: " + e.getMessage());
        }
    }

    @Transactional
    public CapacitacionResponse createCapacitacion(CapacitacionRequest request) {
        try {
            Capacitacion capacitacion = new Capacitacion();
            capacitacion.setTitulo(request.getTitulo());
            capacitacion.setDescripcion(request.getDescripcion());
            capacitacion.setFecha(request.getFecha());
            capacitacion.setActivo(request.getActivo() != null ? request.getActivo() : true);
            capacitacion.setDuracionMinutos(request.getDuracionMinutos());
            capacitacion.setTipo(request.getTipo());
            capacitacion.setUrlContenido(request.getUrlContenido());

            Capacitacion saved = capacitacionRepository.save(capacitacion);
            return CapacitacionResponse.fromEntity(saved);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear capacitación: " + e.getMessage());
        }
    }

    @Transactional
    public CapacitacionResponse updateCapacitacion(Long id, CapacitacionRequest request) {
        try {
            Capacitacion capacitacion = capacitacionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Capacitación no encontrada"));

            if (request.getTitulo() != null) capacitacion.setTitulo(request.getTitulo());
            if (request.getDescripcion() != null) capacitacion.setDescripcion(request.getDescripcion());
            if (request.getFecha() != null) capacitacion.setFecha(request.getFecha());
            if (request.getActivo() != null) capacitacion.setActivo(request.getActivo());
            if (request.getDuracionMinutos() != null) capacitacion.setDuracionMinutos(request.getDuracionMinutos());
            if (request.getTipo() != null) capacitacion.setTipo(request.getTipo());
            if (request.getUrlContenido() != null) capacitacion.setUrlContenido(request.getUrlContenido());

            Capacitacion saved = capacitacionRepository.save(capacitacion);
            return CapacitacionResponse.fromEntity(saved);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar capacitación: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteCapacitacion(Long id) {
        try {
            if (!capacitacionRepository.existsById(id)) {
                throw new IllegalArgumentException("Capacitación no encontrada");
            }
            capacitacionRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar capacitación: " + e.getMessage());
        }
    }

    @Transactional
    public CapacitacionConsultoraResponse inscribirConsultora(Long capacitacionId, Long consultoraId) {
        try {
            Capacitacion capacitacion = capacitacionRepository.findById(capacitacionId)
                    .orElseThrow(() -> new IllegalArgumentException("Capacitación no encontrada"));

            Consultora consultora = consultoraRepository.findById(consultoraId)
                    .orElseThrow(() -> new IllegalArgumentException("Consultora no encontrada"));

            CapacitacionConsultora inscripcion = new CapacitacionConsultora();
            inscripcion.setCapacitacion(capacitacion);
            inscripcion.setConsultora(consultora);
            inscripcion.setCompletado(false);

            CapacitacionConsultora savedInscripcion = capacitacionConsultoraRepository.save(inscripcion);
            return CapacitacionConsultoraResponse.fromEntity(savedInscripcion);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al inscribir consultora: " + e.getMessage());
        }
    }

    @Transactional
    public CapacitacionConsultoraResponse completarCapacitacion(Long id, java.math.BigDecimal puntaje) {
        try {
            CapacitacionConsultora inscripcion = capacitacionConsultoraRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));

            inscripcion.setCompletado(true);
            inscripcion.setPuntaje(puntaje);

            CapacitacionConsultora updatedInscripcion = capacitacionConsultoraRepository.save(inscripcion);
            return CapacitacionConsultoraResponse.fromEntity(updatedInscripcion);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al completar capacitación: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteCapacitacionConsultora(Long id) {
        try {
            CapacitacionConsultora inscripcion = capacitacionConsultoraRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));
            capacitacionConsultoraRepository.delete(inscripcion);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar inscripción: " + e.getMessage());
        }
    }
}
