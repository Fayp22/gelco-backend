package com.gelco.dto;

import com.gelco.model.Ruta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaResponse {
    private Long id;
    private Long zonaId;
    private String zonaNombre;
    private Long vehiculoId;
    private String vehiculoPlaca;
    private Long choferId;
    private String choferNombre;
    private LocalDateTime fecha;

    public static RutaResponse fromEntity(Ruta ruta) {
        return new RutaResponse(
                ruta.getId(),
                ruta.getZona().getId(),
                ruta.getZona().getNombre(),
                ruta.getVehiculo().getId(),
                ruta.getVehiculo().getPlaca(),
                ruta.getChofer().getId(),
                ruta.getChofer().getNombre(),
                ruta.getFecha()
        );
    }
}
