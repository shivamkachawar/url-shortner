package com.shivam.urlshortner.service;

import com.shivam.urlshortner.dto.CachedUrl;
import com.shivam.urlshortner.entity.Url;
import com.shivam.urlshortner.entity.User;
import com.shivam.urlshortner.repository.UrlRepository;
import com.shivam.urlshortner.repository.UserRepository;
import com.shivam.urlshortner.util.Base62Util;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.time.Instant;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final RedisCacheService redisCacheService;

    public UrlService(UrlRepository urlRepository, UserRepository userRepository, @Lazy RedisCacheService redisCacheService) {
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
        this.redisCacheService = redisCacheService;
    }

    public Url createShortUrl(String originalUrl,
                              Instant expiryDate,
                              String customCode) {

        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Instant now = Instant.now();

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setUser(user);
        url.setCreatedAt(now);

        // 🔥 Expiry
        if (expiryDate != null) {

            if (expiryDate.isBefore(now)) {
                throw new RuntimeException("Expiry cannot be in the past");
            }

            url.setExpiryDate(expiryDate);

        } else {
            url.setExpiryDate(null);
        }

        // 🔥 Custom short code
        if (customCode != null && !customCode.trim().isEmpty()) {

            String finalCode;

            do {

                String randomPart =
                        Base62Util.generateRandomCode(6);

                finalCode =
                        customCode + "-" + randomPart;

            } while (
                    urlRepository
                            .findByShortCode(finalCode)
                            .isPresent()
            );

            url.setShortCode(finalCode);

            Url savedUrl = urlRepository.save(url);

            redisCacheService.save(
                    savedUrl.getShortCode(),
                    new CachedUrl(
                            savedUrl.getOriginalUrl(),
                            savedUrl.getExpiryDate()
                    )
            );

            return savedUrl;
        }

        // 🔥 Random Base62 flow
        String shortCode;

        do {
            shortCode = Base62Util.generateRandomCode(6);
        } while (urlRepository.findByShortCode(shortCode).isPresent());

        url.setShortCode(shortCode);

        Url savedUrl = urlRepository.save(url);

        redisCacheService.save(
                savedUrl.getShortCode(),
                new CachedUrl(
                        savedUrl.getOriginalUrl(),
                        savedUrl.getExpiryDate()
                )
        );

        return savedUrl;
    }

    public Url getOriginalUrl(String shortCode) {

        CachedUrl cachedUrl =
                redisCacheService.get(shortCode);

        if (cachedUrl != null) {

            System.out.println("REDIS HIT: " + shortCode);

            Url url = new Url();

            url.setShortCode(shortCode);
            url.setOriginalUrl(cachedUrl.getOriginalUrl());
            url.setExpiryDate(cachedUrl.getExpiryDate());

            return url;
        }

        System.out.println("REDIS MISS: " + shortCode);

        return urlRepository.findByShortCode(shortCode)
                .orElse(null);
    }

    public java.util.List<Url> getUserUrls() {

        Object principal = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String username = principal.toString();

        return urlRepository.findByUserUsername(username);
    }

    public void deleteUrl(Long id) {
        urlRepository.deleteById(id);
    }

    public void updateExpiry(Long id, String expiry) {

        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        if (expiry == null || expiry.trim().isEmpty()) {
            throw new RuntimeException("Expiry cannot be empty");
        }

        Instant newExpiry;

        try {

            newExpiry = Instant.parse(expiry);

        } catch (Exception e) {

            throw new RuntimeException("Invalid expiry format");

        }

        if (newExpiry.isBefore(Instant.now())) {
            throw new RuntimeException("Expiry cannot be in the past");
        }

        url.setExpiryDate(newExpiry);

        urlRepository.save(url);
    }
}