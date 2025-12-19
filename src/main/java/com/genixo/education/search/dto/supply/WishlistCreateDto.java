package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistCreateDto {

    @NotNull(message = "Product ID is required")
    private Long productId;
}

