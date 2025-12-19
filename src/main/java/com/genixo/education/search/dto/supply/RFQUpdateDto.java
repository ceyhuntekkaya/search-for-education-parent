package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.RFQType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFQUpdateDto {

    private String title;
    private String description;
    private RFQType rfqType;
    private LocalDate submissionDeadline;
    private LocalDate expectedDeliveryDate;
    private String paymentTerms;
    private String evaluationCriteria;
    private String technicalRequirements;
}

