package com.genixo.education.search.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageStatisticsDto {
    private Long totalMessages;
    private Long newMessages;
    private Long inProgressMessages;
    private Long resolvedMessages;
    private Long overdueMessages;
    private Double averageResponseTimeHours;
    private Double averageResolutionTimeHours;
    private Double satisfactionRating;
    private Long messagesRequiringFollowUp;

    // By type
    private Long inquiryMessages;
    private Long complaintMessages;
    private Long appointmentRequests;
    private Long callbackRequests;

    // By priority
    private Long urgentMessages;
    private Long highPriorityMessages;
    private Long normalPriorityMessages;
    private Long lowPriorityMessages;
}
