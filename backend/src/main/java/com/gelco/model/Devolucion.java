package com.gelco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "devoluciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // El ítem específico del pedido que se devuelve/cambia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_pedido_id", nullable = false)
    private DetallePedido detallePedido;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, length = 20)
    private String tipo; // "Cambio" | "Devolucion"

    @Column(length = 300)
    private String motivo;

    @Column(name = "condicion_producto", nullable = false, length = 20)
    private String condicionProducto; // "Apto" | "No apto"

    // Queda en "Procesada" — listo para que el Facturador la tome en otra HU
    @Column(nullable = false, length = 30)
    private String estado = "Procesada";

    @Column(length = 500)
    private String observaciones;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recepcionista_id", nullable = false)
    private Usuario recepcionista;
}