package com.shivam.urlshortner.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/auth/ping")
    public String ping() {
        return "Sniply backend is awake";
    }
}