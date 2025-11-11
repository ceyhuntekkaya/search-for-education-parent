package com.genixo.education.search.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionUsageDto {
    private Long subscriptionId;
    private String campusName;
    private String planName;

    // Current usage
    private Integer schoolsUsed;
    private Integer schoolsLimit;
    private Double schoolsUsagePercentage;

    private Integer usersUsed;
    private Integer usersLimit;
    private Double usersUsagePercentage;

    private Integer appointmentsThisMonth;
    private Integer appointmentsLimit;
    private Double appointmentsUsagePercentage;

    private Integer galleryItemsUsed;
    private Integer galleryItemsLimit;
    private Double galleryItemsUsagePercentage;

    private Integer postsThisMonth;
    private Integer postsLimit;
    private Double postsUsagePercentage;

    private Long storageUsedMb;
    private Long storageLimitMb;
    private Double storageUsagePercentage;

    // Usage warnings
    private List<String> usageWarnings;
    private List<String> limitExceeded;

    // Recommendations
    private List<String> upgradeRecommendations;
}