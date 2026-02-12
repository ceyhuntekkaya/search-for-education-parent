package com.genixo.education.search.service.webinar;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.common.util.SecurityUtils;
import com.genixo.education.search.dto.webinar.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.webinar.Event;
import com.genixo.education.search.entity.webinar.EventRegistration;
import com.genixo.education.search.repository.user.UserRepository;
import com.genixo.education.search.repository.webinar.EventRegistrationRepository;
import com.genixo.education.search.repository.webinar.EventRepository;
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
public class EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventRegistrationDto create(EventRegistrationCreateDto dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", dto.getEventId()));
        User teacher = userRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.getTeacherId()));

        if (eventRegistrationRepository.existsByEventIdAndTeacherId(dto.getEventId(), dto.getTeacherId())) {
            throw BusinessException.duplicateResource("EventRegistration", "event+teacher", dto.getEventId() + "+" + dto.getTeacherId());
        }

        if (event.isCapacityFull()) {
            throw BusinessException.resourceInUse("Event", "kontenjan dolu");
        }

        if (event.getStatus() != Event.EventStatus.PUBLISHED && event.getStatus() != Event.EventStatus.DRAFT) {
            throw BusinessException.invalidState(event.getStatus().name(), "kayıt");
        }

        if (event.getRegistrationDeadline() != null && event.getRegistrationDeadline().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Kayıt tarihi geçmiş");
        }

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setTeacher(teacher);
        registration.setRegistrationNote(dto.getRegistrationNote());
        registration.setStatus(event.getAutoApproveRegistration() != null && event.getAutoApproveRegistration()
                ? EventRegistration.RegistrationStatus.APPROVED
                : EventRegistration.RegistrationStatus.PENDING);

        EventRegistration saved = eventRegistrationRepository.save(registration);
        return mapToDto(saved);
    }

    public Page<EventRegistrationDto> search(Long eventId, Long teacherId, String status, Pageable pageable) {
        EventRegistration.RegistrationStatus statusEnum = parseRegistrationStatus(status);
        return eventRegistrationRepository.search(eventId, teacherId, statusEnum, pageable)
                .map(this::mapToDto);
    }

    public EventRegistrationDto getById(Long id) {
        EventRegistration registration = eventRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventRegistration", id));
        return mapToDto(registration);
    }

    public Page<EventRegistrationDto> getByEventId(Long eventId, Pageable pageable) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event", eventId);
        }
        return eventRegistrationRepository.findByEventId(eventId, pageable).map(this::mapToDto);
    }

    public Page<EventRegistrationDto> getByTeacherId(Long teacherId, Pageable pageable) {
        if (!userRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("User", teacherId);
        }
        return eventRegistrationRepository.findByTeacherId(teacherId, pageable).map(this::mapToDto);
    }

    @Transactional
    public EventRegistrationDto updateStatus(Long id, EventRegistrationStatusUpdateDto dto) {
        EventRegistration registration = eventRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventRegistration", id));
        registration.setStatus(EventRegistration.RegistrationStatus.valueOf(dto.getStatus()));
        return mapToDto(eventRegistrationRepository.save(registration));
    }

    @Transactional
    public EventRegistrationDto markAttendance(Long id, boolean attended) {
        User currentUser = resolveCurrentUser();
        EventRegistration registration = eventRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventRegistration", id));
        registration.setAttended(attended);
        registration.setAttendanceMarkedAt(LocalDateTime.now());
        registration.setAttendanceMarkedBy(currentUser);
        return mapToDto(eventRegistrationRepository.save(registration));
    }

    @Transactional
    public void delete(Long id) {
        if (!eventRegistrationRepository.existsById(id)) {
            throw new ResourceNotFoundException("EventRegistration", id);
        }
        eventRegistrationRepository.deleteById(id);
    }

    private EventRegistration.RegistrationStatus parseRegistrationStatus(String status) {
        if (status == null || status.isBlank()) return null;
        try {
            return EventRegistration.RegistrationStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private User resolveCurrentUser() {
        String email = SecurityUtils.getCurrentUser();
        if (email == null) {
            throw new BusinessException("Oturum açmış kullanıcı gerekli");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    private EventRegistrationDto mapToDto(EventRegistration r) {
        String teacherName = r.getTeacher() != null
                ? (r.getTeacher().getFirstName() + " " + r.getTeacher().getLastName()).trim()
                : null;
        return EventRegistrationDto.builder()
                .id(r.getId())
                .eventId(r.getEvent().getId())
                .event(EventSummaryDto.builder()
                        .id(r.getEvent().getId())
                        .title(r.getEvent().getTitle())
                        .eventType(r.getEvent().getEventType().name())
                        .startDateTime(r.getEvent().getStartDateTime())
                        .endDateTime(r.getEvent().getEndDateTime())
                        .status(r.getEvent().getStatus().name())
                        .organizer(r.getEvent().getOrganizer() != null ? EventOrganizerSummaryDto.builder()
                                .id(r.getEvent().getOrganizer().getId())
                                .name(r.getEvent().getOrganizer().getName())
                                .slug(r.getEvent().getOrganizer().getSlug())
                                .type(r.getEvent().getOrganizer().getType().name())
                                .build() : null)
                        .build())
                .teacherId(r.getTeacher() != null ? r.getTeacher().getId() : null)
                .teacherEmail(r.getTeacher() != null ? r.getTeacher().getEmail() : null)
                .teacherName(teacherName)
                .registrationNote(r.getRegistrationNote())
                .status(r.getStatus().name())
                .attended(r.getAttended())
                .attendanceMarkedAt(r.getAttendanceMarkedAt())
                .attendanceMarkedByUserId(r.getAttendanceMarkedBy() != null ? r.getAttendanceMarkedBy().getId() : null)
                .certificateUrl(r.getCertificateUrl())
                .certificateGeneratedAt(r.getCertificateGeneratedAt())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
