package com.genixo.education.search.entity.supply;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "category_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAttribute extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String attributeName;

    // JSON olarak saklanabilir: ["Kırmızı", "Mavi", "Yeşil"]
    @Column(columnDefinition = "TEXT")
    private String possibleValues;

    @Column(nullable = false)
    private Boolean isRequired = false;

    private Integer displayOrder = 0;
}