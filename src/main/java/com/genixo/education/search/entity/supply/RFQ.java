package com.genixo.education.search.entity.supply;


import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.enumaration.RFQStatus;
import com.genixo.education.search.enumaration.RFQType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rfqs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RFQ extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Campus company;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RFQType rfqType = RFQType.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RFQStatus status = RFQStatus.DRAFT;

    @Column(nullable = false)
    private LocalDate submissionDeadline;

    private LocalDate expectedDeliveryDate;

    @Column(columnDefinition = "TEXT")
    private String paymentTerms;

    @Column(columnDefinition = "TEXT")
    private String evaluationCriteria;

    @Column(columnDefinition = "TEXT")
    private String technicalRequirements;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
