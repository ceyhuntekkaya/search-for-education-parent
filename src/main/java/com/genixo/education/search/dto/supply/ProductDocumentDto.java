package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDocumentDto {

    private Long id;
    private Long productId;
    private String productName;
    private String documentName;
    private String documentUrl;
    private String documentType;
    private LocalDateTime createdAt;
}

