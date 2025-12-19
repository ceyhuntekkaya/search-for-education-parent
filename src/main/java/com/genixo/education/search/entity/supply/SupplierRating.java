package com.genixo.education.search.entity.supply;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.institution.Campus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRating extends BaseEntity {


    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Campus company;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Integer deliveryRating; // 1-5

    @Column(nullable = false)
    private Integer qualityRating; // 1-5

    @Column(nullable = false)
    private Integer communicationRating; // 1-5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}