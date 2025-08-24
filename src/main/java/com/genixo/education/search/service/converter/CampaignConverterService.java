package com.genixo.education.search.service.converter;

import com.genixo.education.search.dto.campaign.*;
import com.genixo.education.search.entity.campaign.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignConverterService {

    private final UserConverterService userConverterService;
    private final InstitutionConverterService institutionConverterService;


    /**
     * Campaign entity'sini CampaignDto'ya dönüştürür (full detay)
     */
    public CampaignDto mapToDto(Campaign entity) {
        if (entity == null) {
            return null;
        }

        return CampaignDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .shortDescription(entity.getShortDescription())
                .campaignType(entity.getCampaignType())
                .discountType(entity.getDiscountType())

                // Discount values
                .discountAmount(entity.getDiscountAmount())
                .discountPercentage(entity.getDiscountPercentage())
                .maxDiscountAmount(entity.getMaxDiscountAmount())
                .minPurchaseAmount(entity.getMinPurchaseAmount())

                // Campaign period
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .earlyBirdEndDate(entity.getEarlyBirdEndDate())
                .registrationDeadline(entity.getRegistrationDeadline())
                .enrollmentStartDate(entity.getEnrollmentStartDate())
                .enrollmentEndDate(entity.getEnrollmentEndDate())
                .academicYear(entity.getAcademicYear())

                // Campaign status
                .status(entity.getStatus())
                .isFeatured(entity.getIsFeatured())
                .isPublic(entity.getIsPublic())
                .requiresApproval(entity.getRequiresApproval())

                // Usage limits
                .usageLimit(entity.getUsageLimit())
                .usageCount(entity.getUsageCount())
                .perUserLimit(entity.getPerUserLimit())
                .perSchoolLimit(entity.getPerSchoolLimit())

                // Target audience
                .targetAudience(entity.getTargetAudience())
                .targetGradeLevels(entity.getTargetGradeLevels())
                .targetAgeMin(entity.getTargetAgeMin())
                .targetAgeMax(entity.getTargetAgeMax())
                .targetNewStudentsOnly(entity.getTargetNewStudentsOnly())
                .targetSiblingDiscount(entity.getTargetSiblingDiscount())

                // Promotional content
                .promoCode(entity.getPromoCode())
                .bannerImageUrl(entity.getBannerImageUrl())
                .thumbnailImageUrl(entity.getThumbnailImageUrl())
                .videoUrl(entity.getVideoUrl())
                .ctaText(entity.getCtaText())
                .ctaUrl(entity.getCtaUrl())
                .badgeText(entity.getBadgeText())
                .badgeColor(entity.getBadgeColor())

                // Terms and conditions
                .termsAndConditions(entity.getTermsAndConditions())
                .finePrint(entity.getFinePrint())
                .exclusions(entity.getExclusions())

                // SEO
                .metaTitle(entity.getMetaTitle())
                .metaDescription(entity.getMetaDescription())
                .metaKeywords(entity.getMetaKeywords())

                // Analytics
                .viewCount(entity.getViewCount())
                .clickCount(entity.getClickCount())
                .applicationCount(entity.getApplicationCount())
                .conversionCount(entity.getConversionCount())
                .conversionRate(entity.getConversionRate())

                // Additional features
                .freeTrialDays(entity.getFreeTrialDays())
                .installmentOptions(entity.getInstallmentOptions())
                .paymentDeadlineDays(entity.getPaymentDeadlineDays())
                .refundPolicy(entity.getRefundPolicy())
                .freeServices(entity.getFreeServices())
                .bonusFeatures(entity.getBonusFeatures())
                .giftItems(entity.getGiftItems())

                // Display and priority
                .priority(entity.getPriority())
                .sortOrder(entity.getSortOrder())

                // Creator info
                .createdByUserName(getCreatorName(entity.getCreatedByUser()))
                .approvedByUserName(getApproverName(entity.getApprovedBy()))
                .approvedAt(entity.getApprovedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())

                // Calculated fields
                .isActive(calculateIsActive(entity))
                .isExpired(calculateIsExpired(entity))
                .daysRemaining(calculateDaysRemaining(entity))
                .formattedDiscountAmount(formatDiscountAmount(entity))
                .displayDiscount(calculateDisplayDiscount(entity))
                .campaignPeriod(formatCampaignPeriod(entity))

                // Relationships
                .campaignSchools(mapCampaignSchoolsToDto(entity.getCampaignSchools()))
                .campaignContents(mapCampaignContentsToDto(entity.getCampaignContents()))

                .build();
    }

    /**
     * Campaign entity'sini CampaignSummaryDto'ya dönüştürür (özet)
     */
    public CampaignSummaryDto mapToSummaryDto(Campaign entity) {
        if (entity == null) {
            return null;
        }

        return CampaignSummaryDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .shortDescription(entity.getShortDescription())
                .campaignType(entity.getCampaignType())
                .discountType(entity.getDiscountType())
                .displayDiscount(calculateDisplayDiscount(entity))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .badgeText(entity.getBadgeText())
                .badgeColor(entity.getBadgeColor())
                .thumbnailImageUrl(entity.getThumbnailImageUrl())
                .isActive(calculateIsActive(entity))
                .isExpired(calculateIsExpired(entity))
                .daysRemaining(calculateDaysRemaining(entity))
                .schoolCount(getSchoolCount(entity))
                .applicationCount(entity.getApplicationCount())
                .conversionRate(entity.getConversionRate())
                .build();
    }

    /**
     * CampaignCreateDto'dan Campaign entity'si oluşturur
     */
    public Campaign mapToEntity(CampaignCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Campaign entity = new Campaign();

        // Basic information
        entity.setTitle(dto.getTitle());
        entity.setSlug(ConversionUtils.generateSlug(dto.getTitle()));
        entity.setDescription(dto.getDescription());
        entity.setShortDescription(dto.getShortDescription());
        entity.setCampaignType(dto.getCampaignType());
        entity.setDiscountType(dto.getDiscountType());

        // Discount values
        entity.setDiscountAmount(dto.getDiscountAmount());
        entity.setDiscountPercentage(dto.getDiscountPercentage());
        entity.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        entity.setMinPurchaseAmount(dto.getMinPurchaseAmount());

        // Campaign period
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setEarlyBirdEndDate(dto.getEarlyBirdEndDate());
        entity.setRegistrationDeadline(dto.getRegistrationDeadline());
        entity.setEnrollmentStartDate(dto.getEnrollmentStartDate());
        entity.setEnrollmentEndDate(dto.getEnrollmentEndDate());
        entity.setAcademicYear(dto.getAcademicYear());

        // Campaign settings
        entity.setIsFeatured(ConversionUtils.defaultIfNull(dto.getIsFeatured(), false));
        entity.setIsPublic(ConversionUtils.defaultIfNull(dto.getIsPublic(), true));
        entity.setRequiresApproval(ConversionUtils.defaultIfNull(dto.getRequiresApproval(), false));

        // Usage limits
        entity.setUsageLimit(dto.getUsageLimit());
        entity.setUsageCount(0);
        entity.setPerUserLimit(ConversionUtils.defaultIfNull(dto.getPerUserLimit(), 1));
        entity.setPerSchoolLimit(dto.getPerSchoolLimit());

        // Target audience
        entity.setTargetAudience(dto.getTargetAudience());
        entity.setTargetGradeLevels(dto.getTargetGradeLevels());
        entity.setTargetAgeMin(dto.getTargetAgeMin());
        entity.setTargetAgeMax(dto.getTargetAgeMax());
        entity.setTargetNewStudentsOnly(ConversionUtils.defaultIfNull(dto.getTargetNewStudentsOnly(), false));
        entity.setTargetSiblingDiscount(ConversionUtils.defaultIfNull(dto.getTargetSiblingDiscount(), false));

        // Promotional content
        entity.setPromoCode(dto.getPromoCode());
        entity.setBannerImageUrl(dto.getBannerImageUrl());
        entity.setThumbnailImageUrl(dto.getThumbnailImageUrl());
        entity.setVideoUrl(dto.getVideoUrl());
        entity.setCtaText(dto.getCtaText());
        entity.setCtaUrl(dto.getCtaUrl());
        entity.setBadgeText(dto.getBadgeText());
        entity.setBadgeColor(dto.getBadgeColor());

        // Terms and conditions
        entity.setTermsAndConditions(dto.getTermsAndConditions());
        entity.setFinePrint(dto.getFinePrint());
        entity.setExclusions(dto.getExclusions());

        // SEO
        entity.setMetaTitle(dto.getMetaTitle());
        entity.setMetaDescription(dto.getMetaDescription());
        entity.setMetaKeywords(dto.getMetaKeywords());

        // Additional features
        entity.setFreeTrialDays(dto.getFreeTrialDays());
        entity.setInstallmentOptions(dto.getInstallmentOptions());
        entity.setPaymentDeadlineDays(dto.getPaymentDeadlineDays());
        entity.setRefundPolicy(dto.getRefundPolicy());
        entity.setFreeServices(dto.getFreeServices());
        entity.setBonusFeatures(dto.getBonusFeatures());
        entity.setGiftItems(dto.getGiftItems());

        // Display and priority
        entity.setPriority(ConversionUtils.defaultIfNull(dto.getPriority(), 0));
        entity.setSortOrder(ConversionUtils.defaultIfNull(dto.getSortOrder(), 0));

        // Initialize analytics
        entity.setViewCount(0L);
        entity.setClickCount(0L);
        entity.setApplicationCount(0L);
        entity.setConversionCount(0L);
        entity.setConversionRate(0.0);

        return entity;
    }

    /**
     * CampaignUpdateDto ile Campaign entity'sini günceller
     */
    public void updateEntity(CampaignUpdateDto dto, Campaign entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Basic information
        if (StringUtils.hasText(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
            entity.setSlug(ConversionUtils.generateSlug(dto.getTitle()));
        }

        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }

        if (dto.getShortDescription() != null) {
            entity.setShortDescription(dto.getShortDescription());
        }

        if (dto.getDiscountType() != null) {
            entity.setDiscountType(dto.getDiscountType());
        }

        // Discount values
        if (dto.getDiscountAmount() != null) {
            entity.setDiscountAmount(dto.getDiscountAmount());
        }

        if (dto.getDiscountPercentage() != null) {
            entity.setDiscountPercentage(dto.getDiscountPercentage());
        }

        if (dto.getMaxDiscountAmount() != null) {
            entity.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        }

        if (dto.getMinPurchaseAmount() != null) {
            entity.setMinPurchaseAmount(dto.getMinPurchaseAmount());
        }

        // Campaign period
        if (dto.getStartDate() != null) {
            entity.setStartDate(dto.getStartDate());
        }

        if (dto.getEndDate() != null) {
            entity.setEndDate(dto.getEndDate());
        }

        if (dto.getEarlyBirdEndDate() != null) {
            entity.setEarlyBirdEndDate(dto.getEarlyBirdEndDate());
        }

        if (dto.getRegistrationDeadline() != null) {
            entity.setRegistrationDeadline(dto.getRegistrationDeadline());
        }

        // Campaign settings
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }

        if (dto.getIsFeatured() != null) {
            entity.setIsFeatured(dto.getIsFeatured());
        }

        if (dto.getIsPublic() != null) {
            entity.setIsPublic(dto.getIsPublic());
        }

        // Usage limits
        if (dto.getUsageLimit() != null) {
            entity.setUsageLimit(dto.getUsageLimit());
        }

        if (dto.getPerUserLimit() != null) {
            entity.setPerUserLimit(dto.getPerUserLimit());
        }

        if (dto.getPerSchoolLimit() != null) {
            entity.setPerSchoolLimit(dto.getPerSchoolLimit());
        }

        // Target audience
        if (dto.getTargetAudience() != null) {
            entity.setTargetAudience(dto.getTargetAudience());
        }

        if (dto.getTargetGradeLevels() != null) {
            entity.setTargetGradeLevels(dto.getTargetGradeLevels());
        }

        if (dto.getTargetAgeMin() != null) {
            entity.setTargetAgeMin(dto.getTargetAgeMin());
        }

        if (dto.getTargetAgeMax() != null) {
            entity.setTargetAgeMax(dto.getTargetAgeMax());
        }

        // Promotional content
        if (dto.getBannerImageUrl() != null) {
            entity.setBannerImageUrl(dto.getBannerImageUrl());
        }

        if (dto.getThumbnailImageUrl() != null) {
            entity.setThumbnailImageUrl(dto.getThumbnailImageUrl());
        }

        if (dto.getVideoUrl() != null) {
            entity.setVideoUrl(dto.getVideoUrl());
        }

        if (dto.getCtaText() != null) {
            entity.setCtaText(dto.getCtaText());
        }

        if (dto.getCtaUrl() != null) {
            entity.setCtaUrl(dto.getCtaUrl());
        }

        if (dto.getBadgeText() != null) {
            entity.setBadgeText(dto.getBadgeText());
        }

        if (dto.getBadgeColor() != null) {
            entity.setBadgeColor(dto.getBadgeColor());
        }

        // Terms and conditions
        if (dto.getTermsAndConditions() != null) {
            entity.setTermsAndConditions(dto.getTermsAndConditions());
        }

        if (dto.getFinePrint() != null) {
            entity.setFinePrint(dto.getFinePrint());
        }

        if (dto.getExclusions() != null) {
            entity.setExclusions(dto.getExclusions());
        }

        // Additional features
        if (dto.getFreeTrialDays() != null) {
            entity.setFreeTrialDays(dto.getFreeTrialDays());
        }

        if (dto.getInstallmentOptions() != null) {
            entity.setInstallmentOptions(dto.getInstallmentOptions());
        }

        if (dto.getPaymentDeadlineDays() != null) {
            entity.setPaymentDeadlineDays(dto.getPaymentDeadlineDays());
        }

        if (dto.getRefundPolicy() != null) {
            entity.setRefundPolicy(dto.getRefundPolicy());
        }

        // Display
        if (dto.getPriority() != null) {
            entity.setPriority(dto.getPriority());
        }

        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
    }

    // ================== CAMPAIGN SCHOOL CONVERSION ==================

    /**
     * CampaignSchool entity'sini CampaignSchoolDto'ya dönüştürür
     */
    public CampaignSchoolDto mapCampaignSchoolToDto(CampaignSchool entity) {
        if (entity == null) {
            return null;
        }

        return CampaignSchoolDto.builder()
                .id(entity.getId())
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getId() : null)
                .campaignTitle(entity.getCampaign() != null ? entity.getCampaign().getTitle() : null)
                .schoolId(entity.getSchool() != null ? entity.getSchool().getId() : null)
                .schoolName(entity.getSchool() != null ? entity.getSchool().getName() : null)
                .campusName(entity.getSchool() != null && entity.getSchool().getCampus() != null ?
                        entity.getSchool().getCampus().getName() : null)
                .assignedByUserName(entity.getAssignedByUser() != null ?
                        entity.getAssignedByUser().getFirstName() + " " +
                                entity.getAssignedByUser().getLastName() : null)
                .assignedAt(entity.getAssignedAt())
                .status(entity.getStatus())

                // School-specific customizations
                .customDiscountAmount(entity.getCustomDiscountAmount())
                .customDiscountPercentage(entity.getCustomDiscountPercentage())
                .customUsageLimit(entity.getCustomUsageLimit())
                .customStartDate(entity.getCustomStartDate())
                .customEndDate(entity.getCustomEndDate())
                .customTerms(entity.getCustomTerms())

                // Display settings
                .priority(entity.getPriority())
                .isFeaturedOnSchool(entity.getIsFeaturedOnSchool())
                .showOnHomepage(entity.getShowOnHomepage())
                .showOnPricingPage(entity.getShowOnPricingPage())

                // Usage tracking
                .usageCount(entity.getUsageCount())
                .applicationCount(entity.getApplicationCount())
                .conversionCount(entity.getConversionCount())
                .revenueGenerated(entity.getRevenueGenerated())

                // Performance metrics
                .viewCount(entity.getViewCount())
                .clickCount(entity.getClickCount())
                .inquiryCount(entity.getInquiryCount())
                .appointmentCount(entity.getAppointmentCount())

                // Approval
                .approvedBySchool(entity.getApprovedBySchool())
                .approvedBySchoolUserName(getSchoolApproverName(entity))
                .approvedBySchoolAt(entity.getApprovedBySchoolAt())
                .schoolNotes(entity.getSchoolNotes())

                // Calculated fields
                .effectiveDiscount(calculateEffectiveDiscount(entity))
                .effectivePeriod(calculateEffectivePeriod(entity))
                .performanceScore(calculatePerformanceScore(entity))

                .build();
    }

    // ================== CAMPAIGN CONTENT CONVERSION ==================

    /**
     * CampaignContent entity'sini CampaignContentDto'ya dönüştürür
     */
    public CampaignContentDto mapCampaignContentToDto(CampaignContent entity) {
        if (entity == null) {
            return null;
        }

        return CampaignContentDto.builder()
                .id(entity.getId())
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getId() : null)
                .campaignTitle(entity.getCampaign() != null ? entity.getCampaign().getTitle() : null)
                .contentType(entity.getContentType())
                .title(entity.getTitle())
                .content(entity.getContent())
                .mediaUrl(entity.getMediaUrl())
                .thumbnailUrl(entity.getThumbnailUrl())
                .altText(entity.getAltText())
                .caption(entity.getCaption())
                .fileSizeBytes(entity.getFileSizeBytes())
                .mimeType(entity.getMimeType())
                .durationSeconds(entity.getDurationSeconds())
                .dimensions(entity.getDimensions())
                .usageContext(entity.getUsageContext())
                .sortOrder(entity.getSortOrder())
                .isPrimary(entity.getIsPrimary())
                .languageCode(entity.getLanguageCode())

                // Social media specific
                .hashtags(entity.getHashtags())
                .mentionAccounts(entity.getMentionAccounts())
                .socialMediaPlatforms(entity.getSocialMediaPlatforms())

                // A/B testing
                .variantName(entity.getVariantName())
                .isTestVariant(entity.getIsTestVariant())

                // Performance tracking
                .viewCount(entity.getViewCount())
                .clickCount(entity.getClickCount())
                .downloadCount(entity.getDownloadCount())
                .shareCount(entity.getShareCount())
                .engagementRate(entity.getEngagementRate())

                // Content approval
                .approvalStatus(entity.getApprovalStatus())
                .approvedByUserName(getContentApproverName(entity))
                .approvedAt(entity.getApprovedAt())
                .rejectionReason(entity.getRejectionReason())

                // Copyright and licensing
                .copyrightOwner(entity.getCopyrightOwner())
                .licenseType(entity.getLicenseType())
                .usageRights(entity.getUsageRights())
                .attributionRequired(entity.getAttributionRequired())
                .attributionText(entity.getAttributionText())

                // Metadata
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())

                .build();
    }

    // ================== CAMPAIGN USAGE CONVERSION ==================

    /**
     * CampaignUsage entity'sini CampaignUsageDto'ya dönüştürür
     */
    public CampaignUsageDto mapCampaignUsageToDto(CampaignUsage entity) {
        if (entity == null) {
            return null;
        }

        return CampaignUsageDto.builder()
                .id(entity.getId())
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getId() : null)
                .campaignTitle(entity.getCampaign() != null ? entity.getCampaign().getTitle() : null)
                .schoolId(entity.getSchool() != null ? entity.getSchool().getId() : null)
                .schoolName(entity.getSchool() != null ? entity.getSchool().getName() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .userFullName(entity.getUser() != null ?
                        entity.getUser().getFirstName() + " " + entity.getUser().getLastName() : null)
                .usageDate(entity.getUsageDate())
                .usageType(entity.getUsageType())
                .status(entity.getStatus())

                // Financial info
                .originalAmount(entity.getOriginalAmount())
                .discountAmount(entity.getDiscountAmount())
                .finalAmount(entity.getFinalAmount())
                .promoCodeUsed(entity.getPromoCodeUsed())

                // Student information
                .studentName(entity.getStudentName())
                .studentAge(entity.getStudentAge())
                .gradeLevel(entity.getGradeLevel())
                .enrollmentYear(entity.getEnrollmentYear())

                // Contact information
                .parentName(entity.getParentName())
                .parentEmail(entity.getParentEmail())
                .parentPhone(entity.getParentPhone())

                // Tracking information
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .referrerUrl(entity.getReferrerUrl())
                .utmSource(entity.getUtmSource())
                .utmMedium(entity.getUtmMedium())
                .utmCampaign(entity.getUtmCampaign())

                // Processing
                .approvedByUserName(getUsageApproverName(entity))
                .approvedAt(entity.getApprovedAt())
                .processedAt(entity.getProcessedAt())
                .notes(entity.getNotes())

                // Follow-up
                .followUpRequired(entity.getFollowUpRequired())
                .followUpDate(entity.getFollowUpDate())
                .followUpCompleted(entity.getFollowUpCompleted())

                // Related entities
                .appointmentId(entity.getAppointmentId())
                .enrollmentId(entity.getEnrollmentId())
                .invoiceId(entity.getInvoiceId())

                // Validation
                .validationCode(entity.getValidationCode())
                .validationExpiresAt(entity.getValidationExpiresAt())
                .isValidated(entity.getIsValidated())
                .validatedAt(entity.getValidatedAt())

                // Formatted values
                .formattedOriginalAmount(ConversionUtils.formatPrice(entity.getOriginalAmount()))
                .formattedDiscountAmount(ConversionUtils.formatPrice(entity.getDiscountAmount()))
                .formattedFinalAmount(ConversionUtils.formatPrice(entity.getFinalAmount()))
                .savingsAmount(calculateSavingsAmount(entity))
                .savingsPercentage(calculateSavingsPercentage(entity))

                .build();
    }

    // ================== COLLECTION MAPPING METHODS ==================

    public List<CampaignDto> mapToDto(List<Campaign> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<CampaignSummaryDto> mapToSummaryDto(List<Campaign> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<CampaignSchoolDto> mapCampaignSchoolsToDto(Set<CampaignSchool> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::mapCampaignSchoolToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<CampaignContentDto> mapCampaignContentsToDto(Set<CampaignContent> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::mapCampaignContentToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<CampaignUsageDto> mapCampaignUsagesToDto(Set<CampaignUsage> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::mapCampaignUsageToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ================== PRIVATE HELPER METHODS ==================

    private String getCreatorName(User user) {
        if (user == null) {
            return null;
        }
        return user.getFirstName() + " " + user.getLastName();
    }

    private String getApproverName(Long approvedBy) {
        // TODO: Bu method'u UserService ile entegre etmek gerekiyor
        // Şimdilik null return ediyoruz
        return null;
    }

    private String getSchoolApproverName(CampaignSchool entity) {
        if (entity.getApprovedBySchoolUserId() == null) {
            return null;
        }
        // TODO: Bu method'u UserService ile entegre etmek gerekiyor
        return null;
    }

    private String getContentApproverName(CampaignContent entity) {
        if (entity.getApprovedBy() == null) {
            return null;
        }
        // TODO: Bu method'u UserService ile entegre etmek gerekiyor
        return null;
    }

    private String getUsageApproverName(CampaignUsage entity) {
        if (entity.getApprovedBy() == null) {
            return null;
        }
        // TODO: Bu method'u UserService ile entegre etmek gerekiyor
        return null;
    }

    private Boolean calculateIsActive(Campaign entity) {
        if (entity.getStatus() == null || entity.getStartDate() == null || entity.getEndDate() == null) {
            return false;
        }

        LocalDate now = LocalDate.now();
        return entity.getStatus().name().equals("ACTIVE") &&
                !now.isBefore(entity.getStartDate()) &&
                !now.isAfter(entity.getEndDate());
    }

    private Boolean calculateIsExpired(Campaign entity) {
        if (entity.getEndDate() == null) {
            return false;
        }

        return LocalDate.now().isAfter(entity.getEndDate());
    }

    private Integer calculateDaysRemaining(Campaign entity) {
        if (entity.getEndDate() == null) {
            return null;
        }

        LocalDate now = LocalDate.now();
        if (now.isAfter(entity.getEndDate())) {
            return 0; // Expired
        }

        return (int) ChronoUnit.DAYS.between(now, entity.getEndDate());
    }

    private String formatDiscountAmount(Campaign entity) {
        if (entity.getDiscountAmount() != null) {
            return ConversionUtils.formatPrice(entity.getDiscountAmount());
        }
        return null;
    }

    private String calculateDisplayDiscount(Campaign entity) {
        if (entity.getDiscountType() == null) {
            return null;
        }

        switch (entity.getDiscountType()) {
            case FIXED_AMOUNT:
                if (entity.getDiscountAmount() != null) {
                    return ConversionUtils.formatPrice(entity.getDiscountAmount()) + " İndirim";
                }
                break;
            case PERCENTAGE:
                if (entity.getDiscountPercentage() != null) {
                    return "%" + ConversionUtils.formatNumber(entity.getDiscountPercentage().intValue()) + " İndirim";
                }
                break;
            case FREE_MONTHS:
                if (entity.getFreeTrialDays() != null && entity.getFreeTrialDays() > 0) {
                    int months = entity.getFreeTrialDays() / 30;
                    return months + " Ay Ücretsiz";
                }
                break;
            case BUY_X_GET_Y:
                return "Al-Öde Kampanyası";
            case TIERED:
                return "Kademeli İndirim";
            case BUNDLE:
                return "Paket İndirim";
            case NO_DISCOUNT:
                return "Özel Kampanya";
        }

        return "Kampanya";
    }

    private String formatCampaignPeriod(Campaign entity) {
        if (entity.getStartDate() == null || entity.getEndDate() == null) {
            return null;
        }

        String startDate = ConversionUtils.formatDate(entity.getStartDate());
        String endDate = ConversionUtils.formatDate(entity.getEndDate());

        return startDate + " - " + endDate;
    }

    private Long getSchoolCount(Campaign entity) {
        if (entity.getCampaignSchools() == null) {
            return 0L;
        }

        return (long) entity.getCampaignSchools().size();
    }

    private String calculateEffectiveDiscount(CampaignSchool entity) {
        Campaign campaign = entity.getCampaign();
        if (campaign == null) {
            return null;
        }

        // Önce school-specific discount kontrolü
        if (entity.getCustomDiscountAmount() != null) {
            return ConversionUtils.formatPrice(entity.getCustomDiscountAmount()) + " İndirim";
        }

        if (entity.getCustomDiscountPercentage() != null) {
            return "%" + ConversionUtils.formatNumber(entity.getCustomDiscountPercentage().intValue()) + " İndirim";
        }

        // Campaign default discount
        return calculateDisplayDiscount(campaign);
    }

    private String calculateEffectivePeriod(CampaignSchool entity) {
        Campaign campaign = entity.getCampaign();
        if (campaign == null) {
            return null;
        }

        // Önce school-specific period kontrolü
        LocalDate startDate = entity.getCustomStartDate() != null ?
                entity.getCustomStartDate() : campaign.getStartDate();
        LocalDate endDate = entity.getCustomEndDate() != null ?
                entity.getCustomEndDate() : campaign.getEndDate();

        if (startDate == null || endDate == null) {
            return null;
        }

        return ConversionUtils.formatDate(startDate) + " - " + ConversionUtils.formatDate(endDate);
    }

    private Double calculatePerformanceScore(CampaignSchool entity) {
        // Performance score hesaplama algoritması
        // View count, conversion rate, revenue gibi metrikler kullanılabilir

        double score = 0.0;
        int factors = 0;

        // View count contribution (0-25 points)
        if (entity.getViewCount() != null && entity.getViewCount() > 0) {
            score += Math.min(25.0, entity.getViewCount().doubleValue() / 100.0 * 25.0);
            factors++;
        }

        // Conversion rate contribution (0-35 points)
        if (entity.getApplicationCount() != null && entity.getConversionCount() != null &&
                entity.getApplicationCount() > 0) {
            double conversionRate = (entity.getConversionCount().doubleValue() /
                    entity.getApplicationCount().doubleValue()) * 100.0;
            score += Math.min(35.0, conversionRate * 0.35);
            factors++;
        }

        // Revenue contribution (0-40 points)
        if (entity.getRevenueGenerated() != null && entity.getRevenueGenerated().compareTo(BigDecimal.ZERO) > 0) {
            // Revenue'yi normalize et (örnek: 10000 TL = 40 puan)
            double revenueScore = Math.min(40.0, entity.getRevenueGenerated().doubleValue() / 10000.0 * 40.0);
            score += revenueScore;
            factors++;
        }

        return factors > 0 ? score / factors * (factors / 3.0) * 100.0 / 100.0 : 0.0;
    }

    private String calculateSavingsAmount(CampaignUsage entity) {
        if (entity.getOriginalAmount() != null && entity.getFinalAmount() != null) {
            BigDecimal savings = entity.getOriginalAmount().subtract(entity.getFinalAmount());
            if (savings.compareTo(BigDecimal.ZERO) > 0) {
                return ConversionUtils.formatPrice(savings);
            }
        }

        if (entity.getDiscountAmount() != null) {
            return ConversionUtils.formatPrice(entity.getDiscountAmount());
        }

        return null;
    }

    private String calculateSavingsPercentage(CampaignUsage entity) {
        if (entity.getOriginalAmount() != null && entity.getFinalAmount() != null &&
                entity.getOriginalAmount().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal savings = entity.getOriginalAmount().subtract(entity.getFinalAmount());
            if (savings.compareTo(BigDecimal.ZERO) > 0) {
                double percentage = savings.divide(entity.getOriginalAmount(), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
                return ConversionUtils.formatPercentage(percentage);
            }
        }

        return null;
    }


    /**
     * CampaignContentCreateDto'dan CampaignContent entity'si oluşturur
     */
    public CampaignContent mapContentCreateToEntity(CampaignContentCreateDto dto) {
        if (dto == null) {
            return null;
        }

        CampaignContent entity = new CampaignContent();

        // Note: Campaign reference will be set by the service layer
        entity.setContentType(dto.getContentType());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setMediaUrl(dto.getMediaUrl());
        entity.setThumbnailUrl(dto.getThumbnailUrl());
        entity.setAltText(dto.getAltText());
        entity.setCaption(dto.getCaption());
        entity.setFileSizeBytes(dto.getFileSizeBytes());
        entity.setMimeType(dto.getMimeType());
        entity.setDurationSeconds(dto.getDurationSeconds());
        entity.setDimensions(dto.getDimensions());
        entity.setUsageContext(dto.getUsageContext());
        entity.setSortOrder(ConversionUtils.defaultIfNull(dto.getSortOrder(), 0));
        entity.setIsPrimary(ConversionUtils.defaultIfNull(dto.getIsPrimary(), false));
        entity.setLanguageCode(ConversionUtils.defaultIfEmpty(dto.getLanguageCode(), "tr"));

        // Social media specific
        entity.setHashtags(dto.getHashtags());
        entity.setMentionAccounts(dto.getMentionAccounts());
        entity.setSocialMediaPlatforms(dto.getSocialMediaPlatforms());

        // A/B testing
        entity.setVariantName(dto.getVariantName());
        entity.setIsTestVariant(ConversionUtils.defaultIfNull(dto.getIsTestVariant(), false));

        // Copyright and licensing
        entity.setCopyrightOwner(dto.getCopyrightOwner());
        entity.setLicenseType(dto.getLicenseType());
        entity.setUsageRights(dto.getUsageRights());
        entity.setAttributionRequired(ConversionUtils.defaultIfNull(dto.getAttributionRequired(), false));
        entity.setAttributionText(dto.getAttributionText());

        // Initialize metrics
        entity.setViewCount(0L);
        entity.setClickCount(0L);
        entity.setDownloadCount(0L);
        entity.setShareCount(0L);
        entity.setEngagementRate(0.0);

        return entity;
    }

    /**
     * CampaignUsageCreateDto'dan CampaignUsage entity'si oluşturur
     */
    public CampaignUsage mapUsageCreateToEntity(CampaignUsageCreateDto dto) {
        if (dto == null) {
            return null;
        }

        CampaignUsage entity = new CampaignUsage();

        // Note: Campaign, School, User references will be set by the service layer
        entity.setUsageType(dto.getUsageType());
        entity.setOriginalAmount(dto.getOriginalAmount());
        entity.setPromoCodeUsed(dto.getPromoCodeUsed());

        // Student information
        entity.setStudentName(dto.getStudentName());
        entity.setStudentAge(dto.getStudentAge());
        entity.setGradeLevel(dto.getGradeLevel());
        entity.setEnrollmentYear(dto.getEnrollmentYear());

        // Contact information
        entity.setParentName(dto.getParentName());
        entity.setParentEmail(dto.getParentEmail());
        entity.setParentPhone(dto.getParentPhone());

        // Tracking information
        entity.setIpAddress(dto.getIpAddress());
        entity.setUserAgent(dto.getUserAgent());
        entity.setReferrerUrl(dto.getReferrerUrl());
        entity.setUtmSource(dto.getUtmSource());
        entity.setUtmMedium(dto.getUtmMedium());
        entity.setUtmCampaign(dto.getUtmCampaign());

        // Additional info
        entity.setNotes(dto.getNotes());
        entity.setFollowUpRequired(ConversionUtils.defaultIfNull(dto.getFollowUpRequired(), false));
        entity.setFollowUpDate(dto.getFollowUpDate());

        // Set usage date
        entity.setUsageDate(LocalDateTime.now());

        return entity;
    }

    /**
     * CampaignSchoolAssignDto'dan CampaignSchool entity'si oluşturur
     */
    public List<CampaignSchool> mapSchoolAssignToEntities(CampaignSchoolAssignDto dto, Campaign campaign) {
        if (dto == null || campaign == null || ConversionUtils.isEmpty(dto.getSchoolIds())) {
            return new ArrayList<>();
        }

        return dto.getSchoolIds().stream()
                .map(schoolId -> {
                    CampaignSchool entity = new CampaignSchool();
                    entity.setCampaign(campaign);
                    // Note: School reference will be set by the service layer using schoolId

                    // Optional customizations
                    entity.setCustomDiscountAmount(dto.getCustomDiscountAmount());
                    entity.setCustomDiscountPercentage(dto.getCustomDiscountPercentage());
                    entity.setCustomUsageLimit(dto.getCustomUsageLimit());
                    entity.setCustomStartDate(dto.getCustomStartDate());
                    entity.setCustomEndDate(dto.getCustomEndDate());
                    entity.setCustomTerms(dto.getCustomTerms());

                    // Display settings
                    entity.setPriority(ConversionUtils.defaultIfNull(dto.getPriority(), 0));
                    entity.setIsFeaturedOnSchool(ConversionUtils.defaultIfNull(dto.getIsFeaturedOnSchool(), false));
                    entity.setShowOnHomepage(ConversionUtils.defaultIfNull(dto.getShowOnHomepage(), true));
                    entity.setShowOnPricingPage(ConversionUtils.defaultIfNull(dto.getShowOnPricingPage(), true));

                    // Set assignment info
                    entity.setAssignedAt(LocalDateTime.now());
                    entity.setApprovedBySchool(true); // Default approved

                    // Initialize counters
                    entity.setUsageCount(0);
                    entity.setApplicationCount(0);
                    entity.setConversionCount(0);
                    entity.setRevenueGenerated(BigDecimal.ZERO);
                    entity.setViewCount(0L);
                    entity.setClickCount(0L);
                    entity.setInquiryCount(0L);
                    entity.setAppointmentCount(0L);

                    return entity;
                })
                .collect(Collectors.toList());
    }
}