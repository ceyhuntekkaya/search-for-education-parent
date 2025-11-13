package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.pricing.*;
import com.genixo.education.search.entity.pricing.*;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.repository.pricing.CustomFeeRepository;
import com.genixo.education.search.repository.pricing.PriceHistoryRepository;
import com.genixo.education.search.repository.pricing.SchoolPricingRepository;
import com.genixo.education.search.repository.user.UserRepository;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.PricingConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PricingService {

    private final SchoolPricingRepository schoolPricingRepository;
    private final CustomFeeRepository customFeeRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final SchoolRepository schoolRepository;
    private final PricingConverterService converterService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    // ================================ SCHOOL PRICING OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"school_pricing", "pricing_summaries"}, allEntries = true)
    public SchoolPricingDto createSchoolPricing(SchoolPricingCreateDto createDto, HttpServletRequest request) {


        User user = jwtService.getUser(request);

        // Validate school exists and user has access
        School school = schoolRepository.findByIdAndIsActiveTrue(createDto.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School", createDto.getSchoolId()));

        validateUserCanManageSchoolPricing(user, school.getId());

        // Check if pricing already exists for this academic year and grade
        if (schoolPricingRepository.existsBySchoolIdAndAcademicYearAndGradeLevelAndIsActiveTrue(
                createDto.getSchoolId(), createDto.getAcademicYear(), createDto.getGradeLevel())) {
            throw new BusinessException("Pricing already exists for this academic year and grade level")
                    .withErrorCode("PRICING_EXISTS");
        }

        // Validate pricing data
        validatePricingData(createDto);

        SchoolPricing pricing = new SchoolPricing();
        pricing.setSchool(school);
        pricing.setCreatedByUser(user);
        pricing.setAcademicYear(createDto.getAcademicYear());
        pricing.setGradeLevel(createDto.getGradeLevel());
        pricing.setClassLevel(createDto.getClassLevel());
        pricing.setCurrency(createDto.getCurrency() != null ? createDto.getCurrency() : Currency.TRY);

        // Set all fees
        setAllFees(pricing, createDto);

        // Calculate totals
        calculateTotals(pricing);

        pricing.setValidFrom(createDto.getValidFrom());
        pricing.setValidUntil(createDto.getValidUntil());
        pricing.setStatus(PricingStatus.DRAFT);
        pricing.setPaymentFrequency(createDto.getPaymentFrequency() != null ?
                createDto.getPaymentFrequency() : PaymentFrequency.MONTHLY);
        pricing.setInstallmentCount(createDto.getInstallmentCount());
        pricing.setDownPaymentPercentage(createDto.getDownPaymentPercentage());
        pricing.setEarlyPaymentDiscountPercentage(createDto.getEarlyPaymentDiscountPercentage());
        pricing.setSiblingDiscountPercentage(createDto.getSiblingDiscountPercentage());
        pricing.setMultiYearDiscountPercentage(createDto.getMultiYearDiscountPercentage());
        pricing.setLoyaltyDiscountPercentage(createDto.getLoyaltyDiscountPercentage());
        pricing.setNeedBasedAidAvailable(createDto.getNeedBasedAidAvailable());
        pricing.setMeritBasedAidAvailable(createDto.getMeritBasedAidAvailable());
        pricing.setRefundPolicy(createDto.getRefundPolicy());
        pricing.setPaymentTerms(createDto.getPaymentTerms());
        pricing.setLatePaymentPenaltyPercentage(createDto.getLatePaymentPenaltyPercentage());
        pricing.setCancellationFee(createDto.getCancellationFee());
        pricing.setWithdrawalRefundPercentage(createDto.getWithdrawalRefundPercentage());
        pricing.setMarketPosition(createDto.getMarketPosition());
        pricing.setPublicDescription(createDto.getPublicDescription());
        pricing.setShowDetailedBreakdown(createDto.getShowDetailedBreakdown() != null ?
                createDto.getShowDetailedBreakdown() : true);
        pricing.setShowPaymentOptions(createDto.getShowPaymentOptions() != null ?
                createDto.getShowPaymentOptions() : true);
        pricing.setVersion(1);
        pricing.setIsCurrent(true);

        pricing = schoolPricingRepository.save(pricing);

        return converterService.mapToDto(pricing);
    }

    @Cacheable(value = "school_pricing", key = "#id")
    public SchoolPricingDto getSchoolPricingById(Long id, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        SchoolPricing pricing = schoolPricingRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolPricing", id));

        validateUserCanAccessSchoolPricing(user, pricing.getSchool().getId());

        return converterService.mapToDto(pricing);
    }

    @Cacheable(value = "school_pricing", key = "#schoolId + '_' + #gradeLevel + '_' + #academicYear")
    public SchoolPricingDto getCurrentSchoolPricing(Long schoolId, String gradeLevel, String academicYear, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        validateUserCanAccessSchoolPricing(user, schoolId);

        SchoolPricing pricing = schoolPricingRepository.findCurrentPricingBySchoolAndGradeAndYear(
                        schoolId, gradeLevel, academicYear)
                .orElseThrow(() -> new ResourceNotFoundException("No current pricing found for specified criteria"));

        return converterService.mapToDto(pricing);
    }

    public List<SchoolPricingDto> getAllSchoolPricings(Long schoolId, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        validateUserCanAccessSchoolPricing(user, schoolId);

        List<SchoolPricing> pricings = schoolPricingRepository.findBySchoolIdAndIsActiveTrueOrderByCreatedAtDesc(schoolId);
        return pricings.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }


    public List<PricingSummaryDto> getAllSchoolPricingSummaries(Long schoolId, HttpServletRequest request) {

        //User user = jwtService.getUser(request);
        //validateUserCanAccessSchoolPricing(user, schoolId);

        List<SchoolPricing> pricings = schoolPricingRepository.findBySchoolIdAndIsActiveTrueOrderByCreatedAtDesc(schoolId);
        return pricings.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"school_pricing", "pricing_summaries"}, allEntries = true)
    public SchoolPricingDto updateSchoolPricing(Long id, SchoolPricingUpdateDto updateDto, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        SchoolPricing existingPricing = schoolPricingRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolPricing", id));

        validateUserCanManageSchoolPricing(user, existingPricing.getSchool().getId());

        // Create price history for significant changes
        createPriceHistoryIfNeeded(existingPricing, updateDto, user);

        // Validate updated pricing data
        validatePricingUpdateData(updateDto);

        // Update pricing
        updatePricingFields(existingPricing, updateDto, user);

        // Recalculate totals
        calculateTotals(existingPricing);

        // Increment version if major changes
        if (hasMajorPriceChanges(existingPricing, updateDto)) {
            existingPricing.setVersion(existingPricing.getVersion() + 1);
        }

        existingPricing = schoolPricingRepository.save(existingPricing);

        return converterService.mapToDto(existingPricing);
    }

    @Transactional
    @CacheEvict(value = {"school_pricing", "pricing_summaries"}, allEntries = true)
    public SchoolPricingDto approveSchoolPricing(Long id, String approvalNotes, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        SchoolPricing pricing = schoolPricingRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolPricing", id));

        validateUserCanApproveSchoolPricing(user, pricing.getSchool().getId());

        if (pricing.getStatus() != PricingStatus.PENDING_APPROVAL) {
            throw new BusinessException("Pricing is not in pending approval status")
                    .withErrorCode("INVALID_STATUS");
        }

        // Deactivate previous current pricing
        schoolPricingRepository.deactivateCurrentPricingForSchoolAndGrade(
                pricing.getSchool().getId(), pricing.getGradeLevel(), pricing.getAcademicYear(), pricing.getId(), user.getId());

        pricing.setStatus(PricingStatus.ACTIVE);
        pricing.setIsCurrent(true);
        pricing.setApprovedBy(user.getId());
        pricing.setApprovedAt(LocalDateTime.now());
        pricing.setApprovalNotes(approvalNotes);

        pricing = schoolPricingRepository.save(pricing);

        return converterService.mapToDto(pricing);
    }

    public PricingComparisonDto comparePricing(List<Long> schoolIds, String gradeLevel, String academicYear) {

        List<SchoolPricing> pricings = schoolPricingRepository.findCurrentPricingBySchoolIdsAndGradeAndYear(
                schoolIds, gradeLevel, academicYear);

        return converterService.mapToDto(pricings, gradeLevel, academicYear);
    }

    // ================================ CUSTOM FEE OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"custom_fees", "school_pricing"}, allEntries = true)
    public CustomFeeDto createCustomFee(CustomFeeCreateDto createDto, HttpServletRequest request) {

        User user = jwtService.getUser(request);

        School school = schoolRepository.findByIdAndIsActiveTrue(createDto.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("SchoolPricing", createDto.getSchoolId()));

        validateUserCanManageSchoolPricing(user, school.getId());

        // Validate fee data
        validateCustomFeeData(createDto);



        // Check if fee name already exists for this pricing
        if (customFeeRepository.existsByFeeNameIgnoreCaseAndSchoolIdAndIsActiveTrue(
                createDto.getFeeName(), createDto.getSchoolId())) {
            throw new BusinessException("Custom fee with this name already exists for this pricing")
                    .withErrorCode("FEE_NAME_EXISTS");
        }


        CustomFee customFee = new CustomFee();
        customFee.setSchool(school);
        customFee.setCreatedBy(user.getId());
        customFee.setFeeName(createDto.getFeeName());
        customFee.setFeeDescription(createDto.getFeeDescription());
        customFee.setFeeAmount(createDto.getFeeAmount());
        customFee.setFeeType(createDto.getFeeType());
        customFee.setFeeFrequency(createDto.getFeeFrequency());
        customFee.setIsMandatory(createDto.getIsMandatory() != null ? createDto.getIsMandatory() : true);
        customFee.setIsRefundable(createDto.getIsRefundable() != null ? createDto.getIsRefundable() : false);
        customFee.setAppliesToNewStudents(createDto.getAppliesToNewStudents() != null ?
                createDto.getAppliesToNewStudents() : true);
        customFee.setAppliesToExistingStudents(createDto.getAppliesToExistingStudents() != null ?
                createDto.getAppliesToExistingStudents() : true);
        customFee.setAppliesToGrades(createDto.getAppliesToGrades());
        customFee.setMinimumAge(createDto.getMinimumAge());
        customFee.setMaximumAge(createDto.getMaximumAge());
        customFee.setValidFrom(createDto.getValidFrom());
        customFee.setValidUntil(createDto.getValidUntil());
        customFee.setStatus(CustomFeeStatus.ACTIVE);
        customFee.setDueDateOffsetDays(createDto.getDueDateOffsetDays() != null ?
                createDto.getDueDateOffsetDays() : 0);
        customFee.setLateFeePercentage(createDto.getLateFeePercentage() != null ?
                createDto.getLateFeePercentage() : 0.0);
        customFee.setInstallmentAllowed(createDto.getInstallmentAllowed() != null ?
                createDto.getInstallmentAllowed() : false);
        customFee.setMaxInstallments(createDto.getMaxInstallments());
        customFee.setDiscountEligible(createDto.getDiscountEligible() != null ?
                createDto.getDiscountEligible() : true);
        customFee.setScholarshipApplicable(createDto.getScholarshipApplicable() != null ?
                createDto.getScholarshipApplicable() : true);
        customFee.setRequiresApproval(createDto.getRequiresApproval() != null ?
                createDto.getRequiresApproval() : false);
        customFee.setDocumentationRequired(createDto.getDocumentationRequired() != null ?
                createDto.getDocumentationRequired() : false);
        customFee.setRequiredDocuments(createDto.getRequiredDocuments());
        customFee.setFeePolicy(createDto.getFeePolicy());
        customFee.setDisplayOnInvoice(createDto.getDisplayOnInvoice() != null ?
                createDto.getDisplayOnInvoice() : true);
        customFee.setDisplayOrder(createDto.getDisplayOrder() != null ? createDto.getDisplayOrder() : 0);
        customFee.setParentNotificationRequired(createDto.getParentNotificationRequired() != null ?
                createDto.getParentNotificationRequired() : true);
        customFee.setAdvanceNoticeDays(createDto.getAdvanceNoticeDays() != null ?
                createDto.getAdvanceNoticeDays() : 30);

        customFee = customFeeRepository.save(customFee);

        return converterService.mapToDto(customFee);
    }

    @Cacheable(value = "custom_fees", key = "#schoolId")
    public List<CustomFeeDto> getCustomFeesBySchool(Long schoolId, HttpServletRequest request) {
        //User user = jwtService.getUser(request);
        //validateUserCanAccessSchoolPricing(user, schoolId);
        List<CustomFee> customFees = customFeeRepository.findBySchoolIdAndIsActiveTrueOrderByDisplayOrder(schoolId);
        return customFees.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"custom_fees", "school_pricing"}, allEntries = true)
    public CustomFeeDto updateCustomFee(Long id, CustomFeeCreateDto updateDto, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        CustomFee customFee = customFeeRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFee", id));

        validateUserCanManageSchoolPricing(user, customFee.getSchool().getId());

        // Check fee name uniqueness if changed
        if (!customFee.getFeeName().equalsIgnoreCase(updateDto.getFeeName()) &&
                customFeeRepository.existsByFeeNameIgnoreCaseAndSchoolIdAndIdNotAndIsActiveTrue(
                        updateDto.getFeeName(), customFee.getSchool().getId(), id)) {
            throw new BusinessException("Custom fee name already exists")
                    .withErrorCode("FEE_NAME_EXISTS");
        }

        // Update fields
        customFee.setFeeName(updateDto.getFeeName());
        customFee.setFeeDescription(updateDto.getFeeDescription());
        customFee.setFeeAmount(updateDto.getFeeAmount());
        customFee.setFeeType(updateDto.getFeeType());
        customFee.setFeeFrequency(updateDto.getFeeFrequency());
        customFee.setIsMandatory(updateDto.getIsMandatory());
        customFee.setIsRefundable(updateDto.getIsRefundable());
        customFee.setAppliesToNewStudents(updateDto.getAppliesToNewStudents());
        customFee.setAppliesToExistingStudents(updateDto.getAppliesToExistingStudents());
        customFee.setAppliesToGrades(updateDto.getAppliesToGrades());
        customFee.setMinimumAge(updateDto.getMinimumAge());
        customFee.setMaximumAge(updateDto.getMaximumAge());
        customFee.setValidFrom(updateDto.getValidFrom());
        customFee.setValidUntil(updateDto.getValidUntil());
        customFee.setStatus(updateDto.getStatus());
        customFee.setUpdatedBy(user.getId());

        customFee = customFeeRepository.save(customFee);

        return converterService.mapToDto(customFee);
    }

    @Transactional
    @CacheEvict(value = {"custom_fees", "school_pricing"}, allEntries = true)
    public void deleteCustomFee(Long id, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        CustomFee customFee = customFeeRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomFee", id));

        validateUserCanManageSchoolPricing(user, customFee.getSchool().getId());

        customFee.setIsActive(false);
        customFee.setUpdatedBy(user.getId());
        customFeeRepository.save(customFee);

    }

    // ================================ PRICE HISTORY OPERATIONS ================================

    public List<PriceHistoryDto> getPriceHistory(Long pricingId, HttpServletRequest request) {

        User user = jwtService.getUser(request);

        SchoolPricing pricing = schoolPricingRepository.findByIdAndIsActiveTrue(pricingId)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolPricing", pricingId));

        validateUserCanAccessSchoolPricing(user, pricing.getSchool().getId());

        List<PriceHistory> history = priceHistoryRepository.findBySchoolPricingIdOrderByChangeDateDesc(pricingId);
        return history.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }

    public PriceTrendsDto getSchoolPriceTrends(Long schoolId, String gradeLevel,
                                               LocalDateTime startDate, LocalDateTime endDate,
                                               HttpServletRequest request) {

        User user = jwtService.getUser(request);
        validateUserCanAccessSchoolPricing(user, schoolId);

        List<PriceHistory> history = priceHistoryRepository.findPriceTrendsForSchool(
                schoolId, gradeLevel, startDate, endDate);

        return converterService.mapToPriceTrendsDto(history, startDate, endDate);
    }

    // ================================ PRICING ANALYTICS ================================

    public PricingAnalyticsDto getPricingAnalytics(Long schoolId, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        validateUserCanAccessSchoolPricing(user, schoolId);

        return schoolPricingRepository.getPricingAnalytics(schoolId);
    }

    public MarketComparisonDto getMarketComparison(Long schoolId, String gradeLevel,
                                                   String academicYear, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        validateUserCanAccessSchoolPricing(user, schoolId);

        SchoolPricing schoolPricing = schoolPricingRepository.findCurrentPricingBySchoolAndGradeAndYear(
                        schoolId, gradeLevel, academicYear)
                .orElseThrow(() -> new ResourceNotFoundException("No current pricing found"));

        MarketAveragesDto marketAverages = schoolPricingRepository.getMarketAverages(gradeLevel, academicYear);

        return converterService.mapToMarketComparisonDto(schoolPricing, marketAverages);
    }

    // ================================ BULK OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"school_pricing", "custom_fees"}, allEntries = true)
    public BulkPricingResultDto bulkUpdatePricing(BulkPricingUpdateDto bulkDto, HttpServletRequest request) {

        User user = jwtService.getUser(request);

        BulkPricingResultDto result = BulkPricingResultDto.builder()
                .operationDate(LocalDateTime.now())
                .totalRecords(bulkDto.getPricingUpdates().size())
                .successfulOperations(0)
                .failedOperations(0)
                .errors(new java.util.ArrayList<>())
                .warnings(new java.util.ArrayList<>())
                .build();

        for (BulkPricingItemDto item : bulkDto.getPricingUpdates()) {
            try {
                SchoolPricing pricing = schoolPricingRepository.findByIdAndIsActiveTrue(item.getPricingId())
                        .orElseThrow(() -> new ResourceNotFoundException("SchoolPricing", item.getPricingId()));

                validateUserCanManageSchoolPricing(user, pricing.getSchool().getId());

                // Apply bulk updates
                applyBulkPricingUpdate(pricing, item, user);

                result.setSuccessfulOperations(result.getSuccessfulOperations() + 1);

            } catch (Exception e) {
                result.setFailedOperations(result.getFailedOperations() + 1);
                result.getErrors().add("Pricing ID " + item.getPricingId() + ": " + e.getMessage());
                log.error("Failed to update pricing ID: {}", item.getPricingId(), e);
            }
        }

        result.setSuccess(result.getFailedOperations() == 0);


        return result;
    }

    // ================================ PUBLIC METHODS (NO AUTH REQUIRED) ================================

    public SchoolPricingDto getPublicSchoolPricing(String schoolSlug, String gradeLevel, String academicYear) {

        School school = schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(schoolSlug)
                .orElseThrow(() -> new ResourceNotFoundException("School not found or not available"));

        SchoolPricing pricing = schoolPricingRepository.findCurrentPricingBySchoolAndGradeAndYear(
                        school.getId(), gradeLevel, academicYear)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing information not available"));

        // Only return public pricing information
        SchoolPricingDto pricingDto = converterService.mapToDto(pricing);

        // Remove sensitive information for public access
        sanitizePricingForPublic(pricingDto);

        return pricingDto;
    }

    public List<PricingSummaryDto> getPublicPricingSummary(String schoolSlug) {

        School school = schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(schoolSlug)
                .orElseThrow(() -> new ResourceNotFoundException("School not found or not available"));

        List<SchoolPricing> pricing = schoolPricingRepository.findCurrentPricingsBySchoolIdOrderByGradeLevel(school.getId());

        return pricing.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    // ================================ VALIDATION METHODS ================================

    private void validateUserCanManageSchoolPricing(User user, Long schoolId) {
        if (!hasSystemRole(user) && !hasAccessToSchool(user, schoolId)) {
            throw new BusinessException("User does not have permission to manage pricing for this school")
                    .withErrorCode("ACCESS_DENIED");
        }
    }

    private void validateUserCanAccessSchoolPricing(User user, Long schoolId) {
        if (!hasSystemRole(user) && !hasAccessToSchool(user, schoolId)) {
            throw new BusinessException("User does not have access to pricing for this school")
                    .withErrorCode("ACCESS_DENIED");
        }
    }

    private void validateUserCanApproveSchoolPricing(User user, Long schoolId) {
        if (!hasSystemRole(user) && !hasManagerRole(user) && !hasAccessToSchool(user, schoolId)) {
            throw new BusinessException("User does not have permission to approve pricing")
                    .withErrorCode("APPROVAL_PERMISSION_DENIED");
        }
    }

    private void validatePricingData(SchoolPricingCreateDto createDto) {
        List<String> errors = new java.util.ArrayList<>();

        // Validate required fields
        if (createDto.getValidFrom() == null) {
            errors.add("Valid from date is required");
        }
        if (createDto.getValidUntil() == null) {
            errors.add("Valid until date is required");
        }

        // Validate date range
        if (createDto.getValidFrom() != null && createDto.getValidUntil() != null) {
            if (createDto.getValidFrom().isAfter(createDto.getValidUntil())) {
                errors.add("Valid from date must be before valid until date");
            }
        }

        // Validate fees are not negative
        validatePositiveAmount(createDto.getAnnualTuition(), "Annual tuition", errors);
        validatePositiveAmount(createDto.getMonthlyTuition(), "Monthly tuition", errors);
        validatePositiveAmount(createDto.getRegistrationFee(), "Registration fee", errors);

        // Validate payment terms
        if (createDto.getInstallmentCount() != null && createDto.getInstallmentCount() < 1) {
            errors.add("Installment count must be at least 1");
        }

        if (createDto.getDownPaymentPercentage() != null &&
                (createDto.getDownPaymentPercentage() < 0 || createDto.getDownPaymentPercentage() > 100)) {
            errors.add("Down payment percentage must be between 0 and 100");
        }

        if (!errors.isEmpty()) {
            throw BusinessException.validationFailed(errors);
        }
    }

    private void validateCustomFeeData(CustomFeeCreateDto createDto) {
        List<String> errors = new java.util.ArrayList<>();

        if (!StringUtils.hasText(createDto.getFeeName())) {
            errors.add("Fee name is required");
        }

        if (createDto.getFeeAmount() == null || createDto.getFeeAmount().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Fee amount must be positive");
        }

        if (createDto.getFeeType() == null) {
            errors.add("Fee type is required");
        }

        if (createDto.getFeeFrequency() == null) {
            errors.add("Fee frequency is required");
        }

        // Validate age range
        if (createDto.getMinimumAge() != null && createDto.getMaximumAge() != null &&
                createDto.getMinimumAge() > createDto.getMaximumAge()) {
            errors.add("Minimum age cannot be greater than maximum age");
        }

        // Validate date range
        if (createDto.getValidFrom() != null && createDto.getValidUntil() != null &&
                createDto.getValidFrom().isAfter(createDto.getValidUntil())) {
            errors.add("Valid from date must be before valid until date");
        }

        if (!errors.isEmpty()) {
            throw BusinessException.validationFailed(errors);
        }
    }

    private void validatePricingUpdateData(SchoolPricingUpdateDto updateDto) {
        List<String> errors = new java.util.ArrayList<>();

        // Similar validations as create but for update
        if (updateDto.getValidFrom() != null && updateDto.getValidUntil() != null) {
            if (updateDto.getValidFrom().isAfter(updateDto.getValidUntil())) {
                errors.add("Valid from date must be before valid until date");
            }
        }

        if (!errors.isEmpty()) {
            throw BusinessException.validationFailed(errors);
        }
    }

    private void validatePositiveAmount(BigDecimal amount, String fieldName, List<String> errors) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
            errors.add(fieldName + " cannot be negative");
        }
    }

    // ================================ HELPER METHODS ================================

    private boolean hasSystemRole(User user) {
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleLevel() == RoleLevel.SYSTEM);
    }

    private boolean hasManagerRole(User user) {
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleLevel() == RoleLevel.CAMPUS ||
                        userRole.getRoleLevel() == RoleLevel.BRAND);
    }

    private boolean hasAccessToSchool(User user, Long schoolId) {
        return user.getInstitutionAccess().stream()
                .anyMatch(access -> {
                    if (access.getExpiresAt() != null && access.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }

                    switch (access.getAccessType()) {
                        case SCHOOL:
                            return access.getEntityId().equals(schoolId);
                        case CAMPUS:
                            return schoolRepository.existsByIdAndCampusId(schoolId, access.getEntityId());
                        case BRAND:
                            return schoolRepository.existsByIdAndCampusBrandId(schoolId, access.getEntityId());
                        default:
                            return false;
                    }
                });
    }

    private void setAllFees(SchoolPricing pricing, SchoolPricingCreateDto createDto) {
        pricing.setRegistrationFee(createDto.getRegistrationFee() != null ? createDto.getRegistrationFee() : BigDecimal.ZERO);
        pricing.setApplicationFee(createDto.getApplicationFee() != null ? createDto.getApplicationFee() : BigDecimal.ZERO);
        pricing.setEnrollmentFee(createDto.getEnrollmentFee() != null ? createDto.getEnrollmentFee() : BigDecimal.ZERO);
        pricing.setAnnualTuition(createDto.getAnnualTuition());
        pricing.setMonthlyTuition(createDto.getMonthlyTuition());
        pricing.setSemesterTuition(createDto.getSemesterTuition());
        pricing.setBookFee(createDto.getBookFee() != null ? createDto.getBookFee() : BigDecimal.ZERO);
        pricing.setUniformFee(createDto.getUniformFee() != null ? createDto.getUniformFee() : BigDecimal.ZERO);
        pricing.setActivityFee(createDto.getActivityFee() != null ? createDto.getActivityFee() : BigDecimal.ZERO);
        pricing.setTechnologyFee(createDto.getTechnologyFee() != null ? createDto.getTechnologyFee() : BigDecimal.ZERO);
        pricing.setLaboratoryFee(createDto.getLaboratoryFee() != null ? createDto.getLaboratoryFee() : BigDecimal.ZERO);
        pricing.setLibraryFee(createDto.getLibraryFee() != null ? createDto.getLibraryFee() : BigDecimal.ZERO);
        pricing.setSportsFee(createDto.getSportsFee() != null ? createDto.getSportsFee() : BigDecimal.ZERO);
        pricing.setArtFee(createDto.getArtFee() != null ? createDto.getArtFee() : BigDecimal.ZERO);
        pricing.setMusicFee(createDto.getMusicFee() != null ? createDto.getMusicFee() : BigDecimal.ZERO);
        pricing.setTransportationFee(createDto.getTransportationFee() != null ? createDto.getTransportationFee() : BigDecimal.ZERO);
        pricing.setCafeteriaFee(createDto.getCafeteriaFee() != null ? createDto.getCafeteriaFee() : BigDecimal.ZERO);
        pricing.setInsuranceFee(createDto.getInsuranceFee() != null ? createDto.getInsuranceFee() : BigDecimal.ZERO);
        pricing.setMaintenanceFee(createDto.getMaintenanceFee() != null ? createDto.getMaintenanceFee() : BigDecimal.ZERO);
        pricing.setSecurityFee(createDto.getSecurityFee() != null ? createDto.getSecurityFee() : BigDecimal.ZERO);
        pricing.setExamFee(createDto.getExamFee() != null ? createDto.getExamFee() : BigDecimal.ZERO);
        pricing.setGraduationFee(createDto.getGraduationFee() != null ? createDto.getGraduationFee() : BigDecimal.ZERO);
        pricing.setExtendedDayFee(createDto.getExtendedDayFee() != null ? createDto.getExtendedDayFee() : BigDecimal.ZERO);
        pricing.setTutoringFee(createDto.getTutoringFee() != null ? createDto.getTutoringFee() : BigDecimal.ZERO);
        pricing.setSummerSchoolFee(createDto.getSummerSchoolFee() != null ? createDto.getSummerSchoolFee() : BigDecimal.ZERO);
        pricing.setWinterCampFee(createDto.getWinterCampFee() != null ? createDto.getWinterCampFee() : BigDecimal.ZERO);
        pricing.setLanguageCourseFee(createDto.getLanguageCourseFee() != null ? createDto.getLanguageCourseFee() : BigDecimal.ZERO);
        pricing.setPrivateLessonFee(createDto.getPrivateLessonFee() != null ? createDto.getPrivateLessonFee() : BigDecimal.ZERO);
    }

    private void calculateTotals(SchoolPricing pricing) {
        // Calculate one-time fees
        BigDecimal oneTimeFees = BigDecimal.ZERO
                .add(pricing.getRegistrationFee())
                .add(pricing.getApplicationFee())
                .add(pricing.getEnrollmentFee())
                .add(pricing.getBookFee())
                .add(pricing.getUniformFee());

        pricing.setTotalOneTimeFees(oneTimeFees);

        // Calculate monthly recurring fees
        BigDecimal monthlyRecurring = BigDecimal.ZERO
                .add(pricing.getMonthlyTuition() != null ? pricing.getMonthlyTuition() : BigDecimal.ZERO)
                .add(pricing.getActivityFee())
                .add(pricing.getTechnologyFee())
                .add(pricing.getTransportationFee())
                .add(pricing.getCafeteriaFee());

        pricing.setTotalMonthlyCost(monthlyRecurring);

        // Calculate annual cost
        BigDecimal annualCost = pricing.getAnnualTuition() != null ?
                pricing.getAnnualTuition() : monthlyRecurring.multiply(BigDecimal.valueOf(12));
        annualCost = annualCost.add(oneTimeFees);

        pricing.setTotalAnnualCost(annualCost);

        // Calculate installment amount if applicable
        if (pricing.getInstallmentCount() != null && pricing.getInstallmentCount() > 0) {
            BigDecimal installmentBase = annualCost;
            if (pricing.getDownPaymentAmount() != null) {
                installmentBase = installmentBase.subtract(pricing.getDownPaymentAmount());
            }


            int count = pricing.getInstallmentCount(); // 12
            BigDecimal result = installmentBase.divide(
                    BigDecimal.valueOf(count),
                    2, // scale: kaç ondalık basamak istiyorsun? (ör. para için 2)
                    RoundingMode.HALF_UP
            );
            pricing.setInstallmentAmount(result);


          //  pricing.setInstallmentAmount(installmentBase.divide(BigDecimal.valueOf(pricing.getInstallmentCount())));
        }
    }

    private void createPriceHistoryIfNeeded(SchoolPricing existingPricing, SchoolPricingUpdateDto updateDto, User user) {
        // Check for significant price changes
        if (updateDto.getMonthlyTuition() != null &&
                !updateDto.getMonthlyTuition().equals(existingPricing.getMonthlyTuition())) {
            createPriceHistory(existingPricing, "monthlyTuition",
                    existingPricing.getMonthlyTuition(), updateDto.getMonthlyTuition(),
                    "Monthly tuition updated", user);
        }

        if (updateDto.getAnnualTuition() != null &&
                !updateDto.getAnnualTuition().equals(existingPricing.getAnnualTuition())) {
            createPriceHistory(existingPricing, "annualTuition",
                    existingPricing.getAnnualTuition(), updateDto.getAnnualTuition(),
                    "Annual tuition updated", user);
        }

        if (updateDto.getRegistrationFee() != null &&
                !updateDto.getRegistrationFee().equals(existingPricing.getRegistrationFee())) {
            createPriceHistory(existingPricing, "registrationFee",
                    existingPricing.getRegistrationFee(), updateDto.getRegistrationFee(),
                    "Registration fee updated", user);
        }
    }

    private void createPriceHistory(SchoolPricing pricing, String fieldName,
                                    BigDecimal oldValue, BigDecimal newValue,
                                    String reason, User user) {
        PriceHistory history = new PriceHistory();
        history.setSchoolPricing(pricing);
        history.setChangedByUser(user);
        history.setChangeDate(LocalDateTime.now());
        history.setFieldName(fieldName);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setReason(reason);

        // Calculate change metrics
        if (oldValue != null && newValue != null && oldValue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal changeAmount = newValue.subtract(oldValue);
            Double changePercentage = changeAmount.divide(oldValue, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();

            history.setChangeAmount(changeAmount);
            history.setChangePercentage(changePercentage);
            history.setChangeType(changeAmount.compareTo(BigDecimal.ZERO) > 0 ?
                    PriceChangeType.INCREASE : PriceChangeType.DECREASE);
        } else if (oldValue == null || oldValue.compareTo(BigDecimal.ZERO) == 0) {
            history.setChangeType(PriceChangeType.NEW_FEE);
        }

        history.setEffectiveDate(LocalDate.now());

        priceHistoryRepository.save(history);
    }

    private void updatePricingFields(SchoolPricing pricing, SchoolPricingUpdateDto updateDto, User user) {
        if (updateDto.getGradeLevel() != null) pricing.setGradeLevel(updateDto.getGradeLevel());
        if (updateDto.getClassLevel() != null) pricing.setClassLevel(updateDto.getClassLevel());
        if (updateDto.getCurrency() != null) pricing.setCurrency(updateDto.getCurrency());

        // Update all fees if provided
        if (updateDto.getRegistrationFee() != null) pricing.setRegistrationFee(updateDto.getRegistrationFee());
        if (updateDto.getApplicationFee() != null) pricing.setApplicationFee(updateDto.getApplicationFee());
        if (updateDto.getEnrollmentFee() != null) pricing.setEnrollmentFee(updateDto.getEnrollmentFee());
        if (updateDto.getAnnualTuition() != null) pricing.setAnnualTuition(updateDto.getAnnualTuition());
        if (updateDto.getMonthlyTuition() != null) pricing.setMonthlyTuition(updateDto.getMonthlyTuition());
        if (updateDto.getSemesterTuition() != null) pricing.setSemesterTuition(updateDto.getSemesterTuition());

        // Update additional fees
        if (updateDto.getBookFee() != null) pricing.setBookFee(updateDto.getBookFee());
        if (updateDto.getUniformFee() != null) pricing.setUniformFee(updateDto.getUniformFee());
        if (updateDto.getActivityFee() != null) pricing.setActivityFee(updateDto.getActivityFee());
        if (updateDto.getTechnologyFee() != null) pricing.setTechnologyFee(updateDto.getTechnologyFee());
        if (updateDto.getTransportationFee() != null) pricing.setTransportationFee(updateDto.getTransportationFee());

        // Update payment terms
        if (updateDto.getPaymentFrequency() != null) pricing.setPaymentFrequency(updateDto.getPaymentFrequency());
        if (updateDto.getInstallmentCount() != null) pricing.setInstallmentCount(updateDto.getInstallmentCount());
        if (updateDto.getDownPaymentPercentage() != null) pricing.setDownPaymentPercentage(updateDto.getDownPaymentPercentage());

        // Update discount policies
        if (updateDto.getEarlyPaymentDiscountPercentage() != null)
            pricing.setEarlyPaymentDiscountPercentage(updateDto.getEarlyPaymentDiscountPercentage());
        if (updateDto.getSiblingDiscountPercentage() != null)
            pricing.setSiblingDiscountPercentage(updateDto.getSiblingDiscountPercentage());

        // Update validity
        if (updateDto.getValidFrom() != null) pricing.setValidFrom(updateDto.getValidFrom());
        if (updateDto.getValidUntil() != null) pricing.setValidUntil(updateDto.getValidUntil());

        // Update policies
        if (updateDto.getRefundPolicy() != null) pricing.setRefundPolicy(updateDto.getRefundPolicy());
        if (updateDto.getPaymentTerms() != null) pricing.setPaymentTerms(updateDto.getPaymentTerms());
        if (updateDto.getPublicDescription() != null) pricing.setPublicDescription(updateDto.getPublicDescription());

        pricing.setUpdatedBy(user.getId());
    }

    private boolean hasMajorPriceChanges(SchoolPricing existing, SchoolPricingUpdateDto update) {
        // Consider major changes as > 10% change in main tuition fees
        if (update.getMonthlyTuition() != null && existing.getMonthlyTuition() != null) {
            BigDecimal changePercentage = update.getMonthlyTuition()
                    .subtract(existing.getMonthlyTuition())
                    .divide(existing.getMonthlyTuition(), 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            return Math.abs(changePercentage.doubleValue()) > 10.0;
        }

        if (update.getAnnualTuition() != null && existing.getAnnualTuition() != null) {
            BigDecimal changePercentage = update.getAnnualTuition()
                    .subtract(existing.getAnnualTuition())
                    .divide(existing.getAnnualTuition(), 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            return Math.abs(changePercentage.doubleValue()) > 10.0;
        }

        return false;
    }

    private void applyBulkPricingUpdate(SchoolPricing pricing, BulkPricingItemDto item, User user) {
        if (item.getUpdateType().equals("PERCENTAGE_INCREASE")) {
            BigDecimal multiplier = BigDecimal.ONE.add(BigDecimal.valueOf(item.getPercentage() / 100.0));

            if (pricing.getMonthlyTuition() != null) {
                BigDecimal oldValue = pricing.getMonthlyTuition();
                BigDecimal newValue = oldValue.multiply(multiplier);
                pricing.setMonthlyTuition(newValue);

                createPriceHistory(pricing, "monthlyTuition", oldValue, newValue,
                        "Bulk percentage increase: " + item.getPercentage() + "%", user);
            }

            if (pricing.getAnnualTuition() != null) {
                BigDecimal oldValue = pricing.getAnnualTuition();
                BigDecimal newValue = oldValue.multiply(multiplier);
                pricing.setAnnualTuition(newValue);

                createPriceHistory(pricing, "annualTuition", oldValue, newValue,
                        "Bulk percentage increase: " + item.getPercentage() + "%", user);
            }
        } else if (item.getUpdateType().equals("FIXED_AMOUNT_INCREASE")) {
            BigDecimal increment = BigDecimal.valueOf(item.getAmount());

            if (pricing.getMonthlyTuition() != null) {
                BigDecimal oldValue = pricing.getMonthlyTuition();
                BigDecimal newValue = oldValue.add(increment);
                pricing.setMonthlyTuition(newValue);

                createPriceHistory(pricing, "monthlyTuition", oldValue, newValue,
                        "Bulk fixed amount increase: " + item.getAmount(), user);
            }
        }

        // Recalculate totals
        calculateTotals(pricing);
        pricing.setUpdatedBy(user.getId());
        schoolPricingRepository.save(pricing);
    }

    private void sanitizePricingForPublic(SchoolPricingDto pricingDto) {
        // Remove sensitive internal information for public access
        pricingDto.setInternalNotes(null);
        pricingDto.setCompetitorAnalysis(null);
        pricingDto.setApprovalNotes(null);
        pricingDto.setFeeBreakdownNotes(null);
    }

    // ================================ PRICING CALCULATION METHODS ================================

    public PricingCalculationDto calculateTotalCost(PricingCalculationRequestDto calculationRequest) {

        SchoolPricing pricing = schoolPricingRepository.findCurrentPricingBySchoolAndGradeAndYear(
                        calculationRequest.getSchoolId(),
                        calculationRequest.getGradeLevel(),
                        calculationRequest.getAcademicYear())
                .orElseThrow(() -> new ResourceNotFoundException("Pricing not found for specified criteria"));

        PricingCalculationDto calculation = new PricingCalculationDto();
        calculation.setSchoolId(calculationRequest.getSchoolId());
        calculation.setGradeLevel(calculationRequest.getGradeLevel());
        calculation.setAcademicYear(calculationRequest.getAcademicYear());

        // Base costs
        BigDecimal baseTuition = pricing.getAnnualTuition() != null ?
                pricing.getAnnualTuition() :
                (pricing.getMonthlyTuition() != null ? pricing.getMonthlyTuition().multiply(BigDecimal.valueOf(12)) : BigDecimal.ZERO);

        calculation.setBaseTuition(baseTuition);
        calculation.setOneTimeFees(pricing.getTotalOneTimeFees());

        // Apply discounts
        BigDecimal totalDiscounts = BigDecimal.ZERO;

        if (calculationRequest.getHasSibling() && pricing.getSiblingDiscountPercentage() != null) {
            BigDecimal siblingDiscount = baseTuition.multiply(BigDecimal.valueOf(pricing.getSiblingDiscountPercentage() / 100));
            totalDiscounts = totalDiscounts.add(siblingDiscount);
            calculation.setSiblingDiscount(siblingDiscount);
        }

        if (calculationRequest.getEarlyPayment() && pricing.getEarlyPaymentDiscountPercentage() != null) {
            BigDecimal earlyPaymentDiscount = baseTuition.multiply(BigDecimal.valueOf(pricing.getEarlyPaymentDiscountPercentage() / 100));
            totalDiscounts = totalDiscounts.add(earlyPaymentDiscount);
            calculation.setEarlyPaymentDiscount(earlyPaymentDiscount);
        }

        calculation.setTotalDiscounts(totalDiscounts);
        calculation.setFinalAmount(baseTuition.add(pricing.getTotalOneTimeFees()).subtract(totalDiscounts));

        // Calculate installment plan if requested
        if (pricing.getInstallmentCount() != null) {
            calculateInstallmentPlan(calculation, pricing);
        }

        return calculation;
    }

    private void calculateInstallmentPlan(PricingCalculationDto calculation, SchoolPricing pricing) {
        BigDecimal totalAmount = calculation.getFinalAmount();
        BigDecimal downPayment = BigDecimal.ZERO;

        if (pricing.getDownPaymentPercentage() != null) {
            downPayment = totalAmount.multiply(BigDecimal.valueOf(pricing.getDownPaymentPercentage() / 100));
            calculation.setDownPayment(downPayment);
        }

        BigDecimal remainingAmount = totalAmount.subtract(downPayment);
        BigDecimal installmentAmount = remainingAmount.divide(BigDecimal.valueOf(pricing.getInstallmentCount()),
                2, java.math.RoundingMode.HALF_UP);

        calculation.setInstallmentCount(pricing.getInstallmentCount());
        calculation.setInstallmentAmount(installmentAmount);
    }

    // ================================ REPORTING METHODS ================================

    public PricingReportDto generatePricingReport(PricingReportRequestDto reportRequest, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        validateUserCanAccessPricingReports(user);

        List<SchoolPricing> pricings = schoolPricingRepository.findPricingsForReport(
                reportRequest.getSchoolIds(),
                reportRequest.getGradeLevels(),
                reportRequest.getAcademicYears(),
                reportRequest.getStartDate(),
                reportRequest.getEndDate());

        return converterService.mapToPricingReportDto(pricings, reportRequest);
    }

    public byte[] exportPricingData(PricingExportRequestDto exportRequest, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        validateUserCanExportPricingData(user);

        List<SchoolPricing> pricings = schoolPricingRepository.findPricingsForExport(
                exportRequest.getSchoolIds(),
                exportRequest.getIncludeInactive());

        return converterService.exportPricingData(pricings, exportRequest.getFormat());
    }

    // ================================ ANALYTICS METHODS ================================

    @Cacheable(value = "pricing_analytics", key = "#schoolId + '_' + #period")
    public PriceTrendsDto getPricingTrends(Long schoolId, String period, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        validateUserCanAccessSchoolPricing(user, schoolId);

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = switch (period.toLowerCase()) {
            case "1year" -> endDate.minusYears(1);
            case "2years" -> endDate.minusYears(2);
            case "5years" -> endDate.minusYears(5);
            default -> endDate.minusYears(3);
        };

        List<PriceHistory> history = priceHistoryRepository.findPriceTrendsForSchool(schoolId, null, startDate, endDate);
        return converterService.mapToPricingTrendsDto(history, startDate, endDate);
    }

    // ================================ UTILITY METHODS ================================

    private void validateUserCanAccessPricingReports(User user) {
        if (!hasSystemRole(user) && !hasManagerRole(user)) {
            throw new BusinessException("User does not have permission to access pricing reports")
                    .withErrorCode("REPORT_ACCESS_DENIED");
        }
    }

    private void validateUserCanExportPricingData(User user) {
        if (!hasSystemRole(user) && !hasManagerRole(user)) {
            throw new BusinessException("User does not have permission to export pricing data")
                    .withErrorCode("EXPORT_ACCESS_DENIED");
        }
    }

    public CustomFeeDto getCustomFeesById(Long feeId, HttpServletRequest request) {
        CustomFee customFee = customFeeRepository.findById(feeId).orElse(null);
        return converterService.mapToDto(customFee);
    }
}