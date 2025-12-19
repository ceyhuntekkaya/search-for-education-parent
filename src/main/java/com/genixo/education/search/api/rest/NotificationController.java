package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.supply.NotificationDto;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.supply.NotificationService;
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

@RestController
@RequestMapping("/supply/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Management", description = "APIs for managing notifications in the supply system")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;

    @GetMapping
    @Operation(summary = "Get user notifications", description = "Get all notifications for the current user with pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<NotificationDto>>> getUserNotifications(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationDto> notifications = notificationService.getUserNotifications(user.getId(), pageable);

        ApiResponse<Page<NotificationDto>> response = ApiResponse.success(notifications, "Notifications retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count", description = "Get count of unread notifications for the current user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Unread count retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        Long count = notificationService.getUnreadCount(user.getId());

        ApiResponse<Long> response = ApiResponse.success(count, "Unread count retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a notification as read")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification marked as read successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(
            @Parameter(description = "Notification ID") @PathVariable Long id,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        NotificationDto notificationDto = notificationService.markAsRead(id, user.getId());

        ApiResponse<NotificationDto> response = ApiResponse.success(notificationDto, "Notification marked as read successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/mark-all-read")
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read for the current user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All notifications marked as read successfully")
    })
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        notificationService.markAllAsRead(user.getId());

        ApiResponse<Void> response = ApiResponse.success(null, "All notifications marked as read successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Delete a notification (only owner can delete)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @Parameter(description = "Notification ID") @PathVariable Long id,
            HttpServletRequest request) {

        User user = jwtService.getUser(request);
        notificationService.deleteNotification(id, user.getId());

        ApiResponse<Void> response = ApiResponse.success(null, "Notification deleted successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

