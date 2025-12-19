package com.genixo.education.search.dto.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDto {

    private Long id;
    private String companyName;
    private String taxNumber;
    private String email;
    private String phone;
    private String address;
    private Boolean isActive;
    private String description;
    private BigDecimal averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

