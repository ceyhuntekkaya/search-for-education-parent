package com.genixo.education.search.entity.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genixo.education.search.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "provinces")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Province extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "code", nullable = false)
    private String code; // 34 for İstanbul, 06 for Ankara

    @Column(name = "plate_code")
    private String plateCode; // Plaka kodu (Türkiye için)

    @Column(name = "region")
    private String region; // Marmara, İç Anadolu, etc.

    @Column(name = "area_code")
    private String areaCode; // Alan kodu (telefon için)

    @Column(name = "postal_code_prefix")
    private String postalCodePrefix; // Posta kodu prefix

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "population")
    private Long population;

    @Column(name = "area_km2")
    private Double areaKm2;

    @Column(name = "elevation_m")
    private Integer elevationM;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "is_metropolitan")
    private Boolean isMetropolitan = false;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // SEO fields
    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Economic data
    @Column(name = "gdp_per_capita")
    private Double gdpPerCapita;

    @Column(name = "unemployment_rate")
    private Double unemploymentRate;

    @Column(name = "education_index")
    private Double educationIndex;

    // Infrastructure data
    @Column(name = "has_airport")
    private Boolean hasAirport = false;

    @Column(name = "has_university")
    private Boolean hasUniversity = false;

    @Column(name = "has_metro")
    private Boolean hasMetro = false;

    @Column(name = "traffic_density")
    private String trafficDensity; // LOW, MEDIUM, HIGH

    // Statistics for schools
    @Column(name = "school_count")
    private Long schoolCount = 0L;

    @Column(name = "student_count")
    private Long studentCount = 0L;

    @Column(name = "teacher_count")
    private Long teacherCount = 0L;

    @Column(name = "literacy_rate")
    private Double literacyRate;

    // Relationships
    @OneToMany(mappedBy = "province", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<District> districts = new HashSet<>();
}