package com.genixo.education.search.dto.survey;

import com.genixo.education.search.enumaration.RatingCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyRatingDto {
    private Long id;
    private Long schoolId;
    private String schoolName;
    private Long surveyResponseId;
    private RatingCategory ratingCategory;
    private Integer ratingValue;
    private String ratingText;
    private Double weight;

    // Verification
    private Boolean isVerified;
    private String verifiedByUserName;
    private LocalDateTime verifiedAt;

    // Moderation
    private Boolean isFlagged;
    private String flagReason;
    private String flaggedByUserName;
    private LocalDateTime flaggedAt;
    private Boolean isPublic;
    private String moderatorNotes;

    // Calculated fields
    private String ratingCategoryDisplayName;
    private String ratingDisplayText;
    private String starDisplay;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}