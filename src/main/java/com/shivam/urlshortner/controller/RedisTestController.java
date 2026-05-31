package com.shivam.urlshortner.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisTestController {

    private final StringRedisTemplate redisTemplate;

    public RedisTestController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/redis-test")
    public String testRedis() {

        redisTemplate.opsForValue().set("test", "hello-redis");

        return redisTemplate.opsForValue().get("test");
    }
}