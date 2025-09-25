package com.genixo.education.search.entity.institution;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "property_types")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PropertyType extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_group_type_id", nullable = false)
    @ToString.Exclude
    private PropertyGroupType propertyGroupType;


    @Column(name = "sort_order")
    private Integer sortOrder = 0;

}
