package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.dto.campaign.*;
import com.genixo.education.search.entity.campaign.Campaign;
import com.genixo.education.search.entity.campaign.CampaignSchool;
import com.genixo.education.search.entity.campaign.CampaignUsage;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.campaign.*;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.CampaignConverterService;

import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CampaignService Tests")
class CampaignServiceTest {

    @Mock private CampaignRepository campaignRepository;
    @Mock private CampaignSchoolRepository campaignSchoolRepository;
    @Mock private CampaignUsageRepository campaignUsageRepository;
    @Mock private SchoolRepository schoolRepository;
    @Mock private CampaignConverterService converterService;
    @Mock private JwtService jwtService;
    @Mock private HttpServletRequest request;

    @InjectMocks
    private CampaignService campaignService;

    private User systemUser;
    private User campusUser;
    private User regularUser;
    private CampaignCreateDto validCampaignCreateDto;
    private Campaign savedCampaign;
    private CampaignDto expectedCampaignDto;
    private School validSchool;

    @BeforeEach
    void setUp() {
        // System user with SYSTEM role (can create campaigns)
        systemUser = createUser(1L, RoleLevel.SYSTEM);

        // Campus user with CAMPUS role (can create campaigns)
        campusUser = createUser(2L, RoleLevel.CAMPUS);

        // Regular user without campaign creation permissions
        regularUser = createUser(3L, RoleLevel.SCHOOL);

        // Valid school for assignments
        validSchool = new School();
        validSchool.setId(1L);
        validSchool.setName("Test School");
        validSchool.setIsActive(true);

        // Valid campaign create DTO
        validCampaignCreateDto = CampaignCreateDto.builder()
                .title("Early Bird Discount")
                .description("Get 20% off on early registrations")
                .shortDescription("20% Early Bird Discount")
                .campaignType(CampaignType.EARLY_BIRD)
                .discountType(DiscountType.PERCENTAGE)
                .discountPercentage(20.0)
                .maxDiscountAmount(new BigDecimal("5000"))
                .minPurchaseAmount(new BigDecimal("10000"))
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .earlyBirdEndDate(LocalDate.now().plusDays(15))
                .registrationDeadline(LocalDate.now().plusDays(25))
                .enrollmentStartDate(LocalDate.now().plusDays(5))
                .enrollmentEndDate(LocalDate.now().plusDays(35))
                .academicYear("2024-2025")
                .isFeatured(true)
                .isPublic(true)
                .requiresApproval(false)
                .usageLimit(100)
                .perUserLimit(1)
                .perSchoolLimit(50)
                .targetAudience(TargetAudience.NEW_STUDENTS)
                .targetGradeLevels("K-5")
                .targetAgeMin(5)
                .targetAgeMax(11)
                .targetNewStudentsOnly(true)
                .targetSiblingDiscount(false)
                .promoCode("EARLY2024")
                .bannerImageUrl("https://example.com/banner.jpg")
                .thumbnailImageUrl("https://example.com/thumb.jpg")
                .videoUrl("https://example.com/video.mp4")
                .ctaText("Register Now")
                .ctaUrl("https://example.com/register")
                .badgeText("Limited Time")
                .badgeColor("#FF5722")
                .termsAndConditions("Terms apply")
                .finePrint("Valid until stock lasts")
                .exclusions("Cannot be combined with other offers")
                .metaTitle("Early Bird Campaign")
                .metaDescription("Special discount for early birds")
                .metaKeywords("discount,early,registration")
                .freeTrialDays(7)
                .installmentOptions("3,6,12 months")
                .paymentDeadlineDays(30)
                .refundPolicy("30-day money back")
                .freeServices("Free orientation")
                .bonusFeatures("Extra learning materials")
                .giftItems("Welcome kit")
                .priority(1)
                .sortOrder(10)
                .createdByUserId(1L)
                .schoolIds(List.of(1L))
                .build();

        // Saved campaign entity
        savedCampaign = new Campaign();
        savedCampaign.setId(1L);
        savedCampaign.setTitle("Early Bird Discount");
        savedCampaign.setSlug("early-bird-discount");
        savedCampaign.setStatus(CampaignStatus.DRAFT);
        savedCampaign.setCreatedBy(1L);
        savedCampaign.setCreatedAt(LocalDateTime.now());
        savedCampaign.setUsageCount(0);

        // Expected DTO response
        expectedCampaignDto = CampaignDto.builder()
                .id(1L)
                .title("Early Bird Discount")
                .slug("early-bird-discount")
                .status(CampaignStatus.DRAFT)
                .build();
    }

    @Nested
    @DisplayName("createCampaign() Tests")
    class CreateCampaignTests {

        @Test
        @DisplayName("Should create campaign successfully with valid data and school assignments")
        void shouldCreateCampaignSuccessfullyWithValidDataAndSchoolAssignments() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.existsByPromoCodeIgnoreCase("EARLY2024")).thenReturn(false);
            when(campaignRepository.existsBySlug("early-bird-discount")).thenReturn(false);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(savedCampaign);
            when(converterService.mapToDto(savedCampaign)).thenReturn(expectedCampaignDto);
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validSchool));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndIsActiveTrue(anyLong(), anyLong())).thenReturn(false);
            when(campaignSchoolRepository.save(any(CampaignSchool.class))).thenReturn(new CampaignSchool());

            // When
            CampaignDto result = campaignService.createCampaign(validCampaignCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("Early Bird Discount", result.getTitle());
            assertEquals("early-bird-discount", result.getSlug());
            assertEquals(CampaignStatus.DRAFT, result.getStatus());

            verify(jwtService).getUser(request);
            verify(campaignRepository).existsByPromoCodeIgnoreCase("EARLY2024");
            verify(campaignRepository).existsBySlug("early-bird-discount");
            verify(campaignRepository).save(argThat(campaign ->
                    campaign.getTitle().equals("Early Bird Discount") &&
                            campaign.getSlug().equals("early-bird-discount") &&
                            campaign.getStatus() == CampaignStatus.DRAFT &&
                            campaign.getCampaignType() == CampaignType.EARLY_BIRD &&
                            campaign.getDiscountType() == DiscountType.PERCENTAGE &&
                            campaign.getDiscountPercentage().equals(20.0) &&
                            campaign.getCreatedBy().equals(1L)
            ));

            // Verify school assignment
            verify(schoolRepository).findByIdAndIsActiveTrue(1L);
            verify(campaignSchoolRepository).save(any(CampaignSchool.class));
            verify(converterService).mapToDto(savedCampaign);
        }

        @Test
        @DisplayName("Should throw BusinessException when user cannot create campaign")
        void shouldThrowExceptionWhenUserCannotCreateCampaign() {
            // Given
            when(jwtService.getUser(request)).thenReturn(regularUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.createCampaign(validCampaignCreateDto, request));

            assertEquals("User does not have permission to create campaigns", exception.getMessage());

            verify(jwtService).getUser(request);
            verifyNoInteractions(campaignRepository, converterService);
        }

        @Test
        @DisplayName("Should allow campus user to create campaign")
        void shouldAllowCampusUserToCreateCampaign() {
            // Given
            when(jwtService.getUser(request)).thenReturn(campusUser);
            when(campaignRepository.existsByPromoCodeIgnoreCase(anyString())).thenReturn(false);
            when(campaignRepository.existsBySlug(anyString())).thenReturn(false);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(savedCampaign);
            when(converterService.mapToDto(any(Campaign.class))).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.createCampaign(validCampaignCreateDto, request);

            // Then
            assertNotNull(result);
            verify(jwtService).getUser(request);
            verify(campaignRepository).save(any(Campaign.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when promo code already exists")
        void shouldThrowExceptionWhenPromoCodeExists() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.existsByPromoCodeIgnoreCase("EARLY2024")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.createCampaign(validCampaignCreateDto, request));

            assertEquals("Promo code already exists: EARLY2024", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).existsByPromoCodeIgnoreCase("EARLY2024");
            verify(campaignRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when start date is after end date")
        void shouldThrowExceptionWhenStartDateAfterEndDate() {
            // Given
            CampaignCreateDto invalidDateDto = CampaignCreateDto.builder()
                    .title("Invalid Date Campaign")
                    .campaignType(CampaignType.DISCOUNT)
                    .discountType(DiscountType.PERCENTAGE)
                    .discountPercentage(10.0)
                    .startDate(LocalDate.now().plusDays(30))
                    .endDate(LocalDate.now().plusDays(15)) // End date before start date
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.createCampaign(invalidDateDto, request));

            assertEquals("Start date cannot be after end date", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should generate unique slug when original slug exists")
        void shouldGenerateUniqueSlugWhenSlugExists() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.existsByPromoCodeIgnoreCase(anyString())).thenReturn(false);
            when(campaignRepository.existsBySlug("early-bird-discount")).thenReturn(true);
            when(campaignRepository.existsBySlug("early-bird-discount-1")).thenReturn(false);

            Campaign campaignWithUniqueSlug = new Campaign();
            campaignWithUniqueSlug.setId(1L);
            campaignWithUniqueSlug.setSlug("early-bird-discount-1");

            when(campaignRepository.save(any(Campaign.class))).thenReturn(campaignWithUniqueSlug);
            when(converterService.mapToDto(any(Campaign.class))).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.createCampaign(validCampaignCreateDto, request);

            // Then
            assertNotNull(result);

            verify(campaignRepository).existsBySlug("early-bird-discount");
            verify(campaignRepository).save(argThat(campaign ->
                    campaign.getSlug().startsWith("early-bird-discount") &&
                            !campaign.getSlug().equals("early-bird-discount")
            ));
        }

        @Test
        @DisplayName("Should handle campaign creation without school assignments")
        void shouldHandleCampaignCreationWithoutSchoolAssignments() {
            // Given
            CampaignCreateDto noSchoolsDto = CampaignCreateDto.builder()
                    .title("No Schools Campaign")
                    .campaignType(CampaignType.DISCOUNT)
                    .discountType(DiscountType.FIXED_AMOUNT)
                    .discountAmount(new BigDecimal("1000"))
                    .startDate(LocalDate.now().plusDays(1))
                    .endDate(LocalDate.now().plusDays(30))
                    .schoolIds(null) // No school assignments
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.existsBySlug(anyString())).thenReturn(false);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(savedCampaign);
            when(converterService.mapToDto(any(Campaign.class))).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.createCampaign(noSchoolsDto, request);

            // Then
            assertNotNull(result);
            verify(campaignRepository).save(any(Campaign.class));
            verifyNoInteractions(schoolRepository, campaignSchoolRepository);
        }

        @Test
        @DisplayName("Should set all campaign fields correctly from create DTO")
        void shouldSetAllCampaignFieldsCorrectly() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.existsByPromoCodeIgnoreCase(anyString())).thenReturn(false);
            when(campaignRepository.existsBySlug(anyString())).thenReturn(false);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(savedCampaign);
            when(converterService.mapToDto(any(Campaign.class))).thenReturn(expectedCampaignDto);

            // When
            campaignService.createCampaign(validCampaignCreateDto, request);

            // Then
            verify(campaignRepository).save(argThat(campaign ->
                    campaign.getTitle().equals("Early Bird Discount") &&
                            campaign.getDescription().equals("Get 20% off on early registrations") &&
                            campaign.getShortDescription().equals("20% Early Bird Discount") &&
                            campaign.getCampaignType() == CampaignType.EARLY_BIRD &&
                            campaign.getDiscountType() == DiscountType.PERCENTAGE &&
                            campaign.getDiscountPercentage().equals(20.0) &&
                            campaign.getMaxDiscountAmount().equals(new BigDecimal("5000")) &&
                            campaign.getMinPurchaseAmount().equals(new BigDecimal("10000")) &&
                            campaign.getStartDate().equals(validCampaignCreateDto.getStartDate()) &&
                            campaign.getEndDate().equals(validCampaignCreateDto.getEndDate()) &&
                            campaign.getEarlyBirdEndDate().equals(validCampaignCreateDto.getEarlyBirdEndDate()) &&
                            campaign.getAcademicYear().equals("2024-2025") &&
                            campaign.getStatus() == CampaignStatus.DRAFT &&
                            campaign.getIsFeatured().equals(true) &&
                            campaign.getIsPublic().equals(true) &&
                            campaign.getRequiresApproval().equals(false) &&
                            campaign.getUsageLimit().equals(100) &&
                            campaign.getPerUserLimit().equals(1) &&
                            campaign.getPerSchoolLimit().equals(50) &&
                            campaign.getTargetAudience() == TargetAudience.NEW_STUDENTS &&
                            campaign.getTargetGradeLevels().equals("K-5") &&
                            campaign.getTargetAgeMin().equals(5) &&
                            campaign.getTargetAgeMax().equals(11) &&
                            campaign.getPromoCode().equals("EARLY2024") &&
                            campaign.getBannerImageUrl().equals("https://example.com/banner.jpg") &&
                            campaign.getThumbnailImageUrl().equals("https://example.com/thumb.jpg") &&
                            campaign.getVideoUrl().equals("https://example.com/video.mp4") &&
                            campaign.getCtaText().equals("Register Now") &&
                            campaign.getCtaUrl().equals("https://example.com/register") &&
                            campaign.getBadgeText().equals("Limited Time") &&
                            campaign.getBadgeColor().equals("#FF5722") &&
                            campaign.getTermsAndConditions().equals("Terms apply") &&
                            campaign.getFinePrint().equals("Valid until stock lasts") &&
                            campaign.getExclusions().equals("Cannot be combined with other offers") &&
                            campaign.getMetaTitle().equals("Early Bird Campaign") &&
                            campaign.getFreeTrialDays().equals(7) &&
                            campaign.getInstallmentOptions().equals("3,6,12 months") &&
                            campaign.getRefundPolicy().equals("30-day money back") &&
                            campaign.getPriority().equals(1) &&
                            campaign.getSortOrder().equals(10) &&
                            campaign.getCreatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should handle minimal campaign data")
        void shouldHandleMinimalCampaignData() {
            // Given
            CampaignCreateDto minimalDto = CampaignCreateDto.builder()
                    .title("Minimal Campaign")
                    .campaignType(CampaignType.DISCOUNT)
                    .discountType(DiscountType.PERCENTAGE)
                    .discountPercentage(10.0)
                    .startDate(LocalDate.now().plusDays(1))
                    .endDate(LocalDate.now().plusDays(30))
                    // All optional fields are null
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.existsBySlug(anyString())).thenReturn(false);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(savedCampaign);
            when(converterService.mapToDto(any(Campaign.class))).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.createCampaign(minimalDto, request);

            // Then
            assertNotNull(result);
            verify(campaignRepository).save(argThat(campaign ->
                    campaign.getTitle().equals("Minimal Campaign") &&
                            campaign.getDescription() == null &&
                            campaign.getPromoCode() == null &&
                            campaign.getUsageLimit() == null &&
                            campaign.getBannerImageUrl() == null
            ));
        }

        @Test
        @DisplayName("Should handle campaign creation without promo code")
        void shouldHandleCampaignCreationWithoutPromoCode() {
            // Given
            CampaignCreateDto noPromoCodeDto = CampaignCreateDto.builder()
                    .title("No Promo Campaign")
                    .campaignType(CampaignType.DISCOUNT)
                    .discountType(DiscountType.PERCENTAGE)
                    .discountPercentage(15.0)
                    .startDate(LocalDate.now().plusDays(1))
                    .endDate(LocalDate.now().plusDays(30))
                    .promoCode(null) // No promo code
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.existsBySlug(anyString())).thenReturn(false);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(savedCampaign);
            when(converterService.mapToDto(any(Campaign.class))).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.createCampaign(noPromoCodeDto, request);

            // Then
            assertNotNull(result);
            // Should not check promo code uniqueness when promo code is null
            verify(campaignRepository, never()).existsByPromoCodeIgnoreCase(anyString());
            verify(campaignRepository).save(argThat(campaign ->
                    campaign.getPromoCode() == null
            ));
        }

        @Test
        @DisplayName("Should handle empty promo code")
        void shouldHandleEmptyPromoCode() {
            // Given
            CampaignCreateDto emptyPromoCodeDto = CampaignCreateDto.builder()
                    .title("Empty Promo Campaign")
                    .campaignType(CampaignType.DISCOUNT)
                    .discountType(DiscountType.PERCENTAGE)
                    .discountPercentage(15.0)
                    .startDate(LocalDate.now().plusDays(1))
                    .endDate(LocalDate.now().plusDays(30))
                    .promoCode("") // Empty string
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.existsBySlug(anyString())).thenReturn(false);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(savedCampaign);
            when(converterService.mapToDto(any(Campaign.class))).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.createCampaign(emptyPromoCodeDto, request);

            // Then
            assertNotNull(result);
            // Should not check promo code uniqueness when promo code is empty
            verify(campaignRepository, never()).existsByPromoCodeIgnoreCase(anyString());
        }
    }

    // ================================ USAGE MANAGEMENT TESTS ================================

    @Nested
    @DisplayName("createCampaignUsage() Tests")
    class CreateCampaignUsageTests {

        private CampaignUsageCreateDto validUsageCreateDto;
        private Campaign activeCampaign;
        private CampaignUsage savedUsage;
        private CampaignUsageDto expectedUsageDto;

        @BeforeEach
        void setUp() {
            validUsageCreateDto = CampaignUsageCreateDto.builder()
                    .campaignId(1L)
                    .schoolId(1L)
                    .userId(1L)
                    .usageType(CampaignUsageType.ENROLLMENT)
                    .originalAmount(new BigDecimal("10000"))
                    .promoCodeUsed("EARLY2024")
                    .studentName("John Doe")
                    .studentAge(8)
                    .gradeLevel("3rd Grade")
                    .enrollmentYear("2024-2025")
                    .parentName("Jane Doe")
                    .parentEmail("jane.doe@example.com")
                    .parentPhone("+90 555 123 4567")
                    .ipAddress("192.168.1.1")
                    .userAgent("Mozilla/5.0")
                    .referrerUrl("https://example.com/schools")
                    .utmSource("google")
                    .utmMedium("cpc")
                    .utmCampaign("early_bird")
                    .notes("Parent interested in enrollment")
                    .followUpRequired(true)
                    .followUpDate(LocalDateTime.now().plusDays(3))
                    .build();

            activeCampaign = new Campaign();
            activeCampaign.setId(1L);
            activeCampaign.setTitle("Active Campaign");
            activeCampaign.setStartDate(LocalDate.now().minusDays(1));
            activeCampaign.setEndDate(LocalDate.now().plusDays(30));
            activeCampaign.setDiscountType(DiscountType.PERCENTAGE);
            activeCampaign.setDiscountPercentage(20.0);
            activeCampaign.setMaxDiscountAmount(new BigDecimal("5000"));
            activeCampaign.setUsageLimit(100);
            activeCampaign.setUsageCount(50);
            activeCampaign.setPerUserLimit(2);
            activeCampaign.setPerSchoolLimit(20);
            activeCampaign.setPromoCode("EARLY2024");


            savedUsage = new CampaignUsage();
            savedUsage.setId(1L);
            savedUsage.setCampaign(activeCampaign);
            savedUsage.setSchool(validSchool);
            savedUsage.setOriginalAmount(new BigDecimal("10000"));
            savedUsage.setDiscountAmount(new BigDecimal("2000"));
            savedUsage.setFinalAmount(new BigDecimal("8000"));

            expectedUsageDto = CampaignUsageDto.builder()
                    .id(1L)
                    .campaignId(1L)
                    .schoolId(1L)
                    .originalAmount(new BigDecimal("10000"))
                    .discountAmount(new BigDecimal("2000"))
                    .finalAmount(new BigDecimal("8000"))
                    .build();
        }

        @Test
        @DisplayName("Should create campaign usage successfully with percentage discount")
        void shouldCreateCampaignUsageSuccessfullyWithPercentageDiscount() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(activeCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validSchool));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, 1L, CampaignSchoolStatus.ACTIVE)).thenReturn(true);
            when(campaignUsageRepository.countByCampaignIdAndUserIdAndStatusNot(1L, 1L, CampaignUsageStatus.CANCELLED)).thenReturn(0L);
            when(campaignUsageRepository.countByCampaignIdAndSchoolIdAndStatusNot(1L, 1L, CampaignUsageStatus.CANCELLED)).thenReturn(5L);
            when(campaignUsageRepository.save(any(CampaignUsage.class))).thenReturn(savedUsage);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(activeCampaign);
            when(converterService.mapCampaignUsageToDto(savedUsage)).thenReturn(expectedUsageDto);

            // When
            CampaignUsageDto result = campaignService.createCampaignUsage(validUsageCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals(new BigDecimal("10000"), result.getOriginalAmount());
            assertEquals(new BigDecimal("2000"), result.getDiscountAmount());
            assertEquals(new BigDecimal("8000"), result.getFinalAmount());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(1L);
            verify(schoolRepository).findByIdAndIsActiveTrue(1L);
            verify(campaignSchoolRepository).existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, 1L, CampaignSchoolStatus.ACTIVE);
            verify(campaignUsageRepository).save(argThat(usage ->
                    usage.getCampaign().getId().equals(1L) &&
                            usage.getSchool().getId().equals(1L) &&
                            usage.getUser().getId().equals(1L) &&
                            usage.getUsageType() == CampaignUsageType.ENROLLMENT &&
                            usage.getStatus() == CampaignUsageStatus.PENDING &&
                            usage.getOriginalAmount().equals(new BigDecimal("10000")) &&
                            usage.getDiscountAmount().equals(new BigDecimal("2000")) &&
                            usage.getFinalAmount().equals(new BigDecimal("8000")) &&
                            usage.getPromoCodeUsed().equals("EARLY2024") &&
                            usage.getStudentName().equals("John Doe") &&
                            usage.getValidationCode() != null &&
                            usage.getValidationExpiresAt() != null
            ));
            // Verify campaign usage count is incremented
            verify(campaignRepository).save(argThat(campaign ->
                    campaign.getUsageCount().equals(51)
            ));
        }

        @Test
        @DisplayName("Should create usage with fixed amount discount")
        void shouldCreateUsageWithFixedAmountDiscount() {
            // Given
            activeCampaign.setDiscountType(DiscountType.FIXED_AMOUNT);
            activeCampaign.setDiscountAmount(new BigDecimal("1500"));
            activeCampaign.setDiscountPercentage(null);

            CampaignUsage fixedDiscountUsage = new CampaignUsage();
            fixedDiscountUsage.setDiscountAmount(new BigDecimal("1500"));
            fixedDiscountUsage.setFinalAmount(new BigDecimal("8500"));

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(activeCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validSchool));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, 1L, CampaignSchoolStatus.ACTIVE)).thenReturn(true);
            when(campaignUsageRepository.countByCampaignIdAndUserIdAndStatusNot(anyLong(), anyLong(), any())).thenReturn(0L);
            when(campaignUsageRepository.countByCampaignIdAndSchoolIdAndStatusNot(anyLong(), anyLong(), any())).thenReturn(5L);
            when(campaignUsageRepository.save(any(CampaignUsage.class))).thenReturn(fixedDiscountUsage);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(activeCampaign);
            when(converterService.mapCampaignUsageToDto(any(CampaignUsage.class))).thenReturn(expectedUsageDto);

            // When
            campaignService.createCampaignUsage(validUsageCreateDto, request);

            // Then
            verify(campaignUsageRepository).save(argThat(usage ->
                    usage.getDiscountAmount().equals(new BigDecimal("1500")) &&
                            usage.getFinalAmount().equals(new BigDecimal("8500"))
            ));
        }

        @Test
        @DisplayName("Should throw BusinessException when campaign is not active for school")
        void shouldThrowExceptionWhenCampaignNotActiveForSchool() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(activeCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validSchool));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, 1L, CampaignSchoolStatus.ACTIVE)).thenReturn(false);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.createCampaignUsage(validUsageCreateDto, request));

            assertEquals("Campaign is not active for this school", exception.getMessage());

            verify(campaignUsageRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when campaign is not currently active")
        void shouldThrowExceptionWhenCampaignNotCurrentlyActive() {
            // Given
            activeCampaign.setStartDate(LocalDate.now().plusDays(5)); // Future start date

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(activeCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validSchool));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, 1L, CampaignSchoolStatus.ACTIVE)).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.createCampaignUsage(validUsageCreateDto, request));

            assertEquals("Campaign is not currently active", exception.getMessage());

            verify(campaignUsageRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when campaign usage limit exceeded")
        void shouldThrowExceptionWhenUsageLimitExceeded() {
            // Given
            activeCampaign.setUsageLimit(100);
            activeCampaign.setUsageCount(100); // Already at limit

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(activeCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validSchool));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, 1L, CampaignSchoolStatus.ACTIVE)).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.createCampaignUsage(validUsageCreateDto, request));

            assertEquals("Campaign usage limit exceeded", exception.getMessage());

            verify(campaignUsageRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when per-user limit exceeded")
        void shouldThrowExceptionWhenPerUserLimitExceeded() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(activeCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validSchool));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, 1L, CampaignSchoolStatus.ACTIVE)).thenReturn(true);
            when(campaignUsageRepository.countByCampaignIdAndUserIdAndStatusNot(1L, 1L, CampaignUsageStatus.CANCELLED)).thenReturn(2L); // Already at limit

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.createCampaignUsage(validUsageCreateDto, request));

            assertEquals("User usage limit exceeded for this campaign", exception.getMessage());

            verify(campaignUsageRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when per-school limit exceeded")
        void shouldThrowExceptionWhenPerSchoolLimitExceeded() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(activeCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validSchool));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, 1L, CampaignSchoolStatus.ACTIVE)).thenReturn(true);
            when(campaignUsageRepository.countByCampaignIdAndUserIdAndStatusNot(1L, 1L, CampaignUsageStatus.CANCELLED)).thenReturn(0L);
            when(campaignUsageRepository.countByCampaignIdAndSchoolIdAndStatusNot(1L, 1L, CampaignUsageStatus.CANCELLED)).thenReturn(20L); // Already at limit

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.createCampaignUsage(validUsageCreateDto, request));

            assertEquals("School usage limit exceeded for this campaign", exception.getMessage());

            verify(campaignUsageRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when invalid promo code used")
        void shouldThrowExceptionWhenInvalidPromoCodeUsed() {
            // Given
            validUsageCreateDto.setPromoCodeUsed("INVALID_CODE");

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(activeCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validSchool));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, 1L, CampaignSchoolStatus.ACTIVE)).thenReturn(true);
            when(campaignUsageRepository.countByCampaignIdAndUserIdAndStatusNot(anyLong(), anyLong(), any())).thenReturn(0L);
            when(campaignUsageRepository.countByCampaignIdAndSchoolIdAndStatusNot(anyLong(), anyLong(), any())).thenReturn(5L);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.createCampaignUsage(validUsageCreateDto, request));

            assertEquals("Invalid promo code", exception.getMessage());

            verify(campaignUsageRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should apply max discount limit when percentage exceeds maximum")
        void shouldApplyMaxDiscountLimitWhenPercentageExceedsMaximum() {
            // Given
            activeCampaign.setMaxDiscountAmount(new BigDecimal("1000")); // Lower max discount
            validUsageCreateDto.setOriginalAmount(new BigDecimal("20000")); // 20% would be 4000, but max is 1000

            CampaignUsage cappedUsage = new CampaignUsage();
            cappedUsage.setDiscountAmount(new BigDecimal("1000"));
            cappedUsage.setFinalAmount(new BigDecimal("19000"));

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(activeCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validSchool));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, 1L, CampaignSchoolStatus.ACTIVE)).thenReturn(true);
            when(campaignUsageRepository.countByCampaignIdAndUserIdAndStatusNot(anyLong(), anyLong(), any())).thenReturn(0L);
            when(campaignUsageRepository.countByCampaignIdAndSchoolIdAndStatusNot(anyLong(), anyLong(), any())).thenReturn(5L);
            when(campaignUsageRepository.save(any(CampaignUsage.class))).thenReturn(cappedUsage);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(activeCampaign);
            when(converterService.mapCampaignUsageToDto(any(CampaignUsage.class))).thenReturn(expectedUsageDto);

            // When
            campaignService.createCampaignUsage(validUsageCreateDto, request);

            // Then
            verify(campaignUsageRepository).save(argThat(usage ->
                    usage.getDiscountAmount().equals(new BigDecimal("1000")) && // Capped at max
                            usage.getFinalAmount().equals(new BigDecimal("19000"))
            ));
        }

        @Test
        @DisplayName("Should throw BusinessException when minimum purchase amount not met")
        void shouldThrowExceptionWhenMinPurchaseAmountNotMet() {
            // Given
            activeCampaign.setMinPurchaseAmount(new BigDecimal("15000"));
            validUsageCreateDto.setOriginalAmount(new BigDecimal("10000")); // Below minimum

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(activeCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validSchool));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, 1L, CampaignSchoolStatus.ACTIVE)).thenReturn(true);
            when(campaignUsageRepository.countByCampaignIdAndUserIdAndStatusNot(anyLong(), anyLong(), any())).thenReturn(0L);
            when(campaignUsageRepository.countByCampaignIdAndSchoolIdAndStatusNot(anyLong(), anyLong(), any())).thenReturn(5L);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.createCampaignUsage(validUsageCreateDto, request));

            assertEquals("Minimum purchase amount not met for this campaign", exception.getMessage());

            verify(campaignUsageRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("validatePromoCode() Tests")
    class ValidatePromoCodeTests {

        private Campaign validCampaign;

        @BeforeEach
        void setUp() {
            validCampaign = new Campaign();
            validCampaign.setId(1L);
            validCampaign.setTitle("Promo Campaign");
            validCampaign.setPromoCode("VALID2024");
            validCampaign.setStartDate(LocalDate.now().minusDays(1));
            validCampaign.setEndDate(LocalDate.now().plusDays(30));
            validCampaign.setIsActive(true);
        }

        @Test
        @DisplayName("Should validate promo code successfully when all conditions met")
        void shouldValidatePromoCodeSuccessfullyWhenAllConditionsMet() {
            // Given
            String promoCode = "VALID2024";
            Long schoolId = 1L;

            when(campaignRepository.findByPromoCodeIgnoreCaseAndIsActiveTrue(promoCode)).thenReturn(java.util.Optional.of(validCampaign));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, schoolId, CampaignSchoolStatus.ACTIVE)).thenReturn(true);
            when(converterService.mapToDto(validCampaign)).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.validatePromoCode(promoCode, schoolId);

            // Then
            assertNotNull(result);
            assertEquals(expectedCampaignDto.getId(), result.getId());

            verify(campaignRepository).findByPromoCodeIgnoreCaseAndIsActiveTrue(promoCode);
            verify(campaignSchoolRepository).existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, schoolId, CampaignSchoolStatus.ACTIVE);
            verify(converterService).mapToDto(validCampaign);
        }

        @Test
        @DisplayName("Should throw BusinessException when promo code not found")
        void shouldThrowExceptionWhenPromoCodeNotFound() {
            // Given
            String invalidPromoCode = "INVALID2024";
            Long schoolId = 1L;

            when(campaignRepository.findByPromoCodeIgnoreCaseAndIsActiveTrue(invalidPromoCode)).thenReturn(java.util.Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.validatePromoCode(invalidPromoCode, schoolId));

            assertEquals("Invalid promo code: INVALID2024", exception.getMessage());

            verify(campaignRepository).findByPromoCodeIgnoreCaseAndIsActiveTrue(invalidPromoCode);
            verifyNoInteractions(campaignSchoolRepository, converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when promo code not currently valid")
        void shouldThrowExceptionWhenPromoCodeNotCurrentlyValid() {
            // Given
            String promoCode = "EXPIRED2024";
            Long schoolId = 1L;
            validCampaign.setStartDate(LocalDate.now().plusDays(5)); // Future start date

            when(campaignRepository.findByPromoCodeIgnoreCaseAndIsActiveTrue(promoCode)).thenReturn(java.util.Optional.of(validCampaign));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.validatePromoCode(promoCode, schoolId));

            assertEquals("Promo code is not currently valid", exception.getMessage());

            verify(campaignRepository).findByPromoCodeIgnoreCaseAndIsActiveTrue(promoCode);
            verifyNoInteractions(campaignSchoolRepository, converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when promo code not valid for school")
        void shouldThrowExceptionWhenPromoCodeNotValidForSchool() {
            // Given
            String promoCode = "VALID2024";
            Long schoolId = 999L; // School not assigned to campaign

            when(campaignRepository.findByPromoCodeIgnoreCaseAndIsActiveTrue(promoCode)).thenReturn(java.util.Optional.of(validCampaign));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, schoolId, CampaignSchoolStatus.ACTIVE)).thenReturn(false);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.validatePromoCode(promoCode, schoolId));

            assertEquals("Promo code is not valid for this school", exception.getMessage());

            verify(campaignRepository).findByPromoCodeIgnoreCaseAndIsActiveTrue(promoCode);
            verify(campaignSchoolRepository).existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, schoolId, CampaignSchoolStatus.ACTIVE);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should handle case insensitive promo code validation")
        void shouldHandleCaseInsensitivePromoCodeValidation() {
            // Given
            String promoCode = "valid2024"; // Lowercase
            Long schoolId = 1L;

            when(campaignRepository.findByPromoCodeIgnoreCaseAndIsActiveTrue(promoCode)).thenReturn(java.util.Optional.of(validCampaign));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndStatusAndIsActiveTrue(1L, schoolId, CampaignSchoolStatus.ACTIVE)).thenReturn(true);
            when(converterService.mapToDto(validCampaign)).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.validatePromoCode(promoCode, schoolId);

            // Then
            assertNotNull(result);
            verify(campaignRepository).findByPromoCodeIgnoreCaseAndIsActiveTrue(promoCode);
        }
    }

    // ================================ SCHOOL ASSIGNMENT OPERATIONS TESTS ================================

    @Nested
    @DisplayName("assignSchoolsToCampaign() Tests")
    class AssignSchoolsToCampaignTests {

        @Test
        @DisplayName("Should assign multiple schools to campaign successfully")
        void shouldAssignMultipleSchoolsToCampaignSuccessfully() {
            // Given
            Long campaignId = 1L;
            List<Long> schoolIds = List.of(1L, 2L);

            Campaign foundCampaign = new Campaign();
            foundCampaign.setId(campaignId);
            foundCampaign.setTitle("Test Campaign");

            School school1 = new School();
            school1.setId(1L);
            school1.setName("School 1");

            School school2 = new School();
            school2.setId(2L);
            school2.setName("School 2");

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(foundCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(school1));
            when(schoolRepository.findByIdAndIsActiveTrue(2L)).thenReturn(java.util.Optional.of(school2));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, 1L)).thenReturn(false);
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, 2L)).thenReturn(false);
            when(campaignSchoolRepository.save(any(CampaignSchool.class))).thenReturn(new CampaignSchool());

            // When
            BulkCampaignResultDto result = campaignService.assignSchoolsToCampaign(campaignId, schoolIds, request);

            // Then
            assertNotNull(result);
            assertTrue(result.getSuccess());
            assertEquals(2, result.getSuccessfulOperations());
            assertEquals(0, result.getFailedOperations());
            assertEquals(schoolIds, result.getAffectedSchoolIds());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verify(schoolRepository, times(2)).findByIdAndIsActiveTrue(anyLong());
            verify(campaignSchoolRepository, times(2)).save(argThat(campaignSchool ->
                    campaignSchool.getCampaign().getId().equals(campaignId) &&
                            campaignSchool.getStatus() == CampaignSchoolStatus.ACTIVE &&
                            campaignSchool.getApprovedBySchool().equals(true) &&
                            campaignSchool.getCreatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should handle partial success when some schools already assigned")
        void shouldHandlePartialSuccessWhenSomeSchoolsAlreadyAssigned() {
            // Given
            Long campaignId = 1L;
            List<Long> schoolIds = List.of(1L, 2L);

            Campaign foundCampaign = new Campaign();
            foundCampaign.setId(campaignId);

            School school1 = new School();
            school1.setId(1L);
            school1.setName("School 1");

            School school2 = new School();
            school2.setId(2L);
            school2.setName("School 2");

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(foundCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(school1));
            when(schoolRepository.findByIdAndIsActiveTrue(2L)).thenReturn(java.util.Optional.of(school2));
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, 1L)).thenReturn(true); // Already assigned
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, 2L)).thenReturn(false);
            when(campaignSchoolRepository.save(any(CampaignSchool.class))).thenReturn(new CampaignSchool());

            // When
            BulkCampaignResultDto result = campaignService.assignSchoolsToCampaign(campaignId, schoolIds, request);

            // Then
            assertNotNull(result);
            assertTrue(result.getSuccess());
            assertEquals(1, result.getSuccessfulOperations());
            assertEquals(0, result.getFailedOperations());
            assertEquals(1, result.getWarnings().size());
            assertTrue(result.getWarnings().get(0).contains("School 1 is already assigned"));
            assertEquals(List.of(2L), result.getAffectedSchoolIds());

            verify(campaignSchoolRepository, times(1)).save(any(CampaignSchool.class));
        }

        @Test
        @DisplayName("Should handle errors when schools not found")
        void shouldHandleErrorsWhenSchoolsNotFound() {
            // Given
            Long campaignId = 1L;
            List<Long> schoolIds = List.of(1L, 999L); // Second school doesn't exist

            Campaign foundCampaign = new Campaign();
            foundCampaign.setId(campaignId);

            School school1 = new School();
            school1.setId(1L);
            school1.setName("School 1");

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(foundCampaign));
            when(schoolRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(school1));
            when(schoolRepository.findByIdAndIsActiveTrue(999L)).thenReturn(java.util.Optional.empty());
            when(campaignSchoolRepository.existsByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, 1L)).thenReturn(false);
            when(campaignSchoolRepository.save(any(CampaignSchool.class))).thenReturn(new CampaignSchool());

            // When
            BulkCampaignResultDto result = campaignService.assignSchoolsToCampaign(campaignId, schoolIds, request);

            // Then
            assertNotNull(result);
            assertFalse(result.getSuccess());
            assertEquals(1, result.getSuccessfulOperations());
            assertEquals(1, result.getFailedOperations());
            assertEquals(1, result.getErrors().size());
            assertTrue(result.getErrors().get(0).contains("School ID 999"));
            assertEquals(List.of(1L), result.getAffectedSchoolIds());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campaign not found")
        void shouldThrowResourceNotFoundExceptionWhenCampaignNotFound() {
            // Given
            Long nonExistentCampaignId = 999L;
            List<Long> schoolIds = List.of(1L);

            when(campaignRepository.findByIdAndIsActiveTrue(nonExistentCampaignId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> campaignService.assignSchoolsToCampaign(nonExistentCampaignId, schoolIds, request));

            assertEquals("Campaign not found", exception.getMessage());

            verify(campaignRepository).findByIdAndIsActiveTrue(nonExistentCampaignId);
            verifyNoInteractions(schoolRepository, campaignSchoolRepository);
        }
    }

    @Nested
    @DisplayName("updateCampaignSchoolAssignment() Tests")
    class UpdateCampaignSchoolAssignmentTests {

        private CampaignSchoolAssignDto assignDto;
        private Campaign existingCampaign;
        private CampaignSchool existingCampaignSchool;

        @BeforeEach
        void setUp() {
            assignDto = CampaignSchoolAssignDto.builder()
                    .campaignId(1L)
                    .customDiscountAmount(new BigDecimal("1500"))
                    .customDiscountPercentage(25.0)
                    .customUsageLimit(75)
                    .customStartDate(LocalDate.now().plusDays(3))
                    .customEndDate(LocalDate.now().plusDays(28))
                    .customTerms("Special terms for this school")
                    .priority(2)
                    .isFeaturedOnSchool(true)
                    .showOnHomepage(true)
                    .showOnPricingPage(false)
                    .build();

            existingCampaign = new Campaign();
            existingCampaign.setId(1L);
            existingCampaign.setTitle("Test Campaign");

            existingCampaignSchool = new CampaignSchool();
            existingCampaignSchool.setId(1L);
            existingCampaignSchool.setCampaign(existingCampaign);
            existingCampaignSchool.setSchool(validSchool);
            existingCampaignSchool.setStatus(CampaignSchoolStatus.ACTIVE);
        }

        @Test
        @DisplayName("Should update campaign school assignment successfully")
        void shouldUpdateCampaignSchoolAssignmentSuccessfully() {
            // Given
            Long campaignId = 1L;
            Long schoolId = 1L;
            CampaignSchoolDto expectedDto = CampaignSchoolDto.builder()
                    .id(1L)
                    .campaignId(campaignId)
                    .schoolId(schoolId)
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));
            when(campaignSchoolRepository.findByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId))
                    .thenReturn(java.util.Optional.of(existingCampaignSchool));
            when(campaignSchoolRepository.save(any(CampaignSchool.class))).thenReturn(existingCampaignSchool);
            when(converterService.mapCampaignSchoolToDto(existingCampaignSchool)).thenReturn(expectedDto);

            // When
            CampaignSchoolDto result = campaignService.updateCampaignSchoolAssignment(campaignId, schoolId, assignDto, request);

            // Then
            assertNotNull(result);
            assertEquals(campaignId, result.getCampaignId());
            assertEquals(schoolId, result.getSchoolId());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verify(campaignSchoolRepository).findByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId);
            verify(campaignSchoolRepository).save(argThat(campaignSchool ->
                    campaignSchool.getCustomDiscountAmount().equals(new BigDecimal("1500")) &&
                            campaignSchool.getCustomDiscountPercentage().equals(25.0) &&
                            campaignSchool.getCustomUsageLimit().equals(75) &&
                            campaignSchool.getCustomStartDate().equals(assignDto.getCustomStartDate()) &&
                            campaignSchool.getCustomEndDate().equals(assignDto.getCustomEndDate()) &&
                            campaignSchool.getCustomTerms().equals("Special terms for this school") &&
                            campaignSchool.getPriority().equals(2) &&
                            campaignSchool.getIsFeaturedOnSchool().equals(true) &&
                            campaignSchool.getShowOnHomepage().equals(true) &&
                            campaignSchool.getShowOnPricingPage().equals(false) &&
                            campaignSchool.getUpdatedBy().equals(1L)
            ));
            verify(converterService).mapCampaignSchoolToDto(existingCampaignSchool);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campaign not found")
        void shouldThrowResourceNotFoundExceptionWhenCampaignNotFound() {
            // Given
            Long nonExistentCampaignId = 999L;
            Long schoolId = 1L;

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(nonExistentCampaignId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> campaignService.updateCampaignSchoolAssignment(nonExistentCampaignId, schoolId, assignDto, request));

            assertEquals("Campaign not found", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(nonExistentCampaignId);
            verifyNoInteractions(campaignSchoolRepository);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campaign school assignment not found")
        void shouldThrowResourceNotFoundExceptionWhenAssignmentNotFound() {
            // Given
            Long campaignId = 1L;
            Long schoolId = 999L;

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));
            when(campaignSchoolRepository.findByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId))
                    .thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> campaignService.updateCampaignSchoolAssignment(campaignId, schoolId, assignDto, request));

            assertEquals("Campaign school assignment not found", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verify(campaignSchoolRepository).findByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId);
            verify(campaignSchoolRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when user cannot manage campaign")
        void shouldThrowBusinessExceptionWhenUserCannotManageCampaign() {
            // Given
            Long campaignId = 1L;
            Long schoolId = 1L;
            existingCampaign.setCampaignSchools(Collections.emptySet());

            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.updateCampaignSchoolAssignment(campaignId, schoolId, assignDto, request));

            assertEquals("User does not have permission to manage this campaign", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verifyNoInteractions(campaignSchoolRepository);
        }
    }

    @Nested
    @DisplayName("removeSchoolFromCampaign() Tests")
    class RemoveSchoolFromCampaignTests {

        private Campaign existingCampaign;
        private CampaignSchool existingCampaignSchool;

        @BeforeEach
        void setUp() {
            existingCampaign = new Campaign();
            existingCampaign.setId(1L);
            existingCampaign.setTitle("Test Campaign");

            existingCampaignSchool = new CampaignSchool();
            existingCampaignSchool.setId(1L);
            existingCampaignSchool.setCampaign(existingCampaign);
            existingCampaignSchool.setSchool(validSchool);
            existingCampaignSchool.setStatus(CampaignSchoolStatus.ACTIVE);
            existingCampaignSchool.setIsActive(true);
        }

        @Test
        @DisplayName("Should remove school from campaign successfully when no active usages")
        void shouldRemoveSchoolFromCampaignSuccessfullyWhenNoActiveUsages() {
            // Given
            Long campaignId = 1L;
            Long schoolId = 1L;

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));
            when(campaignSchoolRepository.findByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId))
                    .thenReturn(java.util.Optional.of(existingCampaignSchool));
            when(campaignUsageRepository.existsByCampaignIdAndSchoolIdAndStatusIn(eq(campaignId), eq(schoolId), anyList())).thenReturn(false);
            when(campaignSchoolRepository.save(any(CampaignSchool.class))).thenReturn(existingCampaignSchool);

            // When
            assertDoesNotThrow(() -> campaignService.removeSchoolFromCampaign(campaignId, schoolId, request));

            // Then
            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verify(campaignSchoolRepository).findByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId);
            verify(campaignUsageRepository).existsByCampaignIdAndSchoolIdAndStatusIn(eq(campaignId), eq(schoolId),
                    eq(List.of(CampaignUsageStatus.PENDING, CampaignUsageStatus.VALIDATED, CampaignUsageStatus.APPROVED)));
            verify(campaignSchoolRepository).save(argThat(campaignSchool ->
                    !campaignSchool.getIsActive() &&
                            campaignSchool.getStatus() == CampaignSchoolStatus.REMOVED &&
                            campaignSchool.getUpdatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should throw BusinessException when school has active campaign usages")
        void shouldThrowBusinessExceptionWhenSchoolHasActiveUsages() {
            // Given
            Long campaignId = 1L;
            Long schoolId = 1L;

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));
            when(campaignSchoolRepository.findByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId))
                    .thenReturn(java.util.Optional.of(existingCampaignSchool));
            when(campaignUsageRepository.existsByCampaignIdAndSchoolIdAndStatusIn(eq(campaignId), eq(schoolId), anyList())).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.removeSchoolFromCampaign(campaignId, schoolId, request));

            assertEquals("Cannot remove school with active campaign usages", exception.getMessage());

            verify(campaignSchoolRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when assignment not found")
        void shouldThrowResourceNotFoundExceptionWhenAssignmentNotFound() {
            // Given
            Long campaignId = 1L;
            Long schoolId = 999L;

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));
            when(campaignSchoolRepository.findByCampaignIdAndSchoolIdAndIsActiveTrue(campaignId, schoolId))
                    .thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> campaignService.removeSchoolFromCampaign(campaignId, schoolId, request));

            assertEquals("Campaign school assignment not found", exception.getMessage());

            verify(campaignSchoolRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getCampaignById() Tests")
    class GetCampaignByIdTests {

        @Test
        @DisplayName("Should return campaign successfully when user has access")
        void shouldReturnCampaignSuccessfullyWhenUserHasAccess() {
            // Given
            Long campaignId = 1L;
            Campaign foundCampaign = new Campaign();
            foundCampaign.setId(campaignId);
            foundCampaign.setTitle("Test Campaign");

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(foundCampaign));
            when(converterService.mapToDto(foundCampaign)).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.getCampaignById(campaignId, request);

            // Then
            assertNotNull(result);
            assertEquals(expectedCampaignDto.getTitle(), result.getTitle());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verify(converterService).mapToDto(foundCampaign);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campaign not found")
        void shouldThrowResourceNotFoundExceptionWhenCampaignNotFound() {
            // Given
            Long nonExistentCampaignId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(nonExistentCampaignId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> campaignService.getCampaignById(nonExistentCampaignId, request));

            assertEquals("Campaign not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(nonExistentCampaignId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to campaign")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToCampaign() {
            // Given
            Long campaignId = 1L;
            Campaign foundCampaign = new Campaign();
            foundCampaign.setId(campaignId);
            foundCampaign.setCampaignSchools(Collections.emptySet()); // No schools assigned

            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(foundCampaign));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.getCampaignById(campaignId, request));

            assertEquals("User does not have access to this campaign", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should allow access for system user regardless of campaign")
        void shouldAllowAccessForSystemUserRegardlessOfCampaign() {
            // Given
            Long campaignId = 1L;
            Campaign foundCampaign = new Campaign();
            foundCampaign.setId(campaignId);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(foundCampaign));
            when(converterService.mapToDto(foundCampaign)).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.getCampaignById(campaignId, request);

            // Then
            assertNotNull(result);
            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verify(converterService).mapToDto(foundCampaign);
        }
    }

    @Nested
    @DisplayName("updateCampaign() Tests")
    class UpdateCampaignTests {

        private CampaignUpdateDto updateDto;
        private Campaign existingCampaign;

        @BeforeEach
        void setUp() {
            updateDto = CampaignUpdateDto.builder()
                    .title("Updated Campaign Title")
                    .description("Updated description")
                    .shortDescription("Updated short desc")
                    .discountType(DiscountType.FIXED_AMOUNT)
                    .discountAmount(new BigDecimal("2000"))
                    .startDate(LocalDate.now().plusDays(2))
                    .endDate(LocalDate.now().plusDays(35))
                    .status(CampaignStatus.ACTIVE)
                    .isFeatured(false)
                    .bannerImageUrl("https://example.com/updated-banner.jpg")
                    .build();

            existingCampaign = new Campaign();
            existingCampaign.setId(1L);
            existingCampaign.setTitle("Original Campaign");
            existingCampaign.setStatus(CampaignStatus.DRAFT);
            existingCampaign.setCreatedBy(1L);
        }

        @Test
        @DisplayName("Should update campaign successfully with valid data")
        void shouldUpdateCampaignSuccessfullyWithValidData() {
            // Given
            Long campaignId = 1L;
            Campaign updatedCampaign = new Campaign();
            updatedCampaign.setId(campaignId);
            updatedCampaign.setTitle("Updated Campaign Title");
            updatedCampaign.setUpdatedBy(1L);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));
            when(campaignRepository.save(any(Campaign.class))).thenReturn(updatedCampaign);
            when(converterService.mapToDto(updatedCampaign)).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.updateCampaign(campaignId, updateDto, request);

            // Then
            assertNotNull(result);
            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verify(campaignRepository).save(argThat(campaign ->
                    campaign.getTitle().equals("Updated Campaign Title") &&
                            campaign.getDescription().equals("Updated description") &&
                            campaign.getShortDescription().equals("Updated short desc") &&
                            campaign.getDiscountType() == DiscountType.FIXED_AMOUNT &&
                            campaign.getDiscountAmount().equals(new BigDecimal("2000")) &&
                            campaign.getStatus() == CampaignStatus.ACTIVE &&
                            campaign.getIsFeatured().equals(false) &&
                            campaign.getUpdatedBy().equals(1L)
            ));
            verify(converterService).mapToDto(updatedCampaign);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campaign not found")
        void shouldThrowResourceNotFoundExceptionWhenCampaignNotFound() {
            // Given
            Long nonExistentCampaignId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(nonExistentCampaignId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> campaignService.updateCampaign(nonExistentCampaignId, updateDto, request));

            assertEquals("Campaign not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(nonExistentCampaignId);
            verify(campaignRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when user cannot manage campaign")
        void shouldThrowBusinessExceptionWhenUserCannotManageCampaign() {
            // Given
            Long campaignId = 1L;
            existingCampaign.setCampaignSchools(Collections.emptySet()); // No schools for access check

            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.updateCampaign(campaignId, updateDto, request));

            assertEquals("User does not have permission to manage this campaign", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verify(campaignRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when start date is after end date")
        void shouldThrowExceptionWhenStartDateAfterEndDate() {
            // Given
            Long campaignId = 1L;
            CampaignUpdateDto invalidDateDto = CampaignUpdateDto.builder()
                    .startDate(LocalDate.now().plusDays(30))
                    .endDate(LocalDate.now().plusDays(15)) // End date before start date
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.updateCampaign(campaignId, invalidDateDto, request));

            assertEquals("Start date cannot be after end date", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verify(campaignRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow creator to manage campaign")
        void shouldAllowCreatorToManageCampaign() {
            // Given
            Long campaignId = 1L;
            User creatorUser = createUser(2L, RoleLevel.CAMPUS);
            existingCampaign.setCreatedByUser(creatorUser);

            when(jwtService.getUser(request)).thenReturn(creatorUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));
            when(campaignRepository.save(any(Campaign.class))).thenReturn(existingCampaign);
            when(converterService.mapToDto(any(Campaign.class))).thenReturn(expectedCampaignDto);

            // When
            CampaignDto result = campaignService.updateCampaign(campaignId, updateDto, request);

            // Then
            assertNotNull(result);
            verify(campaignRepository).save(any(Campaign.class));
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            // Given
            Long campaignId = 1L;
            CampaignUpdateDto partialUpdate = CampaignUpdateDto.builder()
                    .title("Only Title Updated")
                    .status(CampaignStatus.ACTIVE)
                    // Other fields are null - should not be updated
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));
            when(campaignRepository.save(any(Campaign.class))).thenReturn(existingCampaign);
            when(converterService.mapToDto(any(Campaign.class))).thenReturn(expectedCampaignDto);

            // When
            campaignService.updateCampaign(campaignId, partialUpdate, request);

            // Then
            verify(campaignRepository).save(argThat(campaign ->
                    campaign.getTitle().equals("Only Title Updated") &&
                            campaign.getStatus() == CampaignStatus.ACTIVE &&
                            // These should remain unchanged (null check logic)
                            campaign.getDescription() == null // Original field not updated
            ));
        }
    }

    @Nested
    @DisplayName("deleteCampaign() Tests")
    class DeleteCampaignTests {

        private Campaign existingCampaign;

        @BeforeEach
        void setUp() {
            existingCampaign = new Campaign();
            existingCampaign.setId(1L);
            existingCampaign.setTitle("Campaign to Delete");
            existingCampaign.setIsActive(true);
            existingCampaign.setStatus(CampaignStatus.ACTIVE);
        }

        @Test
        @DisplayName("Should delete campaign successfully when no active usages")
        void shouldDeleteCampaignSuccessfullyWhenNoActiveUsages() {
            // Given
            Long campaignId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));
            when(campaignUsageRepository.existsByCampaignIdAndStatusIn(eq(campaignId), anyList())).thenReturn(false);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(existingCampaign);

            // When
            assertDoesNotThrow(() -> campaignService.deleteCampaign(campaignId, request));

            // Then
            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verify(campaignUsageRepository).existsByCampaignIdAndStatusIn(eq(campaignId),
                    eq(List.of(CampaignUsageStatus.PENDING, CampaignUsageStatus.VALIDATED, CampaignUsageStatus.APPROVED)));
            verify(campaignRepository).save(argThat(campaign ->
                    !campaign.getIsActive() &&
                            campaign.getStatus() == CampaignStatus.CANCELLED &&
                            campaign.getUpdatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campaign not found")
        void shouldThrowResourceNotFoundExceptionWhenCampaignNotFound() {
            // Given
            Long nonExistentCampaignId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(nonExistentCampaignId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> campaignService.deleteCampaign(nonExistentCampaignId, request));

            assertEquals("Campaign not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(nonExistentCampaignId);
            verifyNoInteractions(campaignUsageRepository);
            verify(campaignRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when user cannot manage campaign")
        void shouldThrowBusinessExceptionWhenUserCannotManageCampaign() {
            // Given
            Long campaignId = 1L;
            existingCampaign.setCampaignSchools(Collections.emptySet());

            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.deleteCampaign(campaignId, request));

            assertEquals("User does not have permission to manage this campaign", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verifyNoInteractions(campaignUsageRepository);
            verify(campaignRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when campaign has active usages")
        void shouldThrowBusinessExceptionWhenCampaignHasActiveUsages() {
            // Given
            Long campaignId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));
            when(campaignUsageRepository.existsByCampaignIdAndStatusIn(eq(campaignId), anyList())).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> campaignService.deleteCampaign(campaignId, request));

            assertEquals("Cannot delete campaign with active usages", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campaignRepository).findByIdAndIsActiveTrue(campaignId);
            verify(campaignUsageRepository).existsByCampaignIdAndStatusIn(eq(campaignId),
                    eq(List.of(CampaignUsageStatus.PENDING, CampaignUsageStatus.VALIDATED, CampaignUsageStatus.APPROVED)));
            verify(campaignRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should perform soft delete and update status")
        void shouldPerformSoftDeleteAndUpdateStatus() {
            // Given
            Long campaignId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campaignRepository.findByIdAndIsActiveTrue(campaignId)).thenReturn(java.util.Optional.of(existingCampaign));
            when(campaignUsageRepository.existsByCampaignIdAndStatusIn(eq(campaignId), anyList())).thenReturn(false);

            // When
            campaignService.deleteCampaign(campaignId, request);

            // Then
            verify(campaignRepository).save(argThat(campaign ->
                    !campaign.getIsActive() && // Soft delete check
                            campaign.getStatus() == CampaignStatus.CANCELLED &&
                            campaign.getUpdatedBy().equals(1L) &&
                            campaign.getId().equals(campaignId)
            ));

            // Verify it's not a hard delete
            verify(campaignRepository, never()).delete(any());
            verify(campaignRepository, never()).deleteById(any());
        }
    }

    // Helper methods
    private User createUser(Long id, RoleLevel roleLevel) {
        User user = new User();
        user.setId(id);
        user.setUserRoles(Set.of(createUserRole(roleLevel)));
        user.setInstitutionAccess(Collections.emptySet());
        return user;
    }

    private UserRole createUserRole(RoleLevel roleLevel) {
        UserRole mockRole = new UserRole();
        mockRole.setRoleLevel(roleLevel);
        return mockRole;
    }

}
