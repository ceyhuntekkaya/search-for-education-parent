package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierRatingUpdateDto {

    @Min(value = 1, message = "Delivery rating must be between 1 and 5")
    @Max(value = 5, message = "Delivery rating must be between 1 and 5")
    private Integer deliveryRating;

    @Min(value = 1, message = "Quality rating must be between 1 and 5")
    @Max(value = 5, message = "Quality rating must be between 1 and 5")
    private Integer qualityRating;

    @Min(value = 1, message = "Communication rating must be between 1 and 5")
    @Max(value = 5, message = "Communication rating must be between 1 and 5")
    private Integer communicationRating;

    private String comment;
}

