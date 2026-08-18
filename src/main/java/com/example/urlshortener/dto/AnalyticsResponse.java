package com.example.urlshortener.dto;

import java.time.Instant;

public record AnalyticsResponse(String shortCode, long clicks, long uniqueVisitors, Instant lastClickedAt, String status) {}
