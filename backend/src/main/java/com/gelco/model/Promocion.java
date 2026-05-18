package com.gelco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "promociones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false, unique = true)
    private Producto producto;

    @Column(name = "precio_oferta", nullable = false, precision = 18, scale = 2)
    private BigDecimal precioOferta;

    @Column(name = "en_campania", nullable = false)
    private boolean enCampania = false;

    @Column(nullable = false)
    private boolean activo = true;
}