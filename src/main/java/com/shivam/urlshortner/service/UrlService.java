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

    public Url createShortUrl(String originalUrl) {

        String username = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        System.out.println("Username from context: " + username);

        // Step 1: Fetch user FIRST
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Create URL object
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setUser(user); // attach user here

        // Step 3: Save to get ID
        Url savedUrl = urlRepository.save(url);

        // Step 4: Generate shortCode
        String shortCode = Base62Util.encode(savedUrl.getId());
        savedUrl.setShortCode(shortCode);

        // Step 5: Save again
        return urlRepository.save(savedUrl);
    }

    public Url getOriginalUrl(String shortCode) {
        Optional<Url> optionalUrl = urlRepository.findByShortCode(shortCode);
        return optionalUrl.orElse(null);
    }
    public java.util.List<Url> getUserUrls() {

        Object principal = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String username = principal.toString();

        return urlRepository.findByUserUsername(username);
    }
}