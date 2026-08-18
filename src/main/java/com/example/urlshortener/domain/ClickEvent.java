package com.example.urlshortener.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "click_events", indexes = @Index(name = "idx_click_events_url_time", columnList = "url_id, clicked_at"))
public class ClickEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_id", nullable = false)
    private Long urlId;

    @Column(name = "clicked_at", nullable = false)
    private Instant clickedAt;

    @Column(name = "visitor_hash", length = 64)
    private String visitorHash;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "referrer", length = 2048)
    private String referrer;

    protected ClickEvent() {}

    public ClickEvent(Long urlId, String visitorHash, String userAgent, String referrer) {
        this.urlId = urlId;
        this.visitorHash = visitorHash;
        this.userAgent = userAgent;
        this.referrer = referrer;
        this.clickedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getUrlId() { return urlId; }
    public Instant getClickedAt() { return clickedAt; }
    public String getVisitorHash() { return visitorHash; }
}
