package com.gelco.dto;

import com.gelco.model.Consultora;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ConsultoraResponse {

    private Long id;
    private Long usuarioId;
    private String usuarioEmail;
    private String usuarioNombre;
    private Boolean estadoUsuario;
    private String dni;
    private String direccion;
    private String telefono;
    private String nivel;
    private BigDecimal ventasTotales;
    private LocalDateTime updatedAt;
    private LocalDateTime usuarioCreatedAt;

    public static ConsultoraResponse fromEntity(Consultora consultora) {
        ConsultoraResponse r = new ConsultoraResponse();
        r.setId(consultora.getId());
        r.setUsuarioId(consultora.getUsuario().getId());
        r.setUsuarioEmail(consultora.getUsuario().getEmail());
        r.setUsuarioNombre(consultora.getUsuario().getNombre());
        r.setEstadoUsuario(consultora.getUsuario().getEstado());
        r.setDni(consultora.getDni());
        r.setDireccion(consultora.getDireccion());
        r.setTelefono(consultora.getTelefono());
        r.setNivel(consultora.getNivel());
        r.setVentasTotales(consultora.getVentasTotales());
        r.setUpdatedAt(consultora.getUpdatedAt());
        r.setUsuarioCreatedAt(consultora.getUsuario().getCreatedAt());
        return r;
    }
}