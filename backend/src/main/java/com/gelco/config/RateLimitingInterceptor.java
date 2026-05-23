package com.gelco.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final Map<String, RequestCount> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MS = 60000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        String key = clientIp + ":" + endpoint;

        long now = System.currentTimeMillis();
        RequestCount count = requestCounts.computeIfAbsent(key, k -> new RequestCount());

        synchronized (count) {
            if (now - count.windowStart > WINDOW_MS) {
                count.count = 0;
                count.windowStart = now;
            }

            count.count++;

            if (count.count > MAX_REQUESTS) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":429,\"message\":\"Demasiadas solicitudes. Intente nuevamente en un minuto.\"}");
                return false;
            }
        }

        return true;
    }

    private static class RequestCount {
        int count = 0;
        long windowStart = System.currentTimeMillis();
    }
}
