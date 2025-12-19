package com.genixo.education.search.entity.supply;


import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.enumaration.ProductStatus;
import com.genixo.education.search.enumaration.StockTrackingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String sku; // Stock Keeping Unit

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String technicalSpecs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockTrackingType stockTrackingType = StockTrackingType.LIMITED;

    // Stock için
    private Integer stockQuantity = 0;
    private Integer minStockLevel = 0;

    // Pricing
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.TRY;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("20.00"); // KDV %20

    // Minimum Order Quantity
    private Integer minOrderQuantity = 1;

    // Delivery time in days
    private Integer deliveryDays = 7;

    // Ana görsel URL
    private String mainImageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
