package com.genixo.education.search.entity.institution;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "property_group_types")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PropertyGroupType extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_type_id", nullable = false)
    @ToString.Exclude
    private InstitutionType institutionType;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;


}
