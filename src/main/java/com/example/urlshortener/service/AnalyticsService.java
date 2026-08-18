package com.example.urlshortener.service;

import com.example.urlshortener.domain.ClickEvent;
import com.example.urlshortener.repository.ClickEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class AnalyticsService {
    private final ClickEventRepository clickEvents;

    public AnalyticsService(ClickEventRepository clickEvents) {
        this.clickEvents = clickEvents;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(Long urlId, String ip, String userAgent, String referrer) {
        clickEvents.save(new ClickEvent(urlId, visitorHash(ip, userAgent), userAgent, referrer));
    }

    private String visitorHash(String ip, String userAgent) {
        if (ip == null && userAgent == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String value = String.valueOf(ip) + "|" + String.valueOf(userAgent);
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder(64);
            for (byte b : hash) out.append(String.format("%02x", b));
            return out.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
