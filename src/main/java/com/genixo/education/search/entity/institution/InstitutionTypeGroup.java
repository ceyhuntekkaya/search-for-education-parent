package com.genixo.education.search.entity.institution;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;


@Entity
@Table(name = "institution_type_groups")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionTypeGroup extends BaseEntity {

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "color_code")
    private String colorCode;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // Default properties for this institution type
    @Column(name = "default_properties", columnDefinition = "JSON")
    private String defaultProperties;

}
