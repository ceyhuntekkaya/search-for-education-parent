package com.genixo.education.search.dto.hr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfileDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private String branch;
    private String educationLevel;
    private Integer experienceYears;
    private String bio;
    private String profilePhotoUrl;
    private String videoUrl;
    private String cvUrl;
    private Boolean isActive;
    private List<ProvinceSummaryDto> provinces;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
