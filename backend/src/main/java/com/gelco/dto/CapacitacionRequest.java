package com.gelco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class CapacitacionRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 150, message = "El título debe tener entre 3 y 150 caracteres")
    private String titulo;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    private LocalDateTime fecha;

    private Boolean activo;

    private Integer duracionMinutos;

    private String tipo;

    private String urlContenido;

    private List<PreguntaRequest> preguntas;

    public CapacitacionRequest(String titulo, String descripcion, LocalDateTime fecha,
                              Boolean activo, Integer duracionMinutos, String tipo, String urlContenido) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.activo = activo;
        this.duracionMinutos = duracionMinutos;
        this.tipo = tipo;
        this.urlContenido = urlContenido;
    }
}
