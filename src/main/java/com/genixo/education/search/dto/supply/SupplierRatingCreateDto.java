package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierRatingCreateDto {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Delivery rating is required")
    @Min(value = 1, message = "Delivery rating must be between 1 and 5")
    @Max(value = 5, message = "Delivery rating must be between 1 and 5")
    private Integer deliveryRating;

    @NotNull(message = "Quality rating is required")
    @Min(value = 1, message = "Quality rating must be between 1 and 5")
    @Max(value = 5, message = "Quality rating must be between 1 and 5")
    private Integer qualityRating;

    @NotNull(message = "Communication rating is required")
    @Min(value = 1, message = "Communication rating must be between 1 and 5")
    @Max(value = 5, message = "Communication rating must be between 1 and 5")
    private Integer communicationRating;

    private String comment;
}

