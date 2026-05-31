package com.shivam.urlshortner.dto;

import java.time.Instant;

public class CachedUrl {

    private String originalUrl;

    private Instant expiryDate;

    public CachedUrl() {
    }

    public CachedUrl(String originalUrl, Instant expiryDate) {
        this.originalUrl = originalUrl;
        this.expiryDate = expiryDate;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }
}