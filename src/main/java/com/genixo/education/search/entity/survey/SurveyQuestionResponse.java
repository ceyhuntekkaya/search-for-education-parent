package com.genixo.education.search.entity.survey;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_question_responses")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SurveyQuestionResponse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_response_id", nullable = false)
    private SurveyResponse surveyResponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private SurveyQuestion question;

    // Different response types
    @Column(name = "text_response", columnDefinition = "TEXT")
    private String textResponse;

    @Column(name = "number_response")
    private Double numberResponse;

    @Column(name = "date_response")
    private java.time.LocalDate dateResponse;

    @Column(name = "time_response")
    private java.time.LocalTime timeResponse;

    @Column(name = "datetime_response")
    private LocalDateTime datetimeResponse;

    @Column(name = "boolean_response")
    private Boolean booleanResponse;

    @Column(name = "rating_response")
    private Integer ratingResponse;

    // For multiple choice questions (JSON array)
    @Column(name = "choice_responses")
    private String choiceResponses;

    // For matrix questions (JSON object)
    @Column(name = "matrix_responses")
    private String matrixResponses;

    // File uploads
    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    // Other option text for choice questions
    @Column(name = "other_text")
    private String otherText;

    // Response metadata
    @Column(name = "response_time_seconds")
    private Integer responseTimeSeconds;

    @Column(name = "was_skipped")
    private Boolean wasSkipped = false;

    @Column(name = "skip_reason")
    private String skipReason;

    @Column(name = "response_order")
    private Integer responseOrder; // Hangi sırada yanıtlandı

    @Column(name = "revision_count")
    private Integer revisionCount = 0; // Kaç kez değiştirildi

    @Column(name = "confidence_level")
    private Integer confidenceLevel; // Yanıt güven seviyesi (1-5)
}
