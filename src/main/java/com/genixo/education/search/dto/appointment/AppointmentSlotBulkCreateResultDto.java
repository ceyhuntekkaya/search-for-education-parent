package com.genixo.education.search.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSlotBulkCreateResultDto {
    private Integer totalRequested;
    private Integer createdCount;
    private Integer skippedCount;
    private Integer errorCount;
    private List<String> errors;
    private List<String> skipped;
    private List<AppointmentSlotDto> createdSlots;
}

