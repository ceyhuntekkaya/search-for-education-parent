package com.genixo.education.search.dto.webinar;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUpdateDto {

    private Long organizerId;
    private String title;
    private String description;
    private String eventType;
    private String deliveryFormat;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer maxCapacity;
    private String location;
    private String onlineLink;
    private String targetAudience;
    private String speakerName;
    private String speakerBio;
    private String coverImageUrl;
    private LocalDateTime registrationDeadline;
    private Long categoryId;
    private Boolean autoApproveRegistration;
    private Boolean certificateEnabled;
    private String certificateTemplateUrl;
    private String status;
}
