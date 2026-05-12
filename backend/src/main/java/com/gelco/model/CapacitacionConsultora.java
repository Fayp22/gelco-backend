package com.gelco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "capacitacion_consultora")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacitacionConsultora {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capacitacion_id", nullable = false)
    private Capacitacion capacitacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultora_id", nullable = false)
    private Consultora consultora;
    
    @Column(nullable = false)
    private Boolean completado;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal puntaje;
}
