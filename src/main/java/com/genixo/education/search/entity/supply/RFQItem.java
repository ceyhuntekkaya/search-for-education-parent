package com.genixo.education.search.entity.supply;


import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rfq_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RFQItem extends BaseEntity {


    @ManyToOne
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String itemName;

    @Column(columnDefinition = "TEXT")
    private String specifications;

    @Column(nullable = false)
    private Integer quantity;

    private String unit; // "adet", "kg", "metre"
}