package com.gelco.service;

import com.gelco.dto.CapacitacionConsultoraResponse;
import com.gelco.dto.CapacitacionResponse;
import com.gelco.dto.CapacitacionRequest;
import com.gelco.dto.PreguntaRequest;
import com.gelco.dto.PreguntaResponse;
import com.gelco.dto.EfektividadCapacitacionResponse;
import com.gelco.model.Capacitacion;
import com.gelco.model.CapacitacionConsultora;
import com.gelco.model.CapacitacionPregunta;
import com.gelco.model.Consultora;
import com.gelco.model.VentaConsultora;
import com.gelco.repository.CapacitacionConsultoraRepository;
import com.gelco.repository.CapacitacionPreguntaRepository;
import com.gelco.repository.CapacitacionRepository;
import com.gelco.repository.ConsultoraRepository;
import com.gelco.repository.VentaConsultoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapacitacionService {

    private final CapacitacionRepository capacitacionRepository;
    private final CapacitacionConsultoraRepository capacitacionConsultoraRepository;
    private final CapacitacionPreguntaRepository capacitacionPreguntaRepository;
    private final ConsultoraRepository consultoraRepository;
    private final VentaConsultoraRepository ventaConsultoraRepository;

    public List<CapacitacionResponse> getAllCapacitaciones() {
        try {
            return capacitacionRepository.findAll()
                    .stream()
                    .map(cap -> {
                        CapacitacionResponse r = CapacitacionResponse.fromEntity(cap);
                        r.setTotalInscripciones((int) capacitacionConsultoraRepository.countByCapacitacionId(cap.getId()));
                        r.setCompletadas((int) capacitacionConsultoraRepository.countByCapacitacionIdAndCompletado(cap.getId(), true));
                        return r;
                    })
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

    public List<CapacitacionConsultoraResponse> getCapacitacionesByCapacitacion(Long capId) {
        try {
            return capacitacionConsultoraRepository.findByCapacitacionId(capId)
                    .stream()
                    .map(CapacitacionConsultoraResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener consultoras por capacitacion: " + e.getMessage());
        }
    }

    public List<PreguntaResponse> getPreguntasByCapacitacion(Long capId) {
        try {
            return capacitacionPreguntaRepository.findByCapacitacionIdOrderByOrdenAsc(capId)
                    .stream()
                    .map(PreguntaResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener preguntas: " + e.getMessage());
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

            if (request.getPreguntas() != null && !request.getPreguntas().isEmpty()) {
                for (int i = 0; i < request.getPreguntas().size(); i++) {
                    PreguntaRequest pr = request.getPreguntas().get(i);
                    CapacitacionPregunta pregunta = new CapacitacionPregunta();
                    pregunta.setCapacitacion(saved);
                    pregunta.setPregunta(pr.getPregunta());
                    pregunta.setOrden(pr.getOrden() != null ? pr.getOrden() : i + 1);
                    capacitacionPreguntaRepository.save(pregunta);
                }
            }

            return CapacitacionResponse.fromEntity(saved);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear capacitacion: " + e.getMessage());
        }
    }

    @Transactional
    public CapacitacionResponse updateCapacitacion(Long id, CapacitacionRequest request) {
        try {
            Capacitacion capacitacion = capacitacionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Capacitacion no encontrada"));

            if (request.getTitulo() != null) capacitacion.setTitulo(request.getTitulo());
            if (request.getDescripcion() != null) capacitacion.setDescripcion(request.getDescripcion());
            if (request.getFecha() != null) capacitacion.setFecha(request.getFecha());
            if (request.getActivo() != null) capacitacion.setActivo(request.getActivo());
            if (request.getDuracionMinutos() != null) capacitacion.setDuracionMinutos(request.getDuracionMinutos());
            if (request.getTipo() != null) capacitacion.setTipo(request.getTipo());
            if (request.getUrlContenido() != null) capacitacion.setUrlContenido(request.getUrlContenido());

            if (request.getPreguntas() != null) {
                capacitacionPreguntaRepository.deleteByCapacitacionId(id);
                for (int i = 0; i < request.getPreguntas().size(); i++) {
                    PreguntaRequest pr = request.getPreguntas().get(i);
                    CapacitacionPregunta pregunta = new CapacitacionPregunta();
                    pregunta.setCapacitacion(capacitacion);
                    pregunta.setPregunta(pr.getPregunta());
                    pregunta.setOrden(pr.getOrden() != null ? pr.getOrden() : i + 1);
                    capacitacionPreguntaRepository.save(pregunta);
                }
            }

            Capacitacion saved = capacitacionRepository.save(capacitacion);
            return CapacitacionResponse.fromEntity(saved);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar capacitacion: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteCapacitacion(Long id) {
        try {
            if (!capacitacionRepository.existsById(id)) {
                throw new IllegalArgumentException("Capacitacion no encontrada");
            }
            capacitacionRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar capacitacion: " + e.getMessage());
        }
    }

    @Transactional
    public CapacitacionConsultoraResponse inscribirConsultora(Long capId, Long consulId) {
        try {
            if (capacitacionConsultoraRepository.existsByCapacitacionIdAndConsultoraId(capId, consulId)) {
                throw new IllegalArgumentException("Esta consultora ya esta inscrita en esta capacitacion");
            }

            Capacitacion capacitacion = capacitacionRepository.findById(capId)
                    .orElseThrow(() -> new IllegalArgumentException("Capacitacion no encontrada"));

            Consultora consultora = consultoraRepository.findById(consulId)
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
    public CapacitacionConsultoraResponse completarCapacitacion(Long id, BigDecimal puntaje) {
        try {
            CapacitacionConsultora inscripcion = capacitacionConsultoraRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Inscripcion no encontrada"));

            inscripcion.setCompletado(true);
            inscripcion.setPuntaje(puntaje);

            CapacitacionConsultora updatedInscripcion = capacitacionConsultoraRepository.save(inscripcion);
            return CapacitacionConsultoraResponse.fromEntity(updatedInscripcion);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al completar capacitacion: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteCapacitacionConsultora(Long id) {
        try {
            CapacitacionConsultora inscripcion = capacitacionConsultoraRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Inscripcion no encontrada"));
            capacitacionConsultoraRepository.delete(inscripcion);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar inscripcion: " + e.getMessage());
        }
    }

    @Transactional
    public void cancelarInscripcion(Long capId, Long consulId) {
        try {
            if (!capacitacionConsultoraRepository.existsByCapacitacionIdAndConsultoraId(capId, consulId)) {
                throw new IllegalArgumentException("Inscripcion no encontrada");
            }
            capacitacionConsultoraRepository.deleteByCapacitacionIdAndConsultoraId(capId, consulId);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al cancelar inscripcion: " + e.getMessage());
        }
    }

    public EfektividadCapacitacionResponse getEfektividadByCapacitacion(Long capId) {
        try {
            Capacitacion cap = capacitacionRepository.findById(capId)
                    .orElseThrow(() -> new IllegalArgumentException("Capacitacion no encontrada"));

            List<CapacitacionConsultora> inscripciones = capacitacionConsultoraRepository.findByCapacitacionId(capId);

            List<EfektividadCapacitacionResponse.EfektividadConsultoraItem> detalle = new ArrayList<>();

            BigDecimal sumVentasAntes = BigDecimal.ZERO;
            BigDecimal sumVentasDespues = BigDecimal.ZERO;
            BigDecimal sumPorcentajeMejora = BigDecimal.ZERO;
            BigDecimal sumPuntaje = BigDecimal.ZERO;
            int countVentasAntes = 0;
            int countVentasDespues = 0;
            int countPuntaje = 0;

            LocalDateTime fechaCap = cap.getFecha();
            int mesCap = fechaCap.getMonthValue();
            int anioCap = fechaCap.getYear();

            int mesAntes = mesCap == 1 ? 12 : mesCap - 1;
            int anioAntes = mesCap == 1 ? anioCap - 1 : anioCap;
            int mesDespues = mesCap == 12 ? 1 : mesCap + 1;
            int anioDespues = mesCap == 12 ? anioCap + 1 : anioCap;

            for (CapacitacionConsultora insc : inscripciones) {
                Consultora consul = insc.getConsultora();
                String nombreConsul = consul.getUsuario().getNombre();
                String nivel = consul.getNivel();

                BigDecimal ventasAntes = BigDecimal.ZERO;
                BigDecimal ventasDespues = BigDecimal.ZERO;
                BigDecimal porcentajeMejora = BigDecimal.ZERO;

                Optional<VentaConsultora> optVentaAntes = ventaConsultoraRepository
                        .findByConsultoraIdAndMesAndAnio(consul.getId(), mesAntes, anioAntes);
                if (optVentaAntes.isPresent()) {
                    ventasAntes = optVentaAntes.get().getTotalVentas();
                    sumVentasAntes = sumVentasAntes.add(ventasAntes);
                    countVentasAntes++;
                }

                Optional<VentaConsultora> optVentaDespues = ventaConsultoraRepository
                        .findByConsultoraIdAndMesAndAnio(consul.getId(), mesDespues, anioDespues);
                if (optVentaDespues.isPresent()) {
                    ventasDespues = optVentaDespues.get().getTotalVentas();
                    sumVentasDespues = sumVentasDespues.add(ventasDespues);
                    countVentasDespues++;
                }

                if (ventasAntes.compareTo(BigDecimal.ZERO) > 0 && ventasDespues.compareTo(BigDecimal.ZERO) > 0) {
                    porcentajeMejora = ventasDespues.subtract(ventasAntes)
                            .divide(ventasAntes, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
                    sumPorcentajeMejora = sumPorcentajeMejora.add(porcentajeMejora);
                }

                BigDecimal puntaje = insc.getPuntaje();
                if (puntaje != null) {
                    sumPuntaje = sumPuntaje.add(puntaje);
                    countPuntaje++;
                }

                EfektividadCapacitacionResponse.EfektividadConsultoraItem item =
                        new EfektividadCapacitacionResponse.EfektividadConsultoraItem();
                item.setConsultoraId(consul.getId());
                item.setConsultoraNombre(nombreConsul);
                item.setNivel(nivel);
                item.setCompletado(insc.getCompletado());
                item.setPuntaje(puntaje);
                item.setVentasAntes(ventasAntes);
                item.setVentasDespues(ventasDespues);
                item.setPorcentajeMejora(porcentajeMejora);
                detalle.add(item);
            }

            int totalInscripciones = inscripciones.size();
            int totalCompletadas = (int) inscripciones.stream().filter(i -> Boolean.TRUE.equals(i.getCompletado())).count();

            BigDecimal tasaCompletacion = totalInscripciones > 0
                    ? BigDecimal.valueOf(totalCompletadas).divide(BigDecimal.valueOf(totalInscripciones), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            BigDecimal puntajePromedio = countPuntaje > 0
                    ? sumPuntaje.divide(BigDecimal.valueOf(countPuntaje), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            BigDecimal ventasPromedioAntes = countVentasAntes > 0
                    ? sumVentasAntes.divide(BigDecimal.valueOf(countVentasAntes), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            BigDecimal ventasPromedioDespues = countVentasDespues > 0
                    ? sumVentasDespues.divide(BigDecimal.valueOf(countVentasDespues), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            int countConMejora = (int) detalle.stream()
                    .filter(i -> i.getPorcentajeMejora().compareTo(BigDecimal.ZERO) != 0).count();
            BigDecimal porcentajeMejoraPromedio = countConMejora > 0
                    ? sumPorcentajeMejora.divide(BigDecimal.valueOf(countConMejora), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            EfektividadCapacitacionResponse response = new EfektividadCapacitacionResponse();
            response.setCapacitacionId(cap.getId());
            response.setCapacitacionTitulo(cap.getTitulo());
            response.setCapacitacionDescripcion(cap.getDescripcion());
            response.setCapacitacionFecha(cap.getFecha());
            response.setCapacitacionTipo(cap.getTipo());
            response.setDuracionMinutos(cap.getDuracionMinutos());
            response.setTotalInscripciones(totalInscripciones);
            response.setTotalCompletadas(totalCompletadas);
            response.setTasaCompletacion(tasaCompletacion);
            response.setPuntajePromedio(puntajePromedio);
            response.setVentasPromedioAntes(ventasPromedioAntes);
            response.setVentasPromedioDespues(ventasPromedioDespues);
            response.setPorcentajeMejoraPromedio(porcentajeMejoraPromedio);
            response.setDetalleConsultoras(detalle);

            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener efectividad: " + e.getMessage());
        }
    }
}
