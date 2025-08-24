package com.genixo.education.search.entity.subscription;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "coupons")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Coupon extends BaseEntity {

    private String code;
    private String name;
    private String description;
    private String discountType; // PERCENTAGE, FIXED_AMOUNT
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;

    // Usage limits
    private Integer usageLimit;
    private Integer usageCount;
    private Integer userUsageLimit;

    // Validity
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean isActive;

    // Applicable plans
    private List<Long> applicablePlanIds;
    private String applicablePlans; // JSON

    // Terms
    private String terms;
    private Boolean isPublic;

    private LocalDateTime createdAt;
}
