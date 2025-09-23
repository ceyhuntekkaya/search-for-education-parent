package com.genixo.education.search.entity.institution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.PropertyDataType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "institution_properties")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionProperty extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    private PropertyDataType dataType;

    @Column(name = "is_required")
    private Boolean isRequired = false;

    @Column(name = "is_searchable")
    private Boolean isSearchable = false;

    @Column(name = "is_filterable")
    private Boolean isFilterable = false;

    @Column(name = "show_in_card")
    private Boolean showInCard = false;

    @Column(name = "show_in_profile")
    private Boolean showInProfile = true;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // For SELECT type properties
    @Column(name = "options", columnDefinition = "JSON")
    private String options;

    // Default value
    @Column(name = "default_value")
    private String defaultValue;

    // Validation rules
    @Column(name = "min_value")
    private Double minValue;

    @Column(name = "max_value")
    private Double maxValue;

    @Column(name = "min_length")
    private Integer minLength;

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(name = "regex_pattern")
    private String regexPattern;

    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_type_id", nullable = false)
    private InstitutionType institutionType;


/*
    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<InstitutionPropertyValue> propertyValues = new HashSet<>();

 */

// ceyhun
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_type_id", nullable = false)
    private PropertyType propertyType;
}
