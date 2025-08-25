package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.campaign.*;
import com.genixo.education.search.entity.campaign.*;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.campaign.*;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.CampaignConverterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignSchoolRepository campaignSchoolRepository;
    private final CampaignUsageRepository campaignUsageRepository;
    private final CampaignContentRepository campaignContentRepository;
    private final SchoolRepository schoolRepository;
    private final CampaignConverterService converterService;
    private final JwtService jwtService;

    // ================================ CAMPAIGN CRUD OPERATIONS ================================

    @Operation(summary = "Create campaign", description = "Create a new campaign with specified details and school assignments")
    @Transactional
    @CacheEvict(value = {"campaigns", "campaign_summaries", "active_campaigns"}, allEntries = true)
    public CampaignDto createCampaign(CampaignCreateDto createDto, HttpServletRequest request) {
        log.info("Creating new campaign: {}", createDto.getTitle());

        User user = jwtService.getUser(request);
        validateUserCanCreateCampaign(user);

        // Validate campaign dates
        validateCampaignDates(createDto.getStartDate(), createDto.getEndDate());

        // Check if promo code is unique (if provided)
        if (StringUtils.hasText(createDto.getPromoCode()) &&
                campaignRepository.existsByPromoCodeIgnoreCase(createDto.getPromoCode())) {
            throw new BusinessException("Promo code already exists: " + createDto.getPromoCode());
        }

        Campaign campaign = new Campaign();
        campaign.setTitle(createDto.getTitle());
        campaign.setSlug(generateUniqueSlug(createDto.getTitle()));
        campaign.setDescription(createDto.getDescription());
        campaign.setShortDescription(createDto.getShortDescription());
        campaign.setCampaignType(createDto.getCampaignType());
        campaign.setDiscountType(createDto.getDiscountType());
        campaign.setDiscountAmount(createDto.getDiscountAmount());
        campaign.setDiscountPercentage(createDto.getDiscountPercentage());
        campaign.setMaxDiscountAmount(createDto.getMaxDiscountAmount());
        campaign.setMinPurchaseAmount(createDto.getMinPurchaseAmount());
        campaign.setStartDate(createDto.getStartDate());
        campaign.setEndDate(createDto.getEndDate());
        campaign.setEarlyBirdEndDate(createDto.getEarlyBirdEndDate());
        campaign.setRegistrationDeadline(createDto.getRegistrationDeadline());
        campaign.setEnrollmentStartDate(createDto.getEnrollmentStartDate());
        campaign.setEnrollmentEndDate(createDto.getEnrollmentEndDate());
        campaign.setAcademicYear(createDto.getAcademicYear());
        campaign.setStatus(CampaignStatus.DRAFT);
        campaign.setIsFeatured(createDto.getIsFeatured());
        campaign.setIsPublic(createDto.getIsPublic());
        campaign.setRequiresApproval(createDto.getRequiresApproval());
        campaign.setUsageLimit(createDto.getUsageLimit());
        campaign.setPerUserLimit(createDto.getPerUserLimit());
        campaign.setPerSchoolLimit(createDto.getPerSchoolLimit());
        campaign.setTargetAudience(createDto.getTargetAudience());
        campaign.setTargetGradeLevels(createDto.getTargetGradeLevels());
        campaign.setTargetAgeMin(createDto.getTargetAgeMin());
        campaign.setTargetAgeMax(createDto.getTargetAgeMax());
        campaign.setTargetNewStudentsOnly(createDto.getTargetNewStudentsOnly());
        campaign.setTargetSiblingDiscount(createDto.getTargetSiblingDiscount());
        campaign.setPromoCode(createDto.getPromoCode());
        campaign.setBannerImageUrl(createDto.getBannerImageUrl());
        campaign.setThumbnailImageUrl(createDto.getThumbnailImageUrl());
        campaign.setVideoUrl(createDto.getVideoUrl());
        campaign.setCtaText(createDto.getCtaText());
        campaign.setCtaUrl(createDto.getCtaUrl());
        campaign.setBadgeText(createDto.getBadgeText());
        campaign.setBadgeColor(createDto.getBadgeColor());
        campaign.setTermsAndConditions(createDto.getTermsAndConditions());
        campaign.setFinePrint(createDto.getFinePrint());
        campaign.setExclusions(createDto.getExclusions());
        campaign.setMetaTitle(createDto.getMetaTitle());
        campaign.setMetaDescription(createDto.getMetaDescription());
        campaign.setMetaKeywords(createDto.getMetaKeywords());
        campaign.setFreeTrialDays(createDto.getFreeTrialDays());
        campaign.setInstallmentOptions(createDto.getInstallmentOptions());
        campaign.setPaymentDeadlineDays(createDto.getPaymentDeadlineDays());
        campaign.setRefundPolicy(createDto.getRefundPolicy());
        campaign.setFreeServices(createDto.getFreeServices());
        campaign.setBonusFeatures(createDto.getBonusFeatures());
        campaign.setGiftItems(createDto.getGiftItems());
        campaign.setPriority(createDto.getPriority());
        campaign.setSortOrder(createDto.getSortOrder());
        campaign.setCreatedByUser(user);
        campaign.setCreatedBy(user.getId());

        campaign = campaignRepository.save(campaign);

        // Assign schools if provided
        if (createDto.getSchoolIds() != null && !createDto.getSchoolIds().isEmpty()) {
            assignSchoolsToCampaign(campaign.getId(), createDto.getSchoolIds(), user);
        }

        log.info("Campaign created successfully with ID: {}", campaign.getId());

        return converterService.mapToDto(campaign);
    }

    @Operation(summary = "Get campaign by ID", description = "Retrieve campaign details by campaign ID")
    @Cacheable(value = "campaigns", key = "#id")
    public CampaignDto getCampaignById(Long id, HttpServletRequest request) {
        log.info("Fetching campaign with ID: {}", id);

        User user = jwtService.getUser(request);
        Campaign campaign = campaignRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));

        validateUserCanAccessCampaign(user, campaign);

        return converterService.mapToDto(campaign);
    }

    @Operation(summary = "Get campaign by slug", description = "Retrieve campaign details by slug")
    @Cacheable(value = "campaigns", key = "#slug")
    public CampaignDto getCampaignBySlug(String slug) {
        log.info("Fetching campaign with slug: {}", slug);

        Campaign campaign = campaignRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with slug: " + slug));

        return converterService.mapToDto(campaign);
    }

    @Operation(summary = "Update campaign", description = "Update campaign details")
    @Transactional
    @CacheEvict(value = {"campaigns", "campaign_summaries", "active_campaigns"}, allEntries = true)
    public CampaignDto updateCampaign(Long id, CampaignUpdateDto updateDto, HttpServletRequest request) {
        log.info("Updating campaign with ID: {}", id);

        User user = jwtService.getUser(request);
        Campaign campaign = campaignRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));

        validateUserCanManageCampaign(user, campaign);

        // Validate dates if provided
        if (updateDto.getStartDate() != null && updateDto.getEndDate() != null) {
            validateCampaignDates(updateDto.getStartDate(), updateDto.getEndDate());
        }

        // Update fields
        if (StringUtils.hasText(updateDto.getTitle())) {
            campaign.setTitle(updateDto.getTitle());
        }
        if (StringUtils.hasText(updateDto.getDescription())) {
            campaign.setDescription(updateDto.getDescription());
        }
        if (StringUtils.hasText(updateDto.getShortDescription())) {
            campaign.setShortDescription(updateDto.getShortDescription());
        }
        if (updateDto.getDiscountType() != null) {
            campaign.setDiscountType(updateDto.getDiscountType());
        }
        if (updateDto.getDiscountAmount() != null) {
            campaign.setDiscountAmount(updateDto.getDiscountAmount());
        }
        if (updateDto.getDiscountPercentage() != null) {
            campaign.setDiscountPercentage(updateDto.getDiscountPercentage());
        }
        if (updateDto.getMaxDiscountAmount() != null) {
            campaign.setMaxDiscountAmount(updateDto.getMaxDiscountAmount());
        }
        if (updateDto.getMinPurchaseAmount() != null) {
            campaign.setMinPurchaseAmount(updateDto.getMinPurchaseAmount());
        }
        if (updateDto.getStartDate() != null) {
            campaign.setStartDate(updateDto.getStartDate());
        }
        if (updateDto.getEndDate() != null) {
            campaign.setEndDate(updateDto.getEndDate());
        }
        if (updateDto.getStatus() != null) {
            campaign.setStatus(updateDto.getStatus());
        }
        if (updateDto.getIsFeatured() != null) {
            campaign.setIsFeatured(updateDto.getIsFeatured());
        }
        if (updateDto.getIsPublic() != null) {
            campaign.setIsPublic(updateDto.getIsPublic());
        }
        if (updateDto.getBannerImageUrl() != null) {
            campaign.setBannerImageUrl(updateDto.getBannerImageUrl());
        }
        if (updateDto.getThumbnailImageUrl() != null) {
            campaign.setThumbnailImageUrl(updateDto.getThumbnailImageUrl());
        }

        campaign.setUpdatedBy(user.getId());

        campaign = campaignRepository.save(campaign);
        log.info("Campaign updated successfully with ID: {}", campaign.getId());

        return converterService.mapToDto(campaign);
    }

    @Operation(summary = "Delete campaign", description = "Soft delete a campaign")
    @Transactional
    @CacheEvict(value = {"campaigns", "campaign_summaries", "active_campaigns"}, allEntries = true)
    public void deleteCampaign(Long id, HttpServletRequest request) {
        log.info("Deleting campaign with ID: {}", id);

        User user = jwtService.getUser(request);
        Campaign campaign = campaignRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));

        validateUserCanManageCampaign(user, campaign);

        // Check if campaign has active usages
        if (campaignUsageRepository.existsByCampaignIdAndStatusIn(id,
                List.of(CampaignUsageStatus.PENDING, CampaignUsageStatus.VALIDATED, CampaignUsageStatus.APPROVED))) {
            throw new BusinessException("Cannot delete campaign with active usages");
        }

        campaign.setIsActive(false);
        campaign.setStatus(CampaignStatus.CANCELLED);
        campaign.setUpdatedBy(user.getId());
        campaignRepository.save(campaign);

        log.info("Campaign deleted successfully with ID: {}", id);
    }

    // ================================ SEARCH AND LISTING ================================

    @Operation(summary = "Search campaigns", description = "Search campaigns with various filters")
    public Page<CampaignDto> searchCampaigns(CampaignSearchDto searchDto, HttpServletRequest request) {
        log.info("Searching campaigns with criteria: {}", searchDto.getSearchTerm());

        User user = jwtService.getUser(request);

        Pageable pageable = PageRequest.of(
                searchDto.getPage() != null ? searchDto.getPage() : 0,
                searchDto.getSize() != null ? searchDto.getSize() : 20,
                createCampaignSort(searchDto.getSortBy(), searchDto.getSortDirection())
        );

        Page<Campaign> campaigns = campaignRepository.searchCampaigns(
                searchDto.getSearchTerm(),
                searchDto.getCampaignTypes(),
                searchDto.getDiscountTypes(),
                searchDto.getStatuses(),
                searchDto.getStartDateFrom(),
                searchDto.getStartDateTo(),
                searchDto.getEndDateFrom(),
                searchDto.getEndDateTo(),
                searchDto.getIsActive(),
                searchDto.getIsFeatured(),
                searchDto.getIsPublic(),
                searchDto.getTargetAudiences(),
                searchDto.getMinTargetAge(),
                searchDto.getMaxTargetAge(),
                searchDto.getSchoolId(),
                searchDto.getCreatedByUserId(),
                searchDto.getHasPromoCode(),
                searchDto.getMinDiscountPercentage(),
                searchDto.getMaxDiscountPercentage(),
                searchDto.getMinDiscountAmount(),
                searchDto.getMaxDiscountAmount(),
                searchDto.getMinViewCount(),
                searchDto.getMinApplicationCount(),
                searchDto.getMinConversionRate(),
                getUserAccessibleSchoolIds(user),
                pageable
        );

        return campaigns.map(converterService::mapToDto);
    }

    @Operation(summary = "Get active campaigns", description = "Get all currently active campaigns")
    @Cacheable(value = "active_campaigns")
    public List<CampaignSummaryDto> getActiveCampaigns() {
        log.info("Fetching active campaigns");

        List<Campaign> campaigns = campaignRepository.findActiveCampaigns(LocalDate.now());
        return campaigns.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get campaigns by school", description = "Get all campaigns assigned to a specific school")
    public List<CampaignDto> getCampaignsBySchool(Long schoolId, HttpServletRequest request) {
        log.info("Fetching campaigns for school: {}", schoolId);

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);

        List<Campaign> campaigns = campaignRepository.findBySchoolId(schoolId);
        return campaigns.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }

    // ================================ CAMPAIGN SCHOOL MANAGEMENT ================================

    @Operation(summary = "Assign schools to campaign", description = "Assign multiple schools to a campaign")
    @Transactional
    @CacheEvict(value = {"campaigns", "campaign_summaries"}, allEntries = true)
    public BulkCampaignResultDto assignSchoolsToCampaign(Long campaignId, List<Long> schoolIds, HttpServletRequest request) {
        log.info("Assigning {} schools to campaign: {}", schoolIds.size(), campaignId);

        User user = jwtService.getUser(request);
        return assignSchoolsToCampaign(campaignId, schoolIds, user);
    }

    @Operation(summary = "Update campaign school assignment", description = "Update campaign assignment for a specific school")
    @Transactional
    public CampaignSchoolDto updateCampaignSchoolAssignment(Long campaignId, Long schoolId,
                                                            CampaignSchoolAssignDto assignDto, HttpServletRequest request) {
        log.info("Updating campaign school assignment: campaign={}, school={}", campaignId, schoolId);

        User user = jwtService.getUser(request);

        Campaign campaign = campaignRepository.findByIdAndIsActiveTrue(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        validateUserCanManageCampaign(user, campaign);

        CampaignSchool campaignSchool = campaignSchoolRepository
                .findByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign school assignment not found"));

        // Update customizations
        campaignSchool.setCustomDiscountAmount(assignDto.getCustomDiscountAmount());
        campaignSchool.setCustomDiscountPercentage(assignDto.getCustomDiscountPercentage());
        campaignSchool.setCustomUsageLimit(assignDto.getCustomUsageLimit());
        campaignSchool.setCustomStartDate(assignDto.getCustomStartDate());
        campaignSchool.setCustomEndDate(assignDto.getCustomEndDate());
        campaignSchool.setCustomTerms(assignDto.getCustomTerms());
        campaignSchool.setPriority(assignDto.getPriority());
        campaignSchool.setIsFeaturedOnSchool(assignDto.getIsFeaturedOnSchool());
        campaignSchool.setShowOnHomepage(assignDto.getShowOnHomepage());
        campaignSchool.setShowOnPricingPage(assignDto.getShowOnPricingPage());
        campaignSchool.setUpdatedBy(user.getId());

        campaignSchool = campaignSchoolRepository.save(campaignSchool);

        return converterService.mapCampaignSchoolToDto(campaignSchool);
    }

    @Operation(summary = "Remove school from campaign", description = "Remove a school from campaign assignment")
    @Transactional
    @CacheEvict(value = {"campaigns", "campaign_summaries"}, allEntries = true)
    public void removeSchoolFromCampaign(Long campaignId, Long schoolId, HttpServletRequest request) {
        log.info("Removing school {} from campaign: {}", schoolId, campaignId);

        User user = jwtService.getUser(request);

        Campaign campaign = campaignRepository.findByIdAndIsActiveTrue(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        validateUserCanManageCampaign(user, campaign);

        CampaignSchool campaignSchool = campaignSchoolRepository
                .findByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign school assignment not found"));

        // Check if there are active usages
        if (campaignUsageRepository.existsByCampaignIdAndSchoolIdAndStatusIn(campaignId, schoolId,
                List.of(CampaignUsageStatus.PENDING, CampaignUsageStatus.VALIDATED, CampaignUsageStatus.APPROVED))) {
            throw new BusinessException("Cannot remove school with active campaign usages");
        }

        campaignSchool.setIsActive(false);
        campaignSchool.setStatus(CampaignSchoolStatus.REMOVED);
        campaignSchool.setUpdatedBy(user.getId());
        campaignSchoolRepository.save(campaignSchool);
    }

    // ================================ CAMPAIGN USAGE MANAGEMENT ================================

    @Operation(summary = "Create campaign usage", description = "Create a new campaign usage record")
    @Transactional
    public CampaignUsageDto createCampaignUsage(CampaignUsageCreateDto createDto, HttpServletRequest request) {
        log.info("Creating campaign usage for campaign: {}, school: {}", createDto.getCampaignId(), createDto.getSchoolId());

        User user = jwtService.getUser(request);

        Campaign campaign = campaignRepository.findByIdAndIsActiveTrue(createDto.getCampaignId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        School school = schoolRepository.findByIdAndIsActiveTrue(createDto.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));

        // Validate campaign is assigned to school
        if (!campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(
                createDto.getCampaignId(), createDto.getSchoolId(), CampaignSchoolStatus.ACTIVE)) {
            throw new BusinessException("Campaign is not active for this school");
        }

        // Validate campaign is currently active
        LocalDate now = LocalDate.now();
        if (campaign.getStartDate().isAfter(now) || campaign.getEndDate().isBefore(now)) {
            throw new BusinessException("Campaign is not currently active");
        }

        // Check usage limits
        validateCampaignUsageLimits(campaign, school, user, createDto.getPromoCodeUsed());

        // Calculate discount
        BigDecimal discountAmount = calculateDiscountAmount(campaign, createDto.getOriginalAmount());
        BigDecimal finalAmount = createDto.getOriginalAmount().subtract(discountAmount);

        CampaignUsage campaignUsage = new CampaignUsage();
        campaignUsage.setCampaign(campaign);
        campaignUsage.setSchool(school);
        campaignUsage.setUser(user);
        campaignUsage.setUsageDate(LocalDateTime.now());
        campaignUsage.setUsageType(createDto.getUsageType());
        campaignUsage.setStatus(CampaignUsageStatus.PENDING);
        campaignUsage.setOriginalAmount(createDto.getOriginalAmount());
        campaignUsage.setDiscountAmount(discountAmount);
        campaignUsage.setFinalAmount(finalAmount);
        campaignUsage.setPromoCodeUsed(createDto.getPromoCodeUsed());
        campaignUsage.setStudentName(createDto.getStudentName());
        campaignUsage.setStudentAge(createDto.getStudentAge());
        campaignUsage.setGradeLevel(createDto.getGradeLevel());
        campaignUsage.setEnrollmentYear(createDto.getEnrollmentYear());
        campaignUsage.setParentName(createDto.getParentName());
        campaignUsage.setParentEmail(createDto.getParentEmail());
        campaignUsage.setParentPhone(createDto.getParentPhone());
        campaignUsage.setIpAddress(createDto.getIpAddress());
        campaignUsage.setUserAgent(createDto.getUserAgent());
        campaignUsage.setReferrerUrl(createDto.getReferrerUrl());
        campaignUsage.setUtmSource(createDto.getUtmSource());
        campaignUsage.setUtmMedium(createDto.getUtmMedium());
        campaignUsage.setUtmCampaign(createDto.getUtmCampaign());
        campaignUsage.setNotes(createDto.getNotes());
        campaignUsage.setFollowUpRequired(createDto.getFollowUpRequired());
        campaignUsage.setFollowUpDate(createDto.getFollowUpDate());
        campaignUsage.setValidationCode(generateValidationCode());
        campaignUsage.setValidationExpiresAt(LocalDateTime.now().plusDays(7));
        campaignUsage.setCreatedBy(user.getId());

        campaignUsage = campaignUsageRepository.save(campaignUsage);

        // Update campaign usage count
        campaign.setUsageCount(campaign.getUsageCount() + 1);
        campaignRepository.save(campaign);

        log.info("Campaign usage created with ID: {}", campaignUsage.getId());

        return converterService.mapCampaignUsageToDto(campaignUsage);
    }

    @Operation(summary = "Get campaign usages", description = "Get all usages for a specific campaign")
    public Page<CampaignUsageDto> getCampaignUsages(Long campaignId, Pageable pageable, HttpServletRequest request) {
        log.info("Fetching campaign usages for campaign: {}", campaignId);

        User user = jwtService.getUser(request);

        Campaign campaign = campaignRepository.findByIdAndIsActiveTrue(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        validateUserCanAccessCampaign(user, campaign);

        Page<CampaignUsage> usages = campaignUsageRepository.findByCampaignIdAndIsActiveTrue(campaignId, pageable);
        return usages.map(converterService::mapCampaignUsageToDto);
    }

    // ================================ ANALYTICS AND REPORTING ================================

    @Operation(summary = "Get campaign analytics", description = "Get comprehensive analytics for a campaign")
    @Cacheable(value = "campaign_analytics", key = "#campaignId")
    public CampaignAnalyticsDto getCampaignAnalytics(Long campaignId, HttpServletRequest request) {
        log.info("Generating analytics for campaign: {}", campaignId);

        User user = jwtService.getUser(request);

        Campaign campaign = campaignRepository.findByIdAndIsActiveTrue(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        validateUserCanAccessCampaign(user, campaign);
//  ceyhun return campaignRepository.getCampaignAnalytics(campaignId);
        return null;
    }

    @Operation(summary = "Get campaign report", description = "Generate comprehensive campaign report")
    public CampaignReportDto generateCampaignReport(List<Long> campaignIds, LocalDate startDate, LocalDate endDate,
                                                    String reportType, HttpServletRequest request) {
        log.info("Generating campaign report for {} campaigns", campaignIds.size());

        User user = jwtService.getUser(request);

        // Validate user can access all campaigns
        for (Long campaignId : campaignIds) {
            Campaign campaign = campaignRepository.findByIdAndIsActiveTrue(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found: " + campaignId));
            validateUserCanAccessCampaign(user, campaign);
        }

        CampaignReportDto report = CampaignReportDto.builder()
                .reportId(UUID.randomUUID().toString())
                .generatedAt(LocalDateTime.now())
                .reportType(reportType)
                .periodStart(startDate)
                .periodEnd(endDate)
                .build();

        // Generate campaign summaries
        List<Campaign> campaigns = campaignRepository.findByIdInAndIsActiveTrue(campaignIds);
        report.setCampaigns(campaigns.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList()));

        // Calculate overall analytics  ceyhun
        /*
        CampaignAnalyticsDto overallAnalytics = campaignRepository.getOverallAnalytics(campaignIds, startDate, endDate);
        report.setOverallAnalytics(overallAnalytics);



        // Generate insights and recommendations
        report.setKeyInsights(generateKeyInsights(campaigns, overallAnalytics));
        report.setRecommendations(generateRecommendations(campaigns, overallAnalytics));
 */
        return report;
    }

    // ================================ BULK OPERATIONS ================================

    @Operation(summary = "Bulk campaign operations", description = "Perform bulk operations on multiple campaigns")
    @Transactional
    @CacheEvict(value = {"campaigns", "campaign_summaries", "active_campaigns"}, allEntries = true)
    public BulkCampaignResultDto bulkCampaignOperation(BulkCampaignOperationDto operationDto, HttpServletRequest request) {
        log.info("Performing bulk operation: {} on {} campaigns", operationDto.getOperation(), operationDto.getCampaignIds().size());

        User user = jwtService.getUser(request);

        BulkCampaignResultDto result = BulkCampaignResultDto.builder()
                .operationDate(LocalDateTime.now())
                .totalRecords(operationDto.getCampaignIds().size())
                .successfulOperations(0)
                .failedOperations(0)
                .errors(new java.util.ArrayList<>())
                .warnings(new java.util.ArrayList<>())
                .affectedCampaignIds(new java.util.ArrayList<>())
                .build();

        for (Long campaignId : operationDto.getCampaignIds()) {
            try {
                Campaign campaign = campaignRepository.findByIdAndIsActiveTrue(campaignId)
                        .orElseThrow(() -> new ResourceNotFoundException("Campaign not found: " + campaignId));

                validateUserCanManageCampaign(user, campaign);

                switch (operationDto.getOperation().toUpperCase()) {
                    case "ACTIVATE":
                        campaign.setStatus(CampaignStatus.ACTIVE);
                        break;
                    case "DEACTIVATE":
                        campaign.setStatus(CampaignStatus.PAUSED);
                        break;
                    case "UPDATE_STATUS":
                        campaign.setStatus(operationDto.getNewStatus());
                        break;
                    case "ASSIGN_SCHOOLS":
                        if (operationDto.getSchoolIds() != null) {
                            assignSchoolsToCampaign(campaignId, operationDto.getSchoolIds(), user);
                        }
                        break;
                    case "REMOVE_SCHOOLS":
                        if (operationDto.getSchoolIds() != null) {
                            for (Long schoolId : operationDto.getSchoolIds()) {
                                removeSchoolFromCampaignInternal(campaignId, schoolId, user);
                            }
                        }
                        break;
                    default:
                        throw new BusinessException("Unsupported bulk operation: " + operationDto.getOperation());
                }

                campaign.setUpdatedBy(user.getId());
                campaignRepository.save(campaign);

                result.setSuccessfulOperations(result.getSuccessfulOperations() + 1);
                result.getAffectedCampaignIds().add(campaignId);

            } catch (Exception e) {
                result.setFailedOperations(result.getFailedOperations() + 1);
                result.getErrors().add("Campaign ID " + campaignId + ": " + e.getMessage());
            }
        }

        result.setSuccess(result.getFailedOperations() == 0);
        return result;
    }

    // ================================ PUBLIC METHODS (NO AUTH REQUIRED) ================================

    @Operation(summary = "Get public active campaigns", description = "Get active campaigns visible to public")
    public List<CampaignSummaryDto> getPublicActiveCampaigns() {
        log.info("Fetching public active campaigns");

        List<Campaign> campaigns = campaignRepository.findPublicActiveCampaigns(LocalDate.now());
        return campaigns.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get public campaigns by school", description = "Get active campaigns for a specific school (public view)")
    public List<CampaignSummaryDto> getPublicCampaignsBySchool(Long schoolId) {
        log.info("Fetching public campaigns for school: {}", schoolId);

        List<Campaign> campaigns = campaignRepository.findPublicCampaignsBySchool(schoolId, LocalDate.now());
        return campaigns.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Validate promo code", description = "Validate promo code and return campaign details")
    public CampaignDto validatePromoCode(String promoCode, Long schoolId) {
        log.info("Validating promo code: {} for school: {}", promoCode, schoolId);

        Campaign campaign = campaignRepository.findByPromoCodeIgnoreCaseAndIsActiveTrue(promoCode)
                .orElseThrow(() -> new BusinessException("Invalid promo code: " + promoCode));

        // Check if campaign is currently active
        LocalDate now = LocalDate.now();
        if (campaign.getStartDate().isAfter(now) || campaign.getEndDate().isBefore(now)) {
            throw new BusinessException("Promo code is not currently valid");
        }

        // Check if campaign is assigned to the school
        if (!campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(
                campaign.getId(), schoolId, CampaignSchoolStatus.ACTIVE)) {
            throw new BusinessException("Promo code is not valid for this school");
        }

        return converterService.mapToDto(campaign);
    }

    // ================================ HELPER METHODS ================================

    private void validateUserCanCreateCampaign(User user) {
        // System admins can create campaigns
        if (hasSystemRole(user)) {
            return;
        }

        // Campus level users can create campaigns for their schools
        if (hasCampusRole(user)) {
            return;
        }

        throw new BusinessException("User does not have permission to create campaigns");
    }

    private void validateUserCanAccessCampaign(User user, Campaign campaign) {
        if (hasSystemRole(user)) {
            return;
        }

        // Check if user has access to any of the campaign's schools
        List<Long> userAccessibleSchoolIds = getUserAccessibleSchoolIds(user);
        boolean hasAccess = campaign.getCampaignSchools().stream()
                .anyMatch(cs -> userAccessibleSchoolIds.contains(cs.getSchool().getId()));

        if (!hasAccess) {
            throw new BusinessException("User does not have access to this campaign");
        }
    }

    private void validateUserCanManageCampaign(User user, Campaign campaign) {
        if (hasSystemRole(user)) {
            return;
        }

        // Creator can manage
        if (campaign.getCreatedByUser() != null && campaign.getCreatedByUser().getId().equals(user.getId())) {
            return;
        }

        // Campus managers can manage campaigns for their schools
        if (hasCampusRole(user)) {
            List<Long> userAccessibleSchoolIds = getUserAccessibleSchoolIds(user);
            boolean canManage = campaign.getCampaignSchools().stream()
                    .anyMatch(cs -> userAccessibleSchoolIds.contains(cs.getSchool().getId()));

            if (canManage) {
                return;
            }
        }

        throw new BusinessException("User does not have permission to manage this campaign");
    }

    private void validateUserCanAccessSchool(User user, Long schoolId) {
        if (hasSystemRole(user)) {
            return;
        }

        List<Long> accessibleSchoolIds = getUserAccessibleSchoolIds(user);
        if (!accessibleSchoolIds.contains(schoolId)) {
            throw new BusinessException("User does not have access to this school");
        }
    }

    private void validateCampaignDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date");
        }
    }

    private void validateCampaignUsageLimits(Campaign campaign, School school, User user, String promoCode) {
        // Check overall usage limit
        if (campaign.getUsageLimit() != null && campaign.getUsageCount() >= campaign.getUsageLimit()) {
            throw new BusinessException("Campaign usage limit exceeded");
        }

        // Check per-user limit
        if (campaign.getPerUserLimit() != null) {
            long userUsageCount = campaignUsageRepository.countByCampaignIdAndUserIdAndStatusNot(
                    campaign.getId(), user.getId(), CampaignUsageStatus.CANCELLED);
            if (userUsageCount >= campaign.getPerUserLimit()) {
                throw new BusinessException("User usage limit exceeded for this campaign");
            }
        }

        // Check per-school limit
        if (campaign.getPerSchoolLimit() != null) {
            long schoolUsageCount = campaignUsageRepository.countByCampaignIdAndSchoolIdAndStatusNot(
                    campaign.getId(), school.getId(), CampaignUsageStatus.CANCELLED);
            if (schoolUsageCount >= campaign.getPerSchoolLimit()) {
                throw new BusinessException("School usage limit exceeded for this campaign");
            }
        }

        // Validate promo code if provided
        if (StringUtils.hasText(promoCode) && !promoCode.equalsIgnoreCase(campaign.getPromoCode())) {
            throw new BusinessException("Invalid promo code");
        }
    }

    private BigDecimal calculateDiscountAmount(Campaign campaign, BigDecimal originalAmount) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (campaign.getDiscountType() == DiscountType.FIXED_AMOUNT && campaign.getDiscountAmount() != null) {
            discountAmount = campaign.getDiscountAmount();
        } else if (campaign.getDiscountType() == DiscountType.PERCENTAGE && campaign.getDiscountPercentage() != null) {
            discountAmount = originalAmount.multiply(BigDecimal.valueOf(campaign.getDiscountPercentage() / 100));

            // Apply max discount limit if set
            if (campaign.getMaxDiscountAmount() != null && discountAmount.compareTo(campaign.getMaxDiscountAmount()) > 0) {
                discountAmount = campaign.getMaxDiscountAmount();
            }
        }

        // Ensure discount doesn't exceed original amount
        if (discountAmount.compareTo(originalAmount) > 0) {
            discountAmount = originalAmount;
        }

        // Check minimum purchase amount
        if (campaign.getMinPurchaseAmount() != null && originalAmount.compareTo(campaign.getMinPurchaseAmount()) < 0) {
            throw new BusinessException("Minimum purchase amount not met for this campaign");
        }

        return discountAmount;
    }

    private BulkCampaignResultDto assignSchoolsToCampaign(Long campaignId, List<Long> schoolIds, User user) {
        Campaign campaign = campaignRepository.findByIdAndIsActiveTrue(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        BulkCampaignResultDto result = BulkCampaignResultDto.builder()
                .operationDate(LocalDateTime.now())
                .totalRecords(schoolIds.size())
                .successfulOperations(0)
                .failedOperations(0)
                .errors(new java.util.ArrayList<>())
                .affectedSchoolIds(new java.util.ArrayList<>())
                .build();

        for (Long schoolId : schoolIds) {
            try {
                School school = schoolRepository.findByIdAndIsActiveTrue(schoolId)
                        .orElseThrow(() -> new ResourceNotFoundException("School not found: " + schoolId));

                validateUserCanAccessSchool(user, schoolId);

                // Check if already assigned
                if (campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId)) {
                    result.getWarnings().add("School " + school.getName() + " is already assigned to this campaign");
                    continue;
                }

                CampaignSchool campaignSchool = new CampaignSchool();
                campaignSchool.setCampaign(campaign);
                campaignSchool.setSchool(school);
                campaignSchool.setAssignedByUser(user);
                campaignSchool.setAssignedAt(LocalDateTime.now());
                campaignSchool.setStatus(CampaignSchoolStatus.ACTIVE);
                campaignSchool.setApprovedBySchool(true);
                campaignSchool.setApprovedBySchoolAt(LocalDateTime.now());
                campaignSchool.setCreatedBy(user.getId());

                campaignSchoolRepository.save(campaignSchool);

                result.setSuccessfulOperations(result.getSuccessfulOperations() + 1);
                result.getAffectedSchoolIds().add(schoolId);

            } catch (Exception e) {
                result.setFailedOperations(result.getFailedOperations() + 1);
                result.getErrors().add("School ID " + schoolId + ": " + e.getMessage());
            }
        }

        result.setSuccess(result.getFailedOperations() == 0);
        return result;
    }

    private void removeSchoolFromCampaignInternal(Long campaignId, Long schoolId, User user) {
        CampaignSchool campaignSchool = campaignSchoolRepository
                .findByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign school assignment not found"));

        campaignSchool.setIsActive(false);
        campaignSchool.setStatus(CampaignSchoolStatus.REMOVED);
        campaignSchool.setUpdatedBy(user.getId());
        campaignSchoolRepository.save(campaignSchool);
    }

    private String generateUniqueSlug(String title) {
        if (!StringUtils.hasText(title)) {
            title = "campaign-" + System.currentTimeMillis();
        }

        String baseSlug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        String slug = baseSlug;
        int counter = 1;

        while (campaignRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        return slug;
    }

    private String generateValidationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Sort createCampaignSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        String sortField = switch (sortBy != null ? sortBy.toLowerCase() : "created_date") {
            case "start_date" -> "startDate";
            case "end_date" -> "endDate";
            case "view_count" -> "viewCount";
            case "conversion_rate" -> "conversionRate";
            case "title" -> "title";
            default -> "createdAt";
        };

        return Sort.by(direction, sortField);
    }

    private boolean hasSystemRole(User user) {
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleLevel() == RoleLevel.SYSTEM);
    }

    private boolean hasCampusRole(User user) {
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleLevel() == RoleLevel.CAMPUS);
    }

    private List<Long> getUserAccessibleSchoolIds(User user) {
        if (hasSystemRole(user)) {
            return schoolRepository.findAllActiveSchoolIds();
        }

        return user.getInstitutionAccess().stream()
                .filter(access -> access.getExpiresAt() == null || access.getExpiresAt().isAfter(LocalDateTime.now()))
                .flatMap(access -> {
                    switch (access.getAccessType()) {
                        case SCHOOL:
                            return List.of(access.getEntityId()).stream();
                        case CAMPUS:
                            return schoolRepository.findIdsByCampusId(access.getEntityId()).stream();
                        case BRAND:
                            return schoolRepository.findIdsByBrandId(access.getEntityId()).stream();
                        default:
                            return List.<Long>of().stream();
                    }
                })
                .collect(Collectors.toList());
    }

    private List<String> generateKeyInsights(List<Campaign> campaigns, CampaignAnalyticsDto analytics) {
        List<String> insights = new java.util.ArrayList<>();
/* ceyhun
        if (analytics.getConversionRate() != null) {
            if (analytics.getConversionRate() > 10.0) {
                insights.add("Excellent conversion rate of " + String.format("%.1f%%", analytics.getConversionRate()));
            } else if (analytics.getConversionRate() < 2.0) {
                insights.add("Low conversion rate of " + String.format("%.1f%%", analytics.getConversionRate()) + " needs attention");
            }
        }



        if (analytics.getTotalRevenueGenerated() != null && analytics.getTotalRevenueGenerated().compareTo(BigDecimal.ZERO) > 0) {
            insights.add("Generated " + analytics.getTotalRevenueGenerated() + " TL in total revenue");
        }
 */
        // Find best performing campaign type
        var campaignTypePerformance = campaigns.stream()
                .collect(Collectors.groupingBy(Campaign::getCampaignType,
                        Collectors.averagingDouble(c -> c.getConversionRate() != null ? c.getConversionRate() : 0.0)));

        campaignTypePerformance.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .ifPresent(entry -> insights.add(entry.getKey().name() + " campaigns show best performance"));

        return insights;
    }

    private List<String> generateRecommendations(List<Campaign> campaigns, CampaignAnalyticsDto analytics) {
        List<String> recommendations = new java.util.ArrayList<>();
/* ceyhun
        if (analytics.getConversionRate() != null && analytics.getConversionRate() < 5.0) {
            recommendations.add("Consider improving campaign targeting or offer attractiveness");
        }

        if (analytics.getClickThroughRate() != null && analytics.getClickThroughRate() < 2.0) {
            recommendations.add("Improve campaign creative and call-to-action to increase click-through rate");
        }

 */

        // Check for campaigns ending soon
        long endingSoonCount = campaigns.stream()
                .filter(c -> c.getEndDate().isBefore(LocalDate.now().plusDays(7)))
                .count();

        if (endingSoonCount > 0) {
            recommendations.add("Consider extending or creating follow-up campaigns for " + endingSoonCount + " campaigns ending soon");
        }

        return recommendations;
    }
}