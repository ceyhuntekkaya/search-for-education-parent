package com.genixo.education.search.entity.pricing;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.PriceChangeType;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_pricing_id", nullable = false)
    private SchoolPricing schoolPricing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id", nullable = false)
    private User changedByUser;

    @Column(name = "change_date", nullable = false)
    private LocalDateTime changeDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private PriceChangeType changeType;

    @Column(name = "field_name", nullable = false)
    private String fieldName; // Which price field was changed

    @Column(name = "old_value", precision = 19, scale = 2)
    private BigDecimal oldValue;

    @Column(name = "new_value", precision = 19, scale = 2)
    private BigDecimal newValue;

    @Column(name = "change_percentage")
    private Double changePercentage;

    @Column(name = "change_amount", precision = 19, scale = 2)
    private BigDecimal changeAmount;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "change_notes", columnDefinition = "TEXT")
    private String changeNotes;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // Impact analysis
    @Column(name = "affected_students_count")
    private Integer affectedStudentsCount;

    @Column(name = "revenue_impact", precision = 19, scale = 2)
    private BigDecimal revenueImpact;

    @Column(name = "competitive_analysis", columnDefinition = "TEXT")
    private String competitiveAnalysis;

    // Communication
    @Column(name = "parents_notified")
    private Boolean parentsNotified = false;

    @Column(name = "notification_date")
    private LocalDateTime notificationDate;

    @Column(name = "notification_method")
    private String notificationMethod; // EMAIL, SMS, LETTER, WEBSITE

    @Column(name = "advance_notice_days")
    private Integer advanceNoticeDays;

    // Rollback information
    @Column(name = "can_rollback")
    private Boolean canRollback = false;

    @Column(name = "rollback_deadline")
    private LocalDateTime rollbackDeadline;

    @Column(name = "rollback_reason")
    private String rollbackReason;

    @Column(name = "rolled_back_at")
    private LocalDateTime rolledBackAt;

    @Column(name = "rolled_back_by")
    private Long rolledBackBy;
}