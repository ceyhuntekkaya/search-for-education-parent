package com.genixo.education.search.entity.institution;

import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "institution_types")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionType extends BaseEntity {

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

    // Relationships
    @OneToMany(mappedBy = "institutionType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<School> schools = new HashSet<>();

    @OneToMany(mappedBy = "institutionType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<InstitutionProperty> properties = new HashSet<>();
}
