package com.genixo.education.search.dto.pricing;

import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.enumaration.PaymentFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolPricingCreateDto {
    private Long schoolId;
    private Long createdByUserId;
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

    // Payment terms
    private PaymentFrequency paymentFrequency;
    private Integer installmentCount;
    private Double downPaymentPercentage;

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

    // Policies
    private String refundPolicy;
    private String paymentTerms;
    private Double latePaymentPenaltyPercentage;
    private BigDecimal cancellationFee;
    private Double withdrawalRefundPercentage;

    // Additional info
    private String internalNotes;
    private String publicDescription;
    private String feeBreakdownNotes;
    private String marketPosition;

    // Display preferences
    private Boolean showDetailedBreakdown;
    private Boolean highlightTotalCost;
    private Boolean showPaymentOptions;
    private Boolean showFinancialAidInfo;
}
