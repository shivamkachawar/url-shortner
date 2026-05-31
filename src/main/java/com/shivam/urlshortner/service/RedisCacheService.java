package com.shivam.urlshortner.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.urlshortner.dto.CachedUrl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisCacheService(
            StringRedisTemplate redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String shortCode, CachedUrl cachedUrl) {

        try {

            String json =
                    objectMapper.writeValueAsString(cachedUrl);

            redisTemplate.opsForValue()
                    .set(shortCode, json);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(e);
        }
    }

    public CachedUrl get(String shortCode) {

        String json =
                redisTemplate.opsForValue()
                        .get(shortCode);

        if (json == null) {
            return null;
        }

        try {

            return objectMapper.readValue(
                    json,
                    CachedUrl.class
            );

        } catch (Exception e) {

            return null;
        }
    }

    public void delete(String shortCode) {

        redisTemplate.delete(shortCode);
    }
}