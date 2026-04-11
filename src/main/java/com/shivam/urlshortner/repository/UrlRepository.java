package com.shivam.urlshortner.repository;

import com.shivam.urlshortner.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);
    List<Url> findByUserUsername(String username);
}