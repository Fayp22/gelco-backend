package com.gelco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false)
    private boolean activo = true;

    // Solo @Column para la URL de la imagen
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    // Aquí van las relaciones con otras tablas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id")
    private Marca marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linea_id")
    private LineaProducto linea;

    @OneToOne(mappedBy = "producto", fetch = FetchType.LAZY)
    private Promocion promocion;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}