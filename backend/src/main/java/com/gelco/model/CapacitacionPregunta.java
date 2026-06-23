package com.gelco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "capacitacion_pregunta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacitacionPregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capacitacion_id", nullable = false)
    private Capacitacion capacitacion;

    @Column(nullable = false, length = 500)
    private String pregunta;

    @Column(nullable = false)
    private Integer orden;
}
