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
    private LocalDateTime capacitacionFecha;
    private Long consultoraId;
    private String consultoraNombre;
    private Boolean completado;
    private BigDecimal puntaje;

    public static CapacitacionConsultoraResponse fromEntity(CapacitacionConsultora capacitacionConsultora) {
        return new CapacitacionConsultoraResponse(
                capacitacionConsultora.getId(),
                capacitacionConsultora.getCapacitacion().getId(),
                capacitacionConsultora.getCapacitacion().getTitulo(),
                capacitacionConsultora.getCapacitacion().getFecha(),
                capacitacionConsultora.getConsultora().getId(),
                capacitacionConsultora.getConsultora().getUsuario().getNombre(),
                capacitacionConsultora.getCompletado(),
                capacitacionConsultora.getPuntaje()
        );
    }
}
