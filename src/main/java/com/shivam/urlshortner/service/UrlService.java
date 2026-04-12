package com.shivam.urlshortner.service;

import com.shivam.urlshortner.entity.Url;
import com.shivam.urlshortner.entity.User;
import com.shivam.urlshortner.repository.UrlRepository;
import com.shivam.urlshortner.repository.UserRepository;
import com.shivam.urlshortner.util.Base62Util;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;

    public UrlService(UrlRepository urlRepository, UserRepository userRepository) {
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
    }

    public Url createShortUrl(String originalUrl, java.time.LocalDateTime expiryDate) {

        String username = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥 Use single time reference (important)
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setUser(user);
        url.setCreatedAt(now);

        // 🔥 Expiry logic with validation
        if (expiryDate != null) {

            if (expiryDate.isBefore(now)) {
                throw new RuntimeException("Expiry cannot be in the past");
            }

            // normalize
            expiryDate = expiryDate.withSecond(0).withNano(0);

            url.setExpiryDate(expiryDate);

        } else {
            url.setExpiryDate(now.plusDays(7));
        }

        // Step 1: Save to get ID
        Url savedUrl = urlRepository.save(url);

        // Step 2: Generate short code
        String shortCode = Base62Util.encode(savedUrl.getId());
        savedUrl.setShortCode(shortCode);

        // Step 3: Save again
        return urlRepository.save(savedUrl);
    }

    public Url getOriginalUrl(String shortCode) {

        Optional<Url> optionalUrl = urlRepository.findByShortCode(shortCode);

        if (optionalUrl.isPresent()) {

            Url url = optionalUrl.get();

            // 🔥 increment click count
            if (url.getClickCount() == null) {
                url.setClickCount(0L);
            }

            url.setClickCount(url.getClickCount() + 1);

            urlRepository.save(url);

            return url;
        }

        return null;
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