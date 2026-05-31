package com.shivam.urlshortner.controller;

import com.shivam.urlshortner.entity.Click;
import com.shivam.urlshortner.entity.Url;
import com.shivam.urlshortner.repository.ClickRepository;
import com.shivam.urlshortner.repository.UrlRepository;
import com.shivam.urlshortner.service.UrlService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TestController {
    @Autowired
    private ClickRepository clickRepository;

    @Autowired
    private UrlRepository urlRepository;
    private final UrlService urlService;

    @PersistenceContext
    private EntityManager entityManager;

    public TestController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public Url createShortUrl(@RequestBody Map<String, String> body) {

        String originalUrl = body.get("url");
        String expiry = body.get("expiry");
        String customCode = body.get("customCode"); // 🔥 NEW

        Instant expiryDate = null;

        if (expiry != null && !expiry.trim().isEmpty()) {
            expiryDate = Instant.parse(expiry);
        }

        return urlService.createShortUrl(originalUrl, expiryDate, customCode);
    }
    @GetMapping("/{shortCode}")
    @Transactional
    public void redirect(@PathVariable String shortCode,
                         HttpServletResponse response) throws IOException {

        Url url = urlService.getOriginalUrl(shortCode);

        if (url != null) {

            Instant now = Instant.now();

            // 🔥 1. Expiry check
            if (url.getExpiryDate() != null &&
                    url.getExpiryDate().isBefore(now)) {

                response.sendRedirect("https://snip--ly.vercel.app/expired");
                return;
            }

            // 🔥 2. Increment click (DIRECT DB UPDATE → NO DOUBLE COUNT)
            urlRepository.incrementClickByShortCode(
                    shortCode,
                    now
            );

            // 🔥 3. Save click event (analytics)
//            Click click = new Click();
//            click.setUrl(url);
//            click.setClickedAt(now);
//            clickRepository.save(click);

            // 🔥 4. Redirect
            response.sendRedirect(url.getOriginalUrl());

        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    @GetMapping("/my-urls")
    public java.util.List<Url> getMyUrls() {
        return urlService.getUserUrls();
    }
    @DeleteMapping("/delete/{id}")
    public String deleteUrl(@PathVariable Long id) {
        urlService.deleteUrl(id);
        return "Deleted successfully";
    }
    @PutMapping("/expiry/{id}")
    public String updateExpiry(@PathVariable Long id,
                               @RequestParam String expiry) {

        urlService.updateExpiry(id, expiry);
        return "Expiry updated";
    }
}