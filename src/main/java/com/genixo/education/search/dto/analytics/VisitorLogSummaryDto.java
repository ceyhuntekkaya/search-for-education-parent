package com.genixo.education.search.dto.analytics;

import com.genixo.education.search.enumaration.DeviceType;
import com.genixo.education.search.enumaration.TrafficSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorLogSummaryDto {
    private LocalDateTime visitTime;
    private String pageUrl;
    private String pageTitle;
    private DeviceType deviceType;
    private TrafficSource trafficSource;
    private String country;
    private String city;
    private Integer timeOnPageSeconds;
    private Boolean isNewVisitor;
    private String institutionName;
}