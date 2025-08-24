package com.genixo.education.search.entity.subscription;

import com.genixo.education.search.entity.BaseEntity;
import com.genixo.education.search.enumaration.SubscriptionStatus;
import com.genixo.education.search.entity.institution.Campus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subscriptions")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Subscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;

    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "currency", nullable = false)
    private String currency = "TRY";

    @Column(name = "discount_amount", precision = 19, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "discount_percentage")
    private Double discountPercentage = 0.0;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "auto_renew")
    private Boolean autoRenew = true;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "grace_period_end")
    private LocalDateTime gracePeriodEnd;

    // Usage tracking
    @Column(name = "current_schools_count")
    private Integer currentSchoolsCount = 0;

    @Column(name = "current_users_count")
    private Integer currentUsersCount = 0;

    @Column(name = "current_month_appointments")
    private Integer currentMonthAppointments = 0;

    @Column(name = "current_gallery_items")
    private Integer currentGalleryItems = 0;

    @Column(name = "current_month_posts")
    private Integer currentMonthPosts = 0;

    @Column(name = "storage_used_mb")
    private Long storageUsedMb = 0L;

    // Billing information
    @Column(name = "billing_name")
    private String billingName;

    @Column(name = "billing_email")
    private String billingEmail;

    @Column(name = "billing_phone")
    private String billingPhone;

    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;

    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "tax_office")
    private String taxOffice;

    // Relationships
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Payment> payments = new HashSet<>();

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Invoice> invoices = new HashSet<>();
}