package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.RFQType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFQCreateDto {

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Builder.Default
    private RFQType rfqType = RFQType.OPEN;

    @NotNull(message = "Submission deadline is required")
    private LocalDate submissionDeadline;

    private LocalDate expectedDeliveryDate;

    private String paymentTerms;

    private String evaluationCriteria;

    private String technicalRequirements;
}

