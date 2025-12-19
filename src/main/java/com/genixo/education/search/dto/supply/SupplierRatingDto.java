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
public class SupplierRatingDto {

    private Long id;
    private Long supplierId;
    private String supplierCompanyName;
    private Long companyId;
    private String companyName;
    private Long orderId;
    private String orderNumber;
    private Integer deliveryRating;
    private Integer qualityRating;
    private Integer communicationRating;
    private String comment;
    private LocalDateTime createdAt;
}

