package com.genixo.education.search.service.hr;

import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.hr.HrNotificationDto;
import com.genixo.education.search.entity.hr.Application;
import com.genixo.education.search.entity.hr.JobPosting;
import com.genixo.education.search.entity.hr.HrNotification;
import com.genixo.education.search.entity.hr.TeacherProfile;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.repository.hr.ApplicationRepository;
import com.genixo.education.search.repository.hr.HrNotificationRepository;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HrNotificationService {

    private final HrNotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public void notifyApplicationReceived(JobPosting jobPosting, TeacherProfile teacher, Application application) {
        List<School> schools = schoolRepository.findByCampusIdAndIsActiveTrueOrderByName(jobPosting.getCampus().getId());
        Set<Long> schoolIds = schools.stream().map(School::getId).collect(Collectors.toSet());
        Set<Long> schoolUserIds = userRepository.findByCampus(
                jobPosting.getCampus().getId(),
                schoolIds
        ).stream().map(u -> u.getId()).collect(Collectors.toSet());

        String title = "Yeni Başvuru";
        String message = teacher.getFullName() + " '" + jobPosting.getPositionTitle() + "' pozisyonuna başvurdu.";

        for (Long userId : schoolUserIds) {
            createNotification(userId, application.getId(), title, message, HrNotification.NotificationType.APPLICATION_RECEIVED);
        }
    }

    @Transactional
    public void notifyStatusUpdated(Application application, Application.ApplicationStatus newStatus) {
        Long teacherUserId = application.getTeacher().getUser().getId();
        String title = "Başvuru Durumu Güncellendi";
        String message = application.getJobPosting().getPositionTitle() + " ilanındaki başvurunuz " + newStatus.name() + " olarak güncellendi.";
        createNotification(teacherUserId, application.getId(), title, message, HrNotification.NotificationType.STATUS_UPDATED);
    }

    @Transactional
    public void notifyApplicationWithdrawn(Application application) {
        List<School> schools = schoolRepository.findByCampusIdAndIsActiveTrueOrderByName(application.getJobPosting().getCampus().getId());
        Set<Long> schoolIds = schools.stream().map(School::getId).collect(Collectors.toSet());
        Set<Long> schoolUserIds = userRepository.findByCampus(
                application.getJobPosting().getCampus().getId(),
                schoolIds
        ).stream().map(u -> u.getId()).collect(Collectors.toSet());

        String title = "Başvuru Geri Çekildi";
        String message = application.getTeacher().getFullName() + " başvurusunu geri çekti.";

        for (Long userId : schoolUserIds) {
            createNotification(userId, application.getId(), title, message, HrNotification.NotificationType.APPLICATION_WITHDRAWN);
        }
    }

    @Transactional
    public void notifyNewJobPosted(JobPosting jobPosting) {
        // Could notify teachers who registered for job alerts in relevant provinces
        // For now, this is a placeholder - implement when teacher preferences exist
    }

    @Transactional
    void createNotification(Long userId, Long applicationId, String title, String message, HrNotification.NotificationType type) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        HrNotification n = new HrNotification();
        n.setUser(user);
        n.setApplication(applicationId != null ? applicationRepository.getReferenceById(applicationId) : null);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setIsRead(false);
        notificationRepository.save(n);
    }

    public Page<HrNotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToDto);
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public void markAsRead(Long id, Long userId) {
        HrNotification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HrNotification", id));
        if (!n.getUser().getId().equals(userId)) {
            return;
        }
        n.setIsRead(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    private HrNotificationDto mapToDto(HrNotification n) {
        return HrNotificationDto.builder()
                .id(n.getId())
                .userId(n.getUser().getId())
                .applicationId(n.getApplication() != null ? n.getApplication().getId() : null)
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType().name())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
