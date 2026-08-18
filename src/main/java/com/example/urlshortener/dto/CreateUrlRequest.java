package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record CreateUrlRequest(
        @NotBlank(message = "longUrl is required")
        @Size(max = 2048, message = "longUrl must not exceed 2048 characters")
        String longUrl,
        Instant expiresAt
) {}
