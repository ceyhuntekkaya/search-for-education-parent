package com.genixo.education.search.dto.supply;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageReorderDto {

    @NotNull(message = "Image IDs are required")
    private List<Long> imageIds; // Sıralı image ID'leri
}

