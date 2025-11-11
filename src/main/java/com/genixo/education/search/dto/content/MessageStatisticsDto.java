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
    private Integer totalMessages;
    private Integer newMessages;
    private Integer inProgressMessages;
    private Integer resolvedMessages;
    private Integer overdueMessages;
    private Integer averageResponseTimeHours;
    private Integer averageResolutionTimeHours;
    private Integer satisfactionRating;
    private Integer messagesRequiringFollowUp;

    // By type
    private Integer inquiryMessages;
    private Integer complaintMessages;
    private Integer appointmentRequests;
    private Integer callbackRequests;

    // By priority
    private Integer urgentMessages;
    private Integer highPriorityMessages;
    private Integer normalPriorityMessages;
    private Integer lowPriorityMessages;
}


