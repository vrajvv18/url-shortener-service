package com.example.urlshortener.service;

import com.example.urlshortener.domain.Url;
import com.example.urlshortener.domain.UrlStatus;
import com.example.urlshortener.exception.ApiException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class RedirectService {
    private final UrlService urlService;
    private final AnalyticsService analyticsService;
    private final Counter redirectCounter;
    private final Counter analyticsFailureCounter;

    public RedirectService(UrlService urlService, AnalyticsService analyticsService, MeterRegistry meterRegistry) {
        this.urlService = urlService;
        this.analyticsService = analyticsService;
        this.redirectCounter = meterRegistry.counter("url_shortener.redirects");
        this.analyticsFailureCounter = meterRegistry.counter("url_shortener.analytics.persistence.failures");
    }

    public URI redirect(String code, String remoteAddress, String userAgent, String referrer) {
        Url url = urlService.get(code);
        if (url.getStatus() != UrlStatus.ACTIVE) {
            throw new ApiException(HttpStatus.GONE, "Short URL is disabled");
        }
        if (url.isExpired()) {
            throw new ApiException(HttpStatus.GONE, "Short URL has expired");
        }

        try {
            analyticsService.record(url.getId(), remoteAddress, userAgent, referrer);
        } catch (RuntimeException analyticsFailure) {
            analyticsFailureCounter.increment();
        }
        redirectCounter.increment();
        return URI.create(url.getLongUrl());
    }
}
