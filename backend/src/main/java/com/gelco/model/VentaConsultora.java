package com.gelco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ventas_consultora")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaConsultora {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultora_id", nullable = false)
    private Consultora consultora;
    
    @Column(nullable = false)
    private Integer mes;
    
    @Column(nullable = false)
    private Integer anio;
    
    @Column(name = "total_ventas", precision = 15, scale = 2)
    private BigDecimal totalVentas;
}
