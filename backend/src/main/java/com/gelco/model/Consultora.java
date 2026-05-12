package com.gelco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultoras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultora {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false, length = 20)
    private String dni;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 255)
    private String direccion;
    
    @Column(length = 100)
    private String nivel; // Oro, Plata, Bronce
    
    @Column(name = "ventas_totales", precision = 15, scale = 2)
    private BigDecimal ventasTotales = BigDecimal.ZERO;
    
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
