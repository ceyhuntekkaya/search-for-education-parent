package com.genixo.education.search.service.converter;


import com.genixo.education.search.dto.subscription.*;
import com.genixo.education.search.entity.subscription.*;
import com.genixo.education.search.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SubscriptionConverterService {

    private final InstitutionConverterService institutionConverterService;


    public SubscriptionPlanDto mapToDto(SubscriptionPlan entity) {
        if (entity == null) {
            return null;
        }

        return SubscriptionPlanDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .billingPeriod(entity.getBillingPeriod())
                .trialDays(entity.getTrialDays())
                .maxSchools(entity.getMaxSchools())
                .maxUsers(entity.getMaxUsers())
                .maxAppointmentsPerMonth(entity.getMaxAppointmentsPerMonth())
                .maxGalleryItems(entity.getMaxGalleryItems())
                .maxPostsPerMonth(entity.getMaxPostsPerMonth())
                .hasAnalytics(ConversionUtils.defaultIfNull(entity.getHasAnalytics(), false))
                .hasCustomDomain(ConversionUtils.defaultIfNull(entity.getHasCustomDomain(), false))
                .hasApiAccess(ConversionUtils.defaultIfNull(entity.getHasApiAccess(), false))
                .hasPrioritySupport(ConversionUtils.defaultIfNull(entity.getHasPrioritySupport(), false))
                .hasWhiteLabel(ConversionUtils.defaultIfNull(entity.getHasWhiteLabel(), false))
                .storageGb(ConversionUtils.defaultIfNull(entity.getStorageGb(), 1))
                .isPopular(ConversionUtils.defaultIfNull(entity.getIsPopular(), false))
                .sortOrder(ConversionUtils.defaultIfNull(entity.getSortOrder(), 0))
                .isVisible(ConversionUtils.defaultIfNull(entity.getIsVisible(), true))
                .pricingTiers(entity.getPricingTiers())
                .features(entity.getFeatures())
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public SubscriptionPlanSummaryDto mapToSummaryDto(SubscriptionPlan entity) {
        if (entity == null) {
            return null;
        }

        return SubscriptionPlanSummaryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .price(entity.getPrice())
                .billingPeriod(entity.getBillingPeriod())
                .isPopular(ConversionUtils.defaultIfNull(entity.getIsPopular(), false))
                .hasAnalytics(ConversionUtils.defaultIfNull(entity.getHasAnalytics(), false))
                .hasPrioritySupport(ConversionUtils.defaultIfNull(entity.getHasPrioritySupport(), false))
                .storageGb(ConversionUtils.defaultIfNull(entity.getStorageGb(), 1))
                .subscriberCount(ConversionUtils.safeSize(entity.getSubscriptions() != null ?
                        entity.getSubscriptions().stream()
                                .filter(s -> s.getIsActive() != null && s.getIsActive())
                                .collect(Collectors.toList()) : null))
                .build();
    }

    public SubscriptionPlan mapToEntity(SubscriptionPlanCreateDto dto) {
        if (dto == null) {
            return null;
        }

        SubscriptionPlan entity = new SubscriptionPlan();
        entity.setName(dto.getName());
        entity.setDisplayName(dto.getDisplayName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setBillingPeriod(dto.getBillingPeriod());
        entity.setTrialDays(ConversionUtils.defaultIfNull(dto.getTrialDays(), 0));
        entity.setMaxSchools(dto.getMaxSchools());
        entity.setMaxUsers(dto.getMaxUsers());
        entity.setMaxAppointmentsPerMonth(dto.getMaxAppointmentsPerMonth());
        entity.setMaxGalleryItems(dto.getMaxGalleryItems());
        entity.setMaxPostsPerMonth(dto.getMaxPostsPerMonth());
        entity.setHasAnalytics(ConversionUtils.defaultIfNull(dto.getHasAnalytics(), true));
        entity.setHasCustomDomain(ConversionUtils.defaultIfNull(dto.getHasCustomDomain(), false));
        entity.setHasApiAccess(ConversionUtils.defaultIfNull(dto.getHasApiAccess(), false));
        entity.setHasPrioritySupport(ConversionUtils.defaultIfNull(dto.getHasPrioritySupport(), false));
        entity.setHasWhiteLabel(ConversionUtils.defaultIfNull(dto.getHasWhiteLabel(), false));
        entity.setStorageGb(ConversionUtils.defaultIfNull(dto.getStorageGb(), 1));
        entity.setIsPopular(ConversionUtils.defaultIfNull(dto.getIsPopular(), false));
        entity.setSortOrder(ConversionUtils.defaultIfNull(dto.getSortOrder(), 0));
        entity.setIsVisible(ConversionUtils.defaultIfNull(dto.getIsVisible(), true));
        entity.setPricingTiers(dto.getPricingTiers());
        entity.setFeatures(dto.getFeatures());

        return entity;
    }

    // ================== SUBSCRIPTION ==================

    public SubscriptionDto mapToDto(Subscription entity) {
        if (entity == null) {
            return null;
        }

        // Calculate days remaining
        Integer daysRemaining = null;
        if (entity.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(
                    java.time.LocalDate.now(),
                    entity.getEndDate().toLocalDate()
            );
            daysRemaining = (int) Math.max(0, days);
        }

        return SubscriptionDto.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .trialEndDate(entity.getTrialEndDate())
                .nextBillingDate(entity.getNextBillingDate())
                .price(entity.getPrice())
                .currency(entity.getCurrency())
                .discountAmount(entity.getDiscountAmount())
                .discountPercentage(entity.getDiscountPercentage())
                .couponCode(entity.getCouponCode())
                .autoRenew(ConversionUtils.defaultIfNull(entity.getAutoRenew(), true))
                .canceledAt(entity.getCanceledAt())
                .cancellationReason(entity.getCancellationReason())
                .gracePeriodEnd(entity.getGracePeriodEnd())
                .currentSchoolsCount(ConversionUtils.defaultIfNull(entity.getCurrentSchoolsCount(), 0))
                .currentUsersCount(ConversionUtils.defaultIfNull(entity.getCurrentUsersCount(), 0))
                .currentMonthAppointments(ConversionUtils.defaultIfNull(entity.getCurrentMonthAppointments(), 0))
                .currentGalleryItems(ConversionUtils.defaultIfNull(entity.getCurrentGalleryItems(), 0))
                .currentMonthPosts(ConversionUtils.defaultIfNull(entity.getCurrentMonthPosts(), 0))
                .storageUsedMb(ConversionUtils.defaultIfNull(entity.getStorageUsedMb(), 0L))
                .billingName(entity.getBillingName())
                .billingEmail(entity.getBillingEmail())
                .billingPhone(entity.getBillingPhone())
                .billingAddress(entity.getBillingAddress())
                .taxNumber(entity.getTaxNumber())
                .taxOffice(entity.getTaxOffice())
                .campus(institutionConverterService.mapToSummaryDto(entity.getCampus()))
                .subscriptionPlan(mapToSummaryDto(entity.getSubscriptionPlan()))
                .recentPayments(mapPaymentsToSummaryDto(getRecentPayments(entity.getPayments())))
                .recentInvoices(mapInvoicesToSummaryDto(getRecentInvoices(entity.getInvoices())))
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public SubscriptionSummaryDto mapToSummaryDto(Subscription entity) {
        if (entity == null) {
            return null;
        }

        // Calculate days remaining
        Integer daysRemaining = null;
        if (entity.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(
                    java.time.LocalDate.now(),
                    entity.getEndDate().toLocalDate()
            );
            daysRemaining = (int) Math.max(0, days);
        }

        // Calculate usage percentage (based on schools)
        Double usagePercentage = 0.0;
        if (entity.getSubscriptionPlan() != null &&
                entity.getSubscriptionPlan().getMaxSchools() != null &&
                entity.getSubscriptionPlan().getMaxSchools() > 0) {
            int currentSchools = ConversionUtils.defaultIfNull(entity.getCurrentSchoolsCount(), 0);
            usagePercentage = ConversionUtils.calculatePercentage(
                    Long.valueOf(currentSchools),
                    Long.valueOf(entity.getSubscriptionPlan().getMaxSchools())
            );
        }

        return SubscriptionSummaryDto.builder()
                .id(entity.getId())
                .campusName(entity.getCampus() != null ? entity.getCampus().getName() : null)
                .planName(entity.getSubscriptionPlan() != null ? entity.getSubscriptionPlan().getDisplayName() : null)
                .status(entity.getStatus())
                .price(entity.getPrice())
                .currency(entity.getCurrency())
                .nextBillingDate(entity.getNextBillingDate())
                .endDate(entity.getEndDate())
                .autoRenew(ConversionUtils.defaultIfNull(entity.getAutoRenew(), true))
                .daysRemaining(daysRemaining)
                .usagePercentage(usagePercentage)
                .build();
    }

    public Subscription mapToEntity(SubscriptionCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Subscription entity = new Subscription();
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setCouponCode(dto.getCouponCode());
        entity.setAutoRenew(ConversionUtils.defaultIfNull(dto.getAutoRenew(), true));
        entity.setBillingName(dto.getBillingName());
        entity.setBillingEmail(dto.getBillingEmail());
        entity.setBillingPhone(dto.getBillingPhone());
        entity.setBillingAddress(dto.getBillingAddress());
        entity.setTaxNumber(dto.getTaxNumber());
        entity.setTaxOffice(dto.getTaxOffice());

        return entity;
    }

    public void updateEntity(SubscriptionUpdateDto dto, Subscription entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getSubscriptionPlanId() != null) {
            // Plan will be set by service layer
        }

        if (dto.getAutoRenew() != null) {
            entity.setAutoRenew(dto.getAutoRenew());
        }

        if (StringUtils.hasText(dto.getCancellationReason())) {
            entity.setCancellationReason(dto.getCancellationReason());
        }

        if (StringUtils.hasText(dto.getBillingName())) {
            entity.setBillingName(dto.getBillingName());
        }

        if (StringUtils.hasText(dto.getBillingEmail())) {
            entity.setBillingEmail(dto.getBillingEmail());
        }

        if (StringUtils.hasText(dto.getBillingPhone())) {
            entity.setBillingPhone(dto.getBillingPhone());
        }

        if (StringUtils.hasText(dto.getBillingAddress())) {
            entity.setBillingAddress(dto.getBillingAddress());
        }

        if (StringUtils.hasText(dto.getTaxNumber())) {
            entity.setTaxNumber(dto.getTaxNumber());
        }

        if (StringUtils.hasText(dto.getTaxOffice())) {
            entity.setTaxOffice(dto.getTaxOffice());
        }
    }

    // ================== PAYMENT ==================

    public PaymentDto mapToDto(Payment entity) {
        if (entity == null) {
            return null;
        }

        return PaymentDto.builder()
                .id(entity.getId())
                .paymentReference(entity.getPaymentReference())
                .externalPaymentId(entity.getExternalPaymentId())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .paymentMethod(entity.getPaymentMethod())
                .paymentStatus(entity.getPaymentStatus())
                .paymentDate(entity.getPaymentDate())
                .dueDate(entity.getDueDate())
                .description(entity.getDescription())
                .failureReason(entity.getFailureReason())
                .refundAmount(entity.getRefundAmount())
                .refundDate(entity.getRefundDate())
                .refundReason(entity.getRefundReason())
                .gatewayName(entity.getGatewayName())
                .gatewayTransactionId(entity.getGatewayTransactionId())
                .gatewayResponse(entity.getGatewayResponse())
                .cardLastFour(entity.getCardLastFour())
                .cardBrand(entity.getCardBrand())
                .cardHolderName(entity.getCardHolderName())
                .periodStart(entity.getPeriodStart())
                .periodEnd(entity.getPeriodEnd())
                .subscription(mapToSummaryDto(entity.getSubscription()))
                .invoice(mapInvoiceToSummaryDto(entity.getInvoice()))
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public PaymentSummaryDto mapToSummaryDto(Payment entity) {
        if (entity == null) {
            return null;
        }

        return PaymentSummaryDto.builder()
                .id(entity.getId())
                .paymentReference(entity.getPaymentReference())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .paymentMethod(entity.getPaymentMethod())
                .paymentStatus(entity.getPaymentStatus())
                .paymentDate(entity.getPaymentDate())
                .dueDate(entity.getDueDate())
                .cardLastFour(entity.getCardLastFour())
                .cardBrand(entity.getCardBrand())
                .isRefunded(entity.getRefundAmount() != null &&
                        entity.getRefundAmount().compareTo(java.math.BigDecimal.ZERO) > 0)
                .build();
    }

    public Payment mapToEntity(PaymentCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Payment entity = new Payment();
        entity.setAmount(dto.getAmount());
        entity.setCurrency(ConversionUtils.defaultIfEmpty(dto.getCurrency(), "TRY"));
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setDescription(dto.getDescription());
        entity.setDueDate(dto.getDueDate());
        entity.setGatewayName(dto.getGatewayName());
        entity.setExternalPaymentId(dto.getExternalPaymentId());
        entity.setCardLastFour(dto.getCardLastFour());
        entity.setCardBrand(dto.getCardBrand());
        entity.setCardHolderName(dto.getCardHolderName());
        entity.setPeriodStart(dto.getPeriodStart());
        entity.setPeriodEnd(dto.getPeriodEnd());

        return entity;
    }

    // ================== INVOICE ==================

    public InvoiceDto mapToDto(Invoice entity) {
        if (entity == null) {
            return null;
        }

        return InvoiceDto.builder()
                .id(entity.getId())
                .invoiceNumber(entity.getInvoiceNumber())
                .invoiceDate(entity.getInvoiceDate())
                .dueDate(entity.getDueDate())
                .invoiceStatus(entity.getInvoiceStatus())
                .subtotal(entity.getSubtotal())
                .taxAmount(entity.getTaxAmount())
                .taxRate(entity.getTaxRate())
                .discountAmount(entity.getDiscountAmount())
                .totalAmount(entity.getTotalAmount())
                .currency(entity.getCurrency())
                .description(entity.getDescription())
                .notes(entity.getNotes())
                .billingName(entity.getBillingName())
                .billingEmail(entity.getBillingEmail())
                .billingPhone(entity.getBillingPhone())
                .billingAddress(entity.getBillingAddress())
                .taxNumber(entity.getTaxNumber())
                .taxOffice(entity.getTaxOffice())
                .pdfFileUrl(entity.getPdfFileUrl())
                .pdfGeneratedAt(entity.getPdfGeneratedAt())
                .periodStart(entity.getPeriodStart())
                .periodEnd(entity.getPeriodEnd())
                .lineItems(entity.getLineItems())
                .eInvoiceUuid(entity.getEInvoiceUuid())
                .eInvoiceStatus(entity.getEInvoiceStatus())
                .eInvoiceSentAt(entity.getEInvoiceSentAt())
                .eInvoiceResponse(entity.getEInvoiceResponse())
                .subscription(mapToSummaryDto(entity.getSubscription()))
                .payment(mapToSummaryDto(entity.getPayment()))
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public InvoiceSummaryDto mapInvoiceToSummaryDto(Invoice entity) {
        if (entity == null) {
            return null;
        }

        // Calculate days overdue
        Integer daysOverdue = null;
        if (entity.getDueDate() != null &&
                java.time.LocalDateTime.now().isAfter(entity.getDueDate())) {
            daysOverdue = (int) ChronoUnit.DAYS.between(
                    entity.getDueDate().toLocalDate(),
                    java.time.LocalDate.now()
            );
        }

        return InvoiceSummaryDto.builder()
                .id(entity.getId())
                .invoiceNumber(entity.getInvoiceNumber())
                .invoiceDate(entity.getInvoiceDate())
                .dueDate(entity.getDueDate())
                .invoiceStatus(entity.getInvoiceStatus())
                .totalAmount(entity.getTotalAmount())
                .currency(entity.getCurrency())
                .pdfFileUrl(entity.getPdfFileUrl())
                .isPaid(entity.getPayment() != null)
                .daysOverdue(ConversionUtils.defaultIfNull(daysOverdue, 0))
                .build();
    }

    public Invoice mapToEntity(InvoiceCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Invoice entity = new Invoice();
        entity.setDueDate(dto.getDueDate());
        entity.setSubtotal(dto.getSubtotal());
        entity.setTaxAmount(ConversionUtils.defaultIfNull(dto.getTaxAmount(), java.math.BigDecimal.ZERO));
        entity.setTaxRate(ConversionUtils.defaultIfNull(dto.getTaxRate(), 0.0));
        entity.setDiscountAmount(ConversionUtils.defaultIfNull(dto.getDiscountAmount(), java.math.BigDecimal.ZERO));
        entity.setCurrency(ConversionUtils.defaultIfEmpty(dto.getCurrency(), "TRY"));
        entity.setDescription(dto.getDescription());
        entity.setNotes(dto.getNotes());
        entity.setPeriodStart(dto.getPeriodStart());
        entity.setPeriodEnd(dto.getPeriodEnd());
        entity.setLineItems(dto.getLineItems());
        entity.setGenerateEInvoice(ConversionUtils.defaultIfNull(dto.getGenerateEInvoice(), false));

        // Calculate total amount
        java.math.BigDecimal total = dto.getSubtotal()
                .add(entity.getTaxAmount())
                .subtract(entity.getDiscountAmount());
        entity.setTotalAmount(total);

        return entity;
    }

    // ================== SUBSCRIPTION USAGE ==================

    public SubscriptionUsageDto mapToUsageDto(Subscription entity) {
        if (entity == null || entity.getSubscriptionPlan() == null) {
            return null;
        }

        SubscriptionPlan plan = entity.getSubscriptionPlan();
        List<String> usageWarnings = new ArrayList<>();
        List<String> limitExceeded = new ArrayList<>();
        List<String> upgradeRecommendations = new ArrayList<>();

        // Schools usage
        int schoolsUsed = ConversionUtils.defaultIfNull(entity.getCurrentSchoolsCount(), 0);
        int schoolsLimit = ConversionUtils.defaultIfNull(plan.getMaxSchools(), Integer.MAX_VALUE);
        double schoolsUsagePercentage = calculateUsagePercentage(schoolsUsed, schoolsLimit);

        // Users usage
        int usersUsed = ConversionUtils.defaultIfNull(entity.getCurrentUsersCount(), 0);
        int usersLimit = ConversionUtils.defaultIfNull(plan.getMaxUsers(), Integer.MAX_VALUE);
        double usersUsagePercentage = calculateUsagePercentage(usersUsed, usersLimit);

        // Appointments usage
        int appointmentsUsed = ConversionUtils.defaultIfNull(entity.getCurrentMonthAppointments(), 0);
        int appointmentsLimit = ConversionUtils.defaultIfNull(plan.getMaxAppointmentsPerMonth(), Integer.MAX_VALUE);
        double appointmentsUsagePercentage = calculateUsagePercentage(appointmentsUsed, appointmentsLimit);

        // Gallery items usage
        int galleryItemsUsed = ConversionUtils.defaultIfNull(entity.getCurrentGalleryItems(), 0);
        int galleryItemsLimit = ConversionUtils.defaultIfNull(plan.getMaxGalleryItems(), Integer.MAX_VALUE);
        double galleryItemsUsagePercentage = calculateUsagePercentage(galleryItemsUsed, galleryItemsLimit);

        // Posts usage
        int postsUsed = ConversionUtils.defaultIfNull(entity.getCurrentMonthPosts(), 0);
        int postsLimit = ConversionUtils.defaultIfNull(plan.getMaxPostsPerMonth(), Integer.MAX_VALUE);
        double postsUsagePercentage = calculateUsagePercentage(postsUsed, postsLimit);

        // Storage usage
        long storageUsedMb = ConversionUtils.defaultIfNull(entity.getStorageUsedMb(), 0L);
        long storageLimitMb = ConversionUtils.defaultIfNull(plan.getStorageGb(), 1) * 1024L;
        double storageUsagePercentage = calculateUsagePercentage(storageUsedMb, storageLimitMb);

        // Check for warnings and exceeded limits
        checkUsageWarnings(schoolsUsagePercentage, "Okul sayısı", usageWarnings, limitExceeded);
        checkUsageWarnings(usersUsagePercentage, "Kullanıcı sayısı", usageWarnings, limitExceeded);
        checkUsageWarnings(appointmentsUsagePercentage, "Aylık randevu sayısı", usageWarnings, limitExceeded);
        checkUsageWarnings(galleryItemsUsagePercentage, "Galeri öğesi", usageWarnings, limitExceeded);
        checkUsageWarnings(postsUsagePercentage, "Aylık gönderi sayısı", usageWarnings, limitExceeded);
        checkUsageWarnings(storageUsagePercentage, "Depolama alanı", usageWarnings, limitExceeded);

        // Generate upgrade recommendations
        if (!limitExceeded.isEmpty() || usageWarnings.size() > 2) {
            upgradeRecommendations.add("Daha yüksek limitli bir plana yükseltme öneriyoruz.");
        }

        return SubscriptionUsageDto.builder()
                .subscriptionId(entity.getId())
                .campusName(entity.getCampus() != null ? entity.getCampus().getName() : null)
                .planName(plan.getDisplayName())
                .schoolsUsed(schoolsUsed)
                .schoolsLimit(schoolsLimit)
                .schoolsUsagePercentage(schoolsUsagePercentage)
                .usersUsed(usersUsed)
                .usersLimit(usersLimit)
                .usersUsagePercentage(usersUsagePercentage)
                .appointmentsThisMonth(appointmentsUsed)
                .appointmentsLimit(appointmentsLimit)
                .appointmentsUsagePercentage(appointmentsUsagePercentage)
                .galleryItemsUsed(galleryItemsUsed)
                .galleryItemsLimit(galleryItemsLimit)
                .galleryItemsUsagePercentage(galleryItemsUsagePercentage)
                .postsThisMonth(postsUsed)
                .postsLimit(postsLimit)
                .postsUsagePercentage(postsUsagePercentage)
                .storageUsedMb(storageUsedMb)
                .storageLimitMb(storageLimitMb)
                .storageUsagePercentage(storageUsagePercentage)
                .usageWarnings(usageWarnings)
                .limitExceeded(limitExceeded)
                .upgradeRecommendations(upgradeRecommendations)
                .build();
    }

    // ================== SUBSCRIPTION STATISTICS ==================

    public SubscriptionStatisticsDto mapToStatisticsDto(List<Subscription> subscriptions,
                                                        List<Payment> payments) {
        if (ConversionUtils.isEmpty(subscriptions)) {
            return SubscriptionStatisticsDto.builder().build();
        }

        long totalSubscriptions = subscriptions.size();
        long activeSubscriptions = subscriptions.stream()
                .filter(s -> s.getStatus() == com.genixo.education.search.enumaration.SubscriptionStatus.ACTIVE)
                .count();

        long trialSubscriptions = subscriptions.stream()
                .filter(s -> s.getStatus() == com.genixo.education.search.enumaration.SubscriptionStatus.TRIAL)
                .count();

        long expiredSubscriptions = subscriptions.stream()
                .filter(s -> s.getStatus() == com.genixo.education.search.enumaration.SubscriptionStatus.EXPIRED)
                .count();

        long canceledSubscriptions = subscriptions.stream()
                .filter(s -> s.getStatus() == com.genixo.education.search.enumaration.SubscriptionStatus.CANCELED)
                .count();

        // Calculate revenue metrics
        java.math.BigDecimal totalRevenue = payments.stream()
                .filter(p -> p.getPaymentStatus() == com.genixo.education.search.enumaration.PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        java.math.BigDecimal monthlyRevenue = subscriptions.stream()
                .filter(s -> s.getStatus() == com.genixo.education.search.enumaration.SubscriptionStatus.ACTIVE)
                .map(s -> s.getPrice() != null ? s.getPrice() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        java.math.BigDecimal annualRevenue = monthlyRevenue.multiply(java.math.BigDecimal.valueOf(12));

        java.math.BigDecimal averageRevenue = activeSubscriptions > 0 ?
                monthlyRevenue.divide(java.math.BigDecimal.valueOf(activeSubscriptions), 2, java.math.RoundingMode.HALF_UP) :
                java.math.BigDecimal.ZERO;

        return SubscriptionStatisticsDto.builder()
                .totalSubscriptions(totalSubscriptions)
                .activeSubscriptions(activeSubscriptions)
                .trialSubscriptions(trialSubscriptions)
                .expiredSubscriptions(expiredSubscriptions)
                .canceledSubscriptions(canceledSubscriptions)
                .totalRevenue(totalRevenue)
                .monthlyRecurringRevenue(monthlyRevenue)
                .annualRecurringRevenue(annualRevenue)
                .averageRevenuePerUser(averageRevenue)
                .build();
    }

    // ================== COUPON ==================

    public CouponDto mapToDto(Coupon entity) {
        if (entity == null) {
            return null;
        }

        return CouponDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .maxDiscountAmount(entity.getMaxDiscountAmount())
                .minOrderAmount(entity.getMinOrderAmount())
                .usageLimit(entity.getUsageLimit())
                .usageCount(ConversionUtils.defaultIfNull(entity.getUsageCount(), 0))
                .userUsageLimit(entity.getUserUsageLimit())
                .validFrom(entity.getValidFrom())
                .validUntil(entity.getValidUntil())
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .applicablePlanIds(entity.getApplicablePlanIds())
                .applicablePlans(entity.getApplicablePlans())
                .terms(entity.getTerms())
                .isPublic(ConversionUtils.defaultIfNull(entity.getIsPublic(), false))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public CouponValidationDto mapToValidationDto(String couponCode, boolean isValid,
                                                  String validationMessage,
                                                  java.math.BigDecimal discountAmount,
                                                  java.math.BigDecimal finalAmount) {
        return CouponValidationDto.builder()
                .couponCode(couponCode)
                .isValid(isValid)
                .validationMessage(validationMessage)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .build();
    }

    // ================== BILLING SUMMARY ==================

    public BillingSummaryDto mapToBillingSummaryDto(Subscription entity) {
        if (entity == null) {
            return null;
        }

        List<PaymentSummaryDto> recentPayments = mapPaymentsToSummaryDto(
                getRecentPayments(entity.getPayments())
        );

        // Calculate totals
        java.math.BigDecimal totalPaid = entity.getPayments() != null ?
                entity.getPayments().stream()
                        .filter(p -> p.getPaymentStatus() == com.genixo.education.search.enumaration.PaymentStatus.COMPLETED)
                        .map(Payment::getAmount)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add) :
                java.math.BigDecimal.ZERO;

        java.math.BigDecimal totalOutstanding = entity.getInvoices() != null ?
                entity.getInvoices().stream()
                        .filter(i -> i.getInvoiceStatus() != com.genixo.education.search.enumaration.InvoiceStatus.PAID)
                        .map(Invoice::getTotalAmount)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add) :
                java.math.BigDecimal.ZERO;

        // Check for overdue payments
        List<String> billingAlerts = new ArrayList<>();
        boolean hasOverduePayments = false;
        int overdueCount = 0;
        java.math.BigDecimal overdueAmount = java.math.BigDecimal.ZERO;

        if (entity.getInvoices() != null) {
            List<Invoice> overdueInvoices = entity.getInvoices().stream()
                    .filter(i -> i.getDueDate() != null &&
                            i.getDueDate().isBefore(java.time.LocalDateTime.now()) &&
                            i.getInvoiceStatus() != com.genixo.education.search.enumaration.InvoiceStatus.PAID)
                    .collect(Collectors.toList());

            if (!overdueInvoices.isEmpty()) {
                hasOverduePayments = true;
                overdueCount = overdueInvoices.size();
                overdueAmount = overdueInvoices.stream()
                        .map(Invoice::getTotalAmount)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                billingAlerts.add(overdueCount + " adet vadesi geçmiş fatura bulunmaktadır.");
            }
        }

        // Get payment method info from recent payments
        Payment recentPayment = entity.getPayments() != null && !entity.getPayments().isEmpty() ?
                entity.getPayments().stream()
                        .filter(p -> p.getPaymentStatus() == com.genixo.education.search.enumaration.PaymentStatus.COMPLETED)
                        .max(Comparator.comparing(Payment::getPaymentDate))
                        .orElse(null) : null;

        return BillingSummaryDto.builder()
                .subscriptionId(entity.getId())
                .campusName(entity.getCampus() != null ? entity.getCampus().getName() : null)
                .currentPeriodStart(getCurrentPeriodStart(entity))
                .currentPeriodEnd(getCurrentPeriodEnd(entity))
                .currentPeriodAmount(entity.getPrice())
                .currentPeriodStatus(getCurrentPeriodStatus(entity))
                .nextBillingDate(entity.getNextBillingDate())
                .nextBillingAmount(entity.getPrice())
                .nextBillingCurrency(entity.getCurrency())
                .recentPayments(recentPayments)
                .totalPaid(totalPaid)
                .totalOutstanding(totalOutstanding)
                .preferredPaymentMethod(recentPayment != null ? recentPayment.getPaymentMethod() : null)
                .savedCardLastFour(recentPayment != null ? recentPayment.getCardLastFour() : null)
                .savedCardBrand(recentPayment != null ? recentPayment.getCardBrand() : null)
                .savedCardExpiry(null) // This would need to be stored separately
                .billingAlerts(billingAlerts)
                .hasOverduePayments(hasOverduePayments)
                .overdueCount(overdueCount)
                .overdueAmount(overdueAmount)
                .build();
    }

    // ================== SUBSCRIPTION HEALTH ==================

    public SubscriptionHealthDto mapToHealthDto(Subscription entity) {
        if (entity == null) {
            return null;
        }

        List<String> riskFactors = new ArrayList<>();
        List<String> healthRecommendations = new ArrayList<>();
        List<String> actionItems = new ArrayList<>();

        // Calculate health scores
        String paymentHealth = calculatePaymentHealth(entity);
        String usageHealth = calculateUsageHealth(entity);
        String supportHealth = "NEUTRAL"; // Would need support ticket data
        String engagementHealth = calculateEngagementHealth(entity);

        // Calculate overall health score
        int healthScoreValue = calculateOverallHealthScore(paymentHealth, usageHealth, engagementHealth);
        String healthScore = getHealthScoreCategory(healthScoreValue);

        // Calculate churn risk
        String churnRisk = calculateChurnRisk(entity, paymentHealth, usageHealth, engagementHealth);
        Double churnProbability = calculateChurnProbability(churnRisk);

        // Generate recommendations based on health indicators
        generateHealthRecommendations(paymentHealth, usageHealth, engagementHealth,
                healthRecommendations, actionItems, riskFactors);

        // Determine trend
        String healthTrend = "STABLE"; // Would need historical data to calculate

        return SubscriptionHealthDto.builder()
                .subscriptionId(entity.getId())
                .campusName(entity.getCampus() != null ? entity.getCampus().getName() : null)
                .healthScore(healthScore)
                .healthScoreValue(healthScoreValue)
                .paymentHealth(paymentHealth)
                .usageHealth(usageHealth)
                .supportHealth(supportHealth)
                .engagementHealth(engagementHealth)
                .riskFactors(riskFactors)
                .churnRisk(churnRisk)
                .churnProbability(churnProbability)
                .healthRecommendations(healthRecommendations)
                .actionItems(actionItems)
                .healthTrend(healthTrend)
                .lastHealthCheck(java.time.LocalDateTime.now())
                .build();
    }

    // ================== COLLECTION MAPPERS ==================

    public List<SubscriptionPlanDto> mapPlansToDto(List<SubscriptionPlan> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<SubscriptionPlanSummaryDto> mapPlansToSummaryDto(List<SubscriptionPlan> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<SubscriptionDto> mapSubscriptionsToDto(List<Subscription> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<SubscriptionSummaryDto> mapSubscriptionsToSummaryDto(List<Subscription> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<PaymentDto> mapPaymentsToDto(List<Payment> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<PaymentSummaryDto> mapPaymentsToSummaryDto(List<Payment> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<InvoiceDto> mapInvoicesToDto(List<Invoice> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<InvoiceSummaryDto> mapInvoicesToSummaryDto(List<Invoice> entities) {
        return entities != null ? entities.stream()
                .map(this::mapInvoiceToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    // ================== PRIVATE HELPER METHODS ==================

    private List<Payment> getRecentPayments(Set<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            return new ArrayList<>();
        }

        return payments.stream()
                .sorted(Comparator.comparing(Payment::getPaymentDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<Invoice> getRecentInvoices(Set<Invoice> invoices) {
        if (invoices == null || invoices.isEmpty()) {
            return new ArrayList<>();
        }

        return invoices.stream()
                .sorted(Comparator.comparing(Invoice::getInvoiceDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .collect(Collectors.toList());
    }

    private double calculateUsagePercentage(long used, long limit) {
        if (limit == 0 || limit == Integer.MAX_VALUE) {
            return 0.0;
        }
        return Math.min(100.0, (used * 100.0) / limit);
    }

    private void checkUsageWarnings(double usagePercentage, String resourceName,
                                    List<String> warnings, List<String> exceeded) {
        if (usagePercentage >= 100.0) {
            exceeded.add(resourceName + " limiti aşıldı");
        } else if (usagePercentage >= 80.0) {
            warnings.add(resourceName + " kullanımı %80'i geçti");
        }
    }

    private java.time.LocalDateTime getCurrentPeriodStart(Subscription entity) {
        // This would typically be calculated based on billing cycle
        return entity.getStartDate();
    }

    private java.time.LocalDateTime getCurrentPeriodEnd(Subscription entity) {
        // This would typically be calculated based on billing cycle
        return entity.getNextBillingDate();
    }

    private com.genixo.education.search.enumaration.PaymentStatus getCurrentPeriodStatus(Subscription entity) {
        // This would be determined by checking if current period is paid
        return com.genixo.education.search.enumaration.PaymentStatus.COMPLETED;
    }

    private String calculatePaymentHealth(Subscription entity) {
        if (entity.getPayments() == null || entity.getPayments().isEmpty()) {
            return "WARNING";
        }

        long totalPayments = entity.getPayments().size();
        long failedPayments = entity.getPayments().stream()
                .filter(p -> p.getPaymentStatus() == com.genixo.education.search.enumaration.PaymentStatus.FAILED)
                .count();

        double failureRate = (double) failedPayments / totalPayments;

        if (failureRate > 0.2) {
            return "CRITICAL";
        } else if (failureRate > 0.1) {
            return "WARNING";
        } else {
            return "GOOD";
        }
    }

    private String calculateUsageHealth(Subscription entity) {
        // Calculate based on overall usage across all resources
        SubscriptionUsageDto usage = mapToUsageDto(entity);
        if (usage == null) {
            return "LOW";
        }

        double avgUsage = (usage.getSchoolsUsagePercentage() +
                usage.getUsersUsagePercentage() +
                usage.getStorageUsagePercentage()) / 3.0;

        if (avgUsage > 60.0) {
            return "ACTIVE";
        } else if (avgUsage > 30.0) {
            return "MODERATE";
        } else if (avgUsage > 10.0) {
            return "LOW";
        } else {
            return "INACTIVE";
        }
    }

    private String calculateEngagementHealth(Subscription entity) {
        // This would typically look at login frequency, feature usage, etc.
        int currentPosts = ConversionUtils.defaultIfNull(entity.getCurrentMonthPosts(), 0);
        int currentAppointments = ConversionUtils.defaultIfNull(entity.getCurrentMonthAppointments(), 0);

        if (currentPosts > 10 || currentAppointments > 20) {
            return "HIGH";
        } else if (currentPosts > 5 || currentAppointments > 10) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    private int calculateOverallHealthScore(String paymentHealth, String usageHealth, String engagementHealth) {
        int paymentScore = getHealthScore(paymentHealth);
        int usageScore = getHealthScore(usageHealth);
        int engagementScore = getHealthScore(engagementHealth);

        // Weighted average: payment 40%, usage 35%, engagement 25%
        return (int) (paymentScore * 0.4 + usageScore * 0.35 + engagementScore * 0.25);
    }

    private int getHealthScore(String healthCategory) {
        switch (healthCategory.toUpperCase()) {
            case "EXCELLENT":
            case "GOOD":
            case "ACTIVE":
            case "HIGH":
                return 90;
            case "MODERATE":
            case "MEDIUM":
                return 70;
            case "WARNING":
            case "LOW":
                return 50;
            case "CRITICAL":
            case "INACTIVE":
                return 30;
            default:
                return 60;
        }
    }

    private String getHealthScoreCategory(int score) {
        if (score >= 80) {
            return "EXCELLENT";
        } else if (score >= 70) {
            return "GOOD";
        } else if (score >= 60) {
            return "FAIR";
        } else if (score >= 40) {
            return "POOR";
        } else {
            return "CRITICAL";
        }
    }

    private String calculateChurnRisk(Subscription entity, String paymentHealth,
                                      String usageHealth, String engagementHealth) {
        int riskScore = 0;

        // Payment risk
        if ("CRITICAL".equals(paymentHealth)) riskScore += 40;
        else if ("WARNING".equals(paymentHealth)) riskScore += 20;

        // Usage risk
        if ("INACTIVE".equals(usageHealth)) riskScore += 30;
        else if ("LOW".equals(usageHealth)) riskScore += 15;

        // Engagement risk
        if ("LOW".equals(engagementHealth)) riskScore += 20;
        else if ("MEDIUM".equals(engagementHealth)) riskScore += 10;

        // Subscription age risk (new subscriptions are higher risk)
        if (entity.getCreatedAt() != null) {
            long daysOld = ChronoUnit.DAYS.between(entity.getCreatedAt().toLocalDate(),
                    java.time.LocalDate.now());
            if (daysOld < 30) riskScore += 10;
        }

        if (riskScore >= 70) {
            return "CRITICAL";
        } else if (riskScore >= 50) {
            return "HIGH";
        } else if (riskScore >= 30) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    private Double calculateChurnProbability(String churnRisk) {
        switch (churnRisk) {
            case "CRITICAL":
                return 0.8;
            case "HIGH":
                return 0.6;
            case "MEDIUM":
                return 0.3;
            case "LOW":
            default:
                return 0.1;
        }
    }

    private void generateHealthRecommendations(String paymentHealth, String usageHealth,
                                               String engagementHealth, List<String> recommendations,
                                               List<String> actionItems, List<String> riskFactors) {

        if ("CRITICAL".equals(paymentHealth) || "WARNING".equals(paymentHealth)) {
            riskFactors.add("Ödeme sorunları");
            recommendations.add("Ödeme yöntemini güncellemeyi önerin");
            actionItems.add("Müşteri ile ödeme durumu hakkında iletişim kurun");
        }

        if ("INACTIVE".equals(usageHealth) || "LOW".equals(usageHealth)) {
            riskFactors.add("Düşük platform kullanımı");
            recommendations.add("Eğitim ve onboarding desteği sunun");
            actionItems.add("Kullanım artırıcı özellikler tanıtın");
        }

        if ("LOW".equals(engagementHealth)) {
            riskFactors.add("Düşük kullanıcı etkileşimi");
            recommendations.add("Müşteri success ekibiyle görüşme ayarlayın");
            actionItems.add("Değer gösterimi sunumları planlayın");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Sağlık durumu iyi görünüyor, mevcut performansı sürdürün");
        }
    }
}