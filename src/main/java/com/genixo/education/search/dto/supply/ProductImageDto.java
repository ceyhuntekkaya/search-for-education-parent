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
public class ProductImageDto {

    private Long id;
    private Long productId;
    private String imageUrl;
    private Integer displayOrder;
    private LocalDateTime createdAt;
}

