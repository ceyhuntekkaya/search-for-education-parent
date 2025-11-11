package com.genixo.education.search.entity.subscription;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.BillingPeriod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

// ========================= SUBSCRIPTION PLAN =========================
@Entity
@Table(name = "subscription_plans")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_period", nullable = false)
    private BillingPeriod billingPeriod;

    @Column(name = "trial_days")
    private Integer trialDays = 0;

    @Column(name = "max_schools")
    private Integer maxSchools; // null = unlimited

    @Column(name = "max_users")
    private Integer maxUsers; // null = unlimited

    @Column(name = "max_appointments_per_month")
    private Integer maxAppointmentsPerMonth; // null = unlimited

    @Column(name = "max_gallery_items")
    private Integer maxGalleryItems; // null = unlimited

    @Column(name = "max_posts_per_month")
    private Integer maxPostsPerMonth; // null = unlimited

    // Features
    @Column(name = "has_analytics")
    private Boolean hasAnalytics = true;

    @Column(name = "has_custom_domain")
    private Boolean hasCustomDomain = false;

    @Column(name = "has_api_access")
    private Boolean hasApiAccess = false;

    @Column(name = "has_priority_support")
    private Boolean hasPrioritySupport = false;

    @Column(name = "has_white_label")
    private Boolean hasWhiteLabel = false;

    @Column(name = "storage_gb")
    private Integer storageGb = 1;

    @Column(name = "is_popular")
    private Boolean isPopular = false;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_visible")
    private Boolean isVisible = true;

    // Pricing tiers (JSON format for complex pricing)
    @Column(name = "pricing_tiers")
    private String pricingTiers;

    // Features list (JSON format)
    @Column(name = "features")
    private String features;

    // Relationships
    @OneToMany(mappedBy = "subscriptionPlan", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Subscription> subscriptions = new HashSet<>();
}