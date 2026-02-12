package com.genixo.education.search.service.webinar;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.common.util.SecurityUtils;
import com.genixo.education.search.dto.webinar.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.webinar.Event;
import com.genixo.education.search.entity.webinar.EventCategory;
import com.genixo.education.search.entity.webinar.EventOrganizer;
import com.genixo.education.search.repository.user.UserRepository;
import com.genixo.education.search.repository.webinar.EventCategoryRepository;
import com.genixo.education.search.repository.webinar.EventOrganizerRepository;
import com.genixo.education.search.repository.webinar.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventOrganizerRepository eventOrganizerRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventDto create(EventCreateDto dto) {
        User createdBy = resolveCurrentUser();
        EventOrganizer organizer = eventOrganizerRepository.findById(dto.getOrganizerId())
                .orElseThrow(() -> new ResourceNotFoundException("EventOrganizer", dto.getOrganizerId()));

        Event event = new Event();
        event.setOrganizer(organizer);
        event.setCreatedByUser(createdBy);
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setEventType(Event.EventType.valueOf(dto.getEventType()));
        event.setDeliveryFormat(Event.DeliveryFormat.valueOf(dto.getDeliveryFormat()));
        event.setStartDateTime(dto.getStartDateTime());
        event.setEndDateTime(dto.getEndDateTime());
        event.setMaxCapacity(dto.getMaxCapacity());
        event.setLocation(dto.getLocation());
        event.setOnlineLink(dto.getOnlineLink());
        event.setTargetAudience(dto.getTargetAudience());
        event.setSpeakerName(dto.getSpeakerName());
        event.setSpeakerBio(dto.getSpeakerBio());
        event.setCoverImageUrl(dto.getCoverImageUrl());
        event.setRegistrationDeadline(dto.getRegistrationDeadline());
        event.setStatus(dto.getStatus() != null ? Event.EventStatus.valueOf(dto.getStatus()) : Event.EventStatus.DRAFT);
        event.setAutoApproveRegistration(dto.getAutoApproveRegistration() != null ? dto.getAutoApproveRegistration() : true);
        event.setCertificateEnabled(dto.getCertificateEnabled() != null ? dto.getCertificateEnabled() : false);
        event.setCertificateTemplateUrl(dto.getCertificateTemplateUrl());

        if (dto.getCategoryId() != null) {
            EventCategory category = eventCategoryRepository.findById(dto.getCategoryId())
                    .orElse(null);
            event.setCategory(category);
        }

        Event saved = eventRepository.save(event);
        return mapToDto(saved);
    }

    public Page<EventDto> search(Long organizerId, Long categoryId, String eventType, String status,
                                 String searchTerm, LocalDateTime startDateFrom, LocalDateTime startDateTo,
                                 Pageable pageable) {
        Event.EventStatus statusEnum = parseEventStatus(status);
        return eventRepository.search(organizerId, categoryId, eventType, statusEnum,
                        searchTerm, startDateFrom, startDateTo, pageable)
                .map(this::mapToDto);
    }

    public EventDto getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
        return mapToDto(event);
    }

    public Page<EventDto> getByOrganizerId(Long organizerId, Pageable pageable) {
        if (!eventOrganizerRepository.existsById(organizerId)) {
            throw new ResourceNotFoundException("EventOrganizer", organizerId);
        }
        return eventRepository.findByOrganizerId(organizerId, pageable).map(this::mapToDto);
    }

    public Page<EventDto> getPublishedEvents(Pageable pageable) {
        return eventRepository.search(null, null, null, Event.EventStatus.PUBLISHED,
                        null, LocalDateTime.now(), null, pageable)
                .map(this::mapToDto);
    }

    @Transactional
    public EventDto update(Long id, EventUpdateDto dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        if (dto.getOrganizerId() != null) {
            EventOrganizer organizer = eventOrganizerRepository.findById(dto.getOrganizerId())
                    .orElseThrow(() -> new ResourceNotFoundException("EventOrganizer", dto.getOrganizerId()));
            event.setOrganizer(organizer);
        }
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventType() != null) event.setEventType(Event.EventType.valueOf(dto.getEventType()));
        if (dto.getDeliveryFormat() != null) event.setDeliveryFormat(Event.DeliveryFormat.valueOf(dto.getDeliveryFormat()));
        if (dto.getStartDateTime() != null) event.setStartDateTime(dto.getStartDateTime());
        if (dto.getEndDateTime() != null) event.setEndDateTime(dto.getEndDateTime());
        if (dto.getMaxCapacity() != null) event.setMaxCapacity(dto.getMaxCapacity());
        if (dto.getLocation() != null) event.setLocation(dto.getLocation());
        if (dto.getOnlineLink() != null) event.setOnlineLink(dto.getOnlineLink());
        if (dto.getTargetAudience() != null) event.setTargetAudience(dto.getTargetAudience());
        if (dto.getSpeakerName() != null) event.setSpeakerName(dto.getSpeakerName());
        if (dto.getSpeakerBio() != null) event.setSpeakerBio(dto.getSpeakerBio());
        if (dto.getCoverImageUrl() != null) event.setCoverImageUrl(dto.getCoverImageUrl());
        if (dto.getRegistrationDeadline() != null) event.setRegistrationDeadline(dto.getRegistrationDeadline());
        if (dto.getStatus() != null) event.setStatus(Event.EventStatus.valueOf(dto.getStatus()));
        if (dto.getAutoApproveRegistration() != null) event.setAutoApproveRegistration(dto.getAutoApproveRegistration());
        if (dto.getCertificateEnabled() != null) event.setCertificateEnabled(dto.getCertificateEnabled());
        if (dto.getCertificateTemplateUrl() != null) event.setCertificateTemplateUrl(dto.getCertificateTemplateUrl());
        if (dto.getCategoryId() != null) {
            EventCategory category = eventCategoryRepository.findById(dto.getCategoryId()).orElse(null);
            event.setCategory(category);
        }

        return mapToDto(eventRepository.save(event));
    }

    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event", id);
        }
        eventRepository.deleteById(id);
    }

    private Event.EventStatus parseEventStatus(String status) {
        if (status == null || status.isBlank()) return null;
        try {
            return Event.EventStatus.valueOf(status);
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

    private EventDto mapToDto(Event e) {
        int registrationCount = e.getRegistrations() != null ? e.getRegistrations().size() : 0;
        return EventDto.builder()
                .id(e.getId())
                .organizerId(e.getOrganizer().getId())
                .organizer(EventOrganizerSummaryDto.builder()
                        .id(e.getOrganizer().getId())
                        .name(e.getOrganizer().getName())
                        .slug(e.getOrganizer().getSlug())
                        .type(e.getOrganizer().getType().name())
                        .logoUrl(e.getOrganizer().getLogoUrl())
                        .build())
                .createdByUserId(e.getCreatedByUser() != null ? e.getCreatedByUser().getId() : null)
                .createdByUserEmail(e.getCreatedByUser() != null ? e.getCreatedByUser().getEmail() : null)
                .title(e.getTitle())
                .description(e.getDescription())
                .eventType(e.getEventType().name())
                .deliveryFormat(e.getDeliveryFormat().name())
                .startDateTime(e.getStartDateTime())
                .endDateTime(e.getEndDateTime())
                .maxCapacity(e.getMaxCapacity())
                .remainingCapacity(e.getRemainingCapacity())
                .location(e.getLocation())
                .onlineLink(e.getOnlineLink())
                .targetAudience(e.getTargetAudience())
                .speakerName(e.getSpeakerName())
                .speakerBio(e.getSpeakerBio())
                .coverImageUrl(e.getCoverImageUrl())
                .registrationDeadline(e.getRegistrationDeadline())
                .status(e.getStatus().name())
                .autoApproveRegistration(e.getAutoApproveRegistration())
                .certificateEnabled(e.getCertificateEnabled())
                .certificateTemplateUrl(e.getCertificateTemplateUrl())
                .categoryId(e.getCategory() != null ? e.getCategory().getId() : null)
                .category(e.getCategory() != null ? EventCategorySummaryDto.builder()
                        .id(e.getCategory().getId())
                        .name(e.getCategory().getName())
                        .slug(e.getCategory().getSlug())
                        .icon(e.getCategory().getIcon())
                        .build() : null)
                .registrationCount(registrationCount)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
