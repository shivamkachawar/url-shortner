package com.shivam.urlshortner.controller;

import com.shivam.urlshortner.entity.Url;
import com.shivam.urlshortner.entity.User;
import com.shivam.urlshortner.repository.UrlRepository;
import com.shivam.urlshortner.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final UserRepository userRepository;
    private final UrlRepository urlRepository;

    public AdminController(UserRepository userRepository, UrlRepository urlRepository) {
        this.userRepository = userRepository;
        this.urlRepository = urlRepository;
    }

    // 👥 Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 🔗 Get all URLs
    @GetMapping("/urls")
    public List<Url> getAllUrls() {
        return urlRepository.findAll();
    }

    // 🗑 Delete any URL
    @DeleteMapping("/url/{id}")
    public void deleteUrl(@PathVariable Long id) {
        urlRepository.deleteById(id);
    }
    @GetMapping("/stats")
    public Map<String, Object> getStats() {

        long totalUsers = userRepository.count();
        long totalUrls = urlRepository.count();

        long totalClicks = urlRepository.getTotalClicks();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalUrls", totalUrls);
        stats.put("totalClicks", totalClicks);

        return stats;
    }
}