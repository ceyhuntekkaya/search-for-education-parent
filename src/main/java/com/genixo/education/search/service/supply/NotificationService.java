package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.NotificationDto;
import com.genixo.education.search.entity.supply.Notification;
import com.genixo.education.search.supply.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // ================================ NOTIFICATION CRUD ================================

    public Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        log.info("Fetching notifications for user ID: {}", userId);

        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(this::mapToDto);
    }

    public Long getUnreadCount(Long userId) {
        log.info("Fetching unread notification count for user ID: {}", userId);

        Long count = notificationRepository.countUnreadByUserId(userId);
        return count != null ? count : 0L;
    }

    @Transactional
    public NotificationDto markAsRead(Long id, Long userId) {
        log.info("Marking notification ID: {} as read by user ID: {}", id, userId);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));

        // Verify ownership
        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification", id);
        }

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        Notification updated = notificationRepository.save(notification);

        log.info("Notification marked as read successfully with ID: {}", id);
        return mapToDto(updated);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user ID: {}", userId);

        notificationRepository.markAllAsReadByUserId(userId);
        log.info("All notifications marked as read for user ID: {}", userId);
    }

    @Transactional
    public void deleteNotification(Long id, Long userId) {
        log.info("Deleting notification ID: {} by user ID: {}", id, userId);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));

        // Verify ownership
        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification", id);
        }

        notificationRepository.delete(notification);
        log.info("Notification deleted successfully with ID: {}", id);
    }

    // ================================ HELPER METHODS ================================

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .isRead(notification.getIsRead())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}

