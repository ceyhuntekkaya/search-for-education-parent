package com.genixo.education.search.dto.pricing;

import com.genixo.education.search.enumaration.PricingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkPricingOperationDto {
    private String operation; // COPY_TO_NEW_YEAR, APPLY_INCREASE, UPDATE_STATUS
    private List<Long> pricingIds;
    private String newAcademicYear;
    private Double increasePercentage;
    private PricingStatus newStatus;
    private String reason;
    private Boolean notifyParents;
    private Integer advanceNoticeDays;
}