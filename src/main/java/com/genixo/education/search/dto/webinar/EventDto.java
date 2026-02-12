package com.genixo.education.search.dto.webinar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private Long id;
    private Long organizerId;
    private EventOrganizerSummaryDto organizer;
    private Long createdByUserId;
    private String createdByUserEmail;
    private String title;
    private String description;
    private String eventType;
    private String deliveryFormat;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer maxCapacity;
    private Integer remainingCapacity;
    private String location;
    private String onlineLink;
    private String targetAudience;
    private String speakerName;
    private String speakerBio;
    private String coverImageUrl;
    private LocalDateTime registrationDeadline;
    private String status;
    private Boolean autoApproveRegistration;
    private Boolean certificateEnabled;
    private String certificateTemplateUrl;
    private Long categoryId;
    private EventCategorySummaryDto category;
    private Integer registrationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
