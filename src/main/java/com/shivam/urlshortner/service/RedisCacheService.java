package com.shivam.urlshortner.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.urlshortner.dto.CachedUrl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.Duration;
import java.time.Instant;

@Service
public class RedisCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public RedisCacheService(
            StringRedisTemplate redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String shortCode, CachedUrl cachedUrl) {

        try {

            String json =
                    objectMapper.writeValueAsString(cachedUrl);

            if (cachedUrl.getExpiryDate() != null) {

                Duration ttl = Duration.between(
                        Instant.now(),
                        cachedUrl.getExpiryDate()
                );

                if (!ttl.isNegative() && !ttl.isZero()) {

                    redisTemplate.opsForValue()
                            .set(shortCode, json, ttl);

                }

            } else {

                redisTemplate.opsForValue()
                        .set(shortCode, json);

            }

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