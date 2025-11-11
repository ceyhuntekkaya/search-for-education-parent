package com.genixo.education.search.service.converter;


import com.genixo.education.search.dto.pricing.*;
import com.genixo.education.search.entity.pricing.CustomFee;
import com.genixo.education.search.entity.pricing.PriceHistory;
import com.genixo.education.search.entity.pricing.SchoolPricing;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.enumaration.Currency;
import com.genixo.education.search.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingConverterService {

    private final InstitutionConverterService institutionConverterService;
    private final UserConverterService userConverterService;


    public SchoolPricingDto mapToDto(SchoolPricing entity) {
        if (entity == null) {
            return null;
        }

        return SchoolPricingDto.builder()
                .id(entity.getId())
                .schoolId(entity.getSchool() != null ? entity.getSchool().getId() : null)
                .schoolName(entity.getSchool() != null ? entity.getSchool().getName() : null)
                .academicYear(entity.getAcademicYear())
                .gradeLevel(entity.getGradeLevel())
                .classLevel(entity.getClassLevel())
                .currency(entity.getCurrency())

                // Registration fees
                .registrationFee(entity.getRegistrationFee())
                .applicationFee(entity.getApplicationFee())
                .enrollmentFee(entity.getEnrollmentFee())

                // Tuition fees
                .annualTuition(entity.getAnnualTuition())
                .monthlyTuition(entity.getMonthlyTuition())
                .semesterTuition(entity.getSemesterTuition())

                // Additional fees
                .bookFee(entity.getBookFee())
                .uniformFee(entity.getUniformFee())
                .activityFee(entity.getActivityFee())
                .technologyFee(entity.getTechnologyFee())
                .laboratoryFee(entity.getLaboratoryFee())
                .libraryFee(entity.getLibraryFee())
                .sportsFee(entity.getSportsFee())
                .artFee(entity.getArtFee())
                .musicFee(entity.getMusicFee())
                .transportationFee(entity.getTransportationFee())
                .cafeteriaFee(entity.getCafeteriaFee())
                .insuranceFee(entity.getInsuranceFee())
                .maintenanceFee(entity.getMaintenanceFee())
                .securityFee(entity.getSecurityFee())
                .examFee(entity.getExamFee())
                .graduationFee(entity.getGraduationFee())

                // Optional services
                .extendedDayFee(entity.getExtendedDayFee())
                .tutoringFee(entity.getTutoringFee())
                .summerSchoolFee(entity.getSummerSchoolFee())
                .winterCampFee(entity.getWinterCampFee())
                .languageCourseFee(entity.getLanguageCourseFee())
                .privateLessonFee(entity.getPrivateLessonFee())

                // Calculated totals
                .totalAnnualCost(calculateTotalAnnualCost(entity))
                .totalMonthlyCost(calculateTotalMonthlyCost(entity))
                .totalOneTimeFees(calculateTotalOneTimeFees(entity))

                // Payment terms
                .paymentFrequency(entity.getPaymentFrequency())
                .installmentCount(entity.getInstallmentCount())
                .installmentAmount(calculateInstallmentAmount(entity))
                .downPaymentPercentage(entity.getDownPaymentPercentage())
                .downPaymentAmount(calculateDownPaymentAmount(entity))

                // Discount policies
                .earlyPaymentDiscountPercentage(entity.getEarlyPaymentDiscountPercentage())
                .siblingDiscountPercentage(entity.getSiblingDiscountPercentage())
                .multiYearDiscountPercentage(entity.getMultiYearDiscountPercentage())
                .loyaltyDiscountPercentage(entity.getLoyaltyDiscountPercentage())
                .needBasedAidAvailable(entity.getNeedBasedAidAvailable())
                .meritBasedAidAvailable(entity.getMeritBasedAidAvailable())

                // Validity period
                .validFrom(entity.getValidFrom())
                .validUntil(entity.getValidUntil())
                .status(entity.getStatus())

                // Policies
                .refundPolicy(entity.getRefundPolicy())
                .paymentTerms(entity.getPaymentTerms())
                .latePaymentPenaltyPercentage(entity.getLatePaymentPenaltyPercentage())
                .cancellationFee(entity.getCancellationFee())
                .withdrawalRefundPercentage(entity.getWithdrawalRefundPercentage())

                // Additional info
                .publicDescription(entity.getPublicDescription())
                .feeBreakdownNotes(entity.getFeeBreakdownNotes())
                .marketPosition(entity.getMarketPosition())

                // Display preferences
                .showDetailedBreakdown(entity.getShowDetailedBreakdown())
                .highlightTotalCost(entity.getHighlightTotalCost())
                .showPaymentOptions(entity.getShowPaymentOptions())
                .showFinancialAidInfo(entity.getShowFinancialAidInfo())

                // Metadata
                .version(entity.getVersion())
                .isCurrent(entity.getIsCurrent())
                .createdByUserName(entity.getCreatedByUser() != null ?
                        entity.getCreatedByUser().getFirstName() + " " + entity.getCreatedByUser().getLastName() : null)

                // Formatted values for display
                .formattedAnnualTuition(ConversionUtils.formatPrice(entity.getAnnualTuition(), entity.getCurrency()))
                .formattedMonthlyTuition(ConversionUtils.formatPrice(entity.getMonthlyTuition(), entity.getCurrency()))
                .formattedTotalCost(ConversionUtils.formatPrice(calculateTotalAnnualCost(entity), entity.getCurrency()))
                .ageRange(formatAgeRange(entity))


                // Base entity fields
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public PricingSummaryDto mapToSummaryDto(SchoolPricing entity) {
        if (entity == null) {
            return null;
        }

        BigDecimal totalAnnualCost = calculateTotalAnnualCost(entity);

        return PricingSummaryDto.builder()
                .schoolId(entity.getSchool() != null ? entity.getSchool().getId() : null)
                .schoolName(entity.getSchool() != null ? entity.getSchool().getName() : null)
                .academicYear(entity.getAcademicYear())
                .gradeLevel(entity.getGradeLevel())
                .monthlyTuition(entity.getMonthlyTuition())
                .annualTuition(entity.getAnnualTuition())
                .totalAnnualCost(totalAnnualCost)
                .currency(entity.getCurrency())
                .hasTransportation(hasTransportationFee(entity))
                .hasCafeteria(hasCafeteriaFee(entity))
                .hasExtendedDay(hasExtendedDayFee(entity))
                .hasFinancialAid(hasFinancialAid(entity))
                .paymentFrequency(entity.getPaymentFrequency())
                .installmentCount(entity.getInstallmentCount())
                .formattedMonthlyFee(ConversionUtils.formatPrice(entity.getMonthlyTuition(), entity.getCurrency()))
                .formattedAnnualFee(ConversionUtils.formatPrice(entity.getAnnualTuition(), entity.getCurrency()))
                .marketPosition(entity.getMarketPosition())
                .build();
    }

    public SchoolPricing mapToEntity(SchoolPricingCreateDto dto, School school, User createdByUser) {
        if (dto == null) {
            return null;
        }

        SchoolPricing entity = new SchoolPricing();
        entity.setSchool(school);
        entity.setCreatedByUser(createdByUser);
        entity.setAcademicYear(dto.getAcademicYear());
        entity.setGradeLevel(dto.getGradeLevel());
        entity.setClassLevel(dto.getClassLevel());
        entity.setCurrency(ConversionUtils.defaultIfNull(dto.getCurrency(), Currency.TRY));

        // Registration fees
        entity.setRegistrationFee(ConversionUtils.defaultIfNull(dto.getRegistrationFee(), BigDecimal.ZERO));
        entity.setApplicationFee(ConversionUtils.defaultIfNull(dto.getApplicationFee(), BigDecimal.ZERO));
        entity.setEnrollmentFee(ConversionUtils.defaultIfNull(dto.getEnrollmentFee(), BigDecimal.ZERO));

        // Tuition fees
        entity.setAnnualTuition(dto.getAnnualTuition());
        entity.setMonthlyTuition(dto.getMonthlyTuition());
        entity.setSemesterTuition(dto.getSemesterTuition());

        // Additional fees
        entity.setBookFee(ConversionUtils.defaultIfNull(dto.getBookFee(), BigDecimal.ZERO));
        entity.setUniformFee(ConversionUtils.defaultIfNull(dto.getUniformFee(), BigDecimal.ZERO));
        entity.setActivityFee(ConversionUtils.defaultIfNull(dto.getActivityFee(), BigDecimal.ZERO));
        entity.setTechnologyFee(ConversionUtils.defaultIfNull(dto.getTechnologyFee(), BigDecimal.ZERO));
        entity.setLaboratoryFee(ConversionUtils.defaultIfNull(dto.getLaboratoryFee(), BigDecimal.ZERO));
        entity.setLibraryFee(ConversionUtils.defaultIfNull(dto.getLibraryFee(), BigDecimal.ZERO));
        entity.setSportsFee(ConversionUtils.defaultIfNull(dto.getSportsFee(), BigDecimal.ZERO));
        entity.setArtFee(ConversionUtils.defaultIfNull(dto.getArtFee(), BigDecimal.ZERO));
        entity.setMusicFee(ConversionUtils.defaultIfNull(dto.getMusicFee(), BigDecimal.ZERO));
        entity.setTransportationFee(ConversionUtils.defaultIfNull(dto.getTransportationFee(), BigDecimal.ZERO));
        entity.setCafeteriaFee(ConversionUtils.defaultIfNull(dto.getCafeteriaFee(), BigDecimal.ZERO));
        entity.setInsuranceFee(ConversionUtils.defaultIfNull(dto.getInsuranceFee(), BigDecimal.ZERO));
        entity.setMaintenanceFee(ConversionUtils.defaultIfNull(dto.getMaintenanceFee(), BigDecimal.ZERO));
        entity.setSecurityFee(ConversionUtils.defaultIfNull(dto.getSecurityFee(), BigDecimal.ZERO));
        entity.setExamFee(ConversionUtils.defaultIfNull(dto.getExamFee(), BigDecimal.ZERO));
        entity.setGraduationFee(ConversionUtils.defaultIfNull(dto.getGraduationFee(), BigDecimal.ZERO));

        // Optional services
        entity.setExtendedDayFee(ConversionUtils.defaultIfNull(dto.getExtendedDayFee(), BigDecimal.ZERO));
        entity.setTutoringFee(ConversionUtils.defaultIfNull(dto.getTutoringFee(), BigDecimal.ZERO));
        entity.setSummerSchoolFee(ConversionUtils.defaultIfNull(dto.getSummerSchoolFee(), BigDecimal.ZERO));
        entity.setWinterCampFee(ConversionUtils.defaultIfNull(dto.getWinterCampFee(), BigDecimal.ZERO));
        entity.setLanguageCourseFee(ConversionUtils.defaultIfNull(dto.getLanguageCourseFee(), BigDecimal.ZERO));
        entity.setPrivateLessonFee(ConversionUtils.defaultIfNull(dto.getPrivateLessonFee(), BigDecimal.ZERO));

        // Payment terms
        entity.setPaymentFrequency(dto.getPaymentFrequency());
        entity.setInstallmentCount(dto.getInstallmentCount());
        entity.setDownPaymentPercentage(dto.getDownPaymentPercentage());

        // Discount policies
        entity.setEarlyPaymentDiscountPercentage(ConversionUtils.defaultIfNull(dto.getEarlyPaymentDiscountPercentage(), 0.0));
        entity.setSiblingDiscountPercentage(ConversionUtils.defaultIfNull(dto.getSiblingDiscountPercentage(), 0.0));
        entity.setMultiYearDiscountPercentage(ConversionUtils.defaultIfNull(dto.getMultiYearDiscountPercentage(), 0.0));
        entity.setLoyaltyDiscountPercentage(ConversionUtils.defaultIfNull(dto.getLoyaltyDiscountPercentage(), 0.0));
        entity.setNeedBasedAidAvailable(ConversionUtils.defaultIfNull(dto.getNeedBasedAidAvailable(), false));
        entity.setMeritBasedAidAvailable(ConversionUtils.defaultIfNull(dto.getMeritBasedAidAvailable(), false));

        // Validity period
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidUntil(dto.getValidUntil());

        // Policies
        entity.setRefundPolicy(dto.getRefundPolicy());
        entity.setPaymentTerms(dto.getPaymentTerms());
        entity.setLatePaymentPenaltyPercentage(ConversionUtils.defaultIfNull(dto.getLatePaymentPenaltyPercentage(), 0.0));
        entity.setCancellationFee(ConversionUtils.defaultIfNull(dto.getCancellationFee(), BigDecimal.ZERO));
        entity.setWithdrawalRefundPercentage(ConversionUtils.defaultIfNull(dto.getWithdrawalRefundPercentage(), 0.0));

        // Additional info
        entity.setInternalNotes(dto.getInternalNotes());
        entity.setPublicDescription(dto.getPublicDescription());
        entity.setFeeBreakdownNotes(dto.getFeeBreakdownNotes());
        entity.setMarketPosition(dto.getMarketPosition());

        // Display preferences
        entity.setShowDetailedBreakdown(ConversionUtils.defaultIfNull(dto.getShowDetailedBreakdown(), true));
        entity.setHighlightTotalCost(ConversionUtils.defaultIfNull(dto.getHighlightTotalCost(), true));
        entity.setShowPaymentOptions(ConversionUtils.defaultIfNull(dto.getShowPaymentOptions(), true));
        entity.setShowFinancialAidInfo(ConversionUtils.defaultIfNull(dto.getShowFinancialAidInfo(), false));

        // Calculate totals
        calculateAndSetTotals(entity);

        return entity;
    }

    public void updateEntity(SchoolPricingUpdateDto dto, SchoolPricing entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Basic fees
        if (dto.getRegistrationFee() != null) entity.setRegistrationFee(dto.getRegistrationFee());
        if (dto.getApplicationFee() != null) entity.setApplicationFee(dto.getApplicationFee());
        if (dto.getEnrollmentFee() != null) entity.setEnrollmentFee(dto.getEnrollmentFee());
        if (dto.getAnnualTuition() != null) entity.setAnnualTuition(dto.getAnnualTuition());
        if (dto.getMonthlyTuition() != null) entity.setMonthlyTuition(dto.getMonthlyTuition());
        if (dto.getSemesterTuition() != null) entity.setSemesterTuition(dto.getSemesterTuition());

        // Additional fees
        if (dto.getBookFee() != null) entity.setBookFee(dto.getBookFee());
        if (dto.getUniformFee() != null) entity.setUniformFee(dto.getUniformFee());
        if (dto.getActivityFee() != null) entity.setActivityFee(dto.getActivityFee());
        if (dto.getTechnologyFee() != null) entity.setTechnologyFee(dto.getTechnologyFee());
        if (dto.getTransportationFee() != null) entity.setTransportationFee(dto.getTransportationFee());
        if (dto.getCafeteriaFee() != null) entity.setCafeteriaFee(dto.getCafeteriaFee());

        // Payment terms
        if (dto.getPaymentFrequency() != null) entity.setPaymentFrequency(dto.getPaymentFrequency());
        if (dto.getInstallmentCount() != null) entity.setInstallmentCount(dto.getInstallmentCount());
        if (dto.getDownPaymentPercentage() != null) entity.setDownPaymentPercentage(dto.getDownPaymentPercentage());

        // Discount policies
        if (dto.getEarlyPaymentDiscountPercentage() != null)
            entity.setEarlyPaymentDiscountPercentage(dto.getEarlyPaymentDiscountPercentage());
        if (dto.getSiblingDiscountPercentage() != null)
            entity.setSiblingDiscountPercentage(dto.getSiblingDiscountPercentage());
        if (dto.getMultiYearDiscountPercentage() != null)
            entity.setMultiYearDiscountPercentage(dto.getMultiYearDiscountPercentage());

        // Validity period
        if (dto.getValidFrom() != null) entity.setValidFrom(dto.getValidFrom());
        if (dto.getValidUntil() != null) entity.setValidUntil(dto.getValidUntil());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());

        // Policies
        if (dto.getRefundPolicy() != null) entity.setRefundPolicy(dto.getRefundPolicy());
        if (dto.getPaymentTerms() != null) entity.setPaymentTerms(dto.getPaymentTerms());
        if (dto.getLatePaymentPenaltyPercentage() != null)
            entity.setLatePaymentPenaltyPercentage(dto.getLatePaymentPenaltyPercentage());

        // Display preferences
        if (dto.getShowDetailedBreakdown() != null) entity.setShowDetailedBreakdown(dto.getShowDetailedBreakdown());
        if (dto.getShowPaymentOptions() != null) entity.setShowPaymentOptions(dto.getShowPaymentOptions());
        if (dto.getShowFinancialAidInfo() != null) entity.setShowFinancialAidInfo(dto.getShowFinancialAidInfo());

        // Additional info
        if (dto.getInternalNotes() != null) entity.setInternalNotes(dto.getInternalNotes());
        if (dto.getPublicDescription() != null) entity.setPublicDescription(dto.getPublicDescription());

        // Recalculate totals
        calculateAndSetTotals(entity);
    }

    // ================== CUSTOM FEE CONVERSIONS ==================

    public CustomFeeDto mapToDto(CustomFee entity) {
        if (entity == null) {
            return null;
        }

        return CustomFeeDto.builder()
                .id(entity.getId())
                .schoolId(entity.getSchool() != null ? entity.getSchool().getId() : null)
                .schoolName(entity.getSchool() != null ?
                        entity.getSchool().getName() : null)
                .feeName(entity.getFeeName())
                .feeDescription(entity.getFeeDescription())
                .feeAmount(entity.getFeeAmount())
                .feeType(entity.getFeeType())
                .feeFrequency(entity.getFeeFrequency())
                .isMandatory(entity.getIsMandatory())
                .isRefundable(entity.getIsRefundable())
                .appliesToNewStudents(entity.getAppliesToNewStudents())
                .appliesToExistingStudents(entity.getAppliesToExistingStudents())
                .appliesToGrades(entity.getAppliesToGrades())
                .minimumAge(entity.getMinimumAge())
                .maximumAge(entity.getMaximumAge())
                .validFrom(entity.getValidFrom())
                .validUntil(entity.getValidUntil())
                .status(entity.getStatus())
                .dueDateOffsetDays(entity.getDueDateOffsetDays())
                .lateFeePercentage(entity.getLateFeePercentage())
                .installmentAllowed(entity.getInstallmentAllowed())
                .maxInstallments(entity.getMaxInstallments())
                .discountEligible(entity.getDiscountEligible())
                .scholarshipApplicable(entity.getScholarshipApplicable())
                .documentationRequired(entity.getDocumentationRequired())
                .requiredDocuments(entity.getRequiredDocuments())
                .feePolicy(entity.getFeePolicy())
                .displayOnInvoice(entity.getDisplayOnInvoice())
                .displayOrder(entity.getDisplayOrder())
                .parentNotificationRequired(entity.getParentNotificationRequired())
                .advanceNoticeDays(entity.getAdvanceNoticeDays())
                .collectionRate(entity.getCollectionRate())
                .totalCollected(entity.getTotalCollected())
                .studentsCharged(entity.getStudentsCharged())
                .studentsPaid(entity.getStudentsPaid())
                .averagePaymentDelayDays(entity.getAveragePaymentDelayDays())
                .formattedFeeAmount(ConversionUtils.formatPrice(entity.getFeeAmount()))
                .frequencyDisplayName(ConversionUtils.getDisplayName(entity.getFeeFrequency()))
                .applicabilityDescription(buildApplicabilityDescription(entity))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CustomFee mapToEntity(CustomFeeCreateDto dto, School school, User createdByUser) {
        if (dto == null) {
            return null;
        }

        CustomFee entity = new CustomFee();
        entity.setSchool(school);
        entity.setFeeName(dto.getFeeName());
        entity.setFeeDescription(dto.getFeeDescription());
        entity.setFeeAmount(dto.getFeeAmount());
        entity.setFeeType(dto.getFeeType());
        entity.setFeeFrequency(dto.getFeeFrequency());
        entity.setIsMandatory(ConversionUtils.defaultIfNull(dto.getIsMandatory(), true));
        entity.setIsRefundable(ConversionUtils.defaultIfNull(dto.getIsRefundable(), false));
        entity.setAppliesToNewStudents(ConversionUtils.defaultIfNull(dto.getAppliesToNewStudents(), true));
        entity.setAppliesToExistingStudents(ConversionUtils.defaultIfNull(dto.getAppliesToExistingStudents(), true));
        entity.setAppliesToGrades(dto.getAppliesToGrades());
        entity.setMinimumAge(dto.getMinimumAge());
        entity.setMaximumAge(dto.getMaximumAge());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidUntil(dto.getValidUntil());
        entity.setDueDateOffsetDays(ConversionUtils.defaultIfNull(dto.getDueDateOffsetDays(), 0));
        entity.setLateFeePercentage(ConversionUtils.defaultIfNull(dto.getLateFeePercentage(), 0.0));
        entity.setInstallmentAllowed(ConversionUtils.defaultIfNull(dto.getInstallmentAllowed(), false));
        entity.setMaxInstallments(dto.getMaxInstallments());
        entity.setDiscountEligible(ConversionUtils.defaultIfNull(dto.getDiscountEligible(), true));
        entity.setScholarshipApplicable(ConversionUtils.defaultIfNull(dto.getScholarshipApplicable(), true));
        entity.setDocumentationRequired(ConversionUtils.defaultIfNull(dto.getDocumentationRequired(), false));
        entity.setRequiredDocuments(dto.getRequiredDocuments());
        entity.setFeePolicy(dto.getFeePolicy());
        entity.setDisplayOnInvoice(ConversionUtils.defaultIfNull(dto.getDisplayOnInvoice(), true));
        entity.setDisplayOrder(ConversionUtils.defaultIfNull(dto.getDisplayOrder(), 0));
        entity.setParentNotificationRequired(ConversionUtils.defaultIfNull(dto.getParentNotificationRequired(), true));
        entity.setAdvanceNoticeDays(ConversionUtils.defaultIfNull(dto.getAdvanceNoticeDays(), 30));
        entity.setCreatedBy(createdByUser.getId());
        return entity;
    }

    // ================== PRICE HISTORY CONVERSIONS ==================

    public PriceHistoryDto mapToDto(PriceHistory entity) {
        if (entity == null) {
            return null;
        }

        return PriceHistoryDto.builder()
                .id(entity.getId())
                .schoolPricingId(entity.getSchoolPricing() != null ? entity.getSchoolPricing().getId() : null)
                .schoolName(entity.getSchoolPricing() != null && entity.getSchoolPricing().getSchool() != null ?
                        entity.getSchoolPricing().getSchool().getName() : null)
                .academicYear(entity.getSchoolPricing() != null ? entity.getSchoolPricing().getAcademicYear() : null)
                .gradeLevel(entity.getSchoolPricing() != null ? entity.getSchoolPricing().getGradeLevel() : null)
                .changedByUserName(entity.getChangedByUser() != null ?
                        entity.getChangedByUser().getFirstName() + " " + entity.getChangedByUser().getLastName() : null)
                .changeDate(entity.getChangeDate())
                .changeType(entity.getChangeType())
                .fieldName(entity.getFieldName())
                .fieldDisplayName(getFieldDisplayName(entity.getFieldName()))
                .oldValue(entity.getOldValue())
                .newValue(entity.getNewValue())
                .changePercentage(entity.getChangePercentage())
                .changeAmount(entity.getChangeAmount())
                .reason(entity.getReason())
                .changeNotes(entity.getChangeNotes())
                .effectiveDate(entity.getEffectiveDate())
                .approvedByUserName(getApprovedByUserName(entity.getApprovedBy()))
                .approvedAt(entity.getApprovedAt())
                .affectedStudentsCount(entity.getAffectedStudentsCount())
                .revenueImpact(entity.getRevenueImpact())
                .competitiveAnalysis(entity.getCompetitiveAnalysis())
                .parentsNotified(entity.getParentsNotified())
                .notificationDate(entity.getNotificationDate())
                .notificationMethod(entity.getNotificationMethod())
                .advanceNoticeDays(entity.getAdvanceNoticeDays())
                .canRollback(entity.getCanRollback())
                .rollbackDeadline(entity.getRollbackDeadline())
                .formattedOldValue(ConversionUtils.formatPrice(entity.getOldValue()))
                .formattedNewValue(ConversionUtils.formatPrice(entity.getNewValue()))
                .formattedChangeAmount(ConversionUtils.formatPrice(entity.getChangeAmount()))
                .changeDirection(determineChangeDirection(entity.getOldValue(), entity.getNewValue()))
                .build();
    }

    // ================== COLLECTION CONVERSIONS ==================

    public List<SchoolPricingDto> mapToDto(List<SchoolPricing> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<PriceHistoryDto> mapPriceHistoryToDto(List<PriceHistory> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(PriceHistoryDto::getChangeDate).reversed())
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    // ================== COMPARISON & MARKET ANALYSIS ==================

    public PricingComparisonDto buildPricingComparison(SchoolPricing fromPricing, SchoolPricing toPricing) {
        if (fromPricing == null || toPricing == null) {
            return null;
        }

        BigDecimal fromTotal = calculateTotalAnnualCost(fromPricing);
        BigDecimal toTotal = calculateTotalAnnualCost(toPricing);

        BigDecimal changeAmount = toTotal.subtract(fromTotal);
        Double changePercentage = ConversionUtils.calculateGrowthRate(toTotal, fromTotal);

        List<FeeChangeDto> feeChanges = buildFeeChanges(fromPricing, toPricing);
        String overallTrend = determineOverallTrend(changePercentage);

        return PricingComparisonDto.builder()
                .fromYear(fromPricing.getAcademicYear())
                .toYear(toPricing.getAcademicYear())
                .gradeLevel(toPricing.getGradeLevel())
                .fromTotal(fromTotal)
                .toTotal(toTotal)
                .totalChangeAmount(changeAmount)
                .totalChangePercentage(changePercentage)
                .feeChanges(feeChanges)
                .overallTrend(overallTrend)
                .build();
    }

    public MarketComparisonDto buildMarketComparison(
            SchoolPricing schoolPricing,
            List<CompetitorSummaryDto> competitors,
            PricingStatisticsDto marketStats) {

        if (schoolPricing == null) {
            return null;
        }

        BigDecimal schoolMonthly = schoolPricing.getMonthlyTuition();
        BigDecimal schoolAnnual = schoolPricing.getAnnualTuition();

        // Calculate market position
        String marketPosition = determineMarketPosition(schoolMonthly, marketStats);
        Double percentileRank = calculatePercentileRank(schoolMonthly, competitors);

        int competitorsCount = ConversionUtils.safeSize(competitors);
        int schoolsAbovePrice = (int) competitors.stream()
                .filter(c -> c.getMonthlyTuition() != null && c.getMonthlyTuition().compareTo(schoolMonthly) > 0)
                .count();
        int schoolsBelowPrice = competitorsCount - schoolsAbovePrice;

        List<String> recommendations = generateMarketRecommendations(marketPosition, percentileRank);
        String competitiveAdvantage = determineCompetitiveAdvantage(schoolPricing, competitors);

        return MarketComparisonDto.builder()
                .schoolId(schoolPricing.getSchool().getId())
                .schoolName(schoolPricing.getSchool().getName())
                .districtName(getDistrictName(schoolPricing.getSchool()))
                .institutionTypeName(getInstitutionTypeName(schoolPricing.getSchool()))
                .schoolMonthlyTuition(schoolMonthly)
                .schoolAnnualTuition(schoolAnnual)
                .marketAverageMonthly(marketStats != null ? marketStats.getAverageMonthlyTuition() : null)
                .marketMedianMonthly(calculateMedianPrice(competitors))
                .marketMinMonthly(marketStats != null ? marketStats.getMinMonthlyTuition() : null)
                .marketMaxMonthly(marketStats != null ? marketStats.getMaxMonthlyTuition() : null)
                .marketPosition(marketPosition)
                .percentileRank(percentileRank)
                .competitorsCount(competitorsCount)
                .schoolsAbovePrice(schoolsAbovePrice)
                .schoolsBelowPrice(schoolsBelowPrice)
                .recommendations(recommendations)
                .competitiveAdvantage(competitiveAdvantage)
                .nearbyCompetitors(competitors)
                .build();
    }

    public PricingStatisticsDto buildPricingStatistics(List<SchoolPricing> pricingList) {
        if (ConversionUtils.isEmpty(pricingList)) {
            return createEmptyStatistics();
        }

        SchoolPricing firstPricing = pricingList.get(0);

        List<BigDecimal> monthlyPrices = pricingList.stream()
                .map(SchoolPricing::getMonthlyTuition)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        List<BigDecimal> annualPrices = pricingList.stream()
                .map(SchoolPricing::getAnnualTuition)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        BigDecimal minMonthly = monthlyPrices.isEmpty() ? BigDecimal.ZERO : monthlyPrices.get(0);
        BigDecimal maxMonthly = monthlyPrices.isEmpty() ? BigDecimal.ZERO : monthlyPrices.get(monthlyPrices.size() - 1);
        BigDecimal avgMonthly = calculateAveragePrice(monthlyPrices);

        BigDecimal minAnnual = annualPrices.isEmpty() ? BigDecimal.ZERO : annualPrices.get(0);
        BigDecimal maxAnnual = annualPrices.isEmpty() ? BigDecimal.ZERO : annualPrices.get(annualPrices.size() - 1);
        BigDecimal avgAnnual = calculateAveragePrice(annualPrices);

        return PricingStatisticsDto.builder()
                .schoolId(firstPricing.getSchool().getId())
                .schoolName(firstPricing.getSchool().getName())
                .totalGradeLevels(pricingList.size())
                .minMonthlyTuition(minMonthly)
                .maxMonthlyTuition(maxMonthly)
                .minAnnualTuition(minAnnual)
                .maxAnnualTuition(maxAnnual)
                .averageMonthlyTuition(avgMonthly)
                .averageAnnualTuition(avgAnnual)
                .transportationAvailableCount((int) pricingList.stream().filter(this::hasTransportationFee).count())
                .cafeteriaAvailableCount((int) pricingList.stream().filter(this::hasCafeteriaFee).count())
                .extendedDayAvailableCount((int) pricingList.stream().filter(this::hasExtendedDayFee).count())
                .needBasedAidCount((int) pricingList.stream().filter(p -> p.getNeedBasedAidAvailable()).count())
                .meritBasedAidCount((int) pricingList.stream().filter(p -> p.getMeritBasedAidAvailable()).count())
                .availableFrequencies(pricingList.stream()
                        .map(SchoolPricing::getPaymentFrequency)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()))
                .maxInstallmentCount(pricingList.stream()
                        .map(SchoolPricing::getInstallmentCount)
                        .filter(Objects::nonNull)
                        .max(Integer::compareTo).orElse(0))
                .averageDownPaymentPercentage(pricingList.stream()
                        .map(SchoolPricing::getDownPaymentPercentage)
                        .filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .average().orElse(0.0))
                .overallMarketPosition(determineOverallMarketPosition(pricingList))
                .competitorsAnalyzed(0) // Will be set by service layer
                .marketPercentileRank(50.0) // Will be calculated by service layer
                .build();
    }

    // ================== CALCULATION HELPERS ==================

    private BigDecimal calculateTotalAnnualCost(SchoolPricing entity) {
        if (entity == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;

        // Add annual tuition or calculated from monthly
        if (entity.getAnnualTuition() != null) {
            total = total.add(entity.getAnnualTuition());
        } else if (entity.getMonthlyTuition() != null) {
            total = total.add(entity.getMonthlyTuition().multiply(BigDecimal.valueOf(12)));
        }

        // Add all additional fees
        total = addIfNotNull(total, entity.getBookFee());
        total = addIfNotNull(total, entity.getUniformFee());
        total = addIfNotNull(total, entity.getActivityFee());
        total = addIfNotNull(total, entity.getTechnologyFee());
        total = addIfNotNull(total, entity.getLaboratoryFee());
        total = addIfNotNull(total, entity.getLibraryFee());
        total = addIfNotNull(total, entity.getSportsFee());
        total = addIfNotNull(total, entity.getArtFee());
        total = addIfNotNull(total, entity.getMusicFee());
        total = addIfNotNull(total, entity.getTransportationFee());
        total = addIfNotNull(total, entity.getCafeteriaFee());
        total = addIfNotNull(total, entity.getInsuranceFee());
        total = addIfNotNull(total, entity.getMaintenanceFee());
        total = addIfNotNull(total, entity.getSecurityFee());
        total = addIfNotNull(total, entity.getExamFee());
        total = addIfNotNull(total, entity.getGraduationFee());

        return total;
    }

    private BigDecimal calculateTotalMonthlyCost(SchoolPricing entity) {
        if (entity == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalAnnual = calculateTotalAnnualCost(entity);
        return totalAnnual.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalOneTimeFees(SchoolPricing entity) {
        if (entity == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        total = addIfNotNull(total, entity.getRegistrationFee());
        total = addIfNotNull(total, entity.getApplicationFee());
        total = addIfNotNull(total, entity.getEnrollmentFee());

        return total;
    }

    private BigDecimal calculateInstallmentAmount(SchoolPricing entity) {
        if (entity == null || entity.getInstallmentCount() == null || entity.getInstallmentCount() == 0) {
            return null;
        }

        BigDecimal totalAnnual = calculateTotalAnnualCost(entity);
        return totalAnnual.divide(BigDecimal.valueOf(entity.getInstallmentCount()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDownPaymentAmount(SchoolPricing entity) {
        if (entity == null || entity.getDownPaymentPercentage() == null) {
            return null;
        }

        BigDecimal totalAnnual = calculateTotalAnnualCost(entity);
        BigDecimal percentage = BigDecimal.valueOf(entity.getDownPaymentPercentage()).divide(BigDecimal.valueOf(100));
        return totalAnnual.multiply(percentage).setScale(2, RoundingMode.HALF_UP);
    }

    private void calculateAndSetTotals(SchoolPricing entity) {
        entity.setTotalAnnualCost(calculateTotalAnnualCost(entity));
        entity.setTotalMonthlyCost(calculateTotalMonthlyCost(entity));
        entity.setTotalOneTimeFees(calculateTotalOneTimeFees(entity));
        entity.setInstallmentAmount(calculateInstallmentAmount(entity));
        entity.setDownPaymentAmount(calculateDownPaymentAmount(entity));
    }

    private BigDecimal addIfNotNull(BigDecimal total, BigDecimal value) {
        return value != null ? total.add(value) : total;
    }

    // ================== FEATURE DETECTION ==================

    private boolean hasTransportationFee(SchoolPricing entity) {
        return entity.getTransportationFee() != null && entity.getTransportationFee().compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean hasCafeteriaFee(SchoolPricing entity) {
        return entity.getCafeteriaFee() != null && entity.getCafeteriaFee().compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean hasExtendedDayFee(SchoolPricing entity) {
        return entity.getExtendedDayFee() != null && entity.getExtendedDayFee().compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean hasFinancialAid(SchoolPricing entity) {
        return (entity.getNeedBasedAidAvailable() != null && entity.getNeedBasedAidAvailable()) ||
                (entity.getMeritBasedAidAvailable() != null && entity.getMeritBasedAidAvailable());
    }

    // ================== FORMATTING HELPERS ==================

    private String formatAgeRange(SchoolPricing entity) {
        if (entity.getSchool() == null) {
            return null;
        }
        return ConversionUtils.formatAgeRange(entity.getSchool().getMinAge(), entity.getSchool().getMaxAge());
    }

    private String buildApplicabilityDescription(CustomFee entity) {
        List<String> parts = new ArrayList<>();

        if (entity.getAppliesToNewStudents() != null && entity.getAppliesToNewStudents()) {
            parts.add("Yeni öğrenciler");
        }
        if (entity.getAppliesToExistingStudents() != null && entity.getAppliesToExistingStudents()) {
            parts.add("Mevcut öğrenciler");
        }
        if (entity.getMinimumAge() != null || entity.getMaximumAge() != null) {
            parts.add(ConversionUtils.formatAgeRange(entity.getMinimumAge(), entity.getMaximumAge()));
        }

        return parts.isEmpty() ? "Tüm öğrenciler" : String.join(", ", parts);
    }

    private String getFieldDisplayName(String fieldName) {
        if (fieldName == null) return null;

        Map<String, String> fieldDisplayNames = Map.of(
                "annualTuition", "Yıllık Öğrenim Ücreti",
                "monthlyTuition", "Aylık Öğrenim Ücreti",
                "registrationFee", "Kayıt Ücreti",
                "transportationFee", "Servis Ücreti",
                "cafeteriaFee", "Kantin Ücreti",
                "bookFee", "Kitap Ücreti"
        );

        return fieldDisplayNames.getOrDefault(fieldName, ConversionUtils.capitalizeWords(fieldName.replaceAll("([A-Z])", " $1")));
    }

    private String getApprovedByUserName(Long approvedBy) {
        // This will be implemented in service layer
        return null;
    }

    private String determineChangeDirection(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue == null || newValue == null) {
            return "NO_CHANGE";
        }

        int comparison = newValue.compareTo(oldValue);
        if (comparison > 0) return "INCREASE";
        if (comparison < 0) return "DECREASE";
        return "NO_CHANGE";
    }

    // ================== MARKET ANALYSIS HELPERS ==================

    private List<FeeChangeDto> buildFeeChanges(SchoolPricing from, SchoolPricing to) {
        List<FeeChangeDto> changes = new ArrayList<>();

        // Compare major fees
        addFeeChange(changes, "Yıllık Öğrenim", from.getAnnualTuition(), to.getAnnualTuition());
        addFeeChange(changes, "Aylık Öğrenim", from.getMonthlyTuition(), to.getMonthlyTuition());
        addFeeChange(changes, "Kayıt Ücreti", from.getRegistrationFee(), to.getRegistrationFee());
        addFeeChange(changes, "Servis Ücreti", from.getTransportationFee(), to.getTransportationFee());

        return changes;
    }

    private void addFeeChange(List<FeeChangeDto> changes, String displayName, BigDecimal fromAmount, BigDecimal toAmount) {
        if (fromAmount == null && toAmount == null) {
            return;
        }

        BigDecimal changeAmount = (toAmount != null ? toAmount : BigDecimal.ZERO)
                .subtract(fromAmount != null ? fromAmount : BigDecimal.ZERO);

        Double changePercentage = ConversionUtils.calculateGrowthRate(
                toAmount != null ? toAmount : BigDecimal.ZERO,
                fromAmount != null ? fromAmount : BigDecimal.ONE
        );

        String changeType = determineChangeDirection(fromAmount, toAmount);

        changes.add(FeeChangeDto.builder()
                .fieldName(displayName.toLowerCase().replace(" ", "_"))
                .displayName(displayName)
                .fromAmount(fromAmount)
                .toAmount(toAmount)
                .changeAmount(changeAmount)
                .changePercentage(changePercentage)
                .changeType(changeType)
                .formattedFromAmount(ConversionUtils.formatPrice(fromAmount))
                .formattedToAmount(ConversionUtils.formatPrice(toAmount))
                .formattedChangeAmount(ConversionUtils.formatPrice(changeAmount))
                .build());
    }

    private String determineOverallTrend(Double changePercentage) {
        if (changePercentage == null || Math.abs(changePercentage) < 1.0) {
            return "STABLE";
        }
        return changePercentage > 0 ? "INCREASE" : "DECREASE";
    }

    private String determineMarketPosition(BigDecimal schoolPrice, PricingStatisticsDto stats) {
        if (schoolPrice == null || stats == null || stats.getAverageMonthlyTuition() == null) {
            return "COMPETITIVE";
        }

        BigDecimal avgPrice = stats.getAverageMonthlyTuition();
        double ratio = schoolPrice.divide(avgPrice, 4, RoundingMode.HALF_UP).doubleValue();

        if (ratio < 0.7) return "BUDGET";
        if (ratio < 1.3) return "COMPETITIVE";
        if (ratio < 2.0) return "PREMIUM";
        return "LUXURY";
    }

    private Double calculatePercentileRank(BigDecimal schoolPrice, List<CompetitorSummaryDto> competitors) {
        if (schoolPrice == null || ConversionUtils.isEmpty(competitors)) {
            return 50.0;
        }

        List<BigDecimal> prices = competitors.stream()
                .map(CompetitorSummaryDto::getMonthlyTuition)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        long belowCount = prices.stream()
                .mapToLong(price -> price.compareTo(schoolPrice) < 0 ? 1 : 0)
                .sum();

        return (belowCount * 100.0) / prices.size();
    }

    private BigDecimal calculateMedianPrice(List<CompetitorSummaryDto> competitors) {
        if (ConversionUtils.isEmpty(competitors)) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> prices = competitors.stream()
                .map(CompetitorSummaryDto::getMonthlyTuition)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        if (prices.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int size = prices.size();
        if (size % 2 == 0) {
            return prices.get(size / 2 - 1).add(prices.get(size / 2))
                    .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        } else {
            return prices.get(size / 2);
        }
    }

    private BigDecimal calculateAveragePrice(List<BigDecimal> prices) {
        if (ConversionUtils.isEmpty(prices)) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = prices.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(prices.size()), 2, RoundingMode.HALF_UP);
    }

    private List<String> generateMarketRecommendations(String marketPosition, Double percentileRank) {
        List<String> recommendations = new ArrayList<>();

        if ("BUDGET".equals(marketPosition)) {
            recommendations.add("Fiyat artışı için değer önerileri ekleyin");
            recommendations.add("Ek hizmetler ile gelir artırımı yapın");
        } else if ("LUXURY".equals(marketPosition)) {
            recommendations.add("Premium hizmet kalitesini vurgulayın");
            recommendations.add("Hedef kitleyi daraltın");
        } else if (percentileRank != null && percentileRank > 75) {
            recommendations.add("Rekabet avantajını vurgulayın");
        }

        return recommendations;
    }

    private String determineCompetitiveAdvantage(SchoolPricing pricing, List<CompetitorSummaryDto> competitors) {
        if (hasFinancialAid(pricing)) {
            return "Burs imkanları";
        }
        if (hasTransportationFee(pricing) && hasCafeteriaFee(pricing)) {
            return "Kapsamlı hizmetler";
        }
        return "Kaliteli eğitim";
    }

    private String determineOverallMarketPosition(List<SchoolPricing> pricingList) {
        if (ConversionUtils.isEmpty(pricingList)) {
            return "COMPETITIVE";
        }

        // Simple logic - can be enhanced
        boolean hasLowPrices = pricingList.stream().anyMatch(p ->
                p.getMonthlyTuition() != null && p.getMonthlyTuition().compareTo(BigDecimal.valueOf(1000)) < 0);

        boolean hasHighPrices = pricingList.stream().anyMatch(p ->
                p.getMonthlyTuition() != null && p.getMonthlyTuition().compareTo(BigDecimal.valueOf(5000)) > 0);

        if (hasHighPrices && !hasLowPrices) return "PREMIUM";
        if (hasLowPrices && !hasHighPrices) return "BUDGET";
        return "COMPETITIVE";
    }

    private PricingStatisticsDto createEmptyStatistics() {
        return PricingStatisticsDto.builder()
                .totalGradeLevels(0)
                .minMonthlyTuition(BigDecimal.ZERO)
                .maxMonthlyTuition(BigDecimal.ZERO)
                .averageMonthlyTuition(BigDecimal.ZERO)
                .minAnnualTuition(BigDecimal.ZERO)
                .maxAnnualTuition(BigDecimal.ZERO)
                .averageAnnualTuition(BigDecimal.ZERO)
                .transportationAvailableCount(0)
                .cafeteriaAvailableCount(0)
                .extendedDayAvailableCount(0)
                .needBasedAidCount(0)
                .meritBasedAidCount(0)
                .availableFrequencies(new ArrayList<>())
                .maxInstallmentCount(0)
                .averageDownPaymentPercentage(0.0)
                .overallMarketPosition("COMPETITIVE")
                .competitorsAnalyzed(0)
                .marketPercentileRank(50.0)
                .build();
    }

    // ================== PLACEHOLDER METHODS ==================
    // These will be implemented properly in service layer

    private String getDistrictName(School school) {
        // Will get district name from school's campus location
        return null;
    }

    private String getInstitutionTypeName(School school) {
        return school.getInstitutionType() != null ?
                school.getInstitutionType().getDisplayName() : null;
    }

    public List<PricingSummaryDto> mapToSummaryDto(List<SchoolPricing> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public List<CustomFeeDto> mapCustomFeesToDto(Set<CustomFee> entities) {
        return entities != null ? entities.stream()
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(CustomFeeDto::getDisplayOrder))
                .collect(Collectors.toList()) : new ArrayList<>();
    }


    public PriceTrendsDto mapToPriceTrendsDto(List<PriceHistory> history, LocalDateTime startDate, LocalDateTime endDate) {
        if (ConversionUtils.isEmpty(history) || startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return null;
        }

        List<PriceTrendPoint> trendPoints = history.stream()
                .filter(h -> h.getChangeDate() != null &&
                        !h.getChangeDate().isBefore(startDate) &&
                        !h.getChangeDate().isAfter(endDate))
                .map(h -> new PriceTrendPoint(h.getChangeDate(), h.getNewValue()))
                .sorted(Comparator.comparing(PriceTrendPoint::getDate))
                .collect(Collectors.toList());

        return PriceTrendsDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .trendPoints(trendPoints)
                .build();
    }

    public MarketComparisonDto mapToMarketComparisonDto(SchoolPricing schoolPricing, MarketAveragesDto marketAverages) {
        if (schoolPricing == null || marketAverages == null) {
            return null;
        }

        BigDecimal schoolMonthly = schoolPricing.getMonthlyTuition();
        BigDecimal schoolAnnual = schoolPricing.getAnnualTuition();

        // Ceyhun

        return MarketComparisonDto.builder()
                .schoolId(schoolPricing.getSchool().getId())
                .schoolName(schoolPricing.getSchool().getName())
                .districtName(getDistrictName(schoolPricing.getSchool()))
                .institutionTypeName(getInstitutionTypeName(schoolPricing.getSchool()))
                .schoolMonthlyTuition(schoolMonthly)
                .schoolAnnualTuition(schoolAnnual)
                .competitiveAdvantage(determineCompetitiveAdvantage(schoolPricing, new ArrayList<>()))
                .nearbyCompetitors(new ArrayList<>()) // Will be set in service layer
                .build();
    }

    public PricingReportDto mapToPricingReportDto(List<SchoolPricing> pricings, PricingReportRequestDto reportRequest) {
        if (ConversionUtils.isEmpty(pricings) || reportRequest == null) {
            return null;
        }

        List<SchoolPricingDto> pricingDtos = mapToDto(pricings);
        PricingStatisticsDto statistics = buildPricingStatistics(pricings);

        return PricingReportDto.builder()
                .reportName("Fiyatlandırma Raporu")
                .generatedAt(LocalDate.now())
                .generatedBy("Sistem") // Will be set in service layer
                .filterSchoolIds(reportRequest.getSchoolIds())
                .filterGradeLevels(reportRequest.getGradeLevels())
                .filterAcademicYears(reportRequest.getAcademicYears())
                .filterStartDate(reportRequest.getStartDate())
                .filterEndDate(reportRequest.getEndDate())
                .pricingDetails(pricingDtos)
                .statistics(statistics)
                .build();
    }

    public byte[] exportPricingData(List<SchoolPricing> pricings, String format) {

        return null;
    }

    public PriceTrendsDto mapToPricingTrendsDto(List<PriceHistory> history, LocalDateTime startDate, LocalDateTime endDate) {
        return mapToPriceTrendsDto(history, startDate, endDate);
    }

    public PricingComparisonDto mapToDto(List<SchoolPricing> pricings, String gradeLevel, String academicYear) {
        if (ConversionUtils.isEmpty(pricings) || pricings.size() < 2) {
            return null;
        }

        SchoolPricing fromPricing = pricings.stream()
                .filter(p -> p.getAcademicYear().equals(academicYear) && p.getGradeLevel().equals(gradeLevel))
                .findFirst().orElse(null);

        SchoolPricing toPricing = pricings.stream()
                .filter(p -> !p.getAcademicYear().equals(academicYear) && p.getGradeLevel().equals(gradeLevel))
                .findFirst().orElse(null);

        return buildPricingComparison(fromPricing, toPricing);
    }
}