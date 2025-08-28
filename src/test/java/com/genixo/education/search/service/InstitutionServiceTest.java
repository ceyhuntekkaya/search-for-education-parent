package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.dto.institution.*;
import com.genixo.education.search.entity.institution.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.enumaration.PropertyDataType;
import com.genixo.education.search.enumaration.RoleLevel;
import com.genixo.education.search.repository.insitution.*;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.InstitutionConverterService;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InstitutionService Tests")
class InstitutionServiceTest {

    @Mock private BrandRepository brandRepository;
    @Mock private CampusRepository campusRepository;
    @Mock private SchoolRepository schoolRepository;
    @Mock private InstitutionTypeRepository institutionTypeRepository;
    @Mock private InstitutionPropertyRepository institutionPropertyRepository;
    @Mock private InstitutionPropertyValueRepository institutionPropertyValueRepository;
    @Mock private InstitutionConverterService converterService;
    @Mock private JwtService jwtService;
    @Mock private HttpServletRequest request;

    @InjectMocks
    private InstitutionService institutionService;

    private User systemUser;
    private User regularUser;
    private BrandCreateDto validBrandCreateDto;
    private Brand savedBrand;
    private BrandDto expectedBrandDto;

    // Campus test data
    private CampusCreateDto validCampusCreateDto;
    private Campus savedCampus;
    private CampusDto expectedCampusDto;
    private Brand validBrand;

    // School test data
    private SchoolCreateDto validSchoolCreateDto;
    private School savedSchool;
    private SchoolDto expectedSchoolDto;
    private InstitutionType validInstitutionType;

    @BeforeEach
    void setUp() {
        // System user with SYSTEM role (can create brands)
        systemUser = createUser(1L, RoleLevel.SYSTEM);

        // Regular user without SYSTEM role (cannot create brands)
        regularUser = createUser(2L, RoleLevel.BRAND);

        // Valid brand create DTO
        validBrandCreateDto = BrandCreateDto.builder()
                .name("Test Brand")
                .description("Test brand description")
                .logoUrl("https://example.com/logo.png")
                .coverImageUrl("https://example.com/cover.jpg")
                .websiteUrl("https://testbrand.com")
                .email("info@testbrand.com")
                .phone("+90 555 123 4567")
                .foundedYear(2020)
                .facebookUrl("https://facebook.com/testbrand")
                .twitterUrl("https://twitter.com/testbrand")
                .instagramUrl("https://instagram.com/testbrand")
                .linkedinUrl("https://linkedin.com/company/testbrand")
                .youtubeUrl("https://youtube.com/testbrand")
                .metaTitle("Test Brand - Best Education")
                .metaDescription("Test brand meta description")
                .metaKeywords("education, school, test")
                .build();

        // Saved brand entity
        savedBrand = new Brand();
        savedBrand.setId(1L);
        savedBrand.setName("Test Brand");
        savedBrand.setSlug("test-brand");
        savedBrand.setDescription("Test brand description");
        savedBrand.setCreatedBy(1L);
        savedBrand.setCreatedAt(LocalDateTime.now());

        // Expected DTO response
        expectedBrandDto = BrandDto.builder()
                .id(1L)
                .name("Test Brand")
                .slug("test-brand")
                .description("Test brand description")
                .build();

        // Valid brand for campus tests
        validBrand = new Brand();
        validBrand.setId(1L);
        validBrand.setName("Test Brand");
        validBrand.setSlug("test-brand");
        validBrand.setIsActive(true);

        // Valid campus create DTO
        validCampusCreateDto = CampusCreateDto.builder()
                .brandId(1L)
                .name("Test Campus")
                .description("Test campus description")
                .logoUrl("https://example.com/campus-logo.png")
                .coverImageUrl("https://example.com/campus-cover.jpg")
                .email("campus@testbrand.com")
                .phone("+90 555 123 4567")
                .fax("+90 555 123 4568")
                .websiteUrl("https://campus.testbrand.com")
                .addressLine1("Test Address Line 1")
                .addressLine2("Test Address Line 2")
                .postalCode("34000")
                .latitude(41.0082)
                .longitude(28.9784)
                .establishedYear(2021)
                .facebookUrl("https://facebook.com/testcampus")
                .metaTitle("Test Campus - Best Education")
                .metaDescription("Test campus meta description")
                .build();

        // Saved campus entity
        savedCampus = new Campus();
        savedCampus.setId(1L);
        savedCampus.setName("Test Campus");
        savedCampus.setSlug("test-campus");
        savedCampus.setDescription("Test campus description");
        savedCampus.setBrand(validBrand);
        savedCampus.setCreatedBy(1L);
        savedCampus.setCreatedAt(LocalDateTime.now());

        // Expected campus DTO response
        expectedCampusDto = CampusDto.builder()
                .id(1L)
                .name("Test Campus")
                .slug("test-campus")
                .description("Test campus description")
                .build();

        // Valid institution type for school tests
        validInstitutionType = new InstitutionType();
        validInstitutionType.setId(1L);
        validInstitutionType.setName("PRIMARY_SCHOOL");
        validInstitutionType.setDisplayName("Primary School");
        validInstitutionType.setIsActive(true);

        // Valid school create DTO
        validSchoolCreateDto = SchoolCreateDto.builder()
                .campusId(1L)
                .institutionTypeId(1L)
                .name("Test School")
                .description("Test school description")
                .logoUrl("https://example.com/school-logo.png")
                .coverImageUrl("https://example.com/school-cover.jpg")
                .email("school@testbrand.com")
                .phone("+90 555 123 4567")
                .extension("101")
                .minAge(6)
                .maxAge(11)
                .capacity(500)
                .currentStudentCount(350)
                .classSizeAverage(25)
                .curriculumType("National")
                .languageOfInstruction("Turkish")
                .foreignLanguages("English, German")
                .registrationFee(2500.0)
                .monthlyFee(1200.0)
                .annualFee(12000.0)
                .metaTitle("Test School - Best Primary Education")
                .metaDescription("Test school meta description")
                .metaKeywords("primary, school, education")
                .build();

        // Saved school entity
        savedSchool = new School();
        savedSchool.setId(1L);
        savedSchool.setName("Test School");
        savedSchool.setSlug("test-school");
        savedSchool.setDescription("Test school description");
        savedSchool.setCampus(savedCampus);
        savedSchool.setInstitutionType(validInstitutionType);
        savedSchool.setCreatedBy(1L);
        savedSchool.setCreatedAt(LocalDateTime.now());

        // Expected school DTO response
        expectedSchoolDto = SchoolDto.builder()
                .id(1L)
                .name("Test School")
                .slug("test-school")
                .description("Test school description")
                .build();
    }

    @Nested
    @DisplayName("createBrand() Tests")
    class CreateBrandTests {

        @Test
        @DisplayName("Should create brand successfully with valid data")
        void shouldCreateBrandSuccessfully() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.existsByNameIgnoreCase("Test Brand")).thenReturn(false);
            when(brandRepository.existsBySlug("test-brand")).thenReturn(false);
            when(brandRepository.save(any(Brand.class))).thenReturn(savedBrand);
            when(converterService.mapToDto(savedBrand)).thenReturn(expectedBrandDto);

            // When
            BrandDto result = institutionService.createBrand(validBrandCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("Test Brand", result.getName());
            assertEquals("test-brand", result.getSlug());
            assertEquals("Test brand description", result.getDescription());

            // Verify interactions
            verify(jwtService).getUser(request);
            verify(brandRepository).existsByNameIgnoreCase("Test Brand");
            verify(brandRepository).existsBySlug("test-brand");
            verify(brandRepository).save(argThat(brand ->
                    brand.getName().equals("Test Brand") &&
                            brand.getSlug().equals("test-brand") &&
                            brand.getCreatedBy().equals(1L)
            ));
            verify(converterService).mapToDto(savedBrand);
        }

        @Test
        @DisplayName("Should throw BusinessException when user cannot create brand")
        void shouldThrowExceptionWhenUserCannotCreateBrand() {
            // Given
            when(jwtService.getUser(request)).thenReturn(regularUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.createBrand(validBrandCreateDto, request));

            assertEquals("User does not have permission to create brands", exception.getMessage());

            // Verify no repository interactions
            verify(jwtService).getUser(request);
            verifyNoInteractions(brandRepository, converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when brand name already exists")
        void shouldThrowExceptionWhenBrandNameExists() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.existsByNameIgnoreCase("Test Brand")).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.createBrand(validBrandCreateDto, request));

            assertEquals("Brand name already exists: Test Brand", exception.getMessage());

            // Verify interactions stopped at name check
            verify(jwtService).getUser(request);
            verify(brandRepository).existsByNameIgnoreCase("Test Brand");
            verify(brandRepository, never()).existsBySlug(anyString());
            verify(brandRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should generate unique slug when original slug exists")
        void shouldGenerateUniqueSlugWhenSlugExists() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.existsByNameIgnoreCase("Test Brand")).thenReturn(false);
            when(brandRepository.existsBySlug("test-brand")).thenReturn(true);
            when(brandRepository.existsBySlug(startsWith("test-brand-"))).thenReturn(false);

            Brand brandWithUniqueSlug = new Brand();
            brandWithUniqueSlug.setId(1L);
            brandWithUniqueSlug.setName("Test Brand");
            brandWithUniqueSlug.setSlug("test-brand-" + System.currentTimeMillis());

            when(brandRepository.save(any(Brand.class))).thenReturn(brandWithUniqueSlug);
            when(converterService.mapToDto(any(Brand.class))).thenReturn(expectedBrandDto);

            // When
            BrandDto result = institutionService.createBrand(validBrandCreateDto, request);

            // Then
            assertNotNull(result);

            // Verify slug uniqueness check
            verify(brandRepository).existsBySlug("test-brand");
            verify(brandRepository).save(argThat(brand ->
                    brand.getSlug().startsWith("test-brand-") &&
                            !brand.getSlug().equals("test-brand")
            ));
        }

        @Test
        @DisplayName("Should handle special characters in brand name for slug generation")
        void shouldHandleSpecialCharactersInSlugGeneration() {
            // Given
            BrandCreateDto specialCharBrand = BrandCreateDto.builder()
                    .name("Test Brand!!! @#$ 123 & Co.")
                    .description("Test description")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
            when(brandRepository.existsBySlug(anyString())).thenReturn(false);
            when(brandRepository.save(any(Brand.class))).thenReturn(savedBrand);
            when(converterService.mapToDto(any(Brand.class))).thenReturn(expectedBrandDto);

            // When
            institutionService.createBrand(specialCharBrand, request);

            // Then
            verify(brandRepository).save(argThat(brand -> {
                String slug = brand.getSlug();
                // Slug should only contain lowercase letters, numbers, and hyphens
                return slug.matches("^[a-z0-9-]+$") &&
                        !slug.contains("!") &&
                        !slug.contains("@") &&
                        !slug.contains("#") &&
                        !slug.contains("&");
            }));
        }

        @Test
        @DisplayName("Should set all brand fields correctly from create DTO")
        void shouldSetAllBrandFieldsCorrectly() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
            when(brandRepository.existsBySlug(anyString())).thenReturn(false);
            when(brandRepository.save(any(Brand.class))).thenReturn(savedBrand);
            when(converterService.mapToDto(any(Brand.class))).thenReturn(expectedBrandDto);

            // When
            institutionService.createBrand(validBrandCreateDto, request);

            // Then
            verify(brandRepository).save(argThat(brand ->
                    brand.getName().equals("Test Brand") &&
                            brand.getDescription().equals("Test brand description") &&
                            brand.getLogoUrl().equals("https://example.com/logo.png") &&
                            brand.getCoverImageUrl().equals("https://example.com/cover.jpg") &&
                            brand.getWebsiteUrl().equals("https://testbrand.com") &&
                            brand.getEmail().equals("info@testbrand.com") &&
                            brand.getPhone().equals("+90 555 123 4567") &&
                            brand.getFoundedYear().equals(2020) &&
                            brand.getFacebookUrl().equals("https://facebook.com/testbrand") &&
                            brand.getTwitterUrl().equals("https://twitter.com/testbrand") &&
                            brand.getInstagramUrl().equals("https://instagram.com/testbrand") &&
                            brand.getLinkedinUrl().equals("https://linkedin.com/company/testbrand") &&
                            brand.getYoutubeUrl().equals("https://youtube.com/testbrand") &&
                            brand.getMetaTitle().equals("Test Brand - Best Education") &&
                            brand.getMetaDescription().equals("Test brand meta description") &&
                            brand.getMetaKeywords().equals("education, school, test") &&
                            brand.getCreatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should handle null and empty values gracefully")
        void shouldHandleNullAndEmptyValues() {
            // Given
            BrandCreateDto minimalBrand = BrandCreateDto.builder()
                    .name("Minimal Brand")
                    .build(); // All other fields are null

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
            when(brandRepository.existsBySlug(anyString())).thenReturn(false);
            when(brandRepository.save(any(Brand.class))).thenReturn(savedBrand);
            when(converterService.mapToDto(any(Brand.class))).thenReturn(expectedBrandDto);

            // When
            institutionService.createBrand(minimalBrand, request);

            // Then
            verify(brandRepository).save(argThat(brand ->
                    brand.getName().equals("Minimal Brand") &&
                            brand.getDescription() == null &&
                            brand.getLogoUrl() == null &&
                            brand.getEmail() == null &&
                            brand.getFoundedYear() == null
            ));
        }
    }

    // ================================ PUBLIC METHODS TESTS ================================

    @Nested
    @DisplayName("publicSearchSchools() Tests")
    class PublicSearchSchoolsTests {

        @Test
        @DisplayName("Should search schools with subscription filter automatically applied")
        void shouldSearchSchoolsWithSubscriptionFilterAutomaticallyApplied() {
            // Given
            SchoolSearchDto publicSearchDto = SchoolSearchDto.builder()
                    .searchTerm("public school")
                    .institutionTypeIds(List.of(1L))
                    .minFee(1000.0)
                    .maxFee(2000.0)
                    .build();

            Page<School> mockPublicSchoolPage = new org.springframework.data.domain.PageImpl<>(
                    Collections.singletonList(savedSchool),
                    org.springframework.data.domain.PageRequest.of(0, 20),
                    1L
            );

            when(schoolRepository.searchSchools(
                    eq("public school"),
                    eq(List.of(1L)),
                    eq(null), eq(null),
                    eq(1000.0), eq(2000.0),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(true), // isSubscribed should be forced to true
                    any(org.springframework.data.domain.Pageable.class)
            )).thenReturn(mockPublicSchoolPage);

            when(converterService.mapToSearchResultDto(savedSchool)).thenReturn(
                    SchoolSearchResultDto.builder().id(1L).name("Public School").build()
            );

            // When
            Page<SchoolSearchResultDto> result = institutionService.publicSearchSchools(publicSearchDto);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getContent().size());

            // Verify that isSubscribed is forced to true for public search
            verify(schoolRepository).searchSchools(
                    eq("public school"), eq(List.of(1L)),
                    eq(null), eq(null), eq(1000.0), eq(2000.0),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(true), // This is the key difference from regular search
                    any(org.springframework.data.domain.Pageable.class)
            );
        }

        @Test
        @DisplayName("Should override user-provided subscription filter")
        void shouldOverrideUserProvidedSubscriptionFilter() {
            // Given
            SchoolSearchDto searchWithSubscriptionFalse = SchoolSearchDto.builder()
                    .searchTerm("school")
                    .isSubscribed(false) // User tries to set this to false
                    .build();

            Page<School> emptyPage = new org.springframework.data.domain.PageImpl<>(
                    Collections.emptyList(),
                    org.springframework.data.domain.PageRequest.of(0, 20),
                    0L
            );

            when(schoolRepository.searchSchools(
                    anyString(), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any(), any(),
                    eq(true), // Should be overridden to true
                    any(org.springframework.data.domain.Pageable.class)
            )).thenReturn(emptyPage);

            // When
            institutionService.publicSearchSchools(searchWithSubscriptionFalse);

            // Then
            // Verify that isSubscribed is forced to true despite user setting it to false
            verify(schoolRepository).searchSchools(
                    anyString(), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any(), any(),
                    eq(true), // Should be overridden
                    any(org.springframework.data.domain.Pageable.class)
            );
        }
    }

    @Nested
    @DisplayName("getPublicSchoolBySlug() Tests")
    class GetPublicSchoolBySlugTests {

        @Test
        @DisplayName("Should return school from subscribed campus successfully")
        void shouldReturnSchoolFromSubscribedCampusSuccessfully() {
            // Given
            String slug = "public-school";
            School publicSchool = new School();
            publicSchool.setId(1L);
            publicSchool.setName("Public School");
            publicSchool.setSlug(slug);

            when(schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(slug))
                    .thenReturn(java.util.Optional.of(publicSchool));
            doNothing().when(schoolRepository).incrementViewCount(1L);
            //when(schoolRepository.incrementViewCount(1L)).thenReturn(1);
            when(converterService.mapToDto(publicSchool)).thenReturn(expectedSchoolDto);

            // When
            SchoolDto result = institutionService.getPublicSchoolBySlug(slug);

            // Then
            assertNotNull(result);
            assertEquals(expectedSchoolDto.getName(), result.getName());

            verify(schoolRepository).findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(slug);
            verify(schoolRepository).incrementViewCount(1L);
            verify(converterService).mapToDto(publicSchool);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when school not found or not subscribed")
        void shouldThrowResourceNotFoundExceptionWhenSchoolNotFoundOrNotSubscribed() {
            // Given
            String nonExistentSlug = "non-existent-school";
            when(schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(nonExistentSlug))
                    .thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.getPublicSchoolBySlug(nonExistentSlug));

            assertEquals("School not found or not available", exception.getMessage());

            verify(schoolRepository).findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(nonExistentSlug);
            verify(schoolRepository, never()).incrementViewCount(anyLong());
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should increment view count when school is accessed publicly")
        void shouldIncrementViewCountWhenSchoolIsAccessedPublicly() {
            // Given
            String slug = "viewed-school";
            School viewedSchool = new School();
            viewedSchool.setId(2L);
            viewedSchool.setSlug(slug);

            when(schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(slug))
                    .thenReturn(java.util.Optional.of(viewedSchool));
            //when(schoolRepository.incrementViewCount(2L)).thenReturn(1);
            doNothing().when(schoolRepository).incrementViewCount(1L);
            when(converterService.mapToDto(viewedSchool)).thenReturn(expectedSchoolDto);

            // When
            institutionService.getPublicSchoolBySlug(slug);

            // Then
            verify(schoolRepository).incrementViewCount(2L);
        }

        @Test
        @DisplayName("Should not require authentication")
        void shouldNotRequireAuthentication() {
            // Given
            String slug = "public-school";
            School publicSchool = new School();
            publicSchool.setId(1L);
            publicSchool.setSlug(slug);

            when(schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(slug))
                    .thenReturn(java.util.Optional.of(publicSchool));
            doNothing().when(schoolRepository).incrementViewCount(1L);
            //when(schoolRepository.incrementViewCount(1L)).thenReturn(1);
            when(converterService.mapToDto(publicSchool)).thenReturn(expectedSchoolDto);

            // When
            institutionService.getPublicSchoolBySlug(slug);

            // Then
            // Verify that JWT service is never called (no authentication)
            verifyNoInteractions(jwtService);
        }
    }

    @Nested
    @DisplayName("getPublicCampusBySlug() Tests")
    class GetPublicCampusBySlugTests {

        @Test
        @DisplayName("Should return subscribed campus successfully")
        void shouldReturnSubscribedCampusSuccessfully() {
            // Given
            String slug = "public-campus";
            Campus publicCampus = new Campus();
            publicCampus.setId(1L);
            publicCampus.setName("Public Campus");
            publicCampus.setSlug(slug);

            when(campusRepository.findBySlugAndIsActiveTrueAndIsSubscribedTrue(slug))
                    .thenReturn(java.util.Optional.of(publicCampus));
            //when(campusRepository.incrementViewCount(1L)).thenReturn(1);
            doNothing().when(schoolRepository).incrementViewCount(1L);
            when(converterService.mapToDto(publicCampus)).thenReturn(expectedCampusDto);

            // When
            CampusDto result = institutionService.getPublicCampusBySlug(slug);

            // Then
            assertNotNull(result);
            assertEquals(expectedCampusDto.getName(), result.getName());

            verify(campusRepository).findBySlugAndIsActiveTrueAndIsSubscribedTrue(slug);
            verify(campusRepository).incrementViewCount(1L);
            verify(converterService).mapToDto(publicCampus);
            verifyNoInteractions(jwtService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campus not found or not subscribed")
        void shouldThrowResourceNotFoundExceptionWhenCampusNotFoundOrNotSubscribed() {
            // Given
            String nonExistentSlug = "non-existent-campus";
            when(campusRepository.findBySlugAndIsActiveTrueAndIsSubscribedTrue(nonExistentSlug))
                    .thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.getPublicCampusBySlug(nonExistentSlug));

            assertEquals("Campus not found or not available", exception.getMessage());

            verify(campusRepository).findBySlugAndIsActiveTrueAndIsSubscribedTrue(nonExistentSlug);
            verify(campusRepository, never()).incrementViewCount(anyLong());
        }

        @Test
        @DisplayName("Should increment view count for public campus access")
        void shouldIncrementViewCountForPublicCampusAccess() {
            // Given
            String slug = "popular-campus";
            Campus popularCampus = new Campus();
            popularCampus.setId(3L);
            popularCampus.setSlug(slug);

            when(campusRepository.findBySlugAndIsActiveTrueAndIsSubscribedTrue(slug))
                    .thenReturn(java.util.Optional.of(popularCampus));
            //when(campusRepository.incrementViewCount(3L)).thenReturn(1);
            doNothing().when(schoolRepository).incrementViewCount(1L);
            when(converterService.mapToDto(popularCampus)).thenReturn(expectedCampusDto);

            // When
            institutionService.getPublicCampusBySlug(slug);

            // Then
            verify(campusRepository).incrementViewCount(3L);
        }
    }

    @Nested
    @DisplayName("getPublicBrandBySlug() Tests")
    class GetPublicBrandBySlugTests {

        @Test
        @DisplayName("Should return brand with subscribed campuses successfully")
        void shouldReturnBrandWithSubscribedCampusesSuccessfully() {
            // Given
            String slug = "public-brand";
            Brand publicBrand = new Brand();
            publicBrand.setId(1L);
            publicBrand.setName("Public Brand");
            publicBrand.setSlug(slug);

            when(brandRepository.findBySlugAndIsActiveTrueWithSubscribedCampuses(slug))
                    .thenReturn(java.util.Optional.of(publicBrand));
            //when(brandRepository.incrementViewCount(1L)).thenReturn(1);
            doNothing().when(schoolRepository).incrementViewCount(1L);
            when(converterService.mapToDto(publicBrand)).thenReturn(expectedBrandDto);

            // When
            BrandDto result = institutionService.getPublicBrandBySlug(slug);

            // Then
            assertNotNull(result);
            assertEquals(expectedBrandDto.getName(), result.getName());

            verify(brandRepository).findBySlugAndIsActiveTrueWithSubscribedCampuses(slug);
            verify(brandRepository).incrementViewCount(1L);
            verify(converterService).mapToDto(publicBrand);
            verifyNoInteractions(jwtService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when brand not found or has no subscribed campuses")
        void shouldThrowResourceNotFoundExceptionWhenBrandNotFoundOrHasNoSubscribedCampuses() {
            // Given
            String nonExistentSlug = "non-public-brand";
            when(brandRepository.findBySlugAndIsActiveTrueWithSubscribedCampuses(nonExistentSlug))
                    .thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.getPublicBrandBySlug(nonExistentSlug));

            assertEquals("Brand not found or not available", exception.getMessage());

            verify(brandRepository).findBySlugAndIsActiveTrueWithSubscribedCampuses(nonExistentSlug);
            verify(brandRepository, never()).incrementViewCount(anyLong());
        }

        @Test
        @DisplayName("Should increment view count for public brand access")
        void shouldIncrementViewCountForPublicBrandAccess() {
            // Given
            String slug = "trending-brand";
            Brand trendingBrand = new Brand();
            trendingBrand.setId(5L);
            trendingBrand.setSlug(slug);

            when(brandRepository.findBySlugAndIsActiveTrueWithSubscribedCampuses(slug))
                    .thenReturn(java.util.Optional.of(trendingBrand));
            //when(brandRepository.incrementViewCount(5L)).thenReturn(1);
            doNothing().when(schoolRepository).incrementViewCount(1L);
            when(converterService.mapToDto(trendingBrand)).thenReturn(expectedBrandDto);

            // When
            institutionService.getPublicBrandBySlug(slug);

            // Then
            verify(brandRepository).incrementViewCount(5L);
        }

        @Test
        @DisplayName("Should use specialized repository method for subscription filtering")
        void shouldUseSpecializedRepositoryMethodForSubscriptionFiltering() {
            // Given
            String slug = "filtered-brand";
            Brand filteredBrand = new Brand();
            filteredBrand.setId(6L);
            filteredBrand.setSlug(slug);

            when(brandRepository.findBySlugAndIsActiveTrueWithSubscribedCampuses(slug))
                    .thenReturn(java.util.Optional.of(filteredBrand));
            //when(brandRepository.incrementViewCount(6L)).thenReturn(1);
            doNothing().when(schoolRepository).incrementViewCount(1L);
            when(converterService.mapToDto(filteredBrand)).thenReturn(expectedBrandDto);

            // When
            institutionService.getPublicBrandBySlug(slug);

            // Then
            // Verify it uses the specialized method, not the regular one
            verify(brandRepository).findBySlugAndIsActiveTrueWithSubscribedCampuses(slug);
            verify(brandRepository, never()).findBySlugAndIsActiveTrue(slug);
        }
    }

    @Nested
    @DisplayName("setCampusPropertyValues() Tests")
    class SetCampusPropertyValuesTests {

        private List<InstitutionPropertyValueSetDto> validCampusPropertyValues;
        private InstitutionProperty existingCampusProperty;
        private InstitutionPropertyValue existingCampusPropertyValue;

        @BeforeEach
        void setUp() {
            existingCampusProperty = new InstitutionProperty();
            existingCampusProperty.setId(2L);
            existingCampusProperty.setName("campus-facilities");
            existingCampusProperty.setDataType(PropertyDataType.MULTISELECT);

            existingCampusPropertyValue = new InstitutionPropertyValue();
            existingCampusPropertyValue.setId(2L);
            existingCampusPropertyValue.setProperty(existingCampusProperty);
            existingCampusPropertyValue.setCampus(savedCampus);
            existingCampusPropertyValue.setTextValue("Library,Gym");

            InstitutionPropertyValueSetDto campusValueDto = InstitutionPropertyValueSetDto.builder()
                    .propertyId(2L)
                    .textValue("Library,Gym,Swimming Pool")
                    .build();

            validCampusPropertyValues = List.of(campusValueDto);
        }

        @Test
        @DisplayName("Should update existing campus property value successfully")
        void shouldUpdateExistingCampusPropertyValueSuccessfully() {
            // Given
            Long campusId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(campusId)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionPropertyRepository.findByIdAndIsActiveTrue(2L)).thenReturn(java.util.Optional.of(existingCampusProperty));
            when(institutionPropertyValueRepository.findByPropertyIdAndCampusIdAndIsActiveTrue(2L, campusId))
                    .thenReturn(java.util.Optional.of(existingCampusPropertyValue));
            when(institutionPropertyValueRepository.save(any(InstitutionPropertyValue.class))).thenReturn(existingCampusPropertyValue);

            // When
            assertDoesNotThrow(() -> institutionService.setCampusPropertyValues(campusId, validCampusPropertyValues, request));

            // Then
            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(campusId);
            verify(institutionPropertyRepository).findByIdAndIsActiveTrue(2L);
            verify(institutionPropertyValueRepository).findByPropertyIdAndCampusIdAndIsActiveTrue(2L, campusId);
            verify(institutionPropertyValueRepository).save(argThat(value ->
                    value.getTextValue().equals("Library,Gym,Swimming Pool") &&
                            value.getUpdatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should create new campus property value when not exists")
        void shouldCreateNewCampusPropertyValueWhenNotExists() {
            // Given
            Long campusId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(campusId)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionPropertyRepository.findByIdAndIsActiveTrue(2L)).thenReturn(java.util.Optional.of(existingCampusProperty));
            when(institutionPropertyValueRepository.findByPropertyIdAndCampusIdAndIsActiveTrue(2L, campusId))
                    .thenReturn(java.util.Optional.empty()); // No existing value
            when(institutionPropertyValueRepository.save(any(InstitutionPropertyValue.class))).thenReturn(existingCampusPropertyValue);

            // When
            institutionService.setCampusPropertyValues(campusId, validCampusPropertyValues, request);

            // Then
            verify(institutionPropertyValueRepository).save(argThat(value ->
                    value.getProperty().getId().equals(2L) &&
                            value.getCampus().getId().equals(campusId) &&
                            value.getSchool() == null && // Should be null for campus property
                            value.getTextValue().equals("Library,Gym,Swimming Pool") &&
                            value.getCreatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campus not found")
        void shouldThrowResourceNotFoundExceptionWhenCampusNotFound() {
            // Given
            Long nonExistentCampusId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(nonExistentCampusId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.setCampusPropertyValues(nonExistentCampusId, validCampusPropertyValues, request));

            assertEquals("Campus not found", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(nonExistentCampusId);
            verifyNoInteractions(institutionPropertyRepository, institutionPropertyValueRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to campus")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToCampus() {
            // Given
            Long campusId = 1L;
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(campusRepository.findByIdAndIsActiveTrue(campusId)).thenReturn(java.util.Optional.of(savedCampus));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.setCampusPropertyValues(campusId, validCampusPropertyValues, request));

            assertEquals("User does not have access to this campus", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(campusId);
            verifyNoInteractions(institutionPropertyRepository, institutionPropertyValueRepository);
        }

        @Test
        @DisplayName("Should handle campus-specific property types")
        void shouldHandleCampusSpecificPropertyTypes() {
            // Given
            Long campusId = 1L;
            InstitutionPropertyValueSetDto locationValueDto = InstitutionPropertyValueSetDto.builder()
                    .propertyId(2L)
                    .jsonValue("{\"address\": \"123 Main St\", \"coordinates\": [41.0082, 28.9784]}")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(campusId)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionPropertyRepository.findByIdAndIsActiveTrue(2L)).thenReturn(java.util.Optional.of(existingCampusProperty));
            when(institutionPropertyValueRepository.findByPropertyIdAndCampusIdAndIsActiveTrue(2L, campusId))
                    .thenReturn(java.util.Optional.empty());
            when(institutionPropertyValueRepository.save(any(InstitutionPropertyValue.class))).thenReturn(existingCampusPropertyValue);

            // When
            institutionService.setCampusPropertyValues(campusId, List.of(locationValueDto), request);

            // Then
            verify(institutionPropertyValueRepository).save(argThat(value ->
                    value.getJsonValue().equals("{\"address\": \"123 Main St\", \"coordinates\": [41.0082, 28.9784]}") &&
                            value.getCampus().getId().equals(campusId) &&
                            value.getSchool() == null
            ));
        }
    }

    // ================================ PROPERTY OPERATIONS TESTS ================================

    @Nested
    @DisplayName("createInstitutionProperty() Tests")
    class CreateInstitutionPropertyTests {

        private InstitutionPropertyCreateDto validPropertyCreateDto;
        private InstitutionProperty savedProperty;
        private InstitutionPropertyDto expectedPropertyDto;

        @BeforeEach
        void setUp() {
            validPropertyCreateDto = InstitutionPropertyCreateDto.builder()
                    .institutionTypeId(1L)
                    .name("facilities")
                    .displayName("Facilities")
                    .description("School facilities and amenities")
                    .dataType(PropertyDataType.TEXT)
                    .isRequired(false)
                    .isSearchable(true)
                    .isFilterable(true)
                    .showInCard(true)
                    .showInProfile(true)
                    .sortOrder(1)
                    .options("Swimming Pool,Gymnasium,Library,Cafeteria")
                    .defaultValue("Library")
                    .minValue(null)
                    .maxValue(null)
                    .minLength(1)
                    .maxLength(500)
                    .regexPattern(null)
                    .build();

            savedProperty = new InstitutionProperty();
            savedProperty.setId(1L);
            savedProperty.setName("facilities");
            savedProperty.setDisplayName("Facilities");
            savedProperty.setInstitutionType(validInstitutionType);
            savedProperty.setCreatedBy(1L);

            expectedPropertyDto = InstitutionPropertyDto.builder()
                    .id(1L)
                    .name("facilities")
                    .displayName("Facilities")
                    .description("School facilities and amenities")
                    .build();
        }

        @Test
        @DisplayName("Should create institution property successfully with valid data")
        void shouldCreateInstitutionPropertySuccessfullyWithValidData() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(institutionPropertyRepository.existsByNameIgnoreCaseAndInstitutionTypeIdAndIsActiveTrue("facilities", 1L)).thenReturn(false);
            when(institutionPropertyRepository.save(any(InstitutionProperty.class))).thenReturn(savedProperty);
            when(converterService.mapToDto(savedProperty)).thenReturn(expectedPropertyDto);

            // When
            InstitutionPropertyDto result = institutionService.createInstitutionProperty(validPropertyCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("facilities", result.getName());
            assertEquals("Facilities", result.getDisplayName());
            assertEquals("School facilities and amenities", result.getDescription());

            verify(jwtService).getUser(request);
            verify(institutionTypeRepository).findByIdAndIsActiveTrue(1L);
            verify(institutionPropertyRepository).existsByNameIgnoreCaseAndInstitutionTypeIdAndIsActiveTrue("facilities", 1L);
            verify(institutionPropertyRepository).save(argThat(property ->
                    property.getName().equals("facilities") &&
                            property.getDisplayName().equals("Facilities") &&
                            property.getDescription().equals("School facilities and amenities") &&
                            property.getDataType() == PropertyDataType.TEXT &&
                            property.getIsRequired().equals(false) &&
                            property.getIsSearchable().equals(true) &&
                            property.getIsFilterable().equals(true) &&
                            property.getShowInCard().equals(true) &&
                            property.getShowInProfile().equals(true) &&
                            property.getSortOrder().equals(1) &&
                            property.getOptions().equals("Swimming Pool,Gymnasium,Library,Cafeteria") &&
                            property.getDefaultValue().equals("Library") &&
                            property.getMinLength().equals(1) &&
                            property.getMaxLength().equals(500) &&
                            property.getInstitutionType().getId().equals(1L) &&
                            property.getCreatedBy().equals(1L)
            ));
            verify(converterService).mapToDto(savedProperty);
        }

        @Test
        @DisplayName("Should throw BusinessException when user cannot manage institution types")
        void shouldThrowBusinessExceptionWhenUserCannotManageInstitutionTypes() {
            // Given
            when(jwtService.getUser(request)).thenReturn(regularUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.createInstitutionProperty(validPropertyCreateDto, request));

            assertEquals("User does not have permission to manage institution types", exception.getMessage());

            verify(jwtService).getUser(request);
            verifyNoInteractions(institutionTypeRepository, institutionPropertyRepository);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when institution type not found")
        void shouldThrowResourceNotFoundExceptionWhenInstitutionTypeNotFound() {
            // Given
            Long nonExistentInstitutionTypeId = 999L;
            InstitutionPropertyCreateDto invalidTypeDto = InstitutionPropertyCreateDto.builder()
                    .institutionTypeId(nonExistentInstitutionTypeId)
                    .name("test-property")
                    .displayName("Test Property")
                    .dataType(PropertyDataType.TEXT)
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(institutionTypeRepository.findByIdAndIsActiveTrue(nonExistentInstitutionTypeId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.createInstitutionProperty(invalidTypeDto, request));

            assertEquals("Institution type not found", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(institutionTypeRepository).findByIdAndIsActiveTrue(nonExistentInstitutionTypeId);
            verifyNoInteractions(institutionPropertyRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when property name already exists for institution type")
        void shouldThrowBusinessExceptionWhenPropertyNameExistsForInstitutionType() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(institutionPropertyRepository.existsByNameIgnoreCaseAndInstitutionTypeIdAndIsActiveTrue("facilities", 1L)).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.createInstitutionProperty(validPropertyCreateDto, request));

            assertEquals("Property name already exists for this institution type", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(institutionTypeRepository).findByIdAndIsActiveTrue(1L);
            verify(institutionPropertyRepository).existsByNameIgnoreCaseAndInstitutionTypeIdAndIsActiveTrue("facilities", 1L);
            verify(institutionPropertyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle different property data types correctly")
        void shouldHandleDifferentPropertyDataTypesCorrectly() {
            // Test TEXT type
            testPropertyDataType(PropertyDataType.TEXT, "text-property", null, null, 1, 100, null);

            // Test NUMBER type
            testPropertyDataType(PropertyDataType.NUMBER, "number-property", 1.0, 100.0, null, null, null);

            // Test BOOLEAN type
            testPropertyDataType(PropertyDataType.BOOLEAN, "boolean-property", null, null, null, null, null);

            // Test DATE type
            testPropertyDataType(PropertyDataType.DATE, "date-property", null, null, null, null, "\\d{4}-\\d{2}-\\d{2}");
        }

        private void testPropertyDataType(PropertyDataType dataType, String propertyName,
                                          Double minValue, Double maxValue,
                                          Integer minLength, Integer maxLength, String regexPattern) {
            // Given
            InstitutionPropertyCreateDto typeSpecificDto = InstitutionPropertyCreateDto.builder()
                    .institutionTypeId(1L)
                    .name(propertyName)
                    .displayName(propertyName.replace("-", " "))
                    .dataType(dataType)
                    .minValue(minValue)
                    .maxValue(maxValue)
                    .minLength(minLength)
                    .maxLength(maxLength)
                    .regexPattern(regexPattern)
                    .build();

            InstitutionProperty typeSpecificProperty = new InstitutionProperty();
            typeSpecificProperty.setId(1L);
            typeSpecificProperty.setName(propertyName);
            typeSpecificProperty.setDataType(dataType);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(institutionPropertyRepository.existsByNameIgnoreCaseAndInstitutionTypeIdAndIsActiveTrue(propertyName, 1L)).thenReturn(false);
            when(institutionPropertyRepository.save(any(InstitutionProperty.class))).thenReturn(typeSpecificProperty);
            when(converterService.mapToDto(any(InstitutionProperty.class))).thenReturn(expectedPropertyDto);

            // When
            institutionService.createInstitutionProperty(typeSpecificDto, request);

            // Then
            verify(institutionPropertyRepository).save(argThat(property ->
                    property.getName().equals(propertyName) &&
                            property.getDataType() == dataType &&
                            Objects.equals(property.getMinValue(), minValue) &&
                            Objects.equals(property.getMaxValue(), maxValue) &&
                            Objects.equals(property.getMinLength(), minLength) &&
                            Objects.equals(property.getMaxLength(), maxLength) &&
                            Objects.equals(property.getRegexPattern(), regexPattern)
            ));

            reset(institutionPropertyRepository, converterService);
        }

        @Test
        @DisplayName("Should set default values for optional fields")
        void shouldSetDefaultValuesForOptionalFields() {
            // Given
            InstitutionPropertyCreateDto minimalDto = InstitutionPropertyCreateDto.builder()
                    .institutionTypeId(1L)
                    .name("minimal-property")
                    .displayName("Minimal Property")
                    .dataType(PropertyDataType.TEXT)
                    // All optional fields are null/not set
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(institutionPropertyRepository.existsByNameIgnoreCaseAndInstitutionTypeIdAndIsActiveTrue("minimal-property", 1L)).thenReturn(false);
            when(institutionPropertyRepository.save(any(InstitutionProperty.class))).thenReturn(savedProperty);
            when(converterService.mapToDto(any(InstitutionProperty.class))).thenReturn(expectedPropertyDto);

            // When
            institutionService.createInstitutionProperty(minimalDto, request);

            // Then
            verify(institutionPropertyRepository).save(argThat(property ->
                    property.getName().equals("minimal-property") &&
                            property.getDisplayName().equals("Minimal Property") &&
                            property.getDataType() == PropertyDataType.TEXT &&
                            property.getIsRequired() == null &&
                            property.getIsSearchable() == null &&
                            property.getIsFilterable() == null &&
                            property.getShowInCard() == null &&
                            property.getShowInProfile() == null &&
                            property.getSortOrder() == null &&
                            property.getOptions() == null &&
                            property.getDefaultValue() == null
            ));
        }

        @Test
        @DisplayName("Should handle complex validation rules")
        void shouldHandleComplexValidationRules() {
            // Given
            InstitutionPropertyCreateDto complexValidationDto = InstitutionPropertyCreateDto.builder()
                    .institutionTypeId(1L)
                    .name("email-property")
                    .displayName("Email Property")
                    .dataType(PropertyDataType.EMAIL)
                    .isRequired(true)
                    .minLength(5)
                    .maxLength(100)
                    .regexPattern("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
                    .defaultValue("example@domain.com")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(institutionPropertyRepository.existsByNameIgnoreCaseAndInstitutionTypeIdAndIsActiveTrue("email-property", 1L)).thenReturn(false);
            when(institutionPropertyRepository.save(any(InstitutionProperty.class))).thenReturn(savedProperty);
            when(converterService.mapToDto(any(InstitutionProperty.class))).thenReturn(expectedPropertyDto);

            // When
            institutionService.createInstitutionProperty(complexValidationDto, request);

            // Then
            verify(institutionPropertyRepository).save(argThat(property ->
                    property.getName().equals("email-property") &&
                            property.getDataType() == PropertyDataType.EMAIL &&
                            property.getIsRequired().equals(true) &&
                            property.getMinLength().equals(5) &&
                            property.getMaxLength().equals(100) &&
                            property.getRegexPattern().equals("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") &&
                            property.getDefaultValue().equals("example@domain.com")
            ));
        }
    }

    @Nested
    @DisplayName("setSchoolPropertyValues() Tests")
    class SetSchoolPropertyValuesTests {

        private List<InstitutionPropertyValueSetDto> validPropertyValues;
        private InstitutionProperty existingProperty;
        private InstitutionPropertyValue existingPropertyValue;

        @BeforeEach
        void setUp() {
            existingProperty = new InstitutionProperty();
            existingProperty.setId(1L);
            existingProperty.setName("test-property");
            existingProperty.setDataType(PropertyDataType.TEXT);

            existingPropertyValue = new InstitutionPropertyValue();
            existingPropertyValue.setId(1L);
            existingPropertyValue.setProperty(existingProperty);
            existingPropertyValue.setSchool(savedSchool);
            existingPropertyValue.setTextValue("old value");

            InstitutionPropertyValueSetDto valueDto = InstitutionPropertyValueSetDto.builder()
                    .propertyId(1L)
                    .textValue("updated value")
                    .numberValue(null)
                    .booleanValue(null)
                    .dateValue(null)
                    .datetimeValue(null)
                    .jsonValue(null)
                    .fileUrl(null)
                    .fileName(null)
                    .fileSize(null)
                    .mimeType(null)
                    .build();

            validPropertyValues = List.of(valueDto);
        }

        @Test
        @DisplayName("Should update existing property value successfully")
        void shouldUpdateExistingPropertyValueSuccessfully() {
            // Given
            Long schoolId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));
            when(institutionPropertyRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(existingProperty));
            when(institutionPropertyValueRepository.findByPropertyIdAndSchoolIdAndIsActiveTrue(1L, schoolId))
                    .thenReturn(java.util.Optional.of(existingPropertyValue));
            when(institutionPropertyValueRepository.save(any(InstitutionPropertyValue.class))).thenReturn(existingPropertyValue);

            // When
            assertDoesNotThrow(() -> institutionService.setSchoolPropertyValues(schoolId, validPropertyValues, request));

            // Then
            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(schoolId);
            verify(institutionPropertyRepository).findByIdAndIsActiveTrue(1L);
            verify(institutionPropertyValueRepository).findByPropertyIdAndSchoolIdAndIsActiveTrue(1L, schoolId);
            verify(institutionPropertyValueRepository).save(argThat(value ->
                    value.getTextValue().equals("updated value") &&
                            value.getUpdatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should create new property value when not exists")
        void shouldCreateNewPropertyValueWhenNotExists() {
            // Given
            Long schoolId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));
            when(institutionPropertyRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(existingProperty));
            when(institutionPropertyValueRepository.findByPropertyIdAndSchoolIdAndIsActiveTrue(1L, schoolId))
                    .thenReturn(java.util.Optional.empty()); // No existing value
            when(institutionPropertyValueRepository.save(any(InstitutionPropertyValue.class))).thenReturn(existingPropertyValue);

            // When
            institutionService.setSchoolPropertyValues(schoolId, validPropertyValues, request);

            // Then
            verify(institutionPropertyValueRepository).save(argThat(value ->
                    value.getProperty().getId().equals(1L) &&
                            value.getSchool().getId().equals(schoolId) &&
                            value.getCampus() == null && // Should be null for school property
                            value.getTextValue().equals("updated value") &&
                            value.getCreatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when school not found")
        void shouldThrowResourceNotFoundExceptionWhenSchoolNotFound() {
            // Given
            Long nonExistentSchoolId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(nonExistentSchoolId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.setSchoolPropertyValues(nonExistentSchoolId, validPropertyValues, request));

            assertEquals("School not found", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(nonExistentSchoolId);
            verifyNoInteractions(institutionPropertyRepository, institutionPropertyValueRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to school")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToSchool() {
            // Given
            Long schoolId = 1L;
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.setSchoolPropertyValues(schoolId, validPropertyValues, request));

            assertEquals("User does not have access to this school", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(schoolId);
            verifyNoInteractions(institutionPropertyRepository, institutionPropertyValueRepository);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when property not found")
        void shouldThrowResourceNotFoundExceptionWhenPropertyNotFound() {
            // Given
            Long schoolId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));
            when(institutionPropertyRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.setSchoolPropertyValues(schoolId, validPropertyValues, request));

            assertEquals("Property not found", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(schoolId);
            verify(institutionPropertyRepository).findByIdAndIsActiveTrue(1L);
            verifyNoInteractions(institutionPropertyValueRepository);
        }

        @Test
        @DisplayName("Should handle multiple property values in single request")
        void shouldHandleMultiplePropertyValuesInSingleRequest() {
            // Given
            Long schoolId = 1L;

            InstitutionProperty secondProperty = new InstitutionProperty();
            secondProperty.setId(2L);
            secondProperty.setName("second-property");

            InstitutionPropertyValueSetDto secondValueDto = InstitutionPropertyValueSetDto.builder()
                    .propertyId(2L)
                    .numberValue(42.0)
                    .build();

            List<InstitutionPropertyValueSetDto> multipleValues = List.of(
                    validPropertyValues.get(0), // First property value
                    secondValueDto              // Second property value
            );

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));
            when(institutionPropertyRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(existingProperty));
            when(institutionPropertyRepository.findByIdAndIsActiveTrue(2L)).thenReturn(java.util.Optional.of(secondProperty));
            when(institutionPropertyValueRepository.findByPropertyIdAndSchoolIdAndIsActiveTrue(1L, schoolId))
                    .thenReturn(java.util.Optional.empty());
            when(institutionPropertyValueRepository.findByPropertyIdAndSchoolIdAndIsActiveTrue(2L, schoolId))
                    .thenReturn(java.util.Optional.empty());
            when(institutionPropertyValueRepository.save(any(InstitutionPropertyValue.class))).thenReturn(existingPropertyValue);

            // When
            institutionService.setSchoolPropertyValues(schoolId, multipleValues, request);

            // Then
            verify(institutionPropertyRepository, times(2)).findByIdAndIsActiveTrue(anyLong());
            verify(institutionPropertyValueRepository, times(2)).findByPropertyIdAndSchoolIdAndIsActiveTrue(anyLong(), eq(schoolId));
            verify(institutionPropertyValueRepository, times(2)).save(any(InstitutionPropertyValue.class));
        }

        @Test
        @DisplayName("Should handle different data types in property values")
        void shouldHandleDifferentDataTypesInPropertyValues() {
            // Given
            Long schoolId = 1L;
            InstitutionPropertyValueSetDto complexValueDto = InstitutionPropertyValueSetDto.builder()
                    .propertyId(1L)
                    .textValue("text value")
                    .numberValue(123.45)
                    .booleanValue(true)
                    .dateValue("2024-01-15")
                    .datetimeValue("2024-01-15T10:30:00")
                    .jsonValue("{\"key\": \"value\"}")
                    .fileUrl("https://example.com/file.pdf")
                    .fileName("document.pdf")
                    .fileSize(1024L)
                    .mimeType("application/pdf")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));
            when(institutionPropertyRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(existingProperty));
            when(institutionPropertyValueRepository.findByPropertyIdAndSchoolIdAndIsActiveTrue(1L, schoolId))
                    .thenReturn(java.util.Optional.empty());
            when(institutionPropertyValueRepository.save(any(InstitutionPropertyValue.class))).thenReturn(existingPropertyValue);

            // When
            institutionService.setSchoolPropertyValues(schoolId, List.of(complexValueDto), request);

            // Then
            verify(institutionPropertyValueRepository).save(argThat(value ->
                    value.getTextValue().equals("text value") &&
                            value.getNumberValue().equals(123.45) &&
                            value.getBooleanValue().equals(true) &&
                            value.getDateValue().toString().equals("2024-01-15") &&
                            value.getJsonValue().equals("{\"key\": \"value\"}") &&
                            value.getFileUrl().equals("https://example.com/file.pdf") &&
                            value.getFileName().equals("document.pdf") &&
                            value.getFileSize().equals(1024L) &&
                            value.getMimeType().equals("application/pdf")
            ));
        }
    }

    @Nested
    @DisplayName("searchSchools() Tests")
    class SearchSchoolsTests {

        private SchoolSearchDto validSearchDto;
        private List<School> mockSchoolResults;
        private Page<School> mockSchoolPage;
        private List<SchoolSearchResultDto> expectedSearchResults;
        private Page<SchoolSearchResultDto> expectedSearchPage;

        @BeforeEach
        void setUp() {
            validSearchDto = SchoolSearchDto.builder()
                    .searchTerm("test school")
                    .institutionTypeIds(List.of(1L))
                    .minAge(6)
                    .maxAge(12)
                    .minFee(1000.0)
                    .maxFee(2000.0)
                    .curriculumType("National")
                    .languageOfInstruction("Turkish")
                    .countryId(1L)
                    .provinceId(1L)
                    .districtId(1L)
                    .minRating(4.0)
                    .hasActiveCampaigns(true)
                    .isSubscribed(true)
                    .sortBy("rating")
                    .sortDirection("desc")
                    .page(0)
                    .size(20)
                    .build();

            // Mock school results
            School school1 = new School();
            school1.setId(1L);
            school1.setName("Test School 1");
            school1.setSlug("test-school-1");

            School school2 = new School();
            school2.setId(2L);
            school2.setName("Test School 2");
            school2.setSlug("test-school-2");

            mockSchoolResults = List.of(school1, school2);

            // Mock search result DTOs
            SchoolSearchResultDto result1 = SchoolSearchResultDto.builder()
                    .id(1L)
                    .name("Test School 1")
                    .slug("test-school-1")
                    .build();

            SchoolSearchResultDto result2 = SchoolSearchResultDto.builder()
                    .id(2L)
                    .name("Test School 2")
                    .slug("test-school-2")
                    .build();

            expectedSearchResults = List.of(result1, result2);

            // Mock pages
            mockSchoolPage = new org.springframework.data.domain.PageImpl<>(
                    mockSchoolResults,
                    org.springframework.data.domain.PageRequest.of(0, 20),
                    2L
            );

            expectedSearchPage = new org.springframework.data.domain.PageImpl<>(
                    expectedSearchResults,
                    org.springframework.data.domain.PageRequest.of(0, 20),
                    2L
            );
        }

        @Test
        @DisplayName("Should search schools successfully with all filters")
        void shouldSearchSchoolsSuccessfullyWithAllFilters() {
            // Given
            org.springframework.data.domain.Pageable expectedPageable =
                    org.springframework.data.domain.PageRequest.of(0, 20,
                            org.springframework.data.domain.Sort.by(
                                    org.springframework.data.domain.Sort.Direction.DESC, "ratingAverage"));

            when(schoolRepository.searchSchools(
                    eq("test school"),
                    eq(List.of(1L)),
                    eq(6),
                    eq(12),
                    eq(1000.0),
                    eq(2000.0),
                    eq("National"),
                    eq("Turkish"),
                    eq(1L),
                    eq(1L),
                    eq(1L),
                    eq(null), // neighborhoodId
                    eq(null), // latitude
                    eq(null), // longitude
                    eq(null), // radiusKm
                    eq(4.0),
                    eq(true),
                    eq(true),
                    any(org.springframework.data.domain.Pageable.class)
            )).thenReturn(mockSchoolPage);

            when(converterService.mapToSearchResultDto(mockSchoolResults.get(0))).thenReturn(expectedSearchResults.get(0));
            when(converterService.mapToSearchResultDto(mockSchoolResults.get(1))).thenReturn(expectedSearchResults.get(1));

            // When
            Page<SchoolSearchResultDto> result = institutionService.searchSchools(validSearchDto);

            // Then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals("Test School 1", result.getContent().get(0).getName());
            assertEquals("Test School 2", result.getContent().get(1).getName());
            assertEquals(2L, result.getTotalElements());

            verify(schoolRepository).searchSchools(
                    eq("test school"),
                    eq(List.of(1L)),
                    eq(6),
                    eq(12),
                    eq(1000.0),
                    eq(2000.0),
                    eq("National"),
                    eq("Turkish"),
                    eq(1L),
                    eq(1L),
                    eq(1L),
                    eq(null),
                    eq(null),
                    eq(null),
                    eq(null),
                    eq(4.0),
                    eq(true),
                    eq(true),
                    any(org.springframework.data.domain.Pageable.class)
            );

            verify(converterService, times(2)).mapToSearchResultDto(any(School.class));
        }

        @Test
        @DisplayName("Should handle search with minimal criteria")
        void shouldHandleSearchWithMinimalCriteria() {
            // Given
            SchoolSearchDto minimalSearch = SchoolSearchDto.builder()
                    .searchTerm("school")
                    .build(); // Only search term provided

            when(schoolRepository.searchSchools(
                    eq("school"),
                    eq(null), // institutionTypeIds
                    eq(null), // minAge
                    eq(null), // maxAge
                    eq(null), // minFee
                    eq(null), // maxFee
                    eq(null), // curriculumType
                    eq(null), // languageOfInstruction
                    eq(null), // countryId
                    eq(null), // provinceId
                    eq(null), // districtId
                    eq(null), // neighborhoodId
                    eq(null), // latitude
                    eq(null), // longitude
                    eq(null), // radiusKm
                    eq(null), // minRating
                    eq(null), // hasActiveCampaigns
                    eq(null), // isSubscribed
                    any(org.springframework.data.domain.Pageable.class)
            )).thenReturn(mockSchoolPage);

            when(converterService.mapToSearchResultDto(any(School.class)))
                    .thenReturn(expectedSearchResults.get(0), expectedSearchResults.get(1));

            // When
            Page<SchoolSearchResultDto> result = institutionService.searchSchools(minimalSearch);

            // Then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());

            verify(schoolRepository).searchSchools(
                    eq("school"),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null),
                    any(org.springframework.data.domain.Pageable.class)
            );
        }

        @Test
        @DisplayName("Should use default pagination when not specified")
        void shouldUseDefaultPaginationWhenNotSpecified() {
            // Given
            SchoolSearchDto noPaginationDto = SchoolSearchDto.builder()
                    .searchTerm("school")
                    .page(null) // No pagination specified
                    .size(null)
                    .build();

            when(schoolRepository.searchSchools(
                    anyString(), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                    any(org.springframework.data.domain.Pageable.class)
            )).thenReturn(mockSchoolPage);

            when(converterService.mapToSearchResultDto(any(School.class)))
                    .thenReturn(expectedSearchResults.get(0), expectedSearchResults.get(1));

            // When
            institutionService.searchSchools(noPaginationDto);

            // Then
            verify(schoolRepository).searchSchools(
                    anyString(), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                    argThat(pageable ->
                            pageable.getPageNumber() == 0 && // Default page
                                    pageable.getPageSize() == 20     // Default size
                    )
            );
        }

        @Test
        @DisplayName("Should handle different sort options correctly")
        void shouldHandleDifferentSortOptionsCorrectly() {
            // Test different sort combinations
            testSortOption("rating", "desc", "ratingAverage", org.springframework.data.domain.Sort.Direction.DESC);
            testSortOption("price", "asc", "monthlyFee", org.springframework.data.domain.Sort.Direction.ASC);
            testSortOption("name", "asc", "name", org.springframework.data.domain.Sort.Direction.ASC);
            testSortOption("distance", "asc", "distance", org.springframework.data.domain.Sort.Direction.ASC);
            testSortOption(null, null, "createdAt", org.springframework.data.domain.Sort.Direction.ASC); // Default
        }

        private void testSortOption(String sortBy, String sortDirection, String expectedField, org.springframework.data.domain.Sort.Direction expectedDirection) {
            // Given
            SchoolSearchDto sortTestDto = SchoolSearchDto.builder()
                    .searchTerm("school")
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();

            when(schoolRepository.searchSchools(
                    anyString(), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                    any(org.springframework.data.domain.Pageable.class)
            )).thenReturn(mockSchoolPage);

            when(converterService.mapToSearchResultDto(any(School.class)))
                    .thenReturn(expectedSearchResults.get(0));

            // When
            institutionService.searchSchools(sortTestDto);

            // Then
            verify(schoolRepository).searchSchools(
                    anyString(), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                    argThat(pageable -> {
                        org.springframework.data.domain.Sort sort = pageable.getSort();
                        org.springframework.data.domain.Sort.Order order = sort.getOrderFor(expectedField);
                        return order != null && order.getDirection() == expectedDirection;
                    })
            );

            reset(schoolRepository, converterService);
        }

        @Test
        @DisplayName("Should handle location-based search with coordinates and radius")
        void shouldHandleLocationBasedSearchWithCoordinatesAndRadius() {
            // Given
            SchoolSearchDto locationSearch = SchoolSearchDto.builder()
                    .searchTerm("school")
                    .latitude(41.0082)
                    .longitude(28.9784)
                    .radiusKm(5.0)
                    .build();

            when(schoolRepository.searchSchools(
                    eq("school"),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null), eq(null), // Location IDs should be null when coordinates are used
                    eq(41.0082), eq(28.9784), eq(5.0), // Coordinates and radius
                    eq(null), eq(null), eq(null),
                    any(org.springframework.data.domain.Pageable.class)
            )).thenReturn(mockSchoolPage);

            when(converterService.mapToSearchResultDto(any(School.class)))
                    .thenReturn(expectedSearchResults.get(0), expectedSearchResults.get(1));

            // When
            Page<SchoolSearchResultDto> result = institutionService.searchSchools(locationSearch);

            // Then
            assertNotNull(result);
            verify(schoolRepository).searchSchools(
                    eq("school"),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null), eq(null),
                    eq(41.0082), eq(28.9784), eq(5.0),
                    eq(null), eq(null), eq(null),
                    any(org.springframework.data.domain.Pageable.class)
            );
        }

        @Test
        @DisplayName("Should return empty page when no schools found")
        void shouldReturnEmptyPageWhenNoSchoolsFound() {
            // Given
            SchoolSearchDto searchDto = SchoolSearchDto.builder()
                    .searchTerm("nonexistent")
                    .build();

            Page<School> emptyPage = new org.springframework.data.domain.PageImpl<>(
                    Collections.emptyList(),
                    org.springframework.data.domain.PageRequest.of(0, 20),
                    0L
            );

            when(schoolRepository.searchSchools(
                    anyString(), any(), any(), any(), any(), any(), any(), any(),
                    any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                    any(org.springframework.data.domain.Pageable.class)
            )).thenReturn(emptyPage);

            // When
            Page<SchoolSearchResultDto> result = institutionService.searchSchools(searchDto);

            // Then
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0L, result.getTotalElements());
            assertEquals(0, result.getTotalPages());

            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should handle multiple institution type filters")
        void shouldHandleMultipleInstitutionTypeFilters() {
            // Given
            SchoolSearchDto multiTypeSearch = SchoolSearchDto.builder()
                    .searchTerm("school")
                    .institutionTypeIds(List.of(1L, 2L, 3L)) // Multiple institution types
                    .build();

            when(schoolRepository.searchSchools(
                    eq("school"),
                    eq(List.of(1L, 2L, 3L)), // Multiple institution type IDs
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null),
                    any(org.springframework.data.domain.Pageable.class)
            )).thenReturn(mockSchoolPage);

            when(converterService.mapToSearchResultDto(any(School.class)))
                    .thenReturn(expectedSearchResults.get(0), expectedSearchResults.get(1));

            // When
            Page<SchoolSearchResultDto> result = institutionService.searchSchools(multiTypeSearch);

            // Then
            assertNotNull(result);
            verify(schoolRepository).searchSchools(
                    eq("school"),
                    eq(List.of(1L, 2L, 3L)),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null),
                    any(org.springframework.data.domain.Pageable.class)
            );
        }

        @Test
        @DisplayName("Should handle edge case values for age and fee ranges")
        void shouldHandleEdgeCaseValuesForAgeAndFeeRanges() {
            // Given
            SchoolSearchDto edgeCaseSearch = SchoolSearchDto.builder()
                    .searchTerm("school")
                    .minAge(0)      // Edge case: minimum age
                    .maxAge(100)    // Edge case: maximum age
                    .minFee(0.0)    // Edge case: free schools
                    .maxFee(999999.0) // Edge case: very expensive schools
                    .build();

            when(schoolRepository.searchSchools(
                    eq("school"),
                    eq(null),
                    eq(0), eq(100),           // Age range
                    eq(0.0), eq(999999.0),    // Fee range
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    any(org.springframework.data.domain.Pageable.class)
            )).thenReturn(mockSchoolPage);

            when(converterService.mapToSearchResultDto(any(School.class)))
                    .thenReturn(expectedSearchResults.get(0), expectedSearchResults.get(1));

            // When
            Page<SchoolSearchResultDto> result = institutionService.searchSchools(edgeCaseSearch);

            // Then
            assertNotNull(result);
            verify(schoolRepository).searchSchools(
                    eq("school"), eq(null),
                    eq(0), eq(100), eq(0.0), eq(999999.0),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    eq(null), eq(null), eq(null), eq(null), eq(null), eq(null),
                    any(org.springframework.data.domain.Pageable.class)
            );
        }
    }

    // ================================ SCHOOL OPERATIONS TESTS ================================

    @Nested
    @DisplayName("createSchool() Tests")
    class CreateSchoolTests {

        @Test
        @DisplayName("Should create school successfully with valid data")
        void shouldCreateSchoolSuccessfullyWithValidData() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(schoolRepository.existsByNameIgnoreCaseAndCampusIdAndIsActiveTrue("Test School", 1L)).thenReturn(false);
            when(schoolRepository.existsBySlug("test-school")).thenReturn(false);
            when(schoolRepository.save(any(School.class))).thenReturn(savedSchool);
            when(converterService.mapToDto(savedSchool)).thenReturn(expectedSchoolDto);

            // When
            SchoolDto result = institutionService.createSchool(validSchoolCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("Test School", result.getName());
            assertEquals("test-school", result.getSlug());
            assertEquals("Test school description", result.getDescription());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(1L);
            verify(institutionTypeRepository).findByIdAndIsActiveTrue(1L);
            verify(schoolRepository).existsByNameIgnoreCaseAndCampusIdAndIsActiveTrue("Test School", 1L);
            verify(schoolRepository).existsBySlug("test-school");
            verify(schoolRepository).save(argThat(school ->
                    school.getName().equals("Test School") &&
                            school.getSlug().equals("test-school") &&
                            school.getCampus().getId().equals(1L) &&
                            school.getInstitutionType().getId().equals(1L) &&
                            school.getCreatedBy().equals(1L)
            ));
            verify(converterService).mapToDto(savedSchool);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campus not found")
        void shouldThrowResourceNotFoundExceptionWhenCampusNotFound() {
            // Given
            Long nonExistentCampusId = 999L;
            SchoolCreateDto invalidCampusDto = SchoolCreateDto.builder()
                    .campusId(nonExistentCampusId)
                    .institutionTypeId(1L)
                    .name("Test School")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(nonExistentCampusId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.createSchool(invalidCampusDto, request));

            assertEquals("Campus not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(nonExistentCampusId);
            verifyNoInteractions(institutionTypeRepository, schoolRepository);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when institution type not found")
        void shouldThrowResourceNotFoundExceptionWhenInstitutionTypeNotFound() {
            // Given
            Long nonExistentInstitutionTypeId = 999L;
            SchoolCreateDto invalidInstitutionTypeDto = SchoolCreateDto.builder()
                    .campusId(1L)
                    .institutionTypeId(nonExistentInstitutionTypeId)
                    .name("Test School")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionTypeRepository.findByIdAndIsActiveTrue(nonExistentInstitutionTypeId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.createSchool(invalidInstitutionTypeDto, request));

            assertEquals("Institution type not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(1L);
            verify(institutionTypeRepository).findByIdAndIsActiveTrue(nonExistentInstitutionTypeId);
            verifyNoInteractions(schoolRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to campus")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToCampus() {
            // Given
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.createSchool(validSchoolCreateDto, request));

            assertEquals("User does not have access to this campus", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(1L);
            verify(institutionTypeRepository).findByIdAndIsActiveTrue(1L);
            verifyNoInteractions(schoolRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when school name already exists in campus")
        void shouldThrowBusinessExceptionWhenSchoolNameExistsInCampus() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(schoolRepository.existsByNameIgnoreCaseAndCampusIdAndIsActiveTrue("Test School", 1L)).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.createSchool(validSchoolCreateDto, request));

            assertEquals("School name already exists in this campus: Test School", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(1L);
            verify(institutionTypeRepository).findByIdAndIsActiveTrue(1L);
            verify(schoolRepository).existsByNameIgnoreCaseAndCampusIdAndIsActiveTrue("Test School", 1L);
            verify(schoolRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should generate unique slug when original slug exists")
        void shouldGenerateUniqueSlugWhenSlugExists() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(schoolRepository.existsByNameIgnoreCaseAndCampusIdAndIsActiveTrue("Test School", 1L)).thenReturn(false);
            when(schoolRepository.existsBySlug("test-school")).thenReturn(true);
            when(schoolRepository.existsBySlug(startsWith("test-school-"))).thenReturn(false);

            School schoolWithUniqueSlug = new School();
            schoolWithUniqueSlug.setId(1L);
            schoolWithUniqueSlug.setSlug("test-school-" + System.currentTimeMillis());

            when(schoolRepository.save(any(School.class))).thenReturn(schoolWithUniqueSlug);
            when(converterService.mapToDto(any(School.class))).thenReturn(expectedSchoolDto);

            // When
            SchoolDto result = institutionService.createSchool(validSchoolCreateDto, request);

            // Then
            assertNotNull(result);

            verify(schoolRepository).existsBySlug("test-school");
            verify(schoolRepository).save(argThat(school ->
                    school.getSlug().startsWith("test-school-") &&
                            !school.getSlug().equals("test-school")
            ));
        }

        @Test
        @DisplayName("Should set all school fields correctly from create DTO")
        void shouldSetAllSchoolFieldsCorrectly() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(schoolRepository.existsByNameIgnoreCaseAndCampusIdAndIsActiveTrue(anyString(), anyLong())).thenReturn(false);
            when(schoolRepository.existsBySlug(anyString())).thenReturn(false);
            when(schoolRepository.save(any(School.class))).thenReturn(savedSchool);
            when(converterService.mapToDto(any(School.class))).thenReturn(expectedSchoolDto);

            // When
            institutionService.createSchool(validSchoolCreateDto, request);

            // Then
            verify(schoolRepository).save(argThat(school ->
                    school.getName().equals("Test School") &&
                            school.getDescription().equals("Test school description") &&
                            school.getLogoUrl().equals("https://example.com/school-logo.png") &&
                            school.getCoverImageUrl().equals("https://example.com/school-cover.jpg") &&
                            school.getEmail().equals("school@testbrand.com") &&
                            school.getPhone().equals("+90 555 123 4567") &&
                            school.getExtension().equals("101") &&
                            school.getMinAge().equals(6) &&
                            school.getMaxAge().equals(11) &&
                            school.getCapacity().equals(500) &&
                            school.getCurrentStudentCount().equals(350) &&
                            school.getClassSizeAverage().equals(25) &&
                            school.getCurriculumType().equals("National") &&
                            school.getLanguageOfInstruction().equals("Turkish") &&
                            school.getForeignLanguages().equals("English, German") &&
                            school.getRegistrationFee().equals(2500.0) &&
                            school.getMonthlyFee().equals(1200.0) &&
                            school.getAnnualFee().equals(12000.0) &&
                            school.getMetaTitle().equals("Test School - Best Primary Education") &&
                            school.getMetaDescription().equals("Test school meta description") &&
                            school.getMetaKeywords().equals("primary, school, education") &&
                            school.getCampus().getId().equals(1L) &&
                            school.getInstitutionType().getId().equals(1L) &&
                            school.getCreatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should handle minimal school data")
        void shouldHandleMinimalSchoolData() {
            // Given
            SchoolCreateDto minimalDto = SchoolCreateDto.builder()
                    .campusId(1L)
                    .institutionTypeId(1L)
                    .name("Minimal School")
                    .build(); // Only required fields

            School minimalSchool = new School();
            minimalSchool.setId(1L);
            minimalSchool.setName("Minimal School");
            minimalSchool.setSlug("minimal-school");
            minimalSchool.setCampus(savedCampus);
            minimalSchool.setInstitutionType(validInstitutionType);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(schoolRepository.existsByNameIgnoreCaseAndCampusIdAndIsActiveTrue("Minimal School", 1L)).thenReturn(false);
            when(schoolRepository.existsBySlug("minimal-school")).thenReturn(false);
            when(schoolRepository.save(any(School.class))).thenReturn(minimalSchool);
            when(converterService.mapToDto(any(School.class))).thenReturn(expectedSchoolDto);

            // When
            SchoolDto result = institutionService.createSchool(minimalDto, request);

            // Then
            assertNotNull(result);
            verify(schoolRepository).save(argThat(school ->
                    school.getName().equals("Minimal School") &&
                            school.getDescription() == null &&
                            school.getEmail() == null &&
                            school.getMinAge() == null &&
                            school.getMonthlyFee() == null &&
                            school.getCampus().getId().equals(1L) &&
                            school.getInstitutionType().getId().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should validate age range when both min and max age provided")
        void shouldValidateAgeRangeWhenBothMinAndMaxAgeProvided() {
            // Given - This is more of a documentation test since the validation logic isn't in the current service
            SchoolCreateDto validAgeRangeDto = SchoolCreateDto.builder()
                    .campusId(1L)
                    .institutionTypeId(1L)
                    .name("Age Range School")
                    .minAge(6)
                    .maxAge(11) // Valid range: 6-11
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(schoolRepository.existsByNameIgnoreCaseAndCampusIdAndIsActiveTrue(anyString(), anyLong())).thenReturn(false);
            when(schoolRepository.existsBySlug(anyString())).thenReturn(false);
            when(schoolRepository.save(any(School.class))).thenReturn(savedSchool);
            when(converterService.mapToDto(any(School.class))).thenReturn(expectedSchoolDto);

            // When
            SchoolDto result = institutionService.createSchool(validAgeRangeDto, request);

            // Then
            assertNotNull(result);
            verify(schoolRepository).save(argThat(school ->
                    school.getMinAge().equals(6) &&
                            school.getMaxAge().equals(11)
            ));
        }

        @Test
        @DisplayName("Should handle zero and negative values gracefully")
        void shouldHandleZeroAndNegativeValuesGracefully() {
            // Given
            SchoolCreateDto edgeCaseDto = SchoolCreateDto.builder()
                    .campusId(1L)
                    .institutionTypeId(1L)
                    .name("Edge Case School")
                    .capacity(0) // Zero capacity
                    .currentStudentCount(0) // Zero current students
                    .registrationFee(0.0) // Free registration
                    .monthlyFee(0.0) // Free monthly fee
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(savedCampus));
            when(institutionTypeRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validInstitutionType));
            when(schoolRepository.existsByNameIgnoreCaseAndCampusIdAndIsActiveTrue(anyString(), anyLong())).thenReturn(false);
            when(schoolRepository.existsBySlug(anyString())).thenReturn(false);
            when(schoolRepository.save(any(School.class))).thenReturn(savedSchool);
            when(converterService.mapToDto(any(School.class))).thenReturn(expectedSchoolDto);

            // When
            SchoolDto result = institutionService.createSchool(edgeCaseDto, request);

            // Then
            assertNotNull(result);
            verify(schoolRepository).save(argThat(school ->
                    school.getCapacity().equals(0) &&
                            school.getCurrentStudentCount().equals(0) &&
                            school.getRegistrationFee().equals(0.0) &&
                            school.getMonthlyFee().equals(0.0)
            ));
        }
    }

    @Nested
    @DisplayName("getSchoolById() Tests")
    class GetSchoolByIdTests {

        @Test
        @DisplayName("Should return school successfully when user has access")
        void shouldReturnSchoolSuccessfullyWhenUserHasAccess() {
            // Given
            Long schoolId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));
            when(converterService.mapToDto(savedSchool)).thenReturn(expectedSchoolDto);

            // When
            SchoolDto result = institutionService.getSchoolById(schoolId, request);

            // Then
            assertNotNull(result);
            assertEquals(expectedSchoolDto.getName(), result.getName());
            assertEquals(expectedSchoolDto.getSlug(), result.getSlug());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(schoolId);
            verify(converterService).mapToDto(savedSchool);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when school not found")
        void shouldThrowResourceNotFoundExceptionWhenSchoolNotFound() {
            // Given
            Long nonExistentSchoolId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(nonExistentSchoolId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.getSchoolById(nonExistentSchoolId, request));

            assertEquals("School not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(nonExistentSchoolId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to school")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToSchool() {
            // Given
            Long schoolId = 1L;
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.getSchoolById(schoolId, request));

            assertEquals("User does not have access to this school", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(schoolId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should allow access for system user regardless of school")
        void shouldAllowAccessForSystemUserRegardlessOfSchool() {
            // Given
            Long schoolId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));
            when(converterService.mapToDto(savedSchool)).thenReturn(expectedSchoolDto);

            // When
            SchoolDto result = institutionService.getSchoolById(schoolId, request);

            // Then
            assertNotNull(result);
            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(schoolId);
            verify(converterService).mapToDto(savedSchool);
        }
    }

    @Nested
    @DisplayName("getSchoolDetailById() Tests")
    class GetSchoolDetailByIdTests {

        private SchoolDetailDto expectedSchoolDetailDto;

        @BeforeEach
        void setUp() {
            expectedSchoolDetailDto = SchoolDetailDto.builder()
                    .school(expectedSchoolDto)
                    .campus(expectedCampusDto)
                    .brand(expectedBrandDto)
                    .allProperties(Collections.emptyList())
                    .build();
        }

        @Test
        @DisplayName("Should return detailed school information when user has access")
        void shouldReturnDetailedSchoolInformationWhenUserHasAccess() {
            // Given
            Long schoolId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));
            when(converterService.mapToDetailDto(savedSchool)).thenReturn(expectedSchoolDetailDto);

            // When
            SchoolDetailDto result = institutionService.getSchoolDetailById(schoolId, request);

            // Then
            assertNotNull(result);
            assertNotNull(result.getSchool());
            assertEquals(expectedSchoolDto.getName(), result.getSchool().getName());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(schoolId);
            verify(converterService).mapToDetailDto(savedSchool);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when school not found")
        void shouldThrowResourceNotFoundExceptionWhenSchoolNotFound() {
            // Given
            Long nonExistentSchoolId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(nonExistentSchoolId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.getSchoolDetailById(nonExistentSchoolId, request));

            assertEquals("School not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(nonExistentSchoolId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to school")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToSchool() {
            // Given
            Long schoolId = 1L;
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.getSchoolDetailById(schoolId, request));

            assertEquals("User does not have access to this school", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(schoolRepository).findByIdAndIsActiveTrue(schoolId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should use mapToDetailDto for detailed information")
        void shouldUseMapToDetailDtoForDetailedInformation() {
            // Given
            Long schoolId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(schoolRepository.findByIdAndIsActiveTrue(schoolId)).thenReturn(java.util.Optional.of(savedSchool));
            when(converterService.mapToDetailDto(savedSchool)).thenReturn(expectedSchoolDetailDto);

            // When
            institutionService.getSchoolDetailById(schoolId, request);

            // Then
            // Verify that it uses the detailed converter method, not the regular one
            verify(converterService).mapToDetailDto(savedSchool);
            verify(converterService, never()).mapToDto(any(School.class));
        }
    }

    // ================================ CAMPUS OPERATIONS TESTS ================================

    @Nested
    @DisplayName("createCampus() Tests")
    class CreateCampusTests {

        @Test
        @DisplayName("Should create campus successfully with valid data and existing brand")
        void shouldCreateCampusSuccessfullyWithValidDataAndExistingBrand() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validBrand));
            when(campusRepository.existsByNameIgnoreCaseAndBrandIdAndIsActiveTrue("Test Campus", 1L)).thenReturn(false);
            when(campusRepository.existsBySlug("test-campus")).thenReturn(false);
            when(campusRepository.save(any(Campus.class))).thenReturn(savedCampus);
            when(converterService.mapToDto(savedCampus)).thenReturn(expectedCampusDto);

            // When
            CampusDto result = institutionService.createCampus(validCampusCreateDto, request);

            // Then
            assertNotNull(result);
            assertEquals("Test Campus", result.getName());
            assertEquals("test-campus", result.getSlug());
            assertEquals("Test campus description", result.getDescription());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(1L);
            verify(campusRepository).existsByNameIgnoreCaseAndBrandIdAndIsActiveTrue("Test Campus", 1L);
            verify(campusRepository).existsBySlug("test-campus");
            verify(campusRepository).save(argThat(campus ->
                    campus.getName().equals("Test Campus") &&
                            campus.getSlug().equals("test-campus") &&
                            campus.getBrand().getId().equals(1L) &&
                            campus.getCreatedBy().equals(1L)
            ));
            verify(converterService).mapToDto(savedCampus);
        }

        @Test
        @DisplayName("Should create campus successfully without brand")
        void shouldCreateCampusSuccessfullyWithoutBrand() {
            // Given
            CampusCreateDto noBrandDto = CampusCreateDto.builder()
                    .brandId(null) // No brand specified
                    .name("Independent Campus")
                    .description("Campus without brand")
                    .build();

            Campus savedCampusNoBrand = new Campus();
            savedCampusNoBrand.setId(1L);
            savedCampusNoBrand.setName("Independent Campus");
            savedCampusNoBrand.setSlug("independent-campus");
            savedCampusNoBrand.setBrand(null);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.existsBySlug("independent-campus")).thenReturn(false);
            when(campusRepository.save(any(Campus.class))).thenReturn(savedCampusNoBrand);
            when(converterService.mapToDto(savedCampusNoBrand)).thenReturn(expectedCampusDto);

            // When
            CampusDto result = institutionService.createCampus(noBrandDto, request);

            // Then
            assertNotNull(result);

            verify(jwtService).getUser(request);
            verifyNoInteractions(brandRepository); // No brand lookup
            verify(campusRepository, never()).existsByNameIgnoreCaseAndBrandIdAndIsActiveTrue(anyString(), anyLong());
            verify(campusRepository).save(argThat(campus ->
                    campus.getName().equals("Independent Campus") &&
                            campus.getBrand() == null
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when brand not found")
        void shouldThrowResourceNotFoundExceptionWhenBrandNotFound() {
            // Given
            Long nonExistentBrandId = 999L;
            CampusCreateDto invalidBrandDto = CampusCreateDto.builder()
                    .brandId(nonExistentBrandId)
                    .name("Test Campus")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(nonExistentBrandId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.createCampus(invalidBrandDto, request));

            assertEquals("Brand not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(nonExistentBrandId);
            verifyNoInteractions(campusRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to brand")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToBrand() {
            // Given
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(brandRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validBrand));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.createCampus(validCampusCreateDto, request));

            assertEquals("User does not have access to this brand", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(1L);
            verifyNoInteractions(campusRepository);
        }

        @Test
        @DisplayName("Should throw BusinessException when campus name already exists in brand")
        void shouldThrowBusinessExceptionWhenCampusNameExistsInBrand() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validBrand));
            when(campusRepository.existsByNameIgnoreCaseAndBrandIdAndIsActiveTrue("Test Campus", 1L)).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.createCampus(validCampusCreateDto, request));

            assertEquals("Campus name already exists in this brand: Test Campus", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(1L);
            verify(campusRepository).existsByNameIgnoreCaseAndBrandIdAndIsActiveTrue("Test Campus", 1L);
            verify(campusRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should generate unique slug when original slug exists")
        void shouldGenerateUniqueSlugWhenSlugExists() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validBrand));
            when(campusRepository.existsByNameIgnoreCaseAndBrandIdAndIsActiveTrue("Test Campus", 1L)).thenReturn(false);
            when(campusRepository.existsBySlug("test-campus")).thenReturn(true);
            when(campusRepository.existsBySlug(startsWith("test-campus-"))).thenReturn(false);

            Campus campusWithUniqueSlug = new Campus();
            campusWithUniqueSlug.setId(1L);
            campusWithUniqueSlug.setSlug("test-campus-" + System.currentTimeMillis());

            when(campusRepository.save(any(Campus.class))).thenReturn(campusWithUniqueSlug);
            when(converterService.mapToDto(any(Campus.class))).thenReturn(expectedCampusDto);

            // When
            CampusDto result = institutionService.createCampus(validCampusCreateDto, request);

            // Then
            assertNotNull(result);

            verify(campusRepository).existsBySlug("test-campus");
            verify(campusRepository).save(argThat(campus ->
                    campus.getSlug().startsWith("test-campus-") &&
                            !campus.getSlug().equals("test-campus")
            ));
        }

        @Test
        @DisplayName("Should set all campus fields correctly from create DTO")
        void shouldSetAllCampusFieldsCorrectly() {
            // Given
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(1L)).thenReturn(java.util.Optional.of(validBrand));
            when(campusRepository.existsByNameIgnoreCaseAndBrandIdAndIsActiveTrue(anyString(), anyLong())).thenReturn(false);
            when(campusRepository.existsBySlug(anyString())).thenReturn(false);
            when(campusRepository.save(any(Campus.class))).thenReturn(savedCampus);
            when(converterService.mapToDto(any(Campus.class))).thenReturn(expectedCampusDto);
            //when(converterService.mapToSummaryEntity(any())).thenReturn(null); // District/Province/Country mapping

            // When
            institutionService.createCampus(validCampusCreateDto, request);

            // Then
            verify(campusRepository).save(argThat(campus ->
                    campus.getName().equals("Test Campus") &&
                            campus.getDescription().equals("Test campus description") &&
                            campus.getLogoUrl().equals("https://example.com/campus-logo.png") &&
                            campus.getCoverImageUrl().equals("https://example.com/campus-cover.jpg") &&
                            campus.getEmail().equals("campus@testbrand.com") &&
                            campus.getPhone().equals("+90 555 123 4567") &&
                            campus.getFax().equals("+90 555 123 4568") &&
                            campus.getWebsiteUrl().equals("https://campus.testbrand.com") &&
                            campus.getAddressLine1().equals("Test Address Line 1") &&
                            campus.getAddressLine2().equals("Test Address Line 2") &&
                            campus.getPostalCode().equals("34000") &&
                            campus.getLatitude().equals(41.0082) &&
                            campus.getLongitude().equals(28.9784) &&
                            campus.getEstablishedYear().equals(2021) &&
                            campus.getFacebookUrl().equals("https://facebook.com/testcampus") &&
                            campus.getMetaTitle().equals("Test Campus - Best Education") &&
                            campus.getMetaDescription().equals("Test campus meta description") &&
                            campus.getCreatedBy().equals(1L) &&
                            campus.getBrand().getId().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should handle minimal campus data")
        void shouldHandleMinimalCampusData() {
            // Given
            CampusCreateDto minimalDto = CampusCreateDto.builder()
                    .name("Minimal Campus")
                    .build(); // Only name provided

            Campus minimalCampus = new Campus();
            minimalCampus.setId(1L);
            minimalCampus.setName("Minimal Campus");
            minimalCampus.setSlug("minimal-campus");

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.existsBySlug("minimal-campus")).thenReturn(false);
            when(campusRepository.save(any(Campus.class))).thenReturn(minimalCampus);
            when(converterService.mapToDto(any(Campus.class))).thenReturn(expectedCampusDto);

            // When
            CampusDto result = institutionService.createCampus(minimalDto, request);

            // Then
            assertNotNull(result);
            verify(campusRepository).save(argThat(campus ->
                    campus.getName().equals("Minimal Campus") &&
                            campus.getDescription() == null &&
                            campus.getEmail() == null &&
                            campus.getBrand() == null
            ));
        }
    }

    @Nested
    @DisplayName("getCampusById() Tests")
    class GetCampusByIdTests {

        @Test
        @DisplayName("Should return campus successfully when user has access")
        void shouldReturnCampusSuccessfullyWhenUserHasAccess() {
            // Given
            Long campusId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(campusId)).thenReturn(java.util.Optional.of(savedCampus));
            when(converterService.mapToDto(savedCampus)).thenReturn(expectedCampusDto);

            // When
            CampusDto result = institutionService.getCampusById(campusId, request);

            // Then
            assertNotNull(result);
            assertEquals(expectedCampusDto.getName(), result.getName());
            assertEquals(expectedCampusDto.getSlug(), result.getSlug());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(campusId);
            verify(converterService).mapToDto(savedCampus);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when campus not found")
        void shouldThrowResourceNotFoundExceptionWhenCampusNotFound() {
            // Given
            Long nonExistentCampusId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(nonExistentCampusId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.getCampusById(nonExistentCampusId, request));

            assertEquals("Campus not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(nonExistentCampusId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to campus")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToCampus() {
            // Given
            Long campusId = 1L;
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(campusRepository.findByIdAndIsActiveTrue(campusId)).thenReturn(java.util.Optional.of(savedCampus));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.getCampusById(campusId, request));

            assertEquals("User does not have access to this campus", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(campusId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should allow access for system user regardless of campus")
        void shouldAllowAccessForSystemUserRegardlessOfCampus() {
            // Given
            Long campusId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByIdAndIsActiveTrue(campusId)).thenReturn(java.util.Optional.of(savedCampus));
            when(converterService.mapToDto(savedCampus)).thenReturn(expectedCampusDto);

            // When
            CampusDto result = institutionService.getCampusById(campusId, request);

            // Then
            assertNotNull(result);
            verify(jwtService).getUser(request);
            verify(campusRepository).findByIdAndIsActiveTrue(campusId);
            verify(converterService).mapToDto(savedCampus);
        }
    }

    @Nested
    @DisplayName("getCampusesByBrand() Tests")
    class GetCampusesByBrandTests {

        private List<Campus> brandCampuses;
        private List<CampusSummaryDto> expectedCampusSummaries;

        @BeforeEach
        void setUp() {
            Campus campus1 = new Campus();
            campus1.setId(1L);
            campus1.setName("Campus 1");
            campus1.setSlug("campus-1");
            campus1.setBrand(validBrand);

            Campus campus2 = new Campus();
            campus2.setId(2L);
            campus2.setName("Campus 2");
            campus2.setSlug("campus-2");
            campus2.setBrand(validBrand);

            brandCampuses = List.of(campus1, campus2);

            CampusSummaryDto summary1 = CampusSummaryDto.builder()
                    .id(1L)
                    .name("Campus 1")
                    .slug("campus-1")
                    .build();

            CampusSummaryDto summary2 = CampusSummaryDto.builder()
                    .id(2L)
                    .name("Campus 2")
                    .slug("campus-2")
                    .build();

            expectedCampusSummaries = List.of(summary1, summary2);
        }

        @Test
        @DisplayName("Should return campus summaries when user has brand access")
        void shouldReturnCampusSummariesWhenUserHasBrandAccess() {
            // Given
            Long brandId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByBrandIdAndIsActiveTrueOrderByName(brandId)).thenReturn(brandCampuses);
            when(converterService.mapCampusToSummaryDto(brandCampuses.get(0))).thenReturn(expectedCampusSummaries.get(0));
            when(converterService.mapCampusToSummaryDto(brandCampuses.get(1))).thenReturn(expectedCampusSummaries.get(1));

            // When
            List<CampusSummaryDto> result = institutionService.getCampusesByBrand(brandId, request);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Campus 1", result.get(0).getName());
            assertEquals("Campus 2", result.get(1).getName());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByBrandIdAndIsActiveTrueOrderByName(brandId);
            verify(converterService, times(2)).mapCampusToSummaryDto(any(Campus.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to brand")
        void shouldThrowBusinessExceptionWhenUserHasNoAccessToBrand() {
            // Given
            Long brandId = 1L;
            when(jwtService.getUser(request)).thenReturn(regularUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.getCampusesByBrand(brandId, request));

            assertEquals("User does not have access to this brand", exception.getMessage());

            verify(jwtService).getUser(request);
            verifyNoInteractions(campusRepository, converterService);
        }

        @Test
        @DisplayName("Should return empty list when brand has no campuses")
        void shouldReturnEmptyListWhenBrandHasNoCampuses() {
            // Given
            Long brandId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByBrandIdAndIsActiveTrueOrderByName(brandId)).thenReturn(Collections.emptyList());

            // When
            List<CampusSummaryDto> result = institutionService.getCampusesByBrand(brandId, request);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(jwtService).getUser(request);
            verify(campusRepository).findByBrandIdAndIsActiveTrueOrderByName(brandId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should return campuses ordered by name")
        void shouldReturnCampusesOrderedByName() {
            // Given
            Long brandId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(campusRepository.findByBrandIdAndIsActiveTrueOrderByName(brandId)).thenReturn(brandCampuses);
            when(converterService.mapCampusToSummaryDto(any(Campus.class))).thenReturn(
                    expectedCampusSummaries.get(0), expectedCampusSummaries.get(1)
            );

            // When
            List<CampusSummaryDto> result = institutionService.getCampusesByBrand(brandId, request);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());

            // Verify repository was called with the correct ordering method
            verify(campusRepository).findByBrandIdAndIsActiveTrueOrderByName(brandId);
        }
    }




    // Helper methods
    private User createUser(Long id, RoleLevel roleLevel) {
        User user = new User();
        user.setId(id);

        // Mock user roles - basit bir yapı kuralım
        if (roleLevel == RoleLevel.SYSTEM) {
            // System user can create brands
            //user.setUserRoles(createMockUserRoles(roleLevel));
            user.setUserRoles(Set.of(createUserRole(roleLevel)));
        } else {
            // Regular user cannot create brands
            user.setUserRoles(Set.of(createUserRole(roleLevel)));
        }

        user.setInstitutionAccess(Collections.emptySet());
        return user;
    }

    private MockUserRole createMockUserRole(RoleLevel roleLevel) {
        MockUserRole mockRole = new MockUserRole();
        mockRole.setRoleLevel(roleLevel);
        return mockRole;
    }

    private UserRole createUserRole(RoleLevel roleLevel) {
        UserRole mockRole = new UserRole();
        mockRole.setRoleLevel(roleLevel);
        return mockRole;
    }

    @Nested
    @DisplayName("getBrandById() Tests")
    class GetBrandByIdTests {

        @Test
        @DisplayName("Should return brand successfully when user has access")
        void shouldReturnBrandSuccessfullyWhenUserHasAccess() {
            // Given
            Long brandId = 1L;
            Brand foundBrand = new Brand();
            foundBrand.setId(brandId);
            foundBrand.setName("Test Brand");
            foundBrand.setSlug("test-brand");

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(foundBrand));
            when(converterService.mapToDto(foundBrand)).thenReturn(expectedBrandDto);

            // When
            BrandDto result = institutionService.getBrandById(brandId, request);

            // Then
            assertNotNull(result);
            assertEquals(expectedBrandDto.getName(), result.getName());
            assertEquals(expectedBrandDto.getSlug(), result.getSlug());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(brandId);
            verify(converterService).mapToDto(foundBrand);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when brand not found")
        void shouldThrowResourceNotFoundExceptionWhenBrandNotFound() {
            // Given
            Long nonExistentBrandId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(nonExistentBrandId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.getBrandById(nonExistentBrandId, request));

            assertEquals("Brand not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(nonExistentBrandId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should throw BusinessException when user has no access to brand")
        void shouldThrowBusinessExceptionWhenUserHasNoAccess() {
            // Given
            Long brandId = 1L;
            Brand foundBrand = new Brand();
            foundBrand.setId(brandId);

            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(foundBrand));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.getBrandById(brandId, request));

            assertEquals("User does not have access to this brand", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(brandId);
            verifyNoInteractions(converterService);
        }

        @Test
        @DisplayName("Should allow access for system user regardless of brand")
        void shouldAllowAccessForSystemUserRegardlessOfBrand() {
            // Given
            Long brandId = 1L;
            Brand foundBrand = new Brand();
            foundBrand.setId(brandId);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(foundBrand));
            when(converterService.mapToDto(foundBrand)).thenReturn(expectedBrandDto);

            // When
            BrandDto result = institutionService.getBrandById(brandId, request);

            // Then
            assertNotNull(result);
            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(brandId);
            verify(converterService).mapToDto(foundBrand);
        }
    }

    @Nested
    @DisplayName("updateBrand() Tests")
    class UpdateBrandTests {

        private BrandCreateDto updateDto;
        private Brand existingBrand;

        @BeforeEach
        void setUp() {
            updateDto = BrandCreateDto.builder()
                    .name("Updated Brand Name")
                    .description("Updated description")
                    .logoUrl("https://example.com/new-logo.png")
                    .email("updated@testbrand.com")
                    .phone("+90 555 999 8888")
                    .build();

            existingBrand = new Brand();
            existingBrand.setId(1L);
            existingBrand.setName("Original Brand Name");
            existingBrand.setDescription("Original description");
            existingBrand.setCreatedBy(1L);
        }

        @Test
        @DisplayName("Should update brand successfully with valid data")
        void shouldUpdateBrandSuccessfullyWithValidData() {
            // Given
            Long brandId = 1L;
            Brand updatedBrand = new Brand();
            updatedBrand.setId(brandId);
            updatedBrand.setName("Updated Brand Name");
            updatedBrand.setUpdatedBy(1L);

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(existingBrand));
            when(brandRepository.existsByNameIgnoreCaseAndIdNot("Updated Brand Name", brandId)).thenReturn(false);
            when(brandRepository.save(any(Brand.class))).thenReturn(updatedBrand);
            when(converterService.mapToDto(updatedBrand)).thenReturn(expectedBrandDto);

            // When
            BrandDto result = institutionService.updateBrand(brandId, updateDto, request);

            // Then
            assertNotNull(result);
            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(brandId);
            verify(brandRepository).existsByNameIgnoreCaseAndIdNot("Updated Brand Name", brandId);
            verify(brandRepository).save(argThat(brand ->
                    brand.getName().equals("Updated Brand Name") &&
                            brand.getDescription().equals("Updated description") &&
                            brand.getUpdatedBy().equals(1L)
            ));
            verify(converterService).mapToDto(updatedBrand);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when brand not found")
        void shouldThrowResourceNotFoundExceptionWhenBrandNotFound() {
            // Given
            Long nonExistentBrandId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(nonExistentBrandId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.updateBrand(nonExistentBrandId, updateDto, request));

            assertEquals("Brand not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(nonExistentBrandId);
            verify(brandRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when user cannot manage brand")
        void shouldThrowBusinessExceptionWhenUserCannotManageBrand() {
            // Given
            Long brandId = 1L;
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(existingBrand));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.updateBrand(brandId, updateDto, request));

            assertEquals("User does not have manage permission for this brand", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(brandId);
            verify(brandRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when updated name already exists")
        void shouldThrowBusinessExceptionWhenUpdatedNameExists() {
            // Given
            Long brandId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(existingBrand));
            when(brandRepository.existsByNameIgnoreCaseAndIdNot("Updated Brand Name", brandId)).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.updateBrand(brandId, updateDto, request));

            assertEquals("Brand name already exists: Updated Brand Name", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(brandId);
            verify(brandRepository).existsByNameIgnoreCaseAndIdNot("Updated Brand Name", brandId);
            verify(brandRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow updating with same name")
        void shouldAllowUpdatingWithSameName() {
            // Given
            Long brandId = 1L;
            BrandCreateDto sameNameUpdate = BrandCreateDto.builder()
                    .name("Original Brand Name") // Same as existing
                    .description("Updated description")
                    .build();

            Brand updatedBrand = new Brand();
            updatedBrand.setId(brandId);
            updatedBrand.setName("Original Brand Name");

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(existingBrand));
            when(brandRepository.save(any(Brand.class))).thenReturn(updatedBrand);
            when(converterService.mapToDto(updatedBrand)).thenReturn(expectedBrandDto);

            // When
            BrandDto result = institutionService.updateBrand(brandId, sameNameUpdate, request);

            // Then
            assertNotNull(result);
            // Should NOT check name uniqueness when name hasn't changed
            verify(brandRepository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong());
            verify(brandRepository).save(any(Brand.class));
        }

        @Test
        @DisplayName("Should update all fields correctly")
        void shouldUpdateAllFieldsCorrectly() {
            // Given
            Long brandId = 1L;
            BrandCreateDto completeUpdate = BrandCreateDto.builder()
                    .name("Complete Updated Name")
                    .description("Complete updated description")
                    .logoUrl("https://example.com/complete-logo.png")
                    .coverImageUrl("https://example.com/complete-cover.jpg")
                    .websiteUrl("https://completeupdated.com")
                    .email("complete@updated.com")
                    .phone("+90 555 000 1111")
                    .foundedYear(2023)
                    .facebookUrl("https://facebook.com/completeupdated")
                    .metaTitle("Complete Updated Meta Title")
                    .build();

            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(existingBrand));
            when(brandRepository.existsByNameIgnoreCaseAndIdNot(anyString(), eq(brandId))).thenReturn(false);
            when(brandRepository.save(any(Brand.class))).thenReturn(existingBrand);
            when(converterService.mapToDto(any(Brand.class))).thenReturn(expectedBrandDto);

            // When
            institutionService.updateBrand(brandId, completeUpdate, request);

            // Then
            verify(brandRepository).save(argThat(brand ->
                    brand.getName().equals("Complete Updated Name") &&
                            brand.getDescription().equals("Complete updated description") &&
                            brand.getLogoUrl().equals("https://example.com/complete-logo.png") &&
                            brand.getCoverImageUrl().equals("https://example.com/complete-cover.jpg") &&
                            brand.getWebsiteUrl().equals("https://completeupdated.com") &&
                            brand.getEmail().equals("complete@updated.com") &&
                            brand.getPhone().equals("+90 555 000 1111") &&
                            brand.getFoundedYear().equals(2023) &&
                            brand.getFacebookUrl().equals("https://facebook.com/completeupdated") &&
                            brand.getMetaTitle().equals("Complete Updated Meta Title") &&
                            brand.getUpdatedBy().equals(1L)
            ));
        }
    }

    @Nested
    @DisplayName("deleteBrand() Tests")
    class DeleteBrandTests {

        private Brand existingBrand;

        @BeforeEach
        void setUp() {
            existingBrand = new Brand();
            existingBrand.setId(1L);
            existingBrand.setName("Brand to Delete");
            existingBrand.setIsActive(true);
        }

        @Test
        @DisplayName("Should delete brand successfully when no active campuses")
        void shouldDeleteBrandSuccessfullyWhenNoActiveCampuses() {
            // Given
            Long brandId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(existingBrand));
            when(campusRepository.existsByBrandIdAndIsActiveTrue(brandId)).thenReturn(false);
            when(brandRepository.save(any(Brand.class))).thenReturn(existingBrand);

            // When
            assertDoesNotThrow(() -> institutionService.deleteBrand(brandId, request));

            // Then
            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(brandId);
            verify(campusRepository).existsByBrandIdAndIsActiveTrue(brandId);
            verify(brandRepository).save(argThat(brand ->
                    !brand.getIsActive() &&
                            brand.getUpdatedBy().equals(1L)
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when brand not found")
        void shouldThrowResourceNotFoundExceptionWhenBrandNotFound() {
            // Given
            Long nonExistentBrandId = 999L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(nonExistentBrandId)).thenReturn(java.util.Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> institutionService.deleteBrand(nonExistentBrandId, request));

            assertEquals("Brand not found with ID: 999", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(nonExistentBrandId);
            verifyNoInteractions(campusRepository);
            verify(brandRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when user cannot manage brand")
        void shouldThrowBusinessExceptionWhenUserCannotManageBrand() {
            // Given
            Long brandId = 1L;
            when(jwtService.getUser(request)).thenReturn(regularUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(existingBrand));

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.deleteBrand(brandId, request));

            assertEquals("User does not have manage permission for this brand", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(brandId);
            verifyNoInteractions(campusRepository);
            verify(brandRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BusinessException when brand has active campuses")
        void shouldThrowBusinessExceptionWhenBrandHasActiveCampuses() {
            // Given
            Long brandId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(existingBrand));
            when(campusRepository.existsByBrandIdAndIsActiveTrue(brandId)).thenReturn(true);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> institutionService.deleteBrand(brandId, request));

            assertEquals("Cannot delete brand with active campuses", exception.getMessage());

            verify(jwtService).getUser(request);
            verify(brandRepository).findByIdAndIsActiveTrue(brandId);
            verify(campusRepository).existsByBrandIdAndIsActiveTrue(brandId);
            verify(brandRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should perform soft delete (set isActive to false)")
        void shouldPerformSoftDelete() {
            // Given
            Long brandId = 1L;
            when(jwtService.getUser(request)).thenReturn(systemUser);
            when(brandRepository.findByIdAndIsActiveTrue(brandId)).thenReturn(java.util.Optional.of(existingBrand));
            when(campusRepository.existsByBrandIdAndIsActiveTrue(brandId)).thenReturn(false);

            // When
            institutionService.deleteBrand(brandId, request);

            // Then
            verify(brandRepository).save(argThat(brand ->
                    !brand.getIsActive() && // Soft delete check
                            brand.getUpdatedBy().equals(1L) &&
                            brand.getId().equals(brandId)
            ));

            // Verify it's not a hard delete
            verify(brandRepository, never()).delete(any());
            verify(brandRepository, never()).deleteById(any());
        }
    }

    // Mock inner class for UserRole
    private static class MockUserRole {
        private RoleLevel roleLevel;

        public RoleLevel getRoleLevel() { return roleLevel; }
        public void setRoleLevel(RoleLevel roleLevel) { this.roleLevel = roleLevel; }
    }
}
