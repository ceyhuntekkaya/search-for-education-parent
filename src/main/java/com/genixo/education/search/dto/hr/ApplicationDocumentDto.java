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
public class ApplicationDocumentDto {
    private Long id;
    private Long applicationId;
    private String documentName;
    private String documentUrl;
    private String documentType;
    private Long fileSize;
    private LocalDateTime createdAt;
}
