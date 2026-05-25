package com.gelco.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 40)
    private String telefono;

    @Column(length = 300)
    private String direccion;

    @Column(columnDefinition = "nvarchar(MAX)")
    private String preferencias;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consultora_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Consultora consultora;
}
