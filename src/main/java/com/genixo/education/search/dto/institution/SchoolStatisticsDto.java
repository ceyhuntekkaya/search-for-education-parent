package com.genixo.education.search.dto.institution;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolStatisticsDto {
    private Long totalViews;
    private Long monthlyViews;
    private Long weeklyViews;
    private Long dailyViews;
    private Long totalAppointments;
    private Long completedAppointments;
    private Long totalInquiries;
    private Long totalEnrollments;
    private Double conversionRate;
    private Double appointmentConversionRate;
    private Double ratingAverage;
    private Long ratingCount;
    private Map<String, Long> ratingDistribution;
    private Long socialMediaFollowers;
    private Long socialMediaPosts;
    private Long socialMediaLikes;
}

