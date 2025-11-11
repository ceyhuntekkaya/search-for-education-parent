package com.genixo.education.search.dto.pricing;

import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.enumaration.PaymentFrequency;
import com.genixo.education.search.enumaration.PricingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


// ========================= SCHOOL PRICING DTO =========================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolPricingDto {
    private Long id;
    private Long schoolId;
    private String schoolName;
    private String academicYear;
    private String gradeLevel;
    private String classLevel;
    private Currency currency;

    // Registration fees
    private BigDecimal registrationFee;
    private BigDecimal applicationFee;
    private BigDecimal enrollmentFee;

    // Tuition fees
    private BigDecimal annualTuition;
    private BigDecimal monthlyTuition;
    private BigDecimal semesterTuition;

    // Additional fees
    private BigDecimal bookFee;
    private BigDecimal uniformFee;
    private BigDecimal activityFee;
    private BigDecimal technologyFee;
    private BigDecimal laboratoryFee;
    private BigDecimal libraryFee;
    private BigDecimal sportsFee;
    private BigDecimal artFee;
    private BigDecimal musicFee;
    private BigDecimal transportationFee;
    private BigDecimal cafeteriaFee;
    private BigDecimal insuranceFee;
    private BigDecimal maintenanceFee;
    private BigDecimal securityFee;
    private BigDecimal examFee;
    private BigDecimal graduationFee;

    // Optional services
    private BigDecimal extendedDayFee;
    private BigDecimal tutoringFee;
    private BigDecimal summerSchoolFee;
    private BigDecimal winterCampFee;
    private BigDecimal languageCourseFee;
    private BigDecimal privateLessonFee;

    // Calculated totals
    private BigDecimal totalAnnualCost;
    private BigDecimal totalMonthlyCost;
    private BigDecimal totalOneTimeFees;

    // Payment terms
    private PaymentFrequency paymentFrequency;
    private Integer installmentCount;
    private BigDecimal installmentAmount;
    private Double downPaymentPercentage;
    private BigDecimal downPaymentAmount;

    // Discount policies
    private Double earlyPaymentDiscountPercentage;
    private Double siblingDiscountPercentage;
    private Double multiYearDiscountPercentage;
    private Double loyaltyDiscountPercentage;
    private Boolean needBasedAidAvailable;
    private Boolean meritBasedAidAvailable;

    // Validity period
    private LocalDate validFrom;
    private LocalDate validUntil;
    private PricingStatus status;

    // Policies
    private String refundPolicy;
    private String paymentTerms;
    private Double latePaymentPenaltyPercentage;
    private BigDecimal cancellationFee;
    private Double withdrawalRefundPercentage;

    // Additional info
    private String publicDescription;
    private String feeBreakdownNotes;
    private String marketPosition;

    // Display preferences
    private Boolean showDetailedBreakdown;
    private Boolean highlightTotalCost;
    private Boolean showPaymentOptions;
    private Boolean showFinancialAidInfo;

    // Metadata
    private Integer version;
    private Boolean isCurrent;
    private String createdByUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Formatted values for display
    private String formattedAnnualTuition;
    private String formattedMonthlyTuition;
    private String formattedTotalCost;
    private String ageRange;

    // Custom fees
    private List<CustomFeeDto> customFees;


    private String internalNotes;
    private String competitorAnalysis;
    private String approvalNotes;
}