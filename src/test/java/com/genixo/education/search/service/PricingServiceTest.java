package com.genixo.education.search.service;


import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.pricing.*;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.pricing.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.repository.pricing.CustomFeeRepository;
import com.genixo.education.search.repository.pricing.PriceHistoryRepository;
import com.genixo.education.search.repository.pricing.SchoolPricingRepository;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.PricingConverterService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PricingService Tests")
class PricingServiceTest {

    @Mock private SchoolPricingRepository schoolPricingRepository;
    @Mock private CustomFeeRepository customFeeRepository;
    @Mock private PriceHistoryRepository priceHistoryRepository;
    @Mock private SchoolRepository schoolRepository;
    @Mock private PricingConverterService converterService;
    @Mock private JwtService jwtService;
    @Mock private HttpServletRequest request;

    @InjectMocks
    private PricingService pricingService;

    private User systemUser;
    private User regularUser;
    private School validSchool;
    private SchoolPricingCreateDto validPricingCreateDto;
    private SchoolPricing savedPricing;
    private SchoolPricingDto expectedPricingDto;

    @BeforeEach
    void setUp() {
        // System user with SYSTEM role
        systemUser = createUser(1L, RoleLevel.SYSTEM);

        // Regular user without system permissions
        regularUser = createUser(2L, RoleLevel.SCHOOL);

        // Valid school
        validSchool = new School();
        validSchool.setId(1L);
        validSchool.setName("Test School");
        validSchool.setIsActive(true);

        // Valid pricing create DTO
        validPricingCreateDto = SchoolPricingCreateDto.builder()
                .schoolId(1L)
                .academicYear("2024-2025")
                .gradeLevel("Grade 1")
                .classLevel("A")
                .currency(Currency.TRY)
                .registrationFee(new BigDecimal("2500.00"))
                .applicationFee(new BigDecimal("500.00"))
                .enrollmentFee(new BigDecimal("1000.00"))
                .annualTuition(new BigDecimal("15000.00"))
                .monthlyTuition(new BigDecimal("1250.00"))
                .bookFee(new BigDecimal("800.00"))
                .uniformFee(new BigDecimal("600.00"))
                .activityFee(new BigDecimal("300.00"))
                .technologyFee(new BigDecimal("400.00"))
                .transportationFee(new BigDecimal("500.00"))
                .cafeteriaFee(new BigDecimal("400.00"))
                .paymentFrequency(PaymentFrequency.MONTHLY)
                .installmentCount(10)
                .downPaymentPercentage(20.0)
                .earlyPaymentDiscountPercentage(5.0)
                .siblingDiscountPercentage(10.0)
                .validFrom(LocalDate.of(2024, 9, 1))
                .validUntil(LocalDate.of(2025, 8, 31))
                .refundPolicy("50% refund before semester start")
                .paymentTerms("Payment due by 5th of each month")
                .latePaymentPenaltyPercentage(2.0)
                .publicDescription("Comprehensive education with modern facilities")
                .showDetailedBreakdown(true)
                .showPaymentOptions(true)
                .build();

        // Saved pricing entity
        savedPricing = new SchoolPricing();
        savedPricing.setId(1L);
        savedPricing.setSchool(validSchool);
        savedPricing.setCreatedByUser(systemUser);
        savedPricing.setAcademicYear("2024-2025");
        savedPricing.setGradeLevel("Grade 1");
        savedPricing.setAnnualTuition(new BigDecimal("15000.00"));
        savedPricing.setMonthlyTuition(new BigDecimal("1250.00"));
        savedPricing.setStatus(PricingStatus.DRAFT);
        savedPricing.setIsCurrent(true);
        savedPricing.setVersion(1);

        // Expected DTO response
        expectedPricingDto = SchoolPricingDto.builder()
                .id(1L)
                .schoolId(1L)
                .schoolName("Test School")
                .academicYear("2024-2025")
                .gradeLevel("Grade 1")
                .annualTuition(new BigDecimal("15000.00"))
                .monthlyTuition(new BigDecimal("1250.00"))
                .build();
    }

    @Nested
    @DisplayName("createSchoolPricing() Tests")
    class CreateSchoolPricingTests {

        @Test
        @DisplayName("Should create school pricing successfully with valid data")
        void shouldCreateSchoolPricingSuccessfullyWithValidData() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(schoolPricingRepository.existsBySchoolIdAndAcademicYearAndGradeLevelAndIsActiveTrue(
                    1L, "2024-2025", "Grade 1")).thenReturn(false);
            when(schoolPricingRepository.save(any(SchoolPricing.class))).thenReturn(savedPricing);
            when(converterService.mapToDto(savedPricing)).thenReturn(expectedPricingDto);

            // When
            SchoolPricingDto result = pricingService.createSchoolPricing(validPricingCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("2024-2025", result.getAcademicYear());
            assertEquals("Grade 1", result.getGradeLevel());
            assertEquals(new BigDecimal("15000.00"), result.getAnnualTuition());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(1L);
            verify(schoolPricingRepository).existsBySchoolIdAndAcademicYearAndGradeLevelAndIsActiveTrue(
                    1L, "2024-2025", "Grade 1");
            verify(schoolPricingRepository).save(argThat(pricing ->
                    pricing.getSchool().getId().equals(1L) &&
                            pricing.getAcademicYear().equals("2024-2025") &&
                            pricing.getGradeLevel().equals("Grade 1") &&
                            pricing.getStatus() == PricingStatus.DRAFT &&
                            pricing.getIsCurrent().equals(true) &&
                            pricing.getVersion().equals(1)
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when school not found")
        void shouldThrowResourceNotFoundExceptionWhenSchoolNotFound() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(999L)).thenReturn(Optional.empty());

            SchoolPricingCreateDto invalidSchoolDto = SchoolPricingCreateDto.builder()
                    .schoolId(999L)
                    .academicYear("2024-2025")
                    .gradeLevel("Grade 1")
                    .validFrom(LocalDate.now())
                    .validUntil(LocalDate.now().plusMonths(12))
                    .build();

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> pricingService.createSchoolPricing(invalidSchoolDto, request));

            assertTrue(exception.getMessage().contains("School"));

            verify(schoolRepository).findByIdAndIsActiveTrue(999L);
            verifyNoInteractions(schoolPricingRepository, converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when pricing already exists")
        void shouldThrowBusinessExceptionWhenPricingAlreadyExists() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(schoolPricingRepository.existsBySchoolIdAndAcademicYearAndGradeLevelAndIsActiveTrue(
                    1L, "2024-2025", "Grade 1")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pricingService.createSchoolPricing(validPricingCreateDto, request));

            assertEquals("Pricing already exists for this academic year and grade level", exception.getMessage());

            verify(schoolPricingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate positive amounts and throw exception for negative values")
        void shouldValidatePositiveAmountsAndThrowExceptionForNegativeValues() {
            // Given
            SchoolPricingCreateDto negativeAmountDto = SchoolPricingCreateDto.builder()
                    .schoolId(1L)
                    .academicYear("2024-2025")
                    .gradeLevel("Grade 1")
                    .annualTuition(new BigDecimal("-1000.00")) // Negative amount
                    .validFrom(LocalDate.now())
                    .validUntil(LocalDate.now().plusMonths(12))
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(schoolPricingRepository.existsBySchoolIdAndAcademicYearAndGradeLevelAndIsActiveTrue(
                    anyLong(), anyString(), anyString())).thenReturn(false);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pricingService.createSchoolPricing(negativeAmountDto, request));

            assertTrue(exception.getMessage().contains("cannot be negative"));
            verify(schoolPricingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate date range and throw exception for invalid dates")
        void shouldValidateDateRangeAndThrowExceptionForInvalidDates() {
            // Given
            SchoolPricingCreateDto invalidDateDto = SchoolPricingCreateDto.builder()
                    .schoolId(1L)
                    .academicYear("2024-2025")
                    .gradeLevel("Grade 1")
                    .annualTuition(new BigDecimal("15000.00"))
                    .validFrom(LocalDate.of(2025, 1, 1))
                    .validUntil(LocalDate.of(2024, 12, 31)) // Invalid: until before from
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pricingService.createSchoolPricing(invalidDateDto, request));

            assertTrue(exception.getMessage().contains("Valid from date must be before valid until date"));
        }

        @Test
        @DisplayName("Should set default values for optional fields")
        void shouldSetDefaultValuesForOptionalFields() {
            // Given
            SchoolPricingCreateDto minimalDto = SchoolPricingCreateDto.builder()
                    .schoolId(1L)
                    .academicYear("2024-2025")
                    .gradeLevel("Grade 1")
                    .annualTuition(new BigDecimal("15000.00"))
                    .validFrom(LocalDate.now())
                    .validUntil(LocalDate.now().plusMonths(12))
                    // Currency not specified - should default to TRY
                    // PaymentFrequency not specified - should default to MONTHLY
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(schoolPricingRepository.existsBySchoolIdAndAcademicYearAndGradeLevelAndIsActiveTrue(
                    anyLong(), anyString(), anyString())).thenReturn(false);
            when(schoolPricingRepository.save(any(SchoolPricing.class))).thenReturn(savedPricing);
            when(converterService.mapToDto(any(SchoolPricing.class))).thenReturn(expectedPricingDto);

            // When
            pricingService.createSchoolPricing(minimalDto, request);

            // Then
            verify(schoolPricingRepository).save(argThat(pricing ->
                    pricing.getCurrency() == Currency.TRY &&
                            pricing.getPaymentFrequency() == PaymentFrequency.MONTHLY &&
                            pricing.getShowDetailedBreakdown().equals(true) &&
                            pricing.getShowPaymentOptions().equals(true)
            ));
        }

        @Test
        @DisplayName("Should calculate totals correctly when creating pricing")
        void shouldCalculateTotalsCorrectlyWhenCreatingPricing() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(validSchool));
            when(schoolPricingRepository.existsBySchoolIdAndAcademicYearAndGradeLevelAndIsActiveTrue(
                    anyLong(), anyString(), anyString())).thenReturn(false);
            when(schoolPricingRepository.save(any(SchoolPricing.class))).thenReturn(savedPricing);
            when(converterService.mapToDto(any(SchoolPricing.class))).thenReturn(expectedPricingDto);

            // When
            pricingService.createSchoolPricing(validPricingCreateDto, request);

            // Then
            verify(schoolPricingRepository).save(argThat(pricing -> {
                // One-time fees: registration + application + enrollment + book + uniform
                BigDecimal expectedOneTime = new BigDecimal("2500.00")
                        .add(new BigDecimal("500.00"))
                        .add(new BigDecimal("1000.00"))
                        .add(new BigDecimal("800.00"))
                        .add(new BigDecimal("600.00"));

                // Monthly recurring: monthly tuition + activity + technology + transportation + cafeteria
                BigDecimal expectedMonthly = new BigDecimal("1250.00")
                        .add(new BigDecimal("300.00"))
                        .add(new BigDecimal("400.00"))
                        .add(new BigDecimal("500.00"))
                        .add(new BigDecimal("400.00"));

                return pricing.getTotalOneTimeFees().compareTo(expectedOneTime) == 0 &&
                        pricing.getTotalMonthlyCost().compareTo(expectedMonthly) == 0;
            }));
        }
    }

    @Nested
    @DisplayName("getSchoolPricingById() Tests")
    class GetSchoolPricingByIdTests {

        @Test
        @DisplayName("Should return pricing successfully when user has access")
        void shouldReturnPricingSuccessfullyWhenUserHasAccess() {
            // Given
            Long pricingId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(pricingId)).thenReturn(Optional.of(savedPricing));
            when(converterService.mapToDto(savedPricing)).thenReturn(expectedPricingDto);

            // When
            SchoolPricingDto result = pricingService.getSchoolPricingById(pricingId, request);

            // Then
            assertNotNull(result);
            assertEquals(expectedPricingDto.getSchoolId(), result.getSchoolId());
            assertEquals(expectedPricingDto.getAcademicYear(), result.getAcademicYear());

            verify(jwtService).getUser(request);
            verify(schoolPricingRepository).findByIdAndIsActiveTrue(pricingId);
            verify(converterService).mapToDto(savedPricing);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when pricing not found")
        void shouldThrowResourceNotFoundExceptionWhenPricingNotFound() {
            // Given
            Long nonExistentPricingId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(nonExistentPricingId)).thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> pricingService.getSchoolPricingById(nonExistentPricingId, request));

            assertTrue(exception.getMessage().contains("SchoolPricing"));

            verify(schoolPricingRepository).findByIdAndIsActiveTrue(nonExistentPricingId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to school")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToSchool() {
            // Given
            Long pricingId = 1L;
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(pricingId)).thenReturn(Optional.of(savedPricing));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pricingService.getSchoolPricingById(pricingId, request));

            assertEquals("User does not have access to pricing for this school", exception.getMessage());

            verify(schoolPricingRepository).findByIdAndIsActiveTrue(pricingId);
            verifyNoInteractions(converterService);
        }
    }

    @Nested
    @DisplayName("updateSchoolPricing() Tests")
    class UpdateSchoolPricingTests {

        private SchoolPricingUpdateDto validUpdateDto;

        @BeforeEach
        void setUp() {
            validUpdateDto = SchoolPricingUpdateDto.builder()
                    .annualTuition(new BigDecimal("16000.00")) // Price increase
                    .monthlyTuition(new BigDecimal("1333.33"))
                    .registrationFee(new BigDecimal("2700.00"))
                    .status(PricingStatus.PENDING_APPROVAL)
                    .publicDescription("Updated description with new features")
                    .build();
        }

        @Test
        @DisplayName("Should update pricing successfully with price history creation")
        void shouldUpdatePricingSuccessfullyWithPriceHistoryCreation() {
            // Given
            Long pricingId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(pricingId)).thenReturn(Optional.of(savedPricing));
            when(schoolPricingRepository.save(any(SchoolPricing.class))).thenReturn(savedPricing);
            when(converterService.mapToDto(any(SchoolPricing.class))).thenReturn(expectedPricingDto);
            when(priceHistoryRepository.save(any(PriceHistory.class))).thenReturn(new PriceHistory());

            // When
            SchoolPricingDto result = pricingService.updateSchoolPricing(pricingId, validUpdateDto, request);

            // Then
            assertNotNull(result);

            verify(schoolPricingRepository).findByIdAndIsActiveTrue(pricingId);
            verify(schoolPricingRepository).save(argThat(pricing ->
                    pricing.getAnnualTuition().compareTo(new BigDecimal("16000.00")) == 0 &&
                            pricing.getUpdatedBy().equals(1L)
            ));

            // Verify price history is created for significant changes
            verify(priceHistoryRepository, atLeastOnce()).save(any(PriceHistory.class));
        }

        @Test
        @DisplayName("Should increment version for major price changes")
        void shouldIncrementVersionForMajorPriceChanges() {
            // Given - 20% increase (major change > 10%)
            SchoolPricingUpdateDto majorChangeDto = SchoolPricingUpdateDto.builder()
                    .monthlyTuition(new BigDecimal("1500.00")) // 1250 -> 1500 = 20% increase
                    .build();

            savedPricing.setVersion(3); // Starting version

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(savedPricing));
            when(schoolPricingRepository.save(any(SchoolPricing.class))).thenReturn(savedPricing);
            when(converterService.mapToDto(any(SchoolPricing.class))).thenReturn(expectedPricingDto);

            // When
            pricingService.updateSchoolPricing(1L, majorChangeDto, request);

            // Then
            verify(schoolPricingRepository).save(argThat(pricing ->
                    pricing.getVersion().equals(4) // Should increment from 3 to 4
            ));
        }

        @Test
        @DisplayName("Should not increment version for minor price changes")
        void shouldNotIncrementVersionForMinorPriceChanges() {
            // Given - 5% increase (minor change < 10%)
            SchoolPricingUpdateDto minorChangeDto = SchoolPricingUpdateDto.builder()
                    .monthlyTuition(new BigDecimal("1312.50")) // 1250 -> 1312.50 = 5% increase
                    .build();

            savedPricing.setVersion(3);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(savedPricing));
            when(schoolPricingRepository.save(any(SchoolPricing.class))).thenReturn(savedPricing);
            when(converterService.mapToDto(any(SchoolPricing.class))).thenReturn(expectedPricingDto);

            // When
            pricingService.updateSchoolPricing(1L, minorChangeDto, request);

            // Then
            verify(schoolPricingRepository).save(argThat(pricing ->
                    pricing.getVersion().equals(3) // Should remain same
            ));
        }
    }

    @Nested
    @DisplayName("createCustomFee() Tests")
    class CreateCustomFeeTests {

        private CustomFeeCreateDto validCustomFeeDto;
        private CustomFee savedCustomFee;
        private CustomFeeDto expectedCustomFeeDto;

        @BeforeEach
        void setUp() {
            validCustomFeeDto = CustomFeeCreateDto.builder()
                    .schoolPricingId(1L)
                    .feeName("Laboratory Fee")
                    .feeDescription("Additional fee for laboratory equipment and materials")
                    .feeAmount(new BigDecimal("500.00"))
                    .feeType(CustomFeeType.LABORATORY)
                    .feeFrequency(PaymentFrequency.SEMESTER)
                    .isMandatory(true)
                    .isRefundable(false)
                    .appliesToNewStudents(true)
                    .appliesToExistingStudents(true)
                    .validFrom(LocalDate.of(2024, 9, 1))
                    .validUntil(LocalDate.of(2025, 8, 31))
                    .build();

            savedCustomFee = new CustomFee();
            savedCustomFee.setId(1L);
            savedCustomFee.setSchoolPricing(savedPricing);
            savedCustomFee.setFeeName("Laboratory Fee");
            savedCustomFee.setFeeAmount(new BigDecimal("500.00"));

            expectedCustomFeeDto = CustomFeeDto.builder()
                    .id(1L)
                    .feeName("Laboratory Fee")
                    .feeAmount(new BigDecimal("500.00"))
                    .build();
        }

        @Test
        @DisplayName("Should create custom fee successfully")
        void shouldCreateCustomFeeSuccessfully() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(savedPricing));
            when(customFeeRepository.existsByFeeNameIgnoreCaseAndSchoolPricingIdAndIsActiveTrue(
                    "Laboratory Fee", 1L)).thenReturn(false);
            when(customFeeRepository.save(any(CustomFee.class))).thenReturn(savedCustomFee);
            when(converterService.mapToDto(savedCustomFee)).thenReturn(expectedCustomFeeDto);

            // When
            CustomFeeDto result = pricingService.createCustomFee(validCustomFeeDto, request);

            // Then
            assertNotNull(result);
            assertEquals("Laboratory Fee", result.getFeeName());
            assertEquals(new BigDecimal("500.00"), result.getFeeAmount());

            verify(customFeeRepository).save(argThat(fee ->
                    fee.getFeeName().equals("Laboratory Fee") &&
                            fee.getFeeAmount().compareTo(new BigDecimal("500.00")) == 0 &&
                            fee.getFeeType() == CustomFeeType.LABORATORY &&
                            fee.getIsMandatory().equals(true) &&
                            fee.getStatus() == CustomFeeStatus.ACTIVE
            ));
        }

        @Test
        @DisplayName("Should throw BusinessException when fee name already exists")
        void shouldThrowBusinessExceptionWhenFeeNameAlreadyExists() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(savedPricing));
            when(customFeeRepository.existsByFeeNameIgnoreCaseAndSchoolPricingIdAndIsActiveTrue(
                    "Laboratory Fee", 1L)).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pricingService.createCustomFee(validCustomFeeDto, request));

            assertEquals("Custom fee with this name already exists for this pricing", exception.getMessage());
            verify(customFeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate custom fee data and throw exception for invalid data")
        void shouldValidateCustomFeeDataAndThrowExceptionForInvalidData() {
            // Given - Invalid data: negative amount
            CustomFeeCreateDto invalidFeeDto = CustomFeeCreateDto.builder()
                    .schoolPricingId(1L)
                    .feeName("Invalid Fee")
                    .feeAmount(new BigDecimal("-100.00")) // Negative amount
                    .feeType(CustomFeeType.OTHER)
                    .feeFrequency(PaymentFrequency.MONTHLY)
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(savedPricing));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pricingService.createCustomFee(invalidFeeDto, request));

            assertTrue(exception.getMessage().contains("Fee amount must be positive"));
        }
    }

    @Nested
    @DisplayName("getPublicSchoolPricing() Tests")
    class GetPublicSchoolPricingTests {

        @Test
        @DisplayName("Should return public pricing for subscribed school")
        void shouldReturnPublicPricingForSubscribedSchool() {
            // Given
            String schoolSlug = "test-school";
            String gradeLevel = "Grade 1";
            String academicYear = "2024-2025";

            School subscribedSchool = new School();
            subscribedSchool.setId(1L);
            subscribedSchool.setSlug(schoolSlug);

            when(schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(schoolSlug))
                    .thenReturn(Optional.of(subscribedSchool));
            when(schoolPricingRepository.findCurrentPricingBySchoolAndGradeAndYear(1L, gradeLevel, academicYear))
                    .thenReturn(Optional.of(savedPricing));
            when(converterService.mapToDto(savedPricing)).thenReturn(expectedPricingDto);

            // When
            SchoolPricingDto result = pricingService.getPublicSchoolPricing(schoolSlug, gradeLevel, academicYear);

            // Then
            assertNotNull(result);
            verify(schoolRepository).findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(schoolSlug);
            verify(schoolPricingRepository).findCurrentPricingBySchoolAndGradeAndYear(1L, gradeLevel, academicYear);

            // Verify that sensitive information is removed for public access
            assertNull(result.getInternalNotes());
            assertNull(result.getCompetitorAnalysis());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException for unsubscribed school")
        void shouldThrowResourceNotFoundExceptionForUnsubscribedSchool() {
            // Given
            String schoolSlug = "unsubscribed-school";
            String gradeLevel = "Grade 1";
            String academicYear = "2024-2025";

            when(schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(schoolSlug))
                    .thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> pricingService.getPublicSchoolPricing(schoolSlug, gradeLevel, academicYear));

            assertEquals("School not found or not available", exception.getMessage());

            verify(schoolRepository).findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(schoolSlug);
            verifyNoInteractions(schoolPricingRepository, converterService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when pricing not available")
        void shouldThrowResourceNotFoundExceptionWhenPricingNotAvailable() {
            // Given
            String schoolSlug = "test-school";
            String gradeLevel = "Grade 1";
            String academicYear = "2024-2025";

            School subscribedSchool = new School();
            subscribedSchool.setId(1L);

            when(schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(schoolSlug))
                    .thenReturn(Optional.of(subscribedSchool));
            when(schoolPricingRepository.findCurrentPricingBySchoolAndGradeAndYear(1L, gradeLevel, academicYear))
                    .thenReturn(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> pricingService.getPublicSchoolPricing(schoolSlug, gradeLevel, academicYear));

            assertEquals("Pricing information not available", exception.getMessage());

            verify(schoolRepository).findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(schoolSlug);
            verify(schoolPricingRepository).findCurrentPricingBySchoolAndGradeAndYear(1L, gradeLevel, academicYear);
        }

        @Test
        @DisplayName("Should not require authentication for public pricing")
        void shouldNotRequireAuthenticationForPublicPricing() {
            // Given
            String schoolSlug = "public-school";
            String gradeLevel = "Grade 1";
            String academicYear = "2024-2025";

            School subscribedSchool = new School();
            subscribedSchool.setId(1L);

            when(schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(schoolSlug))
                    .thenReturn(Optional.of(subscribedSchool));
            when(schoolPricingRepository.findCurrentPricingBySchoolAndGradeAndYear(1L, gradeLevel, academicYear))
                    .thenReturn(Optional.of(savedPricing));
            when(converterService.mapToDto(savedPricing)).thenReturn(expectedPricingDto);

            // When
            pricingService.getPublicSchoolPricing(schoolSlug, gradeLevel, academicYear);

            // Then
            verifyNoInteractions(jwtService); // No authentication required
        }
    }

    @Nested
    @DisplayName("approveSchoolPricing() Tests")
    class ApproveSchoolPricingTests {

        @Test
        @DisplayName("Should approve pricing successfully")
        void shouldApprovePricingSuccessfully() {
            // Given
            Long pricingId = 1L;
            String approvalNotes = "Approved after review";

            savedPricing.setStatus(PricingStatus.PENDING_APPROVAL);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(pricingId)).thenReturn(Optional.of(savedPricing));
            when(schoolPricingRepository.save(any(SchoolPricing.class))).thenReturn(savedPricing);
            when(converterService.mapToDto(savedPricing)).thenReturn(expectedPricingDto);

            // When
            SchoolPricingDto result = pricingService.approveSchoolPricing(pricingId, approvalNotes, request);

            // Then
            assertNotNull(result);

            verify(schoolPricingRepository).deactivateCurrentPricingForSchoolAndGrade(
                    1L, "Grade 1", "2024-2025", 1L, 1L);
            verify(schoolPricingRepository).save(argThat(pricing ->
                    pricing.getStatus() == PricingStatus.ACTIVE &&
                            pricing.getIsCurrent().equals(true) &&
                            pricing.getApprovedBy().equals(1L) &&
                            pricing.getApprovalNotes().equals(approvalNotes)
            ));
        }

        @Test
        @DisplayName("Should throw BusinessException when pricing not in pending approval status")
        void shouldThrowBusinessExceptionWhenPricingNotInPendingApprovalStatus() {
            // Given
            Long pricingId = 1L;
            savedPricing.setStatus(PricingStatus.DRAFT); // Wrong status

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(pricingId)).thenReturn(Optional.of(savedPricing));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pricingService.approveSchoolPricing(pricingId, "notes", request));

            assertEquals("Pricing is not in pending approval status", exception.getMessage());
            verify(schoolPricingRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("calculateTotalCost() Tests")
    class CalculateTotalCostTests {

        private PricingCalculationRequestDto calculationRequest;

        @BeforeEach
        void setUp() {
            calculationRequest = PricingCalculationRequestDto.builder()
                    .schoolId(1L)
                    .gradeLevel("Grade 1")
                    .academicYear("2024-2025")
                    .hasSibling(true)
                    .earlyPayment(true)
                    .build();
        }

        @Test
        @DisplayName("Should calculate total cost with discounts correctly")
        void shouldCalculateTotalCostWithDiscountsCorrectly() {
            // Given
            savedPricing.setSiblingDiscountPercentage(10.0);
            savedPricing.setEarlyPaymentDiscountPercentage(5.0);
            savedPricing.setTotalOneTimeFees(new BigDecimal("5000.00"));
            savedPricing.setInstallmentCount(10);
            savedPricing.setDownPaymentPercentage(20.0);

            when(schoolPricingRepository.findCurrentPricingBySchoolAndGradeAndYear(1L, "Grade 1", "2024-2025"))
                    .thenReturn(Optional.of(savedPricing));

            // When
            PricingCalculationDto result = pricingService.calculateTotalCost(calculationRequest);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getSchoolId());
            assertEquals("Grade 1", result.getGradeLevel());

            // Base tuition: 15000
            // Sibling discount: 15000 * 10% = 1500
            // Early payment discount: 15000 * 5% = 750
            // Total discounts: 1500 + 750 = 2250
            // Final amount: 15000 + 5000 (one-time) - 2250 (discounts) = 17750

            assertEquals(new BigDecimal("15000.00"), result.getBaseTuition());
            assertEquals(new BigDecimal("5000.00"), result.getOneTimeFees());
            assertEquals(new BigDecimal("1500.00"), result.getSiblingDiscount());
            assertEquals(new BigDecimal("750.00"), result.getEarlyPaymentDiscount());
            assertEquals(new BigDecimal("2250.00"), result.getTotalDiscounts());
            assertEquals(new BigDecimal("17750.00"), result.getFinalAmount());
        }

        @Test
        @DisplayName("Should calculate installment plan when available")
        void shouldCalculateInstallmentPlanWhenAvailable() {
            // Given
            savedPricing.setTotalOneTimeFees(new BigDecimal("2000.00"));
            savedPricing.setInstallmentCount(12);
            savedPricing.setDownPaymentPercentage(25.0);

            when(schoolPricingRepository.findCurrentPricingBySchoolAndGradeAndYear(1L, "Grade 1", "2024-2025"))
                    .thenReturn(Optional.of(savedPricing));

            // When
            PricingCalculationDto result = pricingService.calculateTotalCost(calculationRequest);

            // Then
            assertNotNull(result);
            assertEquals(Integer.valueOf(12), result.getInstallmentCount());

            // Down payment: 17000 * 25% = 4250
            // Remaining: 17000 - 4250 = 12750
            // Monthly installment: 12750 / 12 = 1062.50

            assertEquals(new BigDecimal("4250.00"), result.getDownPayment());
            assertEquals(new BigDecimal("1062.50"), result.getInstallmentAmount());
        }

        @Test
        @DisplayName("Should handle case without discounts")
        void shouldHandleCaseWithoutDiscounts() {
            // Given
            calculationRequest.setHasSibling(false);
            calculationRequest.setEarlyPayment(false);

            savedPricing.setSiblingDiscountPercentage(10.0); // Available but not applied
            savedPricing.setEarlyPaymentDiscountPercentage(5.0); // Available but not applied
            savedPricing.setTotalOneTimeFees(new BigDecimal("3000.00"));

            when(schoolPricingRepository.findCurrentPricingBySchoolAndGradeAndYear(1L, "Grade 1", "2024-2025"))
                    .thenReturn(Optional.of(savedPricing));

            // When
            PricingCalculationDto result = pricingService.calculateTotalCost(calculationRequest);

            // Then
            assertEquals(new BigDecimal("15000.00"), result.getBaseTuition());
            assertEquals(new BigDecimal("3000.00"), result.getOneTimeFees());
            assertEquals(BigDecimal.ZERO, result.getTotalDiscounts());
            assertEquals(new BigDecimal("18000.00"), result.getFinalAmount());
            assertNull(result.getSiblingDiscount());
            assertNull(result.getEarlyPaymentDiscount());
        }
    }

    @Nested
    @DisplayName("bulkUpdatePricing() Tests")
    class BulkUpdatePricingTests {

        private BulkPricingUpdateDto validBulkDto;
        private List<BulkPricingItemDto> pricingItems;

        @BeforeEach
        void setUp() {
            BulkPricingItemDto item1 = BulkPricingItemDto.builder()
                    .pricingId(1L)
                    .updateType("PERCENTAGE_INCREASE")
                    .percentage(10.0)
                    .build();

            BulkPricingItemDto item2 = BulkPricingItemDto.builder()
                    .pricingId(2L)
                    .updateType("FIXED_AMOUNT_INCREASE")
                    .amount(500.0)
                    .build();

            pricingItems = List.of(item1, item2);

            validBulkDto = BulkPricingUpdateDto.builder()
                    .pricingUpdates(pricingItems)
                    .build();
        }

        @Test
        @DisplayName("Should perform bulk percentage increase successfully")
        void shouldPerformBulkPercentageIncreaseSuccessfully() {
            // Given
            SchoolPricing pricing1 = new SchoolPricing();
            pricing1.setId(1L);
            pricing1.setSchool(validSchool);
            pricing1.setMonthlyTuition(new BigDecimal("1000.00"));
            pricing1.setAnnualTuition(new BigDecimal("12000.00"));

            SchoolPricing pricing2 = new SchoolPricing();
            pricing2.setId(2L);
            pricing2.setSchool(validSchool);
            pricing2.setMonthlyTuition(new BigDecimal("1500.00"));

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(pricing1));
            when(schoolPricingRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.of(pricing2));
            when(schoolPricingRepository.save(any(SchoolPricing.class))).thenReturn(pricing1, pricing2);
            when(priceHistoryRepository.save(any(PriceHistory.class))).thenReturn(new PriceHistory());

            // When
            BulkPricingResultDto result = pricingService.bulkUpdatePricing(validBulkDto, request);

            // Then
            assertNotNull(result);
            assertTrue(result.getSuccess());
            assertEquals(Integer.valueOf(2), result.getSuccessfulOperations());
            assertEquals(Integer.valueOf(0), result.getFailedOperations());

            // Verify percentage increase: 1000 * 1.10 = 1100
            verify(schoolPricingRepository).save(argThat(pricing ->
                    pricing.getId().equals(1L) &&
                            pricing.getMonthlyTuition().compareTo(new BigDecimal("1100.00")) == 0
            ));

            // Verify fixed amount increase: 1500 + 500 = 2000
            verify(schoolPricingRepository).save(argThat(pricing ->
                    pricing.getId().equals(2L) &&
                            pricing.getMonthlyTuition().compareTo(new BigDecimal("2000.00")) == 0
            ));

            // Verify price history records are created
            verify(priceHistoryRepository, atLeast(2)).save(any(PriceHistory.class));
        }

        @Test
        @DisplayName("Should handle partial failures gracefully")
        void shouldHandlePartialFailuresGracefully() {
            // Given
            SchoolPricing pricing1 = new SchoolPricing();
            pricing1.setId(1L);
            pricing1.setSchool(validSchool);
            pricing1.setMonthlyTuition(new BigDecimal("1000.00"));

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolPricingRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(pricing1));
            when(schoolPricingRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.empty()); // Not found
            when(schoolPricingRepository.save(any(SchoolPricing.class))).thenReturn(pricing1);

            // When
            BulkPricingResultDto result = pricingService.bulkUpdatePricing(validBulkDto, request);

            // Then
            assertNotNull(result);
            assertFalse(result.getSuccess()); // Not fully successful
            assertEquals(Integer.valueOf(1), result.getSuccessfulOperations());
            assertEquals(Integer.valueOf(1), result.getFailedOperations());
            assertEquals(1, result.getErrors().size());
            assertTrue(result.getErrors().get(0).contains("Pricing ID 2"));
        }
    }

    private UserRole createUserRole(RoleLevel roleLevel) {
        UserRole mockRole = new UserRole();
        mockRole.setRoleLevel(roleLevel);
        return mockRole;
    }

    // Helper methods
    private User createUser(Long id, RoleLevel roleLevel) {
        User user = new User();
        user.setId(id);
        //user.setUserRoles(createMockUserRoles(roleLevel));
        user.setUserRoles(Set.of(createUserRole(roleLevel)));
        user.setInstitutionAccess(Collections.emptySet());
        return user;
    }

    private List<MockUserRole> createMockUserRoles(RoleLevel roleLevel) {
        MockUserRole mockRole = new MockUserRole();
        mockRole.setRoleLevel(roleLevel);
        return List.of(mockRole);
    }

    // Mock inner class for UserRole
    @Setter
    @Getter
    private static class MockUserRole {
        private RoleLevel roleLevel;

    }


}