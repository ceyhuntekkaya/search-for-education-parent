package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.institution.*;
import com.genixo.education.search.dto.location.CountrySummaryDto;
import com.genixo.education.search.dto.location.DistrictSummaryDto;
import com.genixo.education.search.dto.location.NeighborhoodSummaryDto;
import com.genixo.education.search.dto.location.ProvinceSummaryDto;
import com.genixo.education.search.entity.institution.*;
import com.genixo.education.search.entity.location.Country;
import com.genixo.education.search.entity.location.District;
import com.genixo.education.search.entity.location.Neighborhood;
import com.genixo.education.search.entity.location.Province;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserInstitutionAccess;
import com.genixo.education.search.enumaration.*;
import com.genixo.education.search.enumaration.AccessType;
import com.genixo.education.search.repository.insitution.*;
import com.genixo.education.search.repository.location.CountryRepository;
import com.genixo.education.search.repository.location.DistrictRepository;
import com.genixo.education.search.repository.location.NeighborhoodRepository;
import com.genixo.education.search.repository.location.ProvinceRepository;
import com.genixo.education.search.service.auth.JwtService;
import com.genixo.education.search.service.converter.InstitutionConverterService;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InstitutionService {

    private final BrandRepository brandRepository;
    private final CampusRepository campusRepository;
    private final SchoolRepository schoolRepository;
    private final PropertyGroupTypeRepository propertyGroupTypeRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final InstitutionTypeGroupRepository institutionTypeGroupRepository;
    private final InstitutionTypeRepository institutionTypeRepository;
    private final InstitutionPropertyRepository institutionPropertyRepository;
    private final InstitutionPropertyValueRepository institutionPropertyValueRepository;
    private final InstitutionConverterService converterService;
    private final JwtService jwtService;
    private final NeighborhoodRepository neighborhoodRepository;
    private final CountryRepository countryRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;




    @Transactional
    @CacheEvict(value = {"brands", "brand_summaries"}, allEntries = true)
    public BrandDto createBrand(BrandCreateDto createDto, HttpServletRequest request) {

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

        return converterService.mapToDto(brand);
    }

    @Cacheable(value = "brands", key = "#id")
    public BrandDto getBrandById(Long id, HttpServletRequest request) {

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


    @Cacheable(value = "brands", key = "#id")
    public Brand getBrandClassById(Long id) {
        Brand brand = brandRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + id));
        return brand;
    }


    @Cacheable(value = "brands", key = "#slug")
    public BrandDto getBrandBySlug(String slug) {

        Brand brand = brandRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with slug: " + slug));

        return converterService.mapToDto(brand);
    }

    @Transactional
    @CacheEvict(value = {"brands", "brand_summaries"}, allEntries = true)
    public BrandDto updateBrand(Long id, BrandCreateDto updateDto, HttpServletRequest request) {

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

        return converterService.mapToDto(brand);
    }

    @Transactional
    @CacheEvict(value = {"brands", "brand_summaries", "campuses", "schools"}, allEntries = true)
    public void deleteBrand(Long id, HttpServletRequest request) {

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

    }

    @Cacheable(value = "brand_summaries")
    public List<BrandSummaryDto> getAllBrandSummaries() {
        List<Brand> brands;
        brands = brandRepository.findAllActiveOrderByName();
        return brands.stream()
                .map(converterService::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    // ================================ CAMPUS OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"campuses", "campus_summaries"}, allEntries = true)
    public CampusDto createCampus(CampusCreateDto createDto, HttpServletRequest request) {

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

        Neighborhood neighborhood = neighborhoodRepository.findById(createDto.getNeighborhood().getId()).orElse(null);
        campus.setNeighborhood(neighborhood);

        // Set brand if provided
        if (createDto.getBrandId() != null) {
            Brand brand = brandRepository.findByIdAndIsActiveTrue(createDto.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + createDto.getBrandId()));
            campus.setBrand(brand);
        }

        campus = campusRepository.save(campus);

        return converterService.mapToDto(campus);
    }


    @Transactional
    public Campus createCampusByClass(Campus campus) {
        return campusRepository.saveAndFlush(campus);
    }

    @Cacheable(value = "campuses", key = "#id")
    public CampusDto getCampusById(Long id, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        Campus campus = campusRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with ID: " + id));

        validateUserCanAccessCampus(user, campus.getId());

        return converterService.mapToDto(campus);
    }

    @Cacheable(value = "campuses", key = "#slug")
    public CampusDto getCampusBySlug(String slug) {

        Campus campus = campusRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with slug: " + slug));

        return converterService.mapToDto(campus);
    }

    @Cacheable(value = "campus_summaries", key = "#brandId")
    public List<CampusSummaryDto> getCampusesByBrand(Long brandId, HttpServletRequest request) {

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

        return converterService.mapToDto(school);
    }


    @Cacheable(value = "schools", key = "#id")
    public SchoolDto getSchoolById(Long id) {

        School school = schoolRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + id));
        return converterService.mapToDto(school);
    }

    @Cacheable(value = "schools", key = "#id")
    public List<SchoolDto> getSchoolByBrandId(Long id) {
        Set<School> schools = schoolRepository.findByBrandIdAndIsActiveTrue(id);
        return converterService.mapSchoolsToDto(schools);
    }

    @Cacheable(value = "schools", key = "#id")
    public List<SchoolDto> getSchoolByCampusId(Long id) {
        Set<School> schools = schoolRepository.findByCampusIdAndIsActiveTrue(id);
        return converterService.mapSchoolsToDto(schools);
    }


    @Cacheable(value = "schools", key = "#slug")
    public SchoolDto getSchoolBySlug(String slug) {

        School school = schoolRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with slug: " + slug));

        return converterService.mapToDto(school);
    }

    public SchoolDetailDto getSchoolDetailById(Long id, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        School school = schoolRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + id));

        validateUserCanAccessSchool(user, school.getId());

        return converterService.mapToDetailDto(school);
    }

    // ================================ SEARCH OPERATIONS ================================

    public Page<SchoolSearchResultDto> searchSchools(SchoolSearchDto searchDto) {

        Pageable pageable = PageRequest.of(
                searchDto.getPage() != null ? searchDto.getPage() : 0,
                searchDto.getSize() != null ? searchDto.getSize() : 20,
                createSort(searchDto.getSortBy(), searchDto.getSortDirection())
        );

        Page<School> schools = searchSchoolPages(
                searchDto,
                pageable
        );

/*
        for (School school : schools) {
            for (InstitutionPropertyValue pv : school.getPropertyValues()) {
                InstitutionProperty prop = pv.getProperty();
                PropertyType propType = prop.getPropertyType(); // Bu null olmamalı
                System.out.println("Property: " + prop.getName() +
                        ", Type: " + propType.getName());
            }
        }

 */

        return schools.map(converterService::mapToSearchResultDto);
    }


    public Page<School> searchSchoolPages(SchoolSearchDto searchDto, Pageable pageable) {

        // 1. ID'leri getir (pagination ve sıralama burada)
        String sortBy = "name"; // default
        String sortDirection = "ASC"; // default

        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortBy = order.getProperty();
            sortDirection = order.getDirection().name();
        }

        // institutionTypeIds'i PostgreSQL array formatına çevir
        String institutionTypeIdsArray = null;
        if (searchDto.getInstitutionTypeIds() != null && !searchDto.getInstitutionTypeIds().isEmpty()) {
            institutionTypeIdsArray = "{" +
                    searchDto.getInstitutionTypeIds().stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(",")) + "}";
        }


        String propertyIdsArray = null;
        if (searchDto.getPropertyFilters() != null && !searchDto.getPropertyFilters().isEmpty()) {
            propertyIdsArray = "{" +
                    searchDto.getPropertyFilters().stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(",")) + "}";
        }

        // 1. ID'leri getir (sıralı ve paginated)
        List<Long> schoolIds = schoolRepository.searchSchoolIds(
                searchDto.getSearchTerm(),
                institutionTypeIdsArray,
                propertyIdsArray,
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
                searchDto.getMinRating(),
                searchDto.getHasActiveCampaigns(),
                searchDto.getIsSubscribed(),
                sortBy,
                sortDirection,
                pageable.getPageSize(),
                (int) pageable.getOffset()
        );

        if (schoolIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2. Total count'u al
        long total = schoolRepository.countSchools(
                searchDto.getSearchTerm(),
                institutionTypeIdsArray,
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
                searchDto.getMinRating(),
                searchDto.getHasActiveCampaigns(),
                searchDto.getIsSubscribed()
        );

        // 3. Detayları getir
        List<School> schools = schoolRepository.findByIdsWithAllDetails(schoolIds);

        // 4. ID sırasına göre düzenle (database'den gelen sırayı koru)
        Map<Long, School> schoolMap = schools.stream()
                .collect(Collectors.toMap(School::getId, Function.identity()));

        List<School> orderedSchools = schoolIds.stream()
                .map(schoolMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageImpl<>(orderedSchools, pageable, total);
    }
    // ================================ INSTITUTION TYPE OPERATIONS ================================


    @Cacheable(value = "institution_type_summaries")
    public List<InstitutionTypeSummaryDto> getInstitutionTypeSummaries() {

        return institutionTypeRepository.findInstitutionTypeSummaries();
    }

    // ================================ INSTITUTION PROPERTY OPERATIONS ================================

    @Transactional
    @CacheEvict(value = {"institution_properties"}, allEntries = true)
    public InstitutionPropertyDto createInstitutionProperty(InstitutionPropertyCreateDto createDto, HttpServletRequest request) {

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

        return converterService.mapToDto(property);
    }

    @Cacheable(value = "institution_properties", key = "#institutionTypeId")
    public List<InstitutionPropertyDto> getPropertiesByInstitutionType(Long institutionTypeId) {

        List<InstitutionProperty> properties = institutionPropertyRepository
                .findByInstitutionTypeIdAndIsActiveTrueOrderBySortOrderAscDisplayNameAsc(institutionTypeId);

        return properties.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }

    // ================================ PROPERTY VALUE OPERATIONS ================================

    @Transactional
    public void setSchoolPropertyValues(Long schoolId, List<InstitutionPropertyValueSetDto> propertyValues, HttpServletRequest request) {

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

        User user = jwtService.getUser(request);
        validateUserCanAccessSchool(user, schoolId);
// ceyhun return schoolRepository.getSchoolStatistics(schoolId);
        return null;
    }

    public InstitutionFavoritesDto getUserFavorites(HttpServletRequest request) {

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

        // Only show schools from subscribed campuses
        searchDto.setIsSubscribed(true);

        return searchSchools(searchDto);
    }

    public SchoolDto getPublicSchoolBySlug(String slug) {

        School school = schoolRepository.findBySlugAndIsActiveTrueAndCampusIsSubscribedTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("School not found or not available"));

        // Increment view count
        schoolRepository.incrementViewCount(school.getId());

        return converterService.mapToDto(school);
    }

    public CampusDto getPublicCampusBySlug(String slug) {

        Campus campus = campusRepository.findBySlugAndIsActiveTrueAndIsSubscribedTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found or not available"));

        // Increment view count
        campusRepository.incrementViewCount(campus.getId());

        return converterService.mapToDto(campus);
    }

    public BrandDto getPublicBrandBySlug(String slug) {

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

        return converterService.mapToDto(institutionType);
    }


    public SchoolSearchDto validateSearchSchools(@Valid SchoolSearchDto searchDto) {
        if (searchDto.getSearchTerm() == null) {
            searchDto.setSearchTerm("");
        }
        if (searchDto.getCurriculumType() != null && searchDto.getCurriculumType().trim().isEmpty()) {
            searchDto.setCurriculumType(null);
        }
        if (searchDto.getLanguageOfInstruction() != null && searchDto.getLanguageOfInstruction().trim().isEmpty()) {
            searchDto.setLanguageOfInstruction(null);
        }

        if (searchDto.getInstitutionTypeIds() != null && searchDto.getInstitutionTypeIds().isEmpty()) {
            searchDto.setInstitutionTypeIds(null);
        }

        if (searchDto.getMinAge() != null && searchDto.getMinAge() <= 1) {
            searchDto.setMinAge(null);
        }
        if (searchDto.getMaxAge() != null && searchDto.getMaxAge() <= 1) {
            searchDto.setMaxAge(null);
        }

        if (searchDto.getMinFee() != null && searchDto.getMinFee() < 1) {
            searchDto.setMinFee(null);
        }
        if (searchDto.getMaxFee() != null && searchDto.getMaxFee() < 1) {
            searchDto.setMaxFee(null);
        }

        if (searchDto.getCountryId() != null && searchDto.getCountryId() <= 0) {
            searchDto.setCountryId(null);
        }
        if (searchDto.getProvinceId() != null && searchDto.getProvinceId() <= 0) {
            searchDto.setProvinceId(null);
        }
        if (searchDto.getDistrictId() != null && searchDto.getDistrictId() <= 0) {
            searchDto.setDistrictId(null);
        }
        if (searchDto.getNeighborhoodId() != null && searchDto.getNeighborhoodId() <= 0) {
            searchDto.setNeighborhoodId(null);
        }

        if (searchDto.getLatitude() != null && (searchDto.getLatitude() < -90 || searchDto.getLatitude() > 90)) {
            searchDto.setLatitude(null);
        }
        if (searchDto.getLongitude() != null && (searchDto.getLongitude() < -180 || searchDto.getLongitude() > 180)) {
            searchDto.setLongitude(null);
        }

        if (searchDto.getRadiusKm() != null && searchDto.getRadiusKm() <= 0) {
            searchDto.setRadiusKm(null);
        }

        if (searchDto.getMinRating() != null && searchDto.getMinRating() < 0) {
            searchDto.setMinRating(null);
        }

        if (searchDto.getSortBy() == null || searchDto.getSortBy().trim().isEmpty()) {
            searchDto.setSortBy("NAME");
        }

        if (searchDto.getPage() == null || searchDto.getPage() <= 0) {
            searchDto.setPage(0);
        }
        if (searchDto.getSize() == null || searchDto.getSize() <= 0) {
            searchDto.setSize(10);
        }

        searchDto.setHasActiveCampaigns(null);
        searchDto.setIsSubscribed(null);

        return searchDto;
    }


    /**
     * Tüm aktif InstitutionType'ları PropertyGroupType ve PropertyType'larıyla birlikte getirir
     */
    public List<InstitutionTypeListDto> getAllInstitutionTypesWithProperties() {
        // 1. Tüm aktif InstitutionType'ları getir
        List<InstitutionType> institutionTypes = institutionTypeRepository.findByIsActiveTrue();

        if (institutionTypes.isEmpty()) {
            return List.of();
        }

        // 2. InstitutionType ID'lerini topla
        List<Long> institutionTypeIds = institutionTypes.stream()
                .map(InstitutionType::getId)
                .collect(Collectors.toList());

        // 3. Bu InstitutionType'lara ait PropertyGroupType'ları getir
        List<PropertyGroupType> propertyGroupTypes =
                propertyGroupTypeRepository.findByInstitutionTypeIdInAndIsActiveTrue(institutionTypeIds);

        // 4. PropertyGroupType ID'lerini topla
        List<Long> propertyGroupTypeIds = propertyGroupTypes.stream()
                .map(PropertyGroupType::getId)
                .collect(Collectors.toList());

        // 5. Bu PropertyGroupType'lara ait PropertyType'ları getir
        List<PropertyType> propertyTypes = List.of(); // Boş liste, eğer PropertyType'lar da gerekirse
        if (!propertyGroupTypeIds.isEmpty()) {
            propertyTypes = propertyTypeRepository.findByPropertyGroupTypeIdInAndIsActiveTrue(propertyGroupTypeIds);
        }

        // 6. Verileri grupla ve DTO'ya dönüştür
        return buildInstitutionTypeListDtos(institutionTypes, propertyGroupTypes, propertyTypes);
    }

    /**
     * Belirli bir InstitutionType'ı PropertyGroupType ve PropertyType'larıyla birlikte getirir
     */
    public InstitutionTypeListDto getInstitutionTypeWithProperties(Long institutionTypeId) {
        // 1. InstitutionType'ı getir
        InstitutionType institutionType = institutionTypeRepository.findByIdAndIsActiveTrue(institutionTypeId)
                .orElseThrow(() -> new RuntimeException("InstitutionType not found: " + institutionTypeId));

        // 2. Bu InstitutionType'a ait PropertyGroupType'ları getir
        List<PropertyGroupType> propertyGroupTypes =
                propertyGroupTypeRepository.findByInstitutionTypeIdAndIsActiveTrue(institutionTypeId);

        // 3. PropertyGroupType ID'lerini topla
        List<Long> propertyGroupTypeIds = propertyGroupTypes.stream()
                .map(PropertyGroupType::getId)
                .collect(Collectors.toList());

        // 4. Bu PropertyGroupType'lara ait PropertyType'ları getir
        List<PropertyType> propertyTypes = List.of();
        if (!propertyGroupTypeIds.isEmpty()) {
            propertyTypes = propertyTypeRepository.findByPropertyGroupTypeIdInAndIsActiveTrue(propertyGroupTypeIds);
        }

        // 5. DTO'ya dönüştür
        List<InstitutionTypeListDto> result = buildInstitutionTypeListDtos(
                List.of(institutionType), propertyGroupTypes, propertyTypes);

        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Entity'leri grupla ve DTO'lara dönüştür
     */
    private List<InstitutionTypeListDto> buildInstitutionTypeListDtos(
            List<InstitutionType> institutionTypes,
            List<PropertyGroupType> propertyGroupTypes,
            List<PropertyType> propertyTypes) {

        // PropertyType'ları PropertyGroupType ID'ye göre grupla
        Map<Long, List<PropertyType>> propertyTypesByGroupId = propertyTypes.stream()
                .collect(Collectors.groupingBy(pt -> pt.getPropertyGroupType().getId()));

        // PropertyGroupType'ları InstitutionType ID'ye göre grupla
        Map<Long, List<PropertyGroupType>> propertyGroupsByInstitutionId = propertyGroupTypes.stream()
                .collect(Collectors.groupingBy(pgt -> pgt.getInstitutionType().getId()));

        // Her InstitutionType için InstitutionTypeListDto oluştur
        return institutionTypes.stream()
                .map(institutionType -> {
                    // Bu InstitutionType'a ait PropertyGroupType'ları getir
                    List<PropertyGroupType> institutionPropertyGroups =
                            propertyGroupsByInstitutionId.getOrDefault(institutionType.getId(), List.of());

                    // PropertyGroupType'ları DTO'ya dönüştür
                    List<PropertyGroupTypeDto> propertyGroupDtos = institutionPropertyGroups.stream()
                            .map(propertyGroup -> {
                                // Bu PropertyGroup'a ait PropertyType'ları getir
                                List<PropertyType> groupPropertyTypes =
                                        propertyTypesByGroupId.getOrDefault(propertyGroup.getId(), List.of());

                                // PropertyType'ları DTO'ya dönüştür
                                List<PropertyTypeDto> propertyTypeDtos = groupPropertyTypes.stream()
                                        .map(converterService::mapPropertyTypeToDto)
                                        .collect(Collectors.toList());

                                // PropertyGroupTypeDto oluştur
                                PropertyGroupTypeDto dto = converterService.mapPropertyGroupTypeToDto(propertyGroup);
                                dto.setPropertyTypes(propertyTypeDtos); // PropertyType'ları set et
                                return dto;
                            })
                            .collect(Collectors.toList());

                    // InstitutionTypeListDto oluştur
                    return InstitutionTypeListDto.builder()
                            .institutionTypeDto(converterService.mapToDto(institutionType))
                            .propertyGroupTypeDtos(propertyGroupDtos)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Sadece InstitutionType'ları getirir (PropertyGroup'lar olmadan)
     */
    public List<InstitutionTypeListDto> getAllInstitutionTypes() {
        List<InstitutionType> institutionTypes = institutionTypeRepository.findByIsActiveTrue();

        return institutionTypes.stream()
                .map(institutionType -> InstitutionTypeListDto.builder()
                        .institutionTypeDto(converterService.mapToDto(institutionType))
                        .propertyGroupTypeDtos(List.of()) // Boş liste
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional
    @CacheEvict(value = {"campuses", "campus_summaries"}, allEntries = true)
    public CampusDto updateCampus(Long id, CampusCreateDto updateDto, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        Campus campus = campusRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with ID: " + id));

        validateUserCanAccessCampus(user, campus.getId());

        // Check name uniqueness if changed
        if (!campus.getName().equalsIgnoreCase(updateDto.getName()) &&
                campusRepository.existsByNameIgnoreCaseAndIdNot(updateDto.getName(), id)) {
            throw new BusinessException("Campus name already exists: " + updateDto.getName());
        }

        campus.setName(updateDto.getName());
        campus.setDescription(updateDto.getDescription());
        campus.setLogoUrl(updateDto.getLogoUrl());
        campus.setCoverImageUrl(updateDto.getCoverImageUrl());
        campus.setWebsiteUrl(updateDto.getWebsiteUrl());
        campus.setEmail(updateDto.getEmail());
        campus.setPhone(updateDto.getPhone());
        campus.setFacebookUrl(updateDto.getFacebookUrl());
        campus.setTwitterUrl(updateDto.getTwitterUrl());
        campus.setInstagramUrl(updateDto.getInstagramUrl());
        campus.setLinkedinUrl(updateDto.getLinkedinUrl());
        campus.setYoutubeUrl(updateDto.getYoutubeUrl());
        campus.setMetaTitle(updateDto.getMetaTitle());
        campus.setMetaDescription(updateDto.getMetaDescription());
        campus.setMetaKeywords(updateDto.getMetaKeywords());
        campus.setUpdatedBy(user.getId());



        campus.setAddressLine1(updateDto.getAddressLine1());
        campus.setAddressLine2(updateDto.getAddressLine2());
        campus.setPostalCode(updateDto.getPostalCode());
        campus.setFax(updateDto.getFax());
        campus.setLatitude(updateDto.getLatitude());
        campus.setLongitude(updateDto.getLongitude());
        campus.setEstablishedYear(updateDto.getEstablishedYear());


        Country country = countryRepository.findById(updateDto.getCountry().getId()).orElse(null);
        Province province = provinceRepository.findById(updateDto.getProvince().getId()).orElse(null);
        District district = districtRepository.findById(updateDto.getDistrict().getId()).orElse(null);
        Neighborhood neighborhood = neighborhoodRepository.findById(updateDto.getNeighborhood().getId()).orElse(null);

        campus.setDistrict(district);
        campus.setProvince(province);
        campus.setCountry(country);
        campus.setNeighborhood(neighborhood);

        campus = campusRepository.save(campus);

        return converterService.mapToDto(campus);
    }


    @Transactional
    @CacheEvict(value = {"schools", "school_summaries"}, allEntries = true)
    public SchoolDto updateSchool(Long id, SchoolCreateDto updateDto, HttpServletRequest request) {

        User user = jwtService.getUser(request);
        School school = schoolRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + id));

        validateUserCanAccessSchool(user, school.getId());

        // Check name uniqueness if changed
        if (!school.getName().equalsIgnoreCase(updateDto.getName()) &&
                schoolRepository.existsByNameIgnoreCaseAndIdNot(updateDto.getName(), id)) {
            throw new BusinessException("School name already exists: " + updateDto.getName());
        }


        Campus campus = campusRepository.findByIdAndIsActiveTrue(updateDto.getCampusId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with ID: " + updateDto.getCampusId()));

        InstitutionType institutionType = institutionTypeRepository.findByIdAndIsActiveTrue(updateDto.getInstitutionTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Institution type not found with ID: " + updateDto.getInstitutionTypeId()));

        validateUserCanAccessCampus(user, campus.getId());


        school.setName(updateDto.getName());
        //school.setSlug(slug);
        school.setDescription(updateDto.getDescription());
        school.setLogoUrl(updateDto.getLogoUrl());
        school.setCoverImageUrl(updateDto.getCoverImageUrl());
        school.setEmail(updateDto.getEmail());
        school.setPhone(updateDto.getPhone());
        school.setExtension(updateDto.getExtension());
        school.setMinAge(updateDto.getMinAge());
        school.setMaxAge(updateDto.getMaxAge());
        school.setCapacity(updateDto.getCapacity());
        school.setCurrentStudentCount(updateDto.getCurrentStudentCount());
        school.setClassSizeAverage(updateDto.getClassSizeAverage());
        school.setCurriculumType(updateDto.getCurriculumType());
        school.setLanguageOfInstruction(updateDto.getLanguageOfInstruction());
        school.setForeignLanguages(updateDto.getForeignLanguages());
        school.setRegistrationFee(updateDto.getRegistrationFee());
        school.setMonthlyFee(updateDto.getMonthlyFee());
        school.setAnnualFee(updateDto.getAnnualFee());
        school.setMetaTitle(updateDto.getMetaTitle());
        school.setMetaDescription(updateDto.getMetaDescription());
        school.setMetaKeywords(updateDto.getMetaKeywords());
        school.setCampus(campus);
        school.setInstitutionType(institutionType);
        school.setUpdatedBy(user.getId());



        school.setFacebookUrl(updateDto.getFacebookUrl());
        school.setTwitterUrl(updateDto.getTwitterUrl());
        school.setInstagramUrl(updateDto.getInstagramUrl());
        school.setLinkedinUrl(updateDto.getLinkedinUrl());
        school.setYoutubeUrl(updateDto.getYoutubeUrl());




        school = schoolRepository.save(school);

        return converterService.mapToDto(school);
    }

    public List<InstitutionPropertyValueDto> getSchoolInstitutionPropertyValueList(Long id) {
        List<InstitutionPropertyValue> institutionPropertyValues = institutionPropertyValueRepository.findProfileValuesBySchoolId(id);
        return institutionPropertyValues.stream().map(converterService::mapToDto).collect(Collectors.toList());
    }

    public void addSchoolInstitutionProperty(Long schoolId, Long propertyId) {
        InstitutionProperty institutionProperty = institutionPropertyRepository.findById(propertyId).orElse(null);
        School school = schoolRepository.findById(schoolId).orElse(null);

        if (institutionProperty != null && school != null) {
            InstitutionPropertyValue institutionPropertyValues = institutionPropertyValueRepository.findByPropertyIdAndSchoolIdAndIsActiveTrue(propertyId, schoolId).orElse(null);
            if (institutionPropertyValues == null) {
                InstitutionPropertyValue value = new InstitutionPropertyValue();
                value.setProperty(institutionProperty);
                value.setCampus(null);
                value.setSchool(school);
                value.setTextValue(null);
                value.setNumberValue(null);
                value.setBooleanValue(true);
                value.setDateValue(null);
                value.setDatetimeValue(null);
                value.setJsonValue(null);
                value.setFileUrl(null);
                value.setFileName(null);
                value.setFileSize(null);
                value.setMimeType(null);
                institutionPropertyValueRepository.save(value);
            }
        }
    }

    public void removeSchoolInstitutionProperty(Long schoolId, Long propertyId) {
        InstitutionPropertyValue institutionPropertyValues = institutionPropertyValueRepository.findByPropertyIdAndSchoolIdAndIsActiveTrue(propertyId, schoolId).orElse(null);
        if (institutionPropertyValues != null) {
            institutionPropertyValueRepository.delete(institutionPropertyValues);
        }
    }


    public List<SchoolPropertyDto> getSchoolPropertyValueList(Long id) {
        List<InstitutionPropertyValue> institutionPropertyValues = institutionPropertyValueRepository.findProfileValuesBySchoolId(id);

        List<SchoolPropertyDto> schoolPropertyDtos = new ArrayList<>();
        for (InstitutionPropertyValue pv : institutionPropertyValues) {
            if (pv.getProperty() != null) {
                if (pv.getProperty().getPropertyType() != null) {
                    SchoolPropertyDto schoolPropertyDto = new SchoolPropertyDto();
                    schoolPropertyDto.setSchoolId(id); //schoolId;
                    schoolPropertyDto.setPropertyTypeId(pv.getProperty().getPropertyType().getId()); //propertyTypeId
                    schoolPropertyDto.setInstitutionPropertyValueId(pv.getId()); //institutionPropertyValueId
                    schoolPropertyDto.setInstitutionPropertyId(pv.getProperty().getId()); //institutionPropertyId
                    schoolPropertyDto.setName(pv.getProperty().getPropertyType().getName());//name
                    schoolPropertyDto.setDisplayName(pv.getProperty().getPropertyType().getDisplayName()); //displayName
                    schoolPropertyDto.setPropertyGroupTypeId(pv.getProperty().getPropertyType().getPropertyGroupType().getId()); //propertyGroupTypeId
                    schoolPropertyDto.setGroupSortOrder(pv.getProperty().getPropertyType().getPropertyGroupType().getSortOrder()); //propertyGroupTypeId
                    schoolPropertyDto.setSortOrder(pv.getProperty().getPropertyType().getSortOrder()); //sortOrder;
                    schoolPropertyDto.setGroupName(pv.getProperty().getPropertyType().getPropertyGroupType().getName()); //groupName
                    schoolPropertyDto.setGroupDisplayName(pv.getProperty().getPropertyType().getPropertyGroupType().getDisplayName()); //groupDisplayName;
                    schoolPropertyDto.setInstitutionTypeId(pv.getProperty().getInstitutionType().getId()); //institutionTypeId;
                    schoolPropertyDto.setInstitutionTypeName(pv.getProperty().getInstitutionType().getName()); //institutionTypeName;
                    schoolPropertyDtos.add(schoolPropertyDto);
                }
            }

        }
        return schoolPropertyDtos;
    }


    @Transactional
    public void updateSchoolPropertyList(School school, List<Long> propertyIds) {
        List<PropertyType> propertyTypes = propertyTypeRepository.findAllById(propertyIds);

        List<InstitutionPropertyValue> existingValues =
                institutionPropertyValueRepository.findBySchoolIdWithProperty(school.getId());

        Map<Long, InstitutionPropertyValue> existingMap = existingValues.stream()
                .collect(Collectors.toMap(
                        ipv -> ipv.getProperty().getPropertyType().getId(),
                        ipv -> ipv
                ));

        for (PropertyType propertyType : propertyTypes) {
            if (!existingMap.containsKey(propertyType.getId())) {
                addSchoolProperty(school, propertyType);
            }
        }

        Set<Long> newPropertyTypeIds = propertyTypes.stream()
                .map(PropertyType::getId)
                .collect(Collectors.toSet());

        existingValues.stream()
                .filter(ipv -> !newPropertyTypeIds.contains(ipv.getProperty().getPropertyType().getId()))
                .forEach(ipv -> removeSchoolProperty(school, ipv));
    }

    public School addSchoolProperty(School school, PropertyType propertyType) {

        InstitutionProperty ip = new InstitutionProperty();
        ip.setInstitutionType(school.getInstitutionType());
        ip.setName(propertyType.getName());
        ip.setDisplayName(propertyType.getDisplayName());
        ip.setDataType(PropertyDataType.BOOLEAN);
        ip.setSortOrder(propertyType.getSortOrder());
        ip.setPropertyType(propertyType);
        InstitutionProperty institutionProperty = institutionPropertyRepository.saveAndFlush(ip);

        InstitutionPropertyValue iv = new InstitutionPropertyValue();
        iv.setProperty(institutionProperty);
        iv.setSchool(school);
        iv.setBooleanValue(true);
        institutionPropertyValueRepository.saveAndFlush(iv);

        school.getPropertyValues().add(iv);
        return school;

    }

    public School removeSchoolProperty(School school, InstitutionPropertyValue propertyValue) {
        institutionPropertyValueRepository.delete(propertyValue);
        institutionPropertyRepository.delete(propertyValue.getProperty());
        school.getPropertyValues().remove(propertyValue);
        return school;
    }


    public School getSchoolObjectById(Long schoolId) {
        return schoolRepository.findById(schoolId)
                .orElse(null);
    }
}

