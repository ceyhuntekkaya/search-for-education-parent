package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.RFQStatus;
import com.genixo.education.search.enumaration.RFQType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFQDto {

    private Long id;
    private Long companyId;
    private String companyName;
    private String title;
    private String description;
    private RFQType rfqType;
    private RFQStatus status;
    private LocalDate submissionDeadline;
    private LocalDate expectedDeliveryDate;
    private String paymentTerms;
    private String evaluationCriteria;
    private String technicalRequirements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer itemCount;
    private Integer quotationCount;
    private Integer invitationCount;
}

