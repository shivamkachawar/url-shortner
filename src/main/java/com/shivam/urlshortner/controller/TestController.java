package com.shivam.urlshortner.controller;

import com.shivam.urlshortner.entity.Click;
import com.shivam.urlshortner.entity.Url;
import com.shivam.urlshortner.repository.ClickRepository;
import com.shivam.urlshortner.repository.UrlRepository;
import com.shivam.urlshortner.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {
    @Autowired
    private ClickRepository clickRepository;

    @Autowired
    private UrlRepository urlRepository;
    private final UrlService urlService;

    public TestController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public Url createShortUrl(@RequestBody Map<String, String> body) {

        String originalUrl = body.get("url");
        String expiry = body.get("expiry");
        String customCode = body.get("customCode"); // 🔥 NEW

        LocalDateTime expiryDate = null;

        if (expiry != null && !expiry.trim().isEmpty()) {
            expiryDate = LocalDateTime.parse(
                    expiry,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            );
        }

        return urlService.createShortUrl(originalUrl, expiryDate, customCode);
    }
    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode,
                         HttpServletResponse response) throws IOException {

        Url url = urlService.getOriginalUrl(shortCode);

        if (url != null) {

            java.time.LocalDateTime now = java.time.LocalDateTime.now();

            // 🔥 1. Check expiry
            if (url.getExpiryDate() != null &&
                    url.getExpiryDate().isBefore(now)) {

                response.setStatus(HttpServletResponse.SC_GONE);
                response.getWriter().write("Link expired");
                return;
            }

            // 🔥 2. Increment click count safely
            Long current = url.getClickCount() == null ? 0 : url.getClickCount();
            url.setClickCount(current + 1);

            // 🔥 3. Update last accessed
            url.setLastAccessedAt(now);

            // 🔥 4. Save click event
            Click click = new Click();
            click.setUrl(url);
            click.setClickedAt(now);
            clickRepository.save(click);

            // 🔥 5. Save URL
            urlRepository.save(url);

            // 🔥 6. Redirect
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