package com.gelco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens_revocados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "token_jti", nullable = false, unique = true, length = 255)
    private String tokenJti;
    
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    
    @Column(name = "revocado_en", nullable = false)
    private LocalDateTime revocadoEn;
    
    @Column(name = "expiracion", nullable = false)
    private LocalDateTime expiracion;
}
