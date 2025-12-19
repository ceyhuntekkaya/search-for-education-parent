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
public class WishlistDto {

    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productSku;
    private String productMainImageUrl;
    private String supplierCompanyName;
    private LocalDateTime createdAt;
}

