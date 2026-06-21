package com.gelco.dto;

import com.gelco.model.Capacitacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacitacionResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fecha;
    private Boolean activo;
    private Integer duracionMinutos;
    private String tipo;
    private String urlContenido;
    private Integer totalInscripciones;
    private Integer completadas;

    public static CapacitacionResponse fromEntity(Capacitacion capacitacion) {
        CapacitacionResponse r = new CapacitacionResponse();
        r.setId(capacitacion.getId());
        r.setTitulo(capacitacion.getTitulo());
        r.setDescripcion(capacitacion.getDescripcion());
        r.setFecha(capacitacion.getFecha());
        r.setActivo(capacitacion.getActivo());
        r.setDuracionMinutos(capacitacion.getDuracionMinutos());
        r.setTipo(capacitacion.getTipo());
        r.setUrlContenido(capacitacion.getUrlContenido());
        return r;
    }
}
