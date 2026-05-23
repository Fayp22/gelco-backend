package com.gelco.controller;

import com.gelco.dto.ErrorResponse;
import com.gelco.service.HomeService;
import com.gelco.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeController {

    private final HomeService homeService;
    private final JwtUtil jwtUtil;

    @GetMapping("/public")
    public ResponseEntity<?> getPublicHome() {
        try {
            Map<String, Object> home = homeService.getPublicHome();
            return ResponseEntity.ok(home);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(500, "Error al cargar datos públicos", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getHome(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> home = homeService.getPublicHome();
                return ResponseEntity.ok(home);
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.isTokenValid(token)) {
                Map<String, Object> home = homeService.getPublicHome();
                return ResponseEntity.ok(home);
            }

            String perfil = jwtUtil.getPerfilFromToken(token);
            Long usuarioId = jwtUtil.getUsuarioIdFromToken(token);

            Map<String, Object> home = switch (perfil) {
                case "ADMIN" -> homeService.getAdminHome();
                case "CONSULTORA" -> homeService.getConsultoraHome(usuarioId);
                case "DISTRIBUIDOR" -> homeService.getDistribuidorHome();
                default -> homeService.getPublicHome();
            };

            return ResponseEntity.ok(home);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(500, "Error al cargar dashboard", e.getMessage()));
        }
    }
}
