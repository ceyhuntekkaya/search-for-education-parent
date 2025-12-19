package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDocumentCreateDto {

    @NotBlank(message = "Document name is required")
    private String documentName;

    @NotBlank(message = "Document URL is required")
    private String documentUrl;

    private String documentType; // "certificate", "datasheet", "manual"
}

