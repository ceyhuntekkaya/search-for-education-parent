package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFQInvitationBulkCreateDto {

    @NotEmpty(message = "Supplier IDs are required")
    private List<Long> supplierIds;
}

