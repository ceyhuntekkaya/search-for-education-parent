package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanRevenueDto {
    private Long planId;
    private String planName;
    private BigDecimal revenue;
    private Integer subscriberCount;
    private Double revenuePercentage;
    private Double subscriberPercentage;
    private BigDecimal averageRevenuePerUser;
}