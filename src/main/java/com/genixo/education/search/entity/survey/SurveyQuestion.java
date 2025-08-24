package com.genixo.education.search.entity.survey;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.ConditionType;
import com.genixo.education.search.enumaration.QuestionType;
import com.genixo.education.search.enumaration.RatingCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "survey_questions")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SurveyQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_category")
    private RatingCategory ratingCategory; // Yıldız değerlendirme kategorisi

    @Column(name = "is_required")
    private Boolean isRequired = false;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // For choice questions
    @Column(name = "options", columnDefinition = "JSON")
    private String options; // JSON array of options

    @Column(name = "allow_other_option")
    private Boolean allowOtherOption = false;

    @Column(name = "other_option_label")
    private String otherOptionLabel = "Diğer";

    // For rating questions
    @Column(name = "rating_scale_min")
    private Integer ratingScaleMin = 1;

    @Column(name = "rating_scale_max")
    private Integer ratingScaleMax = 5;

    @Column(name = "rating_scale_step")
    private Integer ratingScaleStep = 1;

    @Column(name = "rating_labels", columnDefinition = "JSON")
    private String ratingLabels; // JSON object for scale labels

    // For text questions
    @Column(name = "text_min_length")
    private Integer textMinLength;

    @Column(name = "text_max_length")
    private Integer textMaxLength;

    @Column(name = "placeholder_text")
    private String placeholderText;

    // Conditional logic
    @Column(name = "show_if_question_id")
    private Long showIfQuestionId;

    @Column(name = "show_if_answer")
    private String showIfAnswer;

    @Enumerated(EnumType.STRING)
    @Column(name = "show_if_condition")
    private ConditionType showIfCondition;

    // Styling
    @Column(name = "custom_css_class")
    private String customCssClass;

    @Column(name = "help_text")
    private String helpText;

    @Column(name = "image_url")
    private String imageUrl;

    // Analytics
    @Column(name = "total_responses")
    private Long totalResponses = 0L;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "skip_count")
    private Long skipCount = 0L;

    // Relationships
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SurveyQuestionResponse> questionResponses = new HashSet<>();
}