package com.gelco.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearDevolucionRequest {

    @NotNull(message = "El detallePedidoId es obligatorio")
    private Long detallePedidoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo; // "Cambio" | "Devolucion"

    private String motivo;

    @NotBlank(message = "La condición del producto es obligatoria")
    private String condicionProducto; // "Apto" | "No apto"

    private String observaciones;
}