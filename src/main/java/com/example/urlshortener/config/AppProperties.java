package com.example.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(PublicUrl publicUrl, RateLimit rateLimit) {
    public record PublicUrl(String baseUrl) {}
    public record RateLimit(int capacity, int windowSeconds) {}
}
