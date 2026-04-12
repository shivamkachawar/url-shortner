package com.shivam.urlshortner.controller;

import com.shivam.urlshortner.entity.User;
import com.shivam.urlshortner.service.UserService;
import com.shivam.urlshortner.util.JwtUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        return userService.registerUser(username, password);
    }
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        userService.loginUser(username, password);

        String token = JwtUtil.generateToken(username);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return response;
    }
    @GetMapping("/me")
    public String getCurrentUser() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}