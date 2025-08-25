package com.genixo.education.search.entity.pricing;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.CustomFeeStatus;
import com.genixo.education.search.enumaration.CustomFeeType;
import com.genixo.education.search.enumaration.PaymentFrequency;
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
@Table(name = "custom_fees")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomFee extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_pricing_id", nullable = false)
    private SchoolPricing schoolPricing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @Column(name = "fee_name", nullable = false)
    private String feeName;

    @Column(name = "fee_description", columnDefinition = "TEXT")
    private String feeDescription;

    @Column(name = "fee_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal feeAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private CustomFeeType feeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_frequency", nullable = false)
    private PaymentFrequency feeFrequency;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = true;

    @Column(name = "is_refundable")
    private Boolean isRefundable = false;

    @Column(name = "applies_to_new_students")
    private Boolean appliesToNewStudents = true;

    @Column(name = "applies_to_existing_students")
    private Boolean appliesToExistingStudents = true;

    @Column(name = "applies_to_grades")
    private String appliesToGrades;

    @Column(name = "minimum_age")
    private Integer minimumAge;

    @Column(name = "maximum_age")
    private Integer maximumAge;

    // Validity
    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CustomFeeStatus status = CustomFeeStatus.ACTIVE;

    // Payment terms
    @Column(name = "due_date_offset_days")
    private Integer dueDateOffsetDays = 0; // Days after enrollment/semester start

    @Column(name = "late_fee_percentage")
    private Double lateFeePercentage = 0.0;

    @Column(name = "installment_allowed")
    private Boolean installmentAllowed = false;

    @Column(name = "max_installments")
    private Integer maxInstallments;

    // Conditions
    @Column(name = "prerequisite_fees", columnDefinition = "JSON")
    private String prerequisiteFees; // Other fees that must be paid first

    @Column(name = "mutually_exclusive_fees", columnDefinition = "JSON")
    private String mutuallyExclusiveFees; // Fees that cannot be combined

    @Column(name = "discount_eligible")
    private Boolean discountEligible = true;

    @Column(name = "scholarship_applicable")
    private Boolean scholarshipApplicable = true;

    // Approval and authorization
    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approval_notes")
    private String approvalNotes;

    // Documentation
    @Column(name = "documentation_required")
    private Boolean documentationRequired = false;

    @Column(name = "required_documents", columnDefinition = "JSON")
    private String requiredDocuments;

    @Column(name = "fee_policy", columnDefinition = "TEXT")
    private String feePolicy;

    // Display and communication
    @Column(name = "display_on_invoice")
    private Boolean displayOnInvoice = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "parent_notification_required")
    private Boolean parentNotificationRequired = true;

    @Column(name = "advance_notice_days")
    private Integer advanceNoticeDays = 30;

    // Analytics
    @Column(name = "collection_rate")
    private Double collectionRate = 0.0;

    @Column(name = "total_collected", precision = 19, scale = 2)
    private BigDecimal totalCollected = BigDecimal.ZERO;

    @Column(name = "students_charged")
    private Integer studentsCharged = 0;

    @Column(name = "students_paid")
    private Integer studentsPaid = 0;

    @Column(name = "average_payment_delay_days")
    private Double averagePaymentDelayDays = 0.0;
}