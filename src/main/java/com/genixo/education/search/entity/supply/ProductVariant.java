package com.genixo.education.search.entity.supply;


import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String variantName; // Örn: "Kırmızı - Large"

    private String sku;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceAdjustment = BigDecimal.ZERO; // Ana fiyata göre +/- fark

    private Integer stockQuantity = 0;

    @Column(nullable = false)
    private Boolean isActive = true;
}
