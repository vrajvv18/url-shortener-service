package com.example.urlshortener.controller;

import com.example.urlshortener.dto.*;
import com.example.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {
    private final UrlService service;
    public UrlController(UrlService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<CreateUrlResponse> create(@Valid @RequestBody CreateUrlRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{code}/analytics")
    public AnalyticsResponse analytics(@PathVariable String code) { return service.analytics(code); }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable String code) { service.disable(code); }
}
