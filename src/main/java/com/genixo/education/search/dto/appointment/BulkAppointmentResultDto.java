package com.genixo.education.search.dto.appointment;

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
public class BulkAppointmentResultDto {
    private Boolean success;
    private Integer totalRecords;
    private Integer successfulOperations;
    private Integer failedOperations;
    private List<String> errors;
    private List<String> warnings;
    private String operationId;
    private LocalDateTime operationDate;
    private List<Long> affectedAppointmentIds;
    private Integer notificationsSent;
}

