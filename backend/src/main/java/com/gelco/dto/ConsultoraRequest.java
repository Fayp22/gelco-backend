package com.gelco.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConsultoraRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener 8 dígitos")
    private String dni;

    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe empezar con 9 y tener 9 dígitos")
    private String telefono;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    private String nivel;

    public ConsultoraRequest(String nombre, String email, String password, String dni,
                            String telefono, String direccion, String nivel) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.dni = dni;
        this.telefono = telefono;
        this.direccion = direccion;
        this.nivel = nivel;
    }
}
