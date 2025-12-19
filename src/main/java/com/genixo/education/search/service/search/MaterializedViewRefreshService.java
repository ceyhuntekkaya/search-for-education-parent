package com.genixo.education.search.service.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Materialized View Refresh Service
 * Okul verileri değiştiğinde materialized view'i günceller
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterializedViewRefreshService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Materialized view'i CONCURRENT olarak refresh et
     * NON-BLOCKING - Kullanıcıları etkilemez
     */
    public void refreshSchoolSearchView() {
        log.info("Starting materialized view refresh (CONCURRENT)...");

        long startTime = System.currentTimeMillis();

        try {
            jdbcTemplate.execute("REFRESH MATERIALIZED VIEW CONCURRENTLY school_search_materialized_v2");

            long duration = System.currentTimeMillis() - startTime;
            log.info("Materialized view refreshed successfully in {} ms", duration);

        } catch (Exception e) {
            log.error("Failed to refresh materialized view", e);
            throw new RuntimeException("Materialized view refresh failed", e);
        }
    }

    /**
     * Async refresh - Blocking olmadan arka planda çalışır
     */
    @Async
    public void refreshSchoolSearchViewAsync() {
        log.info("Starting ASYNC materialized view refresh...");
        refreshSchoolSearchView();
    }

    /**
     * BLOCKING refresh - Daha hızlı ama kullanıcıları etkiler
     * Sadece gece/bakım dönemlerinde kullan!
     */
    public void refreshSchoolSearchViewBlocking() {
        log.warn("Starting BLOCKING materialized view refresh - users will be affected!");

        long startTime = System.currentTimeMillis();

        try {
            jdbcTemplate.execute("REFRESH MATERIALIZED VIEW school_search_materialized_v2");

            long duration = System.currentTimeMillis() - startTime;
            log.info("BLOCKING refresh completed in {} ms", duration);

        } catch (Exception e) {
            log.error("Failed to refresh materialized view (BLOCKING)", e);
            throw new RuntimeException("Materialized view refresh failed", e);
        }
    }

    /**
     * Otomatik refresh - Her gece 2:00'de
     * Cron: "0 0 2 * * ?" = Her gün 02:00
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledRefresh() {
        log.info("=== SCHEDULED REFRESH STARTED at {} ===", LocalDateTime.now());

        try {
            // Gece olduğu için blocking kullanabiliriz (daha hızlı)
            refreshSchoolSearchViewBlocking();

            log.info("=== SCHEDULED REFRESH COMPLETED ===");

        } catch (Exception e) {
            log.error("=== SCHEDULED REFRESH FAILED ===", e);
            // Slack/email notification gönderilebilir
        }
    }

    /**
     * View'in son refresh zamanını kontrol et
     */
    public LocalDateTime getLastRefreshTime() {
        try {
            String sql = """
                SELECT last_refresh 
                FROM pg_matviews 
                WHERE matviewname = 'school_search_materialized_v2'
                """;

            return jdbcTemplate.queryForObject(sql, LocalDateTime.class);

        } catch (Exception e) {
            log.error("Failed to get last refresh time", e);
            return null;
        }
    }

    /**
     * View'in boyutunu kontrol et
     */
    public String getViewSize() {
        try {
            String sql = """
                SELECT pg_size_pretty(pg_total_relation_size('school_search_materialized_v2'))
                """;

            return jdbcTemplate.queryForObject(sql, String.class);

        } catch (Exception e) {
            log.error("Failed to get view size", e);
            return "Unknown";
        }
    }

    /**
     * View'deki kayıt sayısını kontrol et
     */
    public Long getRecordCount() {
        try {
            String sql = "SELECT COUNT(*) FROM school_search_materialized_v2";
            return jdbcTemplate.queryForObject(sql, Long.class);

        } catch (Exception e) {
            log.error("Failed to get record count", e);
            return 0L;
        }
    }

    /**
     * Health check - View sağlıklı mı?
     */
    public boolean isViewHealthy() {
        try {
            Long count = getRecordCount();
            LocalDateTime lastRefresh = getLastRefreshTime();

            // View'de kayıt var mı?
            if (count == null || count == 0) {
                log.warn("View is empty!");
                return false;
            }

            // Son refresh 7 günden eski mi?
            if (lastRefresh != null && lastRefresh.isBefore(LocalDateTime.now().minusDays(7))) {
                log.warn("View hasn't been refreshed in 7 days!");
                return false;
            }

            return true;

        } catch (Exception e) {
            log.error("Health check failed", e);
            return false;
        }
    }
}

/*
// 1. Okul oluşturulduğunda
schoolRepository.save(newSchool);
eventPublisher.publishEvent(new SchoolCreatedEvent(school.getId()));

// 2. Okul güncellendiğinde
schoolRepository.save(updatedSchool);
eventPublisher.publishEvent(new SchoolUpdatedEvent(school.getId()));

// 3. Okul silindiğinde
schoolRepository.delete(school);
eventPublisher.publishEvent(new SchoolDeletedEvent(school.getId()));

// 4. Özellik güncellendiğinde (opsiyonel)
// property save...
eventPublisher.publishEvent(new PropertyUpdatedEvent(schoolId));
 */