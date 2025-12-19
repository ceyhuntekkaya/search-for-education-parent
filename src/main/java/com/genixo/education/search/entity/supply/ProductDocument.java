package com.genixo.education.search.entity.supply;


import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String documentName;

    @Column(nullable = false)
    private String documentUrl;

    private String documentType; // "certificate", "datasheet", "manual"

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}