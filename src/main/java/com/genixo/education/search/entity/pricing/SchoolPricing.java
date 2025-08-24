package com.genixo.education.search.entity.pricing;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.enumaration.PaymentFrequency;
import com.genixo.education.search.enumaration.PricingStatus;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// ========================= SCHOOL PRICING =========================
@Entity
@Table(name = "school_pricing")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SchoolPricing extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @Column(name = "academic_year", nullable = false)
    private String academicYear; // 2024-2025

    @Column(name = "grade_level", nullable = false)
    private String gradeLevel; // Anaokulu, İlkokul, Ortaokul, Lise

    @Column(name = "class_level")
    private String classLevel; // 1, 2, 3, 4 etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency = Currency.TRY;

    // Registration fees
    @Column(name = "registration_fee", precision = 19, scale = 2)
    private BigDecimal registrationFee = BigDecimal.ZERO;

    @Column(name = "application_fee", precision = 19, scale = 2)
    private BigDecimal applicationFee = BigDecimal.ZERO;

    @Column(name = "enrollment_fee", precision = 19, scale = 2)
    private BigDecimal enrollmentFee = BigDecimal.ZERO;

    // Tuition fees
    @Column(name = "annual_tuition", precision = 19, scale = 2)
    private BigDecimal annualTuition;

    @Column(name = "monthly_tuition", precision = 19, scale = 2)
    private BigDecimal monthlyTuition;

    @Column(name = "semester_tuition", precision = 19, scale = 2)
    private BigDecimal semesterTuition;

    // Additional fees
    @Column(name = "book_fee", precision = 19, scale = 2)
    private BigDecimal bookFee = BigDecimal.ZERO;

    @Column(name = "uniform_fee", precision = 19, scale = 2)
    private BigDecimal uniformFee = BigDecimal.ZERO;

    @Column(name = "activity_fee", precision = 19, scale = 2)
    private BigDecimal activityFee = BigDecimal.ZERO;

    @Column(name = "technology_fee", precision = 19, scale = 2)
    private BigDecimal technologyFee = BigDecimal.ZERO;

    @Column(name = "laboratory_fee", precision = 19, scale = 2)
    private BigDecimal laboratoryFee = BigDecimal.ZERO;

    @Column(name = "library_fee", precision = 19, scale = 2)
    private BigDecimal libraryFee = BigDecimal.ZERO;

    @Column(name = "sports_fee", precision = 19, scale = 2)
    private BigDecimal sportsFee = BigDecimal.ZERO;

    @Column(name = "art_fee", precision = 19, scale = 2)
    private BigDecimal artFee = BigDecimal.ZERO;

    @Column(name = "music_fee", precision = 19, scale = 2)
    private BigDecimal musicFee = BigDecimal.ZERO;

    @Column(name = "transportation_fee", precision = 19, scale = 2)
    private BigDecimal transportationFee = BigDecimal.ZERO;

    @Column(name = "cafeteria_fee", precision = 19, scale = 2)
    private BigDecimal cafeteriaFee = BigDecimal.ZERO;

    @Column(name = "insurance_fee", precision = 19, scale = 2)
    private BigDecimal insuranceFee = BigDecimal.ZERO;

    @Column(name = "maintenance_fee", precision = 19, scale = 2)
    private BigDecimal maintenanceFee = BigDecimal.ZERO;

    @Column(name = "security_fee", precision = 19, scale = 2)
    private BigDecimal securityFee = BigDecimal.ZERO;

    @Column(name = "exam_fee", precision = 19, scale = 2)
    private BigDecimal examFee = BigDecimal.ZERO;

    @Column(name = "graduation_fee", precision = 19, scale = 2)
    private BigDecimal graduationFee = BigDecimal.ZERO;

    // Optional services
    @Column(name = "extended_day_fee", precision = 19, scale = 2)
    private BigDecimal extendedDayFee = BigDecimal.ZERO;

    @Column(name = "tutoring_fee", precision = 19, scale = 2)
    private BigDecimal tutoringFee = BigDecimal.ZERO;

    @Column(name = "summer_school_fee", precision = 19, scale = 2)
    private BigDecimal summerSchoolFee = BigDecimal.ZERO;

    @Column(name = "winter_camp_fee", precision = 19, scale = 2)
    private BigDecimal winterCampFee = BigDecimal.ZERO;

    @Column(name = "language_course_fee", precision = 19, scale = 2)
    private BigDecimal languageCourseFee = BigDecimal.ZERO;

    @Column(name = "private_lesson_fee", precision = 19, scale = 2)
    private BigDecimal privateLessonFee = BigDecimal.ZERO;

    // Calculated totals
    @Column(name = "total_annual_cost", precision = 19, scale = 2)
    private BigDecimal totalAnnualCost;

    @Column(name = "total_monthly_cost", precision = 19, scale = 2)
    private BigDecimal totalMonthlyCost;

    @Column(name = "total_one_time_fees", precision = 19, scale = 2)
    private BigDecimal totalOneTimeFees;

    // Payment terms
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_frequency")
    private PaymentFrequency paymentFrequency = PaymentFrequency.MONTHLY;

    @Column(name = "installment_count")
    private Integer installmentCount;

    @Column(name = "installment_amount", precision = 19, scale = 2)
    private BigDecimal installmentAmount;

    @Column(name = "down_payment_percentage")
    private Double downPaymentPercentage;

    @Column(name = "down_payment_amount", precision = 19, scale = 2)
    private BigDecimal downPaymentAmount;

    // Discount policies
    @Column(name = "early_payment_discount_percentage")
    private Double earlyPaymentDiscountPercentage = 0.0;

    @Column(name = "sibling_discount_percentage")
    private Double siblingDiscountPercentage = 0.0;

    @Column(name = "multi_year_discount_percentage")
    private Double multiYearDiscountPercentage = 0.0;

    @Column(name = "loyalty_discount_percentage")
    private Double loyaltyDiscountPercentage = 0.0;

    @Column(name = "need_based_aid_available")
    private Boolean needBasedAidAvailable = false;

    @Column(name = "merit_based_aid_available")
    private Boolean meritBasedAidAvailable = false;

    // Validity period
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PricingStatus status = PricingStatus.DRAFT;

    // Pricing tiers (for different payment plans)
    @Column(name = "pricing_tiers", columnDefinition = "JSON")
    private String pricingTiers;

    // Special conditions
    @Column(name = "refund_policy", columnDefinition = "TEXT")
    private String refundPolicy;

    @Column(name = "payment_terms", columnDefinition = "TEXT")
    private String paymentTerms;

    @Column(name = "late_payment_penalty_percentage")
    private Double latePaymentPenaltyPercentage = 0.0;

    @Column(name = "cancellation_fee", precision = 19, scale = 2)
    private BigDecimal cancellationFee = BigDecimal.ZERO;

    @Column(name = "withdrawal_refund_percentage")
    private Double withdrawalRefundPercentage = 0.0;

    // Approval workflow
    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approval_notes")
    private String approvalNotes;

    // Versioning
    @Column(name = "version")
    private Integer version = 1;

    @Column(name = "previous_pricing_id")
    private Long previousPricingId;

    @Column(name = "is_current")
    private Boolean isCurrent = true;

    // Comparison with competitors
    @Column(name = "market_position")
    private String marketPosition; // BUDGET, COMPETITIVE, PREMIUM, LUXURY

    @Column(name = "competitor_analysis", columnDefinition = "JSON")
    private String competitorAnalysis;

    // Notes and descriptions
    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    @Column(name = "public_description", columnDefinition = "TEXT")
    private String publicDescription;

    @Column(name = "fee_breakdown_notes", columnDefinition = "TEXT")
    private String feeBreakdownNotes;

    // Display preferences
    @Column(name = "show_detailed_breakdown")
    private Boolean showDetailedBreakdown = true;

    @Column(name = "highlight_total_cost")
    private Boolean highlightTotalCost = true;

    @Column(name = "show_payment_options")
    private Boolean showPaymentOptions = true;

    @Column(name = "show_financial_aid_info")
    private Boolean showFinancialAidInfo = false;

    // Relationships
    @OneToMany(mappedBy = "schoolPricing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PriceHistory> priceHistories = new HashSet<>();

    @OneToMany(mappedBy = "schoolPricing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CustomFee> customFees = new HashSet<>();
}