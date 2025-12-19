package com.genixo.education.search.api.rest;

import com.genixo.education.search.service.search.MaterializedViewRefreshService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Admin Materialized View Controller
 * View yönetimi için admin endpoint'leri
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/materialized-view")
@RequiredArgsConstructor
@Tag(name = "Admin - Materialized View", description = "View yönetimi (Sadece admin)")
public class MaterializedViewAdminController {

    private final MaterializedViewRefreshService refreshService;

    /**
     * Manuel refresh (CONCURRENT - Kullanıcıları etkilemez)
     * POST /api/v1/admin/materialized-view/refresh
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Manuel view refresh",
            description = "Materialized view'i manuel olarak günceller (CONCURRENT - non-blocking)"
    )
    public ResponseEntity<Map<String, Object>> refreshView() {
        log.info("Admin triggered manual refresh");

        try {
            long startTime = System.currentTimeMillis();
            refreshService.refreshSchoolSearchView();
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "View refreshed successfully");
            response.put("durationMs", duration);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Manual refresh failed", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Refresh failed: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Async refresh (Arka planda)
     * POST /api/v1/admin/materialized-view/refresh-async
     */
    @PostMapping("/refresh-async")
    @Operation(
            summary = "Async view refresh",
            description = "View'i arka planda günceller, hemen response döner"
    )
    public ResponseEntity<Map<String, Object>> refreshViewAsync() {
        log.info("Admin triggered async refresh");

        try {
            refreshService.refreshSchoolSearchViewAsync();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Async refresh started");
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.accepted().body(response);

        } catch (Exception e) {
            log.error("Async refresh failed", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Async refresh failed: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Blocking refresh (Bakım için)
     * POST /api/v1/admin/materialized-view/refresh-blocking
     */
    @PostMapping("/refresh-blocking")
    @Operation(
            summary = "Blocking view refresh (DANGER!)",
            description = "BLOCKING refresh - Kullanıcıları etkiler! Sadece bakım dönemlerinde kullan."
    )
    public ResponseEntity<Map<String, Object>> refreshViewBlocking() {
        log.warn("Admin triggered BLOCKING refresh - users will be affected!");

        try {
            long startTime = System.currentTimeMillis();
            refreshService.refreshSchoolSearchViewBlocking();
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "BLOCKING refresh completed");
            response.put("durationMs", duration);
            response.put("timestamp", LocalDateTime.now());
            response.put("warning", "This was a blocking operation!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Blocking refresh failed", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Blocking refresh failed: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * View status
     * GET /api/v1/admin/materialized-view/status
     */
    @GetMapping("/status")
    @Operation(
            summary = "View durumu",
            description = "Materialized view'in son refresh zamanı, boyutu ve kayıt sayısı"
    )
    public ResponseEntity<Map<String, Object>> getViewStatus() {
        try {
            LocalDateTime lastRefresh = refreshService.getLastRefreshTime();
            String size = refreshService.getViewSize();
            Long recordCount = refreshService.getRecordCount();
            boolean isHealthy = refreshService.isViewHealthy();

            Map<String, Object> status = new HashMap<>();
            status.put("lastRefresh", lastRefresh);
            status.put("size", size);
            status.put("recordCount", recordCount);
            status.put("isHealthy", isHealthy);
            status.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("Failed to get view status", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check
     * GET /api/v1/admin/materialized-view/health
     */
    @GetMapping("/health")
    @Operation(
            summary = "View sağlık kontrolü",
            description = "View'in sağlıklı olup olmadığını kontrol eder"
    )
    public ResponseEntity<Map<String, Object>> healthCheck() {
        boolean isHealthy = refreshService.isViewHealthy();

        Map<String, Object> health = new HashMap<>();
        health.put("healthy", isHealthy);
        health.put("timestamp", LocalDateTime.now());

        if (isHealthy) {
            return ResponseEntity.ok(health);
        } else {
            health.put("message", "View is not healthy - check logs");
            return ResponseEntity.status(503).body(health);
        }
    }
}