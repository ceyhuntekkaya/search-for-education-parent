package com.genixo.education.search.entity.survey;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.RatingCategory;
import com.genixo.education.search.entity.institution.School;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_ratings")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SurveyRating extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_response_id")
    private SurveyResponse surveyResponse;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_category", nullable = false)
    private RatingCategory ratingCategory;

    @Column(name = "rating_value", nullable = false)
    private Integer ratingValue; // 1-5 yıldız

    @Column(name = "rating_text")
    private String ratingText;

    @Column(name = "weight")
    private Double weight = 1.0; // Ağırlık katsayısı

    // Verification
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    // Moderation
    @Column(name = "is_flagged")
    private Boolean isFlagged = false;

    @Column(name = "flag_reason")
    private String flagReason;

    @Column(name = "flagged_by")
    private Long flaggedBy;

    @Column(name = "flagged_at")
    private LocalDateTime flaggedAt;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "moderator_notes")
    private String moderatorNotes;
}