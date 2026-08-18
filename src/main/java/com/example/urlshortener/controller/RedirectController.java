package com.example.urlshortener.controller;

import com.example.urlshortener.service.RedirectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
public class RedirectController {
    private final RedirectService service;
    public RedirectController(RedirectService service) { this.service = service; }

    @GetMapping("/{code:[A-Za-z0-9]{8}}")
    public ResponseEntity<Void> redirect(@PathVariable String code, HttpServletRequest request) {
        URI destination = service.redirect(code, request.getRemoteAddr(), request.getHeader("User-Agent"), request.getHeader("Referer"));
        return ResponseEntity.status(HttpStatus.FOUND).location(destination).build();
    }
}
