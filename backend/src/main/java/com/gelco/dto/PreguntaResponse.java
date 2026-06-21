package com.gelco.dto;

import com.gelco.model.CapacitacionPregunta;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PreguntaResponse {

    private Long id;
    private String pregunta;
    private Integer orden;

    public static PreguntaResponse fromEntity(CapacitacionPregunta entity) {
        PreguntaResponse r = new PreguntaResponse();
        r.setId(entity.getId());
        r.setPregunta(entity.getPregunta());
        r.setOrden(entity.getOrden());
        return r;
    }
}
