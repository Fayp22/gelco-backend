package com.gelco.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import com.gelco.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                if (jwtUtil.isTokenValid(token)) {
                    String email = jwtUtil.getUsernameFromToken(token);
                    String perfil = jwtUtil.getPerfilFromToken(token);
                    Long userId = jwtUtil.getUsuarioIdFromToken(token);

                    List<SimpleGrantedAuthority> authorities =
                            perfil != null ? List.of(new SimpleGrantedAuthority("ROLE_" + perfil)) : List.of();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                logger.debug("JWT token validation failed: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
