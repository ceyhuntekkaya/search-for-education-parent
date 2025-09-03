package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.institution.*;
import com.genixo.education.search.entity.institution.*;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserInstitutionAccess;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.repository.insitution.*;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.InstitutionConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InstitutionService {

    private final BrandRepository brandRepository;
    private final CampusRepository campusRepository;
    private final SchoolRepository schoolRepository;
    private final InstitutionTypeRepository institutionTypeRepository;
    private final InstitutionPropertyRepository institutionPropertyRepository;
    private final InstitutionPropertyValueRepository institutionPropertyValueRepository;
    private final InstitutionConverterService converterService;
    private final JwtService jwtService;


    @Transactional
    @CacheEvict(value = {"brands", "brand_summaries"}, allEntries = true)
    public BrandDto createBrand(BrandCreateDto createDto, HttpServletRequest request) {
        log.info("Creating new brand: {}", createDto.getName());

        User user = jwtService.getUser(request);
        validateUserCanCreateBrand(user);

        // Check if brand name already exists
        if (brandRepository.existsByNameIgnoreCase(createDto.getName())) {
            throw new BusinessException("Brand name already exists: " + createDto.getName());
        }

        // Check slug uniqueness
        String slug = generateUniqueSlug(createDto.getName(), "brand");
        if (brandRepository.existsBySlug(slug)) {
            slug = generateUniqueSlug(createDto.getName() + "-" + System.currentTimeMillis(), "brand");
        }

        Brand brand = new Brand();
        brand.setName(createDto.getName());
        brand.setSlug(slug);
        brand.setDescription(createDto.getDescription());
        brand.setLogoUrl(createDto.getLogoUrl());
        brand.setCoverImageUrl(createDto.getCoverImageUrl());
        brand.setWebsiteUrl(createDto.getWebsiteUrl());
        brand.setEmail(createDto.getEmail());
        brand.setPhone(createDto.getPhone());
        brand.setFoundedYear(createDto.getFoundedYear());
        brand.setFacebookUrl(createDto.getFacebookUrl());
        brand.setTwitterUrl(createDto.getTwitterUrl());
        brand.setInstagramUrl(createDto.getInstagramUrl());
        brand.setLinkedinUrl(createDto.getLinkedinUrl());
        brand.setYoutubeUrl(createDto.getYoutubeUrl());
        brand.setMetaTitle(createDto.getMetaTitle());
        brand.setMetaDescription(createDto.getMetaDescription());
        brand.setMetaKeywords(createDto.getMetaKeywords());
        brand.setCreatedBy(user.getId());

        brand = brandRepository.save(brand);
        log.info("Brand created successfully with ID: {}", brand.getId());

        return converterService.mapToDto(brand);
    }

    @Cacheable(value = "brands", key = "#id")
    public BrandDto getBrandById(Long id, HttpServletRequest request) {
        log.info("Fetching brand with ID: {}", id);

        User user = jwtService.getUser(request);
        Brand brand = brandRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + id));
        if (!Hibernate.isInitialized(brand.getCampuses())) {

            Hibernate.initialize(brand.getCampuses());
        }
        validateUserCanAccessBrand(user, brand.getId());
        BrandDto reult = converterService.mapToDto(brand);

        return reult;
    }

    @Cacheable(value = "brands", key = "#slug")
    public BrandDto getBrandBySlug(String slug) {
        log.info("Fetching brand with slug: {}", slug);

        Brand brand = brandRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with slug: " + slug));

        return converterService.mapToDto(brand);
    }

    @Transactional
    @CacheEvict(value = {"brands", "brand_summaries"}, allEntries = true)
    public BrandDto updateBrand(Long id, BrandCreateDto updateDto, HttpServletRequest request) {
        log.info("Updating brand with ID: {}", id);

        User user = jwtService.getUser(request);
        Brand brand = brandRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + id));

        validateUserCanManageBrand(user, brand.getId());

        // Check name uniqueness if changed
        if (!brand.getName().equalsIgnoreCase(updateDto.getName()) &&
                brandRepository.existsByNameIgnoreCaseAndIdNot(updateDto.getName(), id)) {
            throw new BusinessException("Brand name already exists: " + updateDto.getName());
        }

        brand.setName(updateDto.getName());
        brand.setDescription(updateDto.getDescription());
        brand.setLogoUrl(updateDto.getLogoUrl());
        brand.setCoverImageUrl(updateDto.getCoverImageUrl());
        brand.setWebsiteUrl(updateDto.getWebsiteUrl());
        brand.setEmail(updateDto.getEmail());
        brand.setPhone(updateDto.getPhone());
        brand.setFoundedYear(updateDto.getFoundedYear());
        brand.setFacebookUrl(updateDto.getFacebookUrl());
        brand.setTwitterUrl(updateDto.getTwitterUrl());
        brand.setInstagramUrl(updateDto.getInstagramUrl());
        brand.setLinkedinUrl(updateDto.getLinkedinUrl());
        brand.setYoutubeUrl(updateDto.getYoutubeUrl());
        brand.setMetaTitle(updateDto.getMetaTitle());
        brand.setMetaDescription(updateDto.getMetaDescription());
        brand.setMetaKeywords(updateDto.getMetaKeywords());
        brand.setUpdatedBy(user.getId());

        brand = brandRepository.save(brand);
        log.info("Brand updated successfully with ID: {}", brand.getId());

        return converterService.mapToDto(brand);
    }

    @Transactional
    @CacheEvict(value = {"brands", "brand_summaries", "campuses", "schools"}, allEntries = true)
    public void deleteBrand(Long id, HttpServletRequest request) {
        log.info("Deleting brand with ID: {}", id);

        User user = jwtService.getUser(request);
        Brand brand = brandRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + id));

        validateUserCanManageBrand(user, brand.getId());

        // Check if brand has active campuses
        if (campusRepository.existsByBrandIdAndIsActiveTrue(id)) {
            throw new BusinessException("Cannot delete brand with active campuses");
        }

        brand.setIsActive(false);
        brand.setUpdatedBy(user.getId());
        brandRepository.save(brand);

        log.info("Brand deleted successfully with ID: {}", id);
    }

    @Cacheable(value = "brand_summaries")
    public List<BrandSummaryDto> getAllBrandSummaries(HttpServletRequest request) {
        log.info("Fetching all brand summaries");

        User user = jwtService.getUser(request);
        List<Brand> brands;

        if (hasSystemRole(user)) {
            brands = brandRepository.findAllActiveOrderByName();
        } else {
            List<Long> accessibleBrandIds = getUserAccessibleBrandIds(user);
            brands = brandRepository.findByIdInAndIsActiveTrueOrderByName(accessibleBrandIds);
        }

        return brands.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    // ================================ CAMPUS OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"campuses", "campus_summaries"}, allEntries = true)
    public CampusDto createCampus(CampusCreateDto createDto, HttpServletRequest request) {
        log.info("Creating new campus: {}", createDto.getName());

        User user = jwtService.getUser(request);

        // Validate brand access if brand is specified
        if (createDto.getBrandId() != null) {
            validateUserCanAccessBrand(user, createDto.getBrandId());
        }

        // Check if campus name already exists in the same brand
        if (createDto.getBrandId() != null &&
                campusRepository.existsByNameIgnoreCaseAndBrandIdAndIsActiveTrue(createDto.getName(), createDto.getBrandId())) {
            throw new BusinessException("Campus name already exists in this brand: " + createDto.getName());
        }

        String slug = generateUniqueSlug(createDto.getName(), "campus");
        if (campusRepository.existsBySlug(slug)) {
            slug = generateUniqueSlug(createDto.getName() + "-" + System.currentTimeMillis(), "campus");
        }

        Campus campus = new Campus();
        campus.setName(createDto.getName());
        campus.setSlug(slug);
        campus.setDescription(createDto.getDescription());
        campus.setLogoUrl(createDto.getLogoUrl());
        campus.setCoverImageUrl(createDto.getCoverImageUrl());
        campus.setEmail(createDto.getEmail());
        campus.setPhone(createDto.getPhone());
        campus.setFax(createDto.getFax());
        campus.setWebsiteUrl(createDto.getWebsiteUrl());
        campus.setAddressLine1(createDto.getAddressLine1());
        campus.setAddressLine2(createDto.getAddressLine2());
        campus.setDistrict(converterService.mapToSummaryEntity(createDto.getDistrict()));
        campus.setProvince(converterService.mapToSummaryEntity(createDto.getProvince()));
        campus.setPostalCode(createDto.getPostalCode());
        campus.setCountry(converterService.mapToSummaryEntity(createDto.getCountry()));
        campus.setLatitude(createDto.getLatitude());
        campus.setLongitude(createDto.getLongitude());
        campus.setEstablishedYear(createDto.getEstablishedYear());
        campus.setFacebookUrl(createDto.getFacebookUrl());
        campus.setTwitterUrl(createDto.getTwitterUrl());
        campus.setInstagramUrl(createDto.getInstagramUrl());
        campus.setLinkedinUrl(createDto.getLinkedinUrl());
        campus.setYoutubeUrl(createDto.getYoutubeUrl());
        campus.setMetaTitle(createDto.getMetaTitle());
        campus.setMetaDescription(createDto.getMetaDescription());
        campus.setMetaKeywords(createDto.getMetaKeywords());
        campus.setCreatedBy(user.getId());

        // Set brand if provided
        if (createDto.getBrandId() != null) {
            Brand brand = brandRepository.findByIdAndIsActiveTrue(createDto.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + createDto.getBrandId()));
            campus.setBrand(brand);
        }

        campus = campusRepository.save(campus);
        log.info("Campus created successfully with ID: {}", campus.getId());

        return converterService.mapToDto(campus);
    }

    @Cacheable(value = "campuses", key = "#id")
    public CampusDto getCampusById(Long id, HttpServletRequest request) {
        log.info("Fetching campus with ID: {}", id);

        User user = jwtService.getUser(request);
        Campus campus = campusRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with ID: " + id));

        validateUserCanAccessCampus(user, campus.getId());

        return converterService.mapToDto(campus);
    }

    @Cacheable(value = "campuses", key = "#slug")
    public CampusDto getCampusBySlug(String slug) {
        log.info("Fetching campus with slug: {}", slug);

        Campus campus = campusRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with slug: " + slug));

        return converterService.mapToDto(campus);
    }

    @Cacheable(value = "campus_summaries", key = "#brandId")
    public List<CampusSummaryDto> getCampusesByBrand(Long brandId, HttpServletRequest request) {
        log.info("Fetching campuses for brand ID: {}", brandId);

        User user = jwtService.getUser(request);
        validateUserCanAccessBrand(user, brandId);

        List<Campus> campuses = campusRepository.findByBrandIdAndIsActiveTrueOrderByName(brandId);
        return campuses.stream()
                .map(converterService::mapCampusToSummaryDto)
                .collect(Collectors.toList());
    }

    // ================================ SCHOOL OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"schools", "school_summaries"}, allEntries = true)
    public SchoolDto createSchool(SchoolCreateDto createDto, HttpServletRequest request) {
        log.info("Creating new school: {}", createDto.getName());

        User user = jwtService.getUser(request);

        // Validate campus and institution type
        Campus campus = campusRepository.findByIdAndIsActiveTrue(createDto.getCampusId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with ID: " + createDto.getCampusId()));

        InstitutionType institutionType = institutionTypeRepository.findByIdAndIsActiveTrue(createDto.getInstitutionTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Institution type not found with ID: " + createDto.getInstitutionTypeId()));

        validateUserCanAccessCampus(user, campus.getId());

        // Check if school name already exists in the same campus
        if (schoolRepository.existsByNameIgnoreCaseAndCampusIdAndIsActiveTrue(createDto.getName(), createDto.getCampusId())) {
            throw new BusinessException("School name already exists in this campus: " + createDto.getName());
        }

        String slug = generateUniqueSlug(createDto.getName(), "school");
        if (schoolRepository.existsBySlug(slug)) {
            slug = generateUniqueSlug(createDto.getName() + "-" + System.currentTimeMillis(), "school");
        }

        School school = new School();
        school.setName(createDto.getName());
        school.setSlug(slug);
        school.setDescription(createDto.getDescription());
        school.setLogoUrl(createDto.getLogoUrl());
        school.setCoverImageUrl(createDto.getCoverImageUrl());
        school.setEmail(createDto.getEmail());
        school.setPhone(createDto.getPhone());
        school.setExtension(createDto.getExtension());
        school.setMinAge(createDto.getMinAge());
        school.setMaxAge(createDto.getMaxAge());
        school.setCapacity(createDto.getCapacity());
        school.setCurrentStudentCount(createDto.getCurrentStudentCount());
        school.setClassSizeAverage(createDto.getClassSizeAverage());
        school.setCurriculumType(createDto.getCurriculumType());
        school.setLanguageOfInstruction(createDto.getLanguageOfInstruction());
        school.setForeignLanguages(createDto.getForeignLanguages());
        school.setRegistrationFee(createDto.getRegistrationFee());
        school.setMonthlyFee(createDto.getMonthlyFee());
        school.setAnnualFee(createDto.getAnnualFee());
        school.setMetaTitle(createDto.getMetaTitle());
        school.setMetaDescription(createDto.getMetaDescription());
        school.setMetaKeywords(createDto.getMetaKeywords());
        school.setCampus(campus);
        school.setInstitutionType(institutionType);
        school.setCreatedBy(user.getId());

        school = schoolRepository.save(school);
        log.info("School created successfully with ID: {}", school.getId());

        return converterService.mapToDto(school);
    }

    @Cacheable(value = "schools", key = "#id")
    public SchoolDto getSchoolById(Long id, HttpServletRequest request) {
        log.info("Fetching school with ID: {}", id);

        User user = jwtService.getUser(request);
        School school = schoolRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + id));

        validateUserCanAccessSchool(user, school.getId());

        return converterService.mapToDto(school);
    }

    @Cacheable(value = "schools", key = "#slug")
    public SchoolDto getSchoolBySlug(String slug) {
        log.info("Fetching school with slug: {}", slug);

        School school = schoolRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with slug: " + slug));

        return converterService.mapToDto(school);
    }

    public SchoolDetailDto getSchoolDetailById(Long id, HttpServletRequest request) {
        log.info("Fetching school detail with ID: {}", id);

        User user = jwtService.getUser(request);
        School school = schoolRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + id));

        validateUserCanAccessSchool(user, school.getId());

        return converterService.mapToDetailDto(school);
    }

    // ================================ SEARCH OPERATIONS ================================

    public Page<SchoolSearchResultDto> searchSchools(SchoolSearchDto searchDto) {
        log.info("Searching schools with criteria: {}", searchDto.getSearchTerm());

        Pageable pageable = PageRequest.of(
                searchDto.getPage() != null ? searchDto.getPage() : 0,
                searchDto.getSize() != null ? searchDto.getSize() : 20,
                createSort(searchDto.getSortBy(), searchDto.getSortDirection())
        );

        Page<School> schools = schoolRepository.searchSchools(
                searchDto.getSearchTerm(),
                searchDto.getInstitutionTypeIds(),
                searchDto.getMinAge(),
                searchDto.getMaxAge(),
                searchDto.getMinFee(),
                searchDto.getMaxFee(),
                searchDto.getCurriculumType(),
                searchDto.getLanguageOfInstruction(),
                searchDto.getCountryId(),
                searchDto.getProvinceId(),
                searchDto.getDistrictId(),
                searchDto.getNeighborhoodId(),
                searchDto.getLatitude(),
                searchDto.getLongitude(),
                searchDto.getRadiusKm(),
                searchDto.getMinRating(),
                searchDto.getHasActiveCampaigns(),
                searchDto.getIsSubscribed(),
                pageable
        );

        return schools.map(converterService::mapToSearchResultDto);
    }

    // ================================ INSTITUTION TYPE OPERATIONS ================================

    @Cacheable(value = "institution_types")
    public List<InstitutionTypeDto> getAllInstitutionTypes() {
        log.info("Fetching all institution types");

        List<InstitutionType> types = institutionTypeRepository.findAllByIsActiveTrueOrderBySortOrderAscNameAsc();
        return types.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "institution_type_summaries")
    public List<InstitutionTypeSummaryDto> getInstitutionTypeSummaries() {
        log.info("Fetching institution type summaries");

        return institutionTypeRepository.findInstitutionTypeSummaries();
    }

    // ================================ INSTITUTION PROPERTY OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"institution_properties"}, allEntries = true)
    public InstitutionPropertyDto createInstitutionProperty(InstitutionPropertyCreateDto createDto, HttpServletRequest request) {
        log.info("Creating institution property: {}", createDto.getName());

        User user = jwtService.getUser(request);
        validateUserCanManageInstitutionTypes(user);

        InstitutionType institutionType = institutionTypeRepository.findByIdAndIsActiveTrue(createDto.getInstitutionTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Institution type not found"));

        // Check if property name already exists for this institution type
        if (institutionPropertyRepository.existsByNameIgnoreCaseAndInstitutionTypeIdAndIsActiveTrue(
                createDto.getName(), createDto.getInstitutionTypeId())) {
            throw new BusinessException("Property name already exists for this institution type");
        }

        InstitutionProperty property = new InstitutionProperty();
        property.setName(createDto.getName());
        property.setDisplayName(createDto.getDisplayName());
        property.setDescription(createDto.getDescription());
        property.setDataType(createDto.getDataType());
        property.setIsRequired(createDto.getIsRequired());
        property.setIsSearchable(createDto.getIsSearchable());
        property.setIsFilterable(createDto.getIsFilterable());
        property.setShowInCard(createDto.getShowInCard());
        property.setShowInProfile(createDto.getShowInProfile());
        property.setSortOrder(createDto.getSortOrder());
        property.setOptions(createDto.getOptions());
        property.setDefaultValue(createDto.getDefaultValue());
        property.setMinValue(createDto.getMinValue());
        property.setMaxValue(createDto.getMaxValue());
        property.setMinLength(createDto.getMinLength());
        property.setMaxLength(createDto.getMaxLength());
        property.setRegexPattern(createDto.getRegexPattern());
        property.setInstitutionType(institutionType);
        property.setCreatedBy(user.getId());

        property = institutionPropertyRepository.save(property);
        log.info("Institution property created with ID: {}", property.getId());

        return converterService.mapToDto(property);
    }

    @Cacheable(value = "institution_properties", key = "#institutionTypeId")
    public List<InstitutionPropertyDto> getPropertiesByInstitutionType(Long institutionTypeId) {
        log.info("Fetching properties for institution type: {}", institutionTypeId);

        List<InstitutionProperty> properties = institutionPropertyRepository
                .findByInstitutionTypeIdAndIsActiveTrueOrderBySortOrderAscDisplayNameAsc(institutionTypeId);

        return properties.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }

    // ================================ PROPERTY VALUE OPERATIONS ================================

    @Transactional
    public void setSchoolPropertyValues(Long schoolId, List<InstitutionPropertyValueSetDto> propertyValues, HttpServletRequest request) {
        log.info("Setting property values for school: {}", schoolId);

        User user = jwtService.getUser(request);
        School school = schoolRepository.findByIdAndIsActiveTrue(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));

        validateUserCanAccessSchool(user, schoolId);

        for (InstitutionPropertyValueSetDto valueDto : propertyValues) {
            InstitutionProperty property = institutionPropertyRepository.findByIdAndIsActiveTrue(valueDto.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

            // Find existing value or create new one
            InstitutionPropertyValue existingValue = institutionPropertyValueRepository
                    .findByPropertyIdAndSchoolIdAndIsActiveTrue(valueDto.getPropertyId(), schoolId)
                    .orElse(null);

            if (existingValue != null) {
                updatePropertyValue(existingValue, valueDto, user.getId());
            } else {
                createPropertyValue(property, school, null, valueDto, user.getId());
            }
        }
    }

    @Transactional
    public void setCampusPropertyValues(Long campusId, List<InstitutionPropertyValueSetDto> propertyValues, HttpServletRequest request) {
        log.info("Setting property values for campus: {}", campusId);

        User user = jwtService.getUser(request);
        Campus campus = campusRepository.findByIdAndIsActiveTrue(campusId)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found"));

        validateUserCanAccessCampus(user, campusId);

        for (InstitutionPropertyValueSetDto valueDto : propertyValues) {
            InstitutionProperty property = institutionPropertyRepository.findByIdAndIsActiveTrue(valueDto.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

            InstitutionPropertyValue existingValue = institutionPropertyValueRepository
                    .findByPropertyIdAndCampusIdAndIsActiveTrue(valueDto.getPropertyId(), campusId)
                    .orElse(null);

            if (existingValue != null) {
                updatePropertyValue(existingValue, valueDto, user.getId());
            } else {
                createPropertyValue(property, null, campus, valueDto, user.getId());
            }
        }
    }

    // ================================ BULK OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"schools", "school_summaries"}, allEntries = true)
    public BulkOperationResultDto bulkUpdateSchools(BulkOperationDto bulkDto, HttpServletRequest request) {
        log.info("Performing bulk operation: {} on {} schools", bulkDto.getOperation(), bulkDto.getEntityIds().size());

        User user = jwtService.getUser(request);
        BulkOperationResultDto result = BulkOperationResultDto.builder()
                .operationDate(LocalDateTime.now())
                .totalRecords(bulkDto.getEntityIds().size())
                .successfulOperations(0)
                .failedOperations(0)
                .errors(new java.util.ArrayList<>())
                .warnings(new java.util.ArrayList<>())
                .build();

        for (Long schoolId : bulkDto.getEntityIds()) {
            try {
                validateUserCanAccessSchool(user, schoolId);

                switch (bulkDto.getOperation().toUpperCase()) {
                    case "ACTIVATE":
                        schoolRepository.updateIsActiveByIdAndUserId(schoolId, true, user.getId());
                        break;
                    case "DEACTIVATE":
                        schoolRepository.updateIsActiveByIdAndUserId(schoolId, false, user.getId());
                        break;
                    case "DELETE":
                        schoolRepository.updateIsActiveByIdAndUserId(schoolId, false, user.getId());
                        break;
                    default:
                        throw new BusinessException("Unsupported bulk operation: " + bulkDto.getOperation());
                }

                result.setSuccessfulOperations(result.getSuccessfulOperations() + 1);
            } catch (Exception e) {
                result.setFailedOperations(result.getFailedOperations() + 1);
                result.getErrors().add("School ID " + schoolId + ": " + e.getMessage());
            }
        }

        result.setSuccess(result.getFailedOperations() == 0);
        return result;
    }

    // ================================ VALIDATION METHODS ================================

    private void validateUserCanCreateBrand(User user) {
        if (!hasSystemRole(user)) {
            throw new BusinessException("User does not have permission to create brands");
        }
    }

    private void validateUserCanAccessBrand(User user, Long brandId) {
        if (!hasSystemRole(user) && !hasAccessToBrand(user, brandId)) {
            throw new BusinessException("User does not have access to this brand");
        }
    }

    private void validateUserCanManageBrand(User user, Long brandId) {
        if (!hasSystemRole(user) && !hasManageAccessToBrand(user, brandId)) {
            throw new BusinessException("User does not have manage permission for this brand");
        }
    }

    private void validateUserCanAccessCampus(User user, Long campusId) {
        if (!hasSystemRole(user) && !hasAccessToCampus(user, campusId)) {
            throw new BusinessException("User does not have access to this campus");
        }
    }

    private void validateUserCanAccessSchool(User user, Long schoolId) {
        if (!hasSystemRole(user) && !hasAccessToSchool(user, schoolId)) {
            throw new BusinessException("User does not have access to this school");
        }
    }

    private void validateUserCanManageInstitutionTypes(User user) {
        if (!hasSystemRole(user)) {
            throw new BusinessException("User does not have permission to manage institution types");
        }
    }

    // ================================ HELPER METHODS ================================

    private boolean hasSystemRole(User user) {
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleLevel() == RoleLevel.SYSTEM);
    }

    private boolean hasAccessToBrand(User user, Long brandId) {
        return user.getInstitutionAccess().stream()
                .anyMatch(access -> access.getAccessType() == AccessType.BRAND &&
                        access.getEntityId().equals(brandId) &&
                        (access.getExpiresAt() == null || access.getExpiresAt().isAfter(LocalDateTime.now())));
    }

    private boolean hasManageAccessToBrand(User user, Long brandId) {
        return user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRoleLevel() == RoleLevel.BRAND) &&
                hasAccessToBrand(user, brandId);
    }

    private boolean hasAccessToCampus(User user, Long campusId) {
        return user.getInstitutionAccess().stream()
                .anyMatch(access -> (access.getAccessType() == AccessType.CAMPUS &&
                        access.getEntityId().equals(campusId)) ||
                        (access.getAccessType() == AccessType.BRAND &&
                                campusRepository.existsByIdAndBrandId(campusId, access.getEntityId())) &&
                                (access.getExpiresAt() == null || access.getExpiresAt().isAfter(LocalDateTime.now())));
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

    private List<Long> getUserAccessibleBrandIds(User user) {
        return user.getInstitutionAccess().stream()
                .filter(access -> access.getAccessType() == AccessType.BRAND)
                .filter(access -> access.getExpiresAt() == null || access.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(UserInstitutionAccess::getEntityId)
                .collect(Collectors.toList());
    }

    private String generateUniqueSlug(String input, String type) {
        if (!StringUtils.hasText(input)) {
            input = type + "-" + System.currentTimeMillis();
        }

        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        String sortField = switch (sortBy != null ? sortBy.toLowerCase() : "created_date") {
            case "rating" -> "ratingAverage";
            case "price" -> "monthlyFee";
            case "name" -> "name";
            case "distance" -> "distance"; // This would need special handling in repository
            default -> "createdAt";
        };

        return Sort.by(direction, sortField);
    }

    private void updatePropertyValue(InstitutionPropertyValue existingValue,
                                     InstitutionPropertyValueSetDto valueDto,
                                     Long userId) {
        existingValue.setTextValue(valueDto.getTextValue());
        existingValue.setNumberValue(valueDto.getNumberValue());
        existingValue.setBooleanValue(valueDto.getBooleanValue());
        existingValue.setDateValue(valueDto.getDateValue() != null ?
                java.time.LocalDate.parse(valueDto.getDateValue()) : null);
        existingValue.setDatetimeValue(valueDto.getDatetimeValue() != null ?
                LocalDateTime.parse(valueDto.getDatetimeValue()) : null);
        existingValue.setJsonValue(valueDto.getJsonValue());
        existingValue.setFileUrl(valueDto.getFileUrl());
        existingValue.setFileName(valueDto.getFileName());
        existingValue.setFileSize(valueDto.getFileSize());
        existingValue.setMimeType(valueDto.getMimeType());
        existingValue.setUpdatedBy(userId);

        institutionPropertyValueRepository.save(existingValue);
    }

    private void createPropertyValue(InstitutionProperty property,
                                     School school,
                                     Campus campus,
                                     InstitutionPropertyValueSetDto valueDto,
                                     Long userId) {
        InstitutionPropertyValue propertyValue = new InstitutionPropertyValue();
        propertyValue.setProperty(property);
        propertyValue.setSchool(school);
        propertyValue.setCampus(campus);
        propertyValue.setTextValue(valueDto.getTextValue());
        propertyValue.setNumberValue(valueDto.getNumberValue());
        propertyValue.setBooleanValue(valueDto.getBooleanValue());
        propertyValue.setDateValue(valueDto.getDateValue() != null ?
                java.time.LocalDate.parse(valueDto.getDateValue()) : null);
        propertyValue.setDatetimeValue(valueDto.getDatetimeValue() != null ?
                LocalDateTime.parse(valueDto.getDatetimeValue()) : null);
        propertyValue.setJsonValue(valueDto.getJsonValue());
        propertyValue.setFileUrl(valueDto.getFileUrl());
        propertyValue.setFileName(valueDto.getFileName());
        propertyValue.setFileSize(valueDto.getFileSize());
        propertyValue.setMimeType(valueDto.getMimeType());
        propertyValue.setCreatedBy(userId);

        institutionPropertyValueRepository.save(propertyValue);
    }

    // ================================ STATISTICS AND ANALYTICS ================================

    @Cacheable(value = "school_statistics", key = "#schoolId")
    public SchoolStatisticsDto getSchoolStatistics(Long schoolId, HttpServletRequest request) {
        log.info("Fetching statistics for school: {}", schoolId);

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);
// ceyhun return schoolRepository.getSchoolStatistics(schoolId);
        return null;
    }

    public InstitutionFavoritesDto getUserFavorites(HttpServletRequest request) {
        log.info("Fetching user favorites");

        User user = jwtService.getUser(request);

        // This would typically involve a separate favorites table
        // For now, returning empty structure
        return InstitutionFavoritesDto.builder()
                .userId(user.getId())
                .favoriteSchools(List.of())
                .favoriteCampuses(List.of())
                .favoriteBrands(List.of())
                .totalFavorites(0)
                .build();
    }

    public InstitutionComparisonDto compareSchools(List<Long> schoolIds, HttpServletRequest request) {
        log.info("Comparing schools: {}", schoolIds);

        User user = jwtService.getUser(request);

        List<School> schools = schoolRepository.findByIdInAndIsActiveTrue(schoolIds);

        // Validate user has access to all schools
        for (School school : schools) {
            validateUserCanAccessSchool(user, school.getId());
        }

        List<SchoolDto> schoolDtos = schools.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());

        // Build comparison data structure
        Map<String, Map<Long, Object>> comparisonData = buildComparisonData(schools);
        List<String> categories = List.of(
                "Basic Information", "Fees", "Facilities", "Academic", "Location"
        );

        return InstitutionComparisonDto.builder()
                .schools(schoolDtos)
                .comparisonCategories(categories)
                .comparisonData(comparisonData)
                .recommendations(generateRecommendations(schools))
                .build();
    }

    private Map<String, Map<Long, Object>> buildComparisonData(List<School> schools) {
        Map<String, Map<Long, Object>> comparisonData = new java.util.HashMap<>();

        // Basic Information
        Map<Long, Object> basicInfo = new java.util.HashMap<>();
        schools.forEach(school -> {
            basicInfo.put(school.getId(), Map.of(
                    "name", school.getName(),
                    "type", school.getInstitutionType().getDisplayName(),
                    "capacity", school.getCapacity() != null ? school.getCapacity() : 0,
                    "ageRange", (school.getMinAge() != null ? school.getMinAge() : 0) + "-" +
                            (school.getMaxAge() != null ? school.getMaxAge() : 0)
            ));
        });
        comparisonData.put("Basic Information", basicInfo);

        // Fees
        Map<Long, Object> fees = new java.util.HashMap<>();
        schools.forEach(school -> {
            fees.put(school.getId(), Map.of(
                    "monthlyFee", school.getMonthlyFee() != null ? school.getMonthlyFee() : 0.0,
                    "annualFee", school.getAnnualFee() != null ? school.getAnnualFee() : 0.0,
                    "registrationFee", school.getRegistrationFee() != null ? school.getRegistrationFee() : 0.0
            ));
        });
        comparisonData.put("Fees", fees);

        // Add more comparison categories as needed

        return comparisonData;
    }

    private List<String> generateRecommendations(List<School> schools) {
        List<String> recommendations = new java.util.ArrayList<>();

        // Find school with lowest fees
        School cheapestSchool = schools.stream()
                .min((s1, s2) -> Double.compare(
                        s1.getMonthlyFee() != null ? s1.getMonthlyFee() : Double.MAX_VALUE,
                        s2.getMonthlyFee() != null ? s2.getMonthlyFee() : Double.MAX_VALUE
                ))
                .orElse(null);

        if (cheapestSchool != null && cheapestSchool.getMonthlyFee() != null) {
            recommendations.add(cheapestSchool.getName() + " has the most affordable monthly fees");
        }

        // Find school with highest rating
        School topRatedSchool = schools.stream()
                .max((s1, s2) -> Double.compare(
                        s1.getRatingAverage() != null ? s1.getRatingAverage() : 0.0,
                        s2.getRatingAverage() != null ? s2.getRatingAverage() : 0.0
                ))
                .orElse(null);

        if (topRatedSchool != null && topRatedSchool.getRatingAverage() != null && topRatedSchool.getRatingAverage() > 0) {
            recommendations.add(topRatedSchool.getName() + " has the highest parent satisfaction rating");
        }

        return recommendations;
    }

    // ================================ PUBLIC SEARCH METHODS (NO AUTH REQUIRED) ================================

    public Page<SchoolSearchResultDto> publicSearchSchools(SchoolSearchDto searchDto) {
        log.info("Public search for schools with criteria: {}", searchDto.getSearchTerm());

        // Only show schools from subscribed campuses
        searchDto.setIsSubscribed(true);

        return searchSchools(searchDto);
    }

    public SchoolDto getPublicSchoolBySlug(String slug) {
        log.info("Public access to school with slug: {}", slug);

        School school = schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("School not found or not available"));

        // Increment view count
        schoolRepository.incrementViewCount(school.getId());

        return converterService.mapToDto(school);
    }

    public CampusDto getPublicCampusBySlug(String slug) {
        log.info("Public access to campus with slug: {}", slug);

        Campus campus = campusRepository.findBySlugAndIsActiveTrueAndIsSubscribedTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found or not available"));

        // Increment view count
        campusRepository.incrementViewCount(campus.getId());

        return converterService.mapToDto(campus);
    }

    public BrandDto getPublicBrandBySlug(String slug) {
        log.info("Public access to brand with slug: {}", slug);

        Brand brand = brandRepository.findBySlugAndIsActiveTrueWithSubscribedCampuses(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found or not available"));

        // Increment view count
        brandRepository.incrementViewCount(brand.getId());

        return converterService.mapToDto(brand);
    }

    // ================================ ADMIN METHODS ================================

    @Transactional
    @CacheEvict(value = {"institution_types"}, allEntries = true)
    public InstitutionTypeDto createInstitutionType(InstitutionTypeDto typeDto, HttpServletRequest request) {
        log.info("Creating institution type: {}", typeDto.getName());

        User user = jwtService.getUser(request);
        validateUserCanManageInstitutionTypes(user);

        if (institutionTypeRepository.existsByNameIgnoreCase(typeDto.getName())) {
            throw new BusinessException("Institution type name already exists: " + typeDto.getName());
        }

        InstitutionType institutionType = new InstitutionType();
        institutionType.setName(typeDto.getName());
        institutionType.setDisplayName(typeDto.getDisplayName());
        institutionType.setDescription(typeDto.getDescription());
        institutionType.setIconUrl(typeDto.getIconUrl());
        institutionType.setColorCode(typeDto.getColorCode());
        institutionType.setSortOrder(typeDto.getSortOrder() != null ? typeDto.getSortOrder() : 0);
        institutionType.setDefaultProperties(typeDto.getDefaultProperties());
        institutionType.setCreatedBy(user.getId());

        institutionType = institutionTypeRepository.save(institutionType);
        log.info("Institution type created with ID: {}", institutionType.getId());

        return converterService.mapToDto(institutionType);
    }
}