package com.genixo.education.search.dto.pricing;

import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.enumaration.PaymentFrequency;
import com.genixo.education.search.enumaration.PricingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class SchoolPricingUpdateDto {
    // Basic fees
    private BigDecimal registrationFee;
    private BigDecimal applicationFee;
    private BigDecimal enrollmentFee;
    private BigDecimal annualTuition;
    private BigDecimal monthlyTuition;
    private BigDecimal semesterTuition;

    // Additional fees
    private BigDecimal bookFee;
    private BigDecimal uniformFee;
    private BigDecimal activityFee;
    private BigDecimal technologyFee;
    private BigDecimal transportationFee;
    private BigDecimal cafeteriaFee;

    // Payment terms
    private PaymentFrequency paymentFrequency;
    private Integer installmentCount;
    private Double downPaymentPercentage;

    // Discount policies
    private Double earlyPaymentDiscountPercentage;
    private Double siblingDiscountPercentage;
    private Double multiYearDiscountPercentage;

    // Policies
    private String refundPolicy;
    private String paymentTerms;
    private Double latePaymentPenaltyPercentage;

    // Validity period
    private LocalDate validFrom;
    private LocalDate validUntil;
    private PricingStatus status;

    // Display preferences
    private Boolean showDetailedBreakdown;
    private Boolean showPaymentOptions;
    private Boolean showFinancialAidInfo;

    // Notes
    private String internalNotes;
    private String publicDescription;


    private String gradeLevel;

    private String classLevel;

    private Currency currency;
}