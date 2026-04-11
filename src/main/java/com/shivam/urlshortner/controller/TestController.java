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
public class TestController {

    private final UrlService urlService;

    public TestController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ShortUrlResponse createShortUrl(@RequestBody Map<String, String> request) {

        String originalUrl = request.get("url");

        // Validation
        if (originalUrl == null || originalUrl.isEmpty()) {
            throw new RuntimeException("URL cannot be empty");
        }

        if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
            throw new RuntimeException("Invalid URL format");
        }

        Url url = urlService.createShortUrl(originalUrl);

        String shortUrl = "http://localhost:8080/api/" + url.getShortCode();

        return new ShortUrlResponse(shortUrl);
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
}