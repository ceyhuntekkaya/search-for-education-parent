package com.genixo.education.search.dto.hr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationNoteDto {
    private Long id;
    private Long applicationId;
    private Long createdByUserId;
    private String createdByUserName;
    private String noteText;
    private LocalDateTime createdAt;
}
