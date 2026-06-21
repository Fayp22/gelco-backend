package com.gelco.dto;

import com.gelco.model.CapacitacionConsultora;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacitacionConsultoraResponse {
    private Long id;
    private Long capacitacionId;
    private String capacitacionTitulo;
    private String capacitacionDescripcion;
    private LocalDateTime capacitacionFecha;
    private String capacitacionTipo;
    private String capacitacionUrlContenido;
    private Integer capacitacionDuracionMinutos;
    private Long consultoraId;
    private String consultoraNombre;
    private Boolean completado;
    private BigDecimal puntaje;

    public static CapacitacionConsultoraResponse fromEntity(CapacitacionConsultora capacitacionConsultora) {
        CapacitacionConsultoraResponse r = new CapacitacionConsultoraResponse();
        r.setId(capacitacionConsultora.getId());
        r.setCapacitacionId(capacitacionConsultora.getCapacitacion().getId());
        r.setCapacitacionTitulo(capacitacionConsultora.getCapacitacion().getTitulo());
        r.setCapacitacionDescripcion(capacitacionConsultora.getCapacitacion().getDescripcion());
        r.setCapacitacionFecha(capacitacionConsultora.getCapacitacion().getFecha());
        r.setCapacitacionTipo(capacitacionConsultora.getCapacitacion().getTipo());
        r.setCapacitacionUrlContenido(capacitacionConsultora.getCapacitacion().getUrlContenido());
        r.setCapacitacionDuracionMinutos(capacitacionConsultora.getCapacitacion().getDuracionMinutos());
        r.setConsultoraId(capacitacionConsultora.getConsultora().getId());
        r.setConsultoraNombre(capacitacionConsultora.getConsultora().getUsuario().getNombre());
        r.setCompletado(capacitacionConsultora.getCompletado());
        r.setPuntaje(capacitacionConsultora.getPuntaje());
        return r;
    }
}
