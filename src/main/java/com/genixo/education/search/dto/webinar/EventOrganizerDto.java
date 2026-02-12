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
public class EventOrganizerDto {
    private Long id;
    private String name;
    private String slug;
    private String type;
    private String description;
    private String logoUrl;
    private String website;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String socialMediaLinks;
    private Boolean isVerified;
    private Boolean isActive;
    private Long createdByUserId;
    private String createdByUserEmail;
    private Integer eventCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
