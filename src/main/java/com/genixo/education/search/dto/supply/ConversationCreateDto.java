package com.genixo.education.search.dto.supply;

import com.genixo.education.search.enumaration.ProductMessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationCreateDto {

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private Long productId;
    private Long quotationId;
    private Long orderId;

    private ProductMessageType messageType;

    @NotBlank(message = "Subject is required")
    private String subject;
}

