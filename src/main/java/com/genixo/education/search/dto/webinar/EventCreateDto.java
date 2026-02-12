package com.genixo.education.search.dto.webinar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class EventCreateDto {

    @NotNull(message = "Düzenleyen ID zorunludur")
    private Long organizerId;

    @NotBlank(message = "Başlık zorunludur")
    @Size(max = 200)
    private String title;

    @Size(max = 5000)
    private String description;

    @NotNull(message = "Etkinlik tipi zorunludur")
    @Size(max = 30)
    private String eventType; // WEBINAR, SEMINAR, TRAINING, WORKSHOP

    @NotNull(message = "Gerçekleşme formatı zorunludur")
    @Size(max = 20)
    private String deliveryFormat; // ONLINE, IN_PERSON, HYBRID

    @NotNull(message = "Başlangıç tarihi zorunludur")
    private LocalDateTime startDateTime;

    @NotNull(message = "Bitiş tarihi zorunludur")
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
    private String status; // DRAFT, PUBLISHED, COMPLETED, CANCELLED
}
