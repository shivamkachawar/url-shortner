package com.shivam.urlshortner.repository;

import com.shivam.urlshortner.entity.Click;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClickRepository extends JpaRepository<Click, Long> {
    List<Click> findByUrlId(Long urlId);
}
