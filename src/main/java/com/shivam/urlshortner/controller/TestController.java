package com.shivam.urlshortner.controller;

import com.shivam.urlshortner.dto.ShortUrlResponse;
import com.shivam.urlshortner.entity.Url;
import com.shivam.urlshortner.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    private final UrlService urlService;

    public TestController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public Url createShortUrl(@RequestBody Map<String, String> body) {

        String originalUrl = body.get("url");
        String expiry = body.get("expiry"); // optional

        java.time.LocalDateTime expiryDate = null;

        if (expiry != null && !expiry.isEmpty()) {
            expiryDate = java.time.LocalDateTime.parse(expiry);
        }

        return urlService.createShortUrl(originalUrl, expiryDate);
    }
    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode,
                         HttpServletResponse response) throws IOException {

        Url url = urlService.getOriginalUrl(shortCode);

        if (url != null) {
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