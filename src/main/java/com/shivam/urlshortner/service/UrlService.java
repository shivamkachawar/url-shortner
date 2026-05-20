package com.shivam.urlshortner.service;

import com.shivam.urlshortner.entity.Url;
import com.shivam.urlshortner.entity.User;
import com.shivam.urlshortner.repository.UrlRepository;
import com.shivam.urlshortner.repository.UserRepository;
import com.shivam.urlshortner.util.Base62Util;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;

    public UrlService(UrlRepository urlRepository, UserRepository userRepository) {
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
    }

    public Url createShortUrl(String originalUrl,
                              LocalDateTime expiryDate,
                              String customCode) {

        String username = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime now = LocalDateTime.now();

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setUser(user);
        url.setCreatedAt(now);

        // 🔥 Expiry
        if (expiryDate != null) {

            if (expiryDate.isBefore(now)) {
                throw new RuntimeException("Expiry cannot be in the past");
            }

            url.setExpiryDate(expiryDate.withSecond(0).withNano(0));

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

            return urlRepository.save(url);
        }

        // 🔥 Random Base62 flow
        String shortCode;

        do {
            shortCode = Base62Util.generateRandomCode(6);
        } while (urlRepository.findByShortCode(shortCode).isPresent());

        url.setShortCode(shortCode);

        return urlRepository.save(url);
    }

    public Url getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode).orElse(null);
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

        java.time.LocalDateTime newExpiry;

        try {
            newExpiry = java.time.LocalDateTime.parse(
                    expiry,
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid expiry format");
        }
        if (newExpiry.isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Expiry cannot be in the past");
        }
        // Optional: normalize seconds
        newExpiry = newExpiry.withSecond(0).withNano(0);

        url.setExpiryDate(newExpiry);

        urlRepository.save(url);
    }
}