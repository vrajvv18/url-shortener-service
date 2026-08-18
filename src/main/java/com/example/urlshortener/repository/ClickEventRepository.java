package com.example.urlshortener.repository;

import com.example.urlshortener.domain.ClickEvent;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    long countByUrlId(Long urlId);

    @Query("select count(distinct c.visitorHash) from ClickEvent c where c.urlId = :urlId and c.visitorHash is not null")
    long countDistinctVisitors(@Param("urlId") Long urlId);

    @Query("select max(c.clickedAt) from ClickEvent c where c.urlId = :urlId")
    Instant findLastClickedAt(@Param("urlId") Long urlId);
}
