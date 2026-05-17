package com.gelco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "capacitaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Capacitacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 150)
    private String titulo;
    
    @Column(length = 500)
    private String descripcion;

    @Column
    private LocalDateTime fecha;
}
