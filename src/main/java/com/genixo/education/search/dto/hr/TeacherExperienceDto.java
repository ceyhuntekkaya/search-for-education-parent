package com.genixo.education.search.dto.hr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherExperienceDto {
    private Long id;
    private Long teacherProfileId;
    private String institution;
    private String roleTitle;
    private LocalDate startDate;
    private LocalDate endDate; // null = hâlâ çalışıyor
    private String description; // Görev tanımı / iş açıklaması
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
