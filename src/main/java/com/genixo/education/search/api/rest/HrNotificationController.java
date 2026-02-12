package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.hr.HrNotificationDto;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.hr.HrNotificationService;
import com.genixo.education.search.entity.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/hr/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR - Bildirimler", description = "İnsan kaynakları bildirimleri")
public class HrNotificationController {

    private final HrNotificationService hrNotificationService;
    private final JwtService jwtService;

    @GetMapping
    @Operation(summary = "Kullanıcının bildirimleri")
    public ResponseEntity<ApiResponse<Page<HrNotificationDto>>> getNotifications(
            @Parameter(description = "Sayfa") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Boyut") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        User user = jwtService.getUser(request);
        Pageable pageable = PageRequest.of(page, size);
        Page<HrNotificationDto> result = hrNotificationService.getUserNotifications(user.getId(), pageable);
        ApiResponse<Page<HrNotificationDto>> response = ApiResponse.success(result, "Bildirimler getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Okunmamış bildirim sayısı")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(HttpServletRequest request) {
        User user = jwtService.getUser(request);
        Long count = hrNotificationService.getUnreadCount(user.getId());
        ApiResponse<Map<String, Long>> response = ApiResponse.success(Map.of("unreadCount", count), "Sayı getirildi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Bildirimi okundu işaretle")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            HttpServletRequest request) {
        User user = jwtService.getUser(request);
        hrNotificationService.markAsRead(id, user.getId());
        ApiResponse<Void> response = ApiResponse.success(null, "Bildirim okundu işaretlendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/mark-all-read")
    @Operation(summary = "Tüm bildirimleri okundu işaretle")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(HttpServletRequest request) {
        User user = jwtService.getUser(request);
        hrNotificationService.markAllAsRead(user.getId());
        ApiResponse<Void> response = ApiResponse.success(null, "Tüm bildirimler okundu işaretlendi");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}
