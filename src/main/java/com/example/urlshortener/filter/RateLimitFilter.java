package com.example.urlshortener.filter;

import com.example.urlshortener.config.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final AppProperties properties;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimitFilter(AppProperties properties) { this.properties = properties; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!"POST".equalsIgnoreCase(request.getMethod()) || !request.getRequestURI().equals("/api/v1/urls")) {
            chain.doFilter(request, response);
            return;
        }

        String key = request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
        long now = Instant.now().getEpochSecond();
        int windowSeconds = Math.max(1, properties.rateLimit().windowSeconds());
        int capacity = Math.max(1, properties.rateLimit().capacity());
        long bucket = now / windowSeconds;

        Window current = windows.compute(key, (k, old) -> {
            if (old == null || old.bucket() != bucket) return new Window(bucket, new AtomicInteger(1));
            old.count().incrementAndGet();
            return old;
        });

        if (current.count().get() > capacity) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(windowSeconds));
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded\"}");
            return;
        }
        chain.doFilter(request, response);
    }

    private record Window(long bucket, AtomicInteger count) {}
}
