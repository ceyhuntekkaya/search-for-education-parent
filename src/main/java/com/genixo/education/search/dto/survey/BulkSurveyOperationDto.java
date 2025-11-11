package com.genixo.education.search.dto.survey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkSurveyOperationDto {
    private String operation; // SEND_INVITATIONS, SEND_REMINDERS, CLOSE_SURVEY, EXPORT_RESPONSES
    private List<Long> surveyIds;
    private List<String> recipientEmails;
    private String customMessage;
    private LocalDateTime scheduledSendTime;
    private String exportFormat; // CSV, EXCEL, PDF
    private Boolean includePersonalData;
}