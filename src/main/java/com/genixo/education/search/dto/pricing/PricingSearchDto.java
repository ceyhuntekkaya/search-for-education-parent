package com.genixo.education.search.dto.pricing;

import com.genixo.education.search.enumaration.PaymentFrequency;
import com.genixo.education.search.enumaration.PricingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingSearchDto {
    private String searchTerm;
    private List<Long> schoolIds;
    private String academicYear;
    private List<String> gradeLevels;
    private BigDecimal minMonthlyTuition;
    private BigDecimal maxMonthlyTuition;
    private BigDecimal minAnnualTuition;
    private BigDecimal maxAnnualTuition;
    private List<PaymentFrequency> paymentFrequencies;
    private Boolean hasFinancialAid;
    private Boolean hasTransportation;
    private Boolean hasCafeteria;
    private Boolean hasExtendedDay;
    private List<PricingStatus> statuses;
    private Boolean isCurrentOnly;

    // Location filters
    private Long countryId;
    private Long provinceId;
    private Long districtId;

    // Institution filters
    private List<Long> institutionTypeIds;

    // Sorting
    private String sortBy; // PRICE, NAME, CREATED_DATE
    private String sortDirection;

    // Pagination
    private Integer page;
    private Integer size;
}