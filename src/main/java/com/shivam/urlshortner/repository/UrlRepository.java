package com.shivam.urlshortner.repository;

import com.shivam.urlshortner.entity.Url;
import com.shivam.urlshortner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);

    List<Url> findByUserUsername(String username);

    List<Url> findByUserId(Long userId);

    List<Url> findTop5ByOrderByClickCountDesc();

    List<Url> findByExpiryDateBefore(LocalDateTime now);

    Optional<Url> findByOriginalUrl(String originalUrl);

    @Modifying
    @Query("UPDATE Url u SET u.clickCount = u.clickCount + 1, u.lastAccessedAt = :now WHERE u.id = :id")
    void incrementClick(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Query("SELECT COALESCE(SUM(u.clickCount), 0) FROM Url u")
    Long getTotalClicks();
}