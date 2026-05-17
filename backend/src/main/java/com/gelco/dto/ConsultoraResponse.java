package com.gelco.dto;

import com.gelco.model.Consultora;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultoraResponse {
    private Long id;
    private Long usuarioId;
    private String usuarioEmail;
    private String usuarioNombre;
    private String dni;
    private String direccion;
    private String telefono;
    private String nivel;
    private BigDecimal ventasTotales;
    private LocalDateTime updatedAt;

    public static ConsultoraResponse fromEntity(Consultora consultora) {
        return new ConsultoraResponse(
                consultora.getId(),
                consultora.getUsuario().getId(),
                consultora.getUsuario().getEmail(),
                consultora.getUsuario().getNombre(),
                consultora.getDni(),
                consultora.getDireccion(),
                consultora.getTelefono(),
                consultora.getNivel(),
                consultora.getVentasTotales(),
                consultora.getUpdatedAt()
        );
    }
}
