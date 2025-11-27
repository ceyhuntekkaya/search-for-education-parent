package com.genixo.education.search.entity.institution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.entity.campaign.CampaignSchool;
import com.genixo.education.search.entity.pricing.SchoolPricing;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "schools")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class School extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    // Contact Information
    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "extension")
    private String extension;

    // Education Information
    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "current_student_count")
    private Integer currentStudentCount = 0;

    @Column(name = "class_size_average")
    private Integer classSizeAverage;

    // Academic Information
    @Column(name = "curriculum_type")
    private String curriculumType;

    @Column(name = "language_of_instruction")
    private String languageOfInstruction;

    @Column(name = "foreign_languages")
    private String foreignLanguages;

    // Fees
    @Column(name = "registration_fee")
    private Double registrationFee;

    @Column(name = "monthly_fee")
    private Double monthlyFee;

    @Column(name = "annual_fee")
    private Double annualFee;

    // SEO Fields
    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "meta_keywords")
    private String metaKeywords;

    // Statistics
    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "rating_average")
    private Double ratingAverage = 0.0;

    @Column(name = "rating_count")
    private Long ratingCount = 0L;

    // Social Media
    @Column(name = "like_count")
    private Long likeCount = 0L;

    @Column(name = "post_count")
    private Long postCount = 0L;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id", nullable = false)
    @ToString.Exclude
    private Campus campus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_type_id", nullable = false)
    @ToString.Exclude
    private InstitutionType institutionType;

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<InstitutionPropertyValue> propertyValues = new HashSet<>();

    // Pricing relationship
    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<SchoolPricing> pricings = new HashSet<>();

    // Campaign relationships
    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<CampaignSchool> campaignSchools = new HashSet<>();

    @Column(name = "facebook_url")
    private String facebookUrl;

    @Column(name = "twitter_url")
    private String twitterUrl;

    @Column(name = "instagram_url")
    private String instagramUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "youtube_url")
    private String youtubeUrl;


}
