package com.shivam.urlshortner.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", columnDefinition = "TEXT")
    private String originalUrl;

    @Column(name = "short_code", unique = true, nullable = false)
    private String shortCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "click_count")
    private Long clickCount = 0L;

    @Column(name = "expiry_date")
    private Instant expiryDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    // 🔥 ADD THIS (CRITICAL)
    @OneToMany(mappedBy = "url", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Click> clicks;

    // getters & setters

    public List<Click> getClicks() {
        return clicks;
    }

    public void setClicks(List<Click> clicks) {
        this.clicks = clicks;
    }

    public Instant getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(Instant lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Url() {}

    public Url(Long id, String originalUrl, String shortCode) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
}