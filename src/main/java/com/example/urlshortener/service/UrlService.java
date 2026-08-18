package com.example.urlshortener.service;

import com.example.urlshortener.config.AppProperties;
import com.example.urlshortener.domain.Url;
import com.example.urlshortener.dto.*;
import com.example.urlshortener.exception.ApiException;
import com.example.urlshortener.repository.*;
import com.example.urlshortener.util.ShortCodeGenerator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Instant;

@Service
public class UrlService {
    private static final int MAX_ALLOCATION_ATTEMPTS = 5;

    private final UrlRepository urlRepository;
    private final ClickEventRepository clickEventRepository;
    private final ShortCodeGenerator generator;
    private final AppProperties properties;

    public UrlService(UrlRepository urlRepository, ClickEventRepository clickEventRepository,
                      ShortCodeGenerator generator, AppProperties properties) {
        this.urlRepository = urlRepository;
        this.clickEventRepository = clickEventRepository;
        this.generator = generator;
        this.properties = properties;
    }

    @Transactional
    public CreateUrlResponse create(CreateUrlRequest request) {
        validateUrl(request.longUrl());
        if (request.expiresAt() != null && !request.expiresAt().isAfter(Instant.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "expiresAt must be in the future");
        }

        for (int attempt = 1; attempt <= MAX_ALLOCATION_ATTEMPTS; attempt++) {
            String code = generator.generate();
            try {
                Url saved = urlRepository.saveAndFlush(new Url(code, request.longUrl(), request.expiresAt()));
                String base = properties.publicUrl().baseUrl().replaceAll("/$", "");
                return new CreateUrlResponse(saved.getShortCode(), base + "/" + saved.getShortCode(),
                        saved.getLongUrl(), saved.getCreatedAt(), saved.getExpiresAt());
            } catch (DataIntegrityViolationException collision) {
                if (attempt == MAX_ALLOCATION_ATTEMPTS) {
                    throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to allocate a unique short code");
                }
            }
        }
        throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to allocate a unique short code");
    }

    @Transactional
    public void disable(String code) {
        Url url = get(code);
        url.disable();
    }

    @Transactional(readOnly = true)
    public Url get(String code) {
        return urlRepository.findByShortCode(code)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Short URL not found"));
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse analytics(String code) {
        Url url = get(code);
        return new AnalyticsResponse(code,
                clickEventRepository.countByUrlId(url.getId()),
                clickEventRepository.countDistinctVisitors(url.getId()),
                clickEventRepository.findLastClickedAt(url.getId()),
                url.getStatus().name());
    }

    private void validateUrl(String value) {
        try {
            URI uri = URI.create(value);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    || uri.getHost() == null || uri.getUserInfo() != null) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "longUrl must be a valid HTTP/HTTPS URL without embedded credentials");
        }
    }
}
