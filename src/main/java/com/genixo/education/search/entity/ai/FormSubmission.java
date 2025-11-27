package com.genixo.education.search.entity.ai;


import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "form_submissions")
@Data
@EqualsAndHashCode(callSuper = true)
public class FormSubmission extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Form alanları
    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "institution_type")
    private String institutionType; // İlkokul, Ortaokul, Lise vb.

    @Column(name = "school_properties", columnDefinition = "TEXT")
    private String schoolProperties; // JSON array: ["Yüzme havuzu", "Laboratuvar"]

    @Column(name = "min_price")
    private Double minPrice;

    @Column(name = "max_price")
    private Double maxPrice;

    @Column(name = "explain", columnDefinition = "TEXT")
    private String explain; // Ek açıklamalar

    @Column(name = "raw_form_data", columnDefinition = "TEXT")
    private String rawFormData; // AI'dan gelen ham JSON

    @Column(name = "search_results_count")
    private Integer searchResultsCount; // Bu kriterlere kaç okul bulundu

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
}