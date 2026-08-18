package com.example.urlshortener.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "urls", indexes = @Index(name = "idx_urls_expires_at", columnList = "expires_at"))
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 8)
    private String shortCode;

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private UrlStatus status;

    protected Url() {}

    public Url(String shortCode, String longUrl, Instant expiresAt) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now();
        this.status = UrlStatus.ACTIVE;
    }

    public Long getId() { return id; }
    public String getShortCode() { return shortCode; }
    public String getLongUrl() { return longUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public UrlStatus getStatus() { return status; }
    public void disable() { this.status = UrlStatus.DISABLED; }
    public boolean isExpired() { return expiresAt != null && !expiresAt.isAfter(Instant.now()); }
}
