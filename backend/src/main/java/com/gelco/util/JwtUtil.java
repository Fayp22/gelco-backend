package com.gelco.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String email, String nombre, String perfil, Long usuarioId) {
        Map<String, Object> claims = new HashMap<>();
        
        claims.put("nombre", nombre);
        claims.put("perfil", perfil);
        claims.put("usuarioId", usuarioId);
        
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getPerfilFromToken(String token) {
        Object perfil = getAllClaims(token).get("perfil");
        if (perfil != null) {
            return (String) perfil;
        }
        return null;
    }

    public String getNombreFromToken(String token) {
        Object nombre = getAllClaims(token).get("nombre");
        if (nombre != null) {
            return (String) nombre;
        }
        return null;
    }

    public Long getUsuarioIdFromToken(String token) {
        Object usuarioId = getAllClaims(token).get("usuarioId");
        if (usuarioId != null) {
            return Long.valueOf(usuarioId.toString());
        }
        return null;
    }
}
