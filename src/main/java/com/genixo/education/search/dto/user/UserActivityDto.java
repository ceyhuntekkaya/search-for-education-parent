package com.genixo.education.search.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActivityDto {
    private Long userId;
    private String userFullName;
    private String activity;
    private String description;
    private LocalDateTime activityTime;
    private String ipAddress;
    private String userAgent;
    private String deviceType;
    private String location;
}