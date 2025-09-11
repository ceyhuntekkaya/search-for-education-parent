package com.genixo.education.search.service.converter;

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
import com.genixo.education.search.util.ConversionUtils;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InstitutionConverterService {


    public InstitutionTypeDto mapToDto(InstitutionType entity) {
        if (entity == null) {
            return null;
        }

        return InstitutionTypeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .iconUrl(entity.getIconUrl())
                .colorCode(entity.getColorCode())
                .sortOrder(entity.getSortOrder())
                .defaultProperties(entity.getDefaultProperties())
                .properties(mapInstitutionPropertiesToDto(entity.getProperties()))
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public InstitutionTypeSummaryDto mapToSummaryDto(InstitutionType entity) {
        if (entity == null) {
            return null;
        }

        return InstitutionTypeSummaryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .iconUrl(entity.getIconUrl())
                .colorCode(entity.getColorCode())
            //    .schoolCount(ConversionUtils.safeSize(entity.getSchools().stream().toList()) != 0 ?  ceyhun
            //            (long) ConversionUtils.safeSize(entity.getSchools().stream().toList()) : 0L)
                .build();
    }

    public List<InstitutionTypeDto> mapInstitutionTypesToDto(Set<InstitutionType> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(InstitutionTypeDto::getSortOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public List<InstitutionTypeSummaryDto> mapInstitutionTypesToSummaryDto(Set<InstitutionType> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(InstitutionTypeSummaryDto::getName,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    // ================== INSTITUTION PROPERTY CONVERSIONS ==================

    public InstitutionPropertyDto mapToDto(InstitutionProperty entity) {
        if (entity == null) {
            return null;
        }

        return InstitutionPropertyDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .dataType(entity.getDataType())
                .isRequired(entity.getIsRequired())
                .isSearchable(entity.getIsSearchable())
                .isFilterable(entity.getIsFilterable())
                .showInCard(entity.getShowInCard())
                .showInProfile(entity.getShowInProfile())
                .sortOrder(entity.getSortOrder())
                .options(entity.getOptions())
                .defaultValue(entity.getDefaultValue())
                .minValue(entity.getMinValue())
                .maxValue(entity.getMaxValue())
                .minLength(entity.getMinLength())
                .maxLength(entity.getMaxLength())
                .regexPattern(entity.getRegexPattern())
                .institutionType(mapToSummaryDto(entity.getInstitutionType()))
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public InstitutionProperty mapToEntity(InstitutionPropertyCreateDto dto) {
        if (dto == null) {
            return null;
        }

        InstitutionProperty entity = new InstitutionProperty();
        entity.setName(dto.getName());
        entity.setDisplayName(dto.getDisplayName());
        entity.setDescription(dto.getDescription());
        entity.setDataType(dto.getDataType());
        entity.setIsRequired(ConversionUtils.defaultIfNull(dto.getIsRequired(), false));
        entity.setIsSearchable(ConversionUtils.defaultIfNull(dto.getIsSearchable(), false));
        entity.setIsFilterable(ConversionUtils.defaultIfNull(dto.getIsFilterable(), false));
        entity.setShowInCard(ConversionUtils.defaultIfNull(dto.getShowInCard(), false));
        entity.setShowInProfile(ConversionUtils.defaultIfNull(dto.getShowInProfile(), true));
        entity.setSortOrder(ConversionUtils.defaultIfNull(dto.getSortOrder(), 0));
        entity.setOptions(dto.getOptions());
        entity.setDefaultValue(dto.getDefaultValue());
        entity.setMinValue(dto.getMinValue());
        entity.setMaxValue(dto.getMaxValue());
        entity.setMinLength(dto.getMinLength());
        entity.setMaxLength(dto.getMaxLength());
        entity.setRegexPattern(dto.getRegexPattern());

        return entity;
    }

    public List<InstitutionPropertyDto> mapInstitutionPropertiesToDto(Set<InstitutionProperty> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .filter(property -> property.getIsActive() != null && property.getIsActive())
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(InstitutionPropertyDto::getSortOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    // ================== INSTITUTION PROPERTY VALUE CONVERSIONS ==================

    public InstitutionPropertyValueDto mapToDto(InstitutionPropertyValue entity) {
        if (entity == null) {
            return null;
        }

        String displayValue = calculateDisplayValue(entity);
        String formattedValue = calculateFormattedValue(entity);

        return InstitutionPropertyValueDto.builder()
                .id(entity.getId())
                .propertyId(entity.getProperty() != null ? entity.getProperty().getId() : null)
                .propertyName(entity.getProperty() != null ? entity.getProperty().getName() : null)
                .propertyDisplayName(entity.getProperty() != null ? entity.getProperty().getDisplayName() : null)
                .dataType(entity.getProperty() != null ? entity.getProperty().getDataType() : null)
                .showInCard(entity.getProperty() != null ? entity.getProperty().getShowInCard() : false)
                .showInProfile(entity.getProperty() != null ? entity.getProperty().getShowInProfile() : false)
                .textValue(entity.getTextValue())
                .numberValue(entity.getNumberValue())
                .booleanValue(entity.getBooleanValue())
                .dateValue(entity.getDateValue() != null ? entity.getDateValue().toString() : null)
                .datetimeValue(entity.getDatetimeValue() != null ? entity.getDatetimeValue().toString() : null)
                .jsonValue(entity.getJsonValue())
                .fileUrl(entity.getFileUrl())
                .fileName(entity.getFileName())
                .displayValue(displayValue)
                .formattedValue(formattedValue)
                .build();
    }

    public InstitutionPropertyValue mapToEntity(InstitutionPropertyValueSetDto dto) {
        if (dto == null) {
            return null;
        }

        InstitutionPropertyValue entity = new InstitutionPropertyValue();
        entity.setTextValue(dto.getTextValue());
        entity.setNumberValue(dto.getNumberValue());
        entity.setBooleanValue(dto.getBooleanValue());

        if (StringUtils.hasText(dto.getDateValue())) {
            try {
                entity.setDateValue(java.time.LocalDate.parse(dto.getDateValue()));
            } catch (Exception ignored) {
                // Invalid date format, keep null
            }
        }

        if (StringUtils.hasText(dto.getDatetimeValue())) {
            try {
                entity.setDatetimeValue(java.time.LocalDateTime.parse(dto.getDatetimeValue()));
            } catch (Exception ignored) {
                // Invalid datetime format, keep null
            }
        }

        entity.setJsonValue(dto.getJsonValue());
        entity.setFileUrl(dto.getFileUrl());
        entity.setFileName(dto.getFileName());
        entity.setFileSize(dto.getFileSize());
        entity.setMimeType(dto.getMimeType());

        return entity;
    }

    public List<InstitutionPropertyValueDto> mapPropertyValuesToDto(Set<InstitutionPropertyValue> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .filter(value -> value.getProperty() != null)
                .filter(value -> value.getProperty().getIsActive() != null && value.getProperty().getIsActive())
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(dto -> dto.getPropertyId(),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    private String calculateDisplayValue(InstitutionPropertyValue entity) {
        if (entity.getProperty() == null) {
            return null;
        }

        switch (entity.getProperty().getDataType()) {
            case TEXT:
            case TEXTAREA:
            case URL:
            case EMAIL:
            case PHONE:
                return entity.getTextValue();

            case NUMBER:
                return entity.getNumberValue() != null ?
                        ConversionUtils.formatNumber(entity.getNumberValue().longValue()) : null;

            case DECIMAL:
                return entity.getNumberValue() != null ?
                        String.valueOf(entity.getNumberValue()) : null;

            case BOOLEAN:
                if (entity.getBooleanValue() != null) {
                    return entity.getBooleanValue() ? "Evet" : "Hayır";
                }
                return null;

            case DATE:
                return ConversionUtils.formatDate(entity.getDateValue());

            case DATETIME:
                return ConversionUtils.formatDateTime(entity.getDatetimeValue());

            case TIME:
                return entity.getDatetimeValue() != null ?
                        ConversionUtils.formatTime(entity.getDatetimeValue().toLocalTime()) : null;

            case FILE:
            case IMAGE:
                return entity.getFileName();

            default:
                return entity.getTextValue();
        }
    }

    private String calculateFormattedValue(InstitutionPropertyValue entity) {
        if (entity.getProperty() == null) {
            return calculateDisplayValue(entity);
        }

        switch (entity.getProperty().getDataType()) {
            case PHONE:
                return ConversionUtils.formatPhoneNumber(entity.getTextValue());

            case URL:
                return ConversionUtils.ensureHttpProtocol(entity.getTextValue());

            case FILE:
            case IMAGE:
                if (entity.getFileSize() != null) {
                    return entity.getFileName() + " (" +
                            ConversionUtils.formatFileSize(entity.getFileSize()) + ")";
                }
                return entity.getFileName();

            default:
                return calculateDisplayValue(entity);
        }
    }

    // ================== BRAND CONVERSIONS ==================
//@Transactional(readOnly = true)
    public BrandDto mapToDto(Brand entity) {
        if (entity == null) {
            return null;
        }

        return BrandDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .logoUrl(entity.getLogoUrl())
                .coverImageUrl(entity.getCoverImageUrl())
                .websiteUrl(ConversionUtils.ensureHttpProtocol(entity.getWebsiteUrl()))
                .email(entity.getEmail())
                .phone(ConversionUtils.formatPhoneNumber(entity.getPhone()))
                .foundedYear(entity.getFoundedYear())
                .facebookUrl(ConversionUtils.ensureHttpProtocol(entity.getFacebookUrl()))
                .twitterUrl(ConversionUtils.ensureHttpProtocol(entity.getTwitterUrl()))
                .instagramUrl(ConversionUtils.ensureHttpProtocol(entity.getInstagramUrl()))
                .linkedinUrl(ConversionUtils.ensureHttpProtocol(entity.getLinkedinUrl()))
                .youtubeUrl(ConversionUtils.ensureHttpProtocol(entity.getYoutubeUrl()))
                .metaTitle(entity.getMetaTitle())
                .metaDescription(entity.getMetaDescription())
                .metaKeywords(entity.getMetaKeywords())
                .viewCount(entity.getViewCount())
                .ratingAverage(entity.getRatingAverage())
                .ratingCount(entity.getRatingCount())
                .campuses(mapCampusesToSummaryDto(entity.getCampuses()))
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public BrandSummaryDto mapToSummaryDto(Brand entity) {
        if (entity == null) {
            return null;
        }

        long campusCount = ConversionUtils.safeSize(entity.getCampuses().stream().toList());
        long schoolCount = entity.getCampuses() != null ?
                entity.getCampuses().stream()
                        .mapToLong(campus -> ConversionUtils.safeSize(campus.getSchools().stream().toList()))
                        .sum() : 0L;

        return BrandSummaryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .logoUrl(entity.getLogoUrl())
                .ratingAverage(entity.getRatingAverage())
                .campusCount((int) campusCount)
                .schoolCount((int) schoolCount)
                .build();
    }

    public Brand mapToEntity(BrandCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Brand entity = new Brand();
        entity.setName(dto.getName());
        entity.setSlug(ConversionUtils.generateSlug(dto.getName()));
        entity.setDescription(dto.getDescription());
        entity.setLogoUrl(dto.getLogoUrl());
        entity.setCoverImageUrl(dto.getCoverImageUrl());
        entity.setWebsiteUrl(dto.getWebsiteUrl());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setFoundedYear(dto.getFoundedYear());
        entity.setFacebookUrl(dto.getFacebookUrl());
        entity.setTwitterUrl(dto.getTwitterUrl());
        entity.setInstagramUrl(dto.getInstagramUrl());
        entity.setLinkedinUrl(dto.getLinkedinUrl());
        entity.setYoutubeUrl(dto.getYoutubeUrl());
        entity.setMetaTitle(dto.getMetaTitle());
        entity.setMetaDescription(dto.getMetaDescription());
        entity.setMetaKeywords(dto.getMetaKeywords());
        entity.setViewCount(0L);
        entity.setRatingAverage(0.0);
        entity.setRatingCount(0L);

        return entity;
    }

    public List<BrandSummaryDto> mapBrandsToSummaryDto(Set<Brand> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .filter(brand -> brand.getIsActive() != null && brand.getIsActive())
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(BrandSummaryDto::getName,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    // ================== CAMPUS CONVERSIONS ==================

    public CampusDto mapToDto(Campus entity) {
        if (entity == null) {
            return null;
        }

        return CampusDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .logoUrl(entity.getLogoUrl())
                .coverImageUrl(entity.getCoverImageUrl())
                .email(entity.getEmail())
                .phone(ConversionUtils.formatPhoneNumber(entity.getPhone()))
                .fax(entity.getFax())
                .websiteUrl(ConversionUtils.ensureHttpProtocol(entity.getWebsiteUrl()))
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .district(mapToSummaryDto(entity.getDistrict()))
                .province(mapToSummaryDto(entity.getProvince()))
                .postalCode(entity.getPostalCode())
                .country(mapToSummaryDto(entity.getCountry()))
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                // location will be set by LocationConverterService when integrated
                .facebookUrl(ConversionUtils.ensureHttpProtocol(entity.getFacebookUrl()))
                .twitterUrl(ConversionUtils.ensureHttpProtocol(entity.getTwitterUrl()))
                .instagramUrl(ConversionUtils.ensureHttpProtocol(entity.getInstagramUrl()))
                .linkedinUrl(ConversionUtils.ensureHttpProtocol(entity.getLinkedinUrl()))
                .youtubeUrl(ConversionUtils.ensureHttpProtocol(entity.getYoutubeUrl()))
                .metaTitle(entity.getMetaTitle())
                .metaDescription(entity.getMetaDescription())
                .metaKeywords(entity.getMetaKeywords())
                .viewCount(entity.getViewCount())
                .ratingAverage(entity.getRatingAverage())
                .ratingCount(entity.getRatingCount())
                .campuses(new ArrayList<>()) // Avoid circular reference in detailed view
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }


    public ProvinceSummaryDto mapToSummaryDto(Province entity) {
        if (entity == null) {
            return null;
        }

        return ProvinceSummaryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .plateCode(entity.getPlateCode())
                .isMetropolitan(entity.getIsMetropolitan())
                .schoolCount(entity.getSchoolCount())
                .build();
    }


    public DistrictSummaryDto mapToSummaryDto(District entity) {
        if (entity == null) {
            return null;
        }

        return DistrictSummaryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .districtType(entity.getDistrictType())
                .isCentral(entity.getIsCentral())
                .schoolCount(entity.getSchoolCount())
                .socioeconomicLevel(entity.getSocioeconomicLevel())
                .build();
    }





    public NeighborhoodSummaryDto mapToSummaryDto(Neighborhood entity) {
        if (entity == null) {
            return null;
        }

        return NeighborhoodSummaryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .neighborhoodType(entity.getNeighborhoodType())
                .schoolCount(entity.getSchoolCount())
                .incomeLevel(entity.getIncomeLevel())
                .schoolPreferenceScore(entity.getSchoolPreferenceScore())
                .build();
    }



    public CountrySummaryDto mapToSummaryDto(Country entity) {
        if (entity == null) {
            return null;
        }

        return CountrySummaryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isoCode2(entity.getIsoCode2())
                .flagEmoji(entity.getFlagEmoji())
                .phoneCode(entity.getPhoneCode())
                .isSupported(entity.getIsSupported())
                .build();
    }


    public CampusSummaryDto mapToSummaryDto(Campus entity) {
        if (entity == null) {
            return null;
        }
        long schoolCount = 0;
        try{
            schoolCount = entity.getSchools() != null ? entity.getSchools().size() : 0;
        }catch(Exception e){}


        return CampusSummaryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .logoUrl(entity.getLogoUrl())
                .province(mapToSummaryDto(entity.getProvince()))
                .district(mapToSummaryDto(entity.getDistrict()))
                .ratingAverage(entity.getRatingAverage())
                .schoolCount(schoolCount)
                .isSubscribed(entity.getIsSubscribed())
                .build();
    }

    public Campus mapToEntity(CampusCreateDto dto) {
        if (dto == null) {
            return null;
        }

        District district = new District();
        district.setId(dto.getDistrict().getId());

        Province province = new Province();
        province.setId(dto.getProvince().getId());

        Country country = new Country();
        country.setId(dto.getCountry().getId());

        Campus entity = new Campus();
        entity.setName(dto.getName());
        entity.setSlug(ConversionUtils.generateSlug(dto.getName()));
        entity.setDescription(dto.getDescription());
        entity.setLogoUrl(dto.getLogoUrl());
        entity.setCoverImageUrl(dto.getCoverImageUrl());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setFax(dto.getFax());
        entity.setWebsiteUrl(dto.getWebsiteUrl());
        entity.setAddressLine1(dto.getAddressLine1());
        entity.setAddressLine2(dto.getAddressLine2());
        entity.setDistrict(district);
        entity.setProvince(province);
        entity.setPostalCode(dto.getPostalCode());
        entity.setCountry(country);
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setFacebookUrl(dto.getFacebookUrl());
        entity.setTwitterUrl(dto.getTwitterUrl());
        entity.setInstagramUrl(dto.getInstagramUrl());
        entity.setLinkedinUrl(dto.getLinkedinUrl());
        entity.setYoutubeUrl(dto.getYoutubeUrl());
        entity.setMetaTitle(dto.getMetaTitle());
        entity.setMetaDescription(dto.getMetaDescription());
        entity.setMetaKeywords(dto.getMetaKeywords());
        entity.setEstablishedYear(dto.getEstablishedYear());
        entity.setViewCount(0L);
        entity.setRatingAverage(0.0);
        entity.setRatingCount(0L);
        entity.setIsSubscribed(false);

        return entity;
    }

    public List<CampusSummaryDto> mapCampusesToSummaryDto(Set<Campus> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .filter(campus -> campus.getIsActive() != null && campus.getIsActive())
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(CampusSummaryDto::getName,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    // ================== SCHOOL CONVERSIONS ==================

    public SchoolDto mapToDto(School entity) {
        if (entity == null) {
            return null;
        }

        return SchoolDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .logoUrl(entity.getLogoUrl())
                .coverImageUrl(entity.getCoverImageUrl())
                .email(entity.getEmail())
                .phone(ConversionUtils.formatPhoneNumber(entity.getPhone()))
                .extension(entity.getExtension())
                .minAge(entity.getMinAge())
                .maxAge(entity.getMaxAge())
                .capacity(entity.getCapacity())
                .currentStudentCount(entity.getCurrentStudentCount())
                .classSizeAverage(entity.getClassSizeAverage())
                .curriculumType(entity.getCurriculumType())
                .languageOfInstruction(entity.getLanguageOfInstruction())
                .foreignLanguages(entity.getForeignLanguages())
                .registrationFee(entity.getRegistrationFee())
                .monthlyFee(entity.getMonthlyFee())
                .annualFee(entity.getAnnualFee())
                .metaTitle(entity.getMetaTitle())
                .metaDescription(entity.getMetaDescription())
                .metaKeywords(entity.getMetaKeywords())
                .viewCount(entity.getViewCount())
                .ratingAverage(entity.getRatingAverage())
                .ratingCount(entity.getRatingCount())
                .likeCount(entity.getLikeCount())
                .postCount(entity.getPostCount())
                .campus(mapToSummaryDto(entity.getCampus()))
                .institutionType(mapToDto(entity.getInstitutionType()))
                .propertyValues(mapPropertyValuesToDto(entity.getPropertyValues()))
                // pricings and campaigns will be set by respective services
                .pricings(new ArrayList<>())
                .activeCampaigns(new ArrayList<>())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public SchoolSummaryDto mapToSummaryDto(School entity) {
        if (entity == null) {
            return null;
        }

        String formattedMonthlyFee = null;
        if (entity.getMonthlyFee() != null) {
            formattedMonthlyFee = ConversionUtils.formatPrice(entity.getMonthlyFee());
        }

        // Check if school has active campaigns (this would be set by CampaignService)
        boolean hasActiveCampaigns = entity.getCampaignSchools() != null &&
                entity.getCampaignSchools().stream()
                        .anyMatch(cs -> cs.getIsActive() != null && cs.getIsActive());

        return SchoolSummaryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .logoUrl(entity.getLogoUrl())
                .institutionTypeName(entity.getInstitutionType() != null ?
                        entity.getInstitutionType().getDisplayName() : null)
                .minAge(entity.getMinAge())
                .maxAge(entity.getMaxAge())
                .monthlyFee(entity.getMonthlyFee())
                .ratingAverage(entity.getRatingAverage())
                .ratingCount(entity.getRatingCount())
                .hasActiveCampaigns(hasActiveCampaigns)
                .build();
    }

    public SchoolSearchResultDto mapToSearchResultDto(School entity) {
        if (entity == null) {
            return null;
        }

        String ageRange = ConversionUtils.formatAgeRange(entity.getMinAge(), entity.getMaxAge());
        String formattedPrice = null;
        if (entity.getMonthlyFee() != null) {
            formattedPrice = ConversionUtils.formatPrice(entity.getMonthlyFee());
        }

        // Distance will be calculated by search service
        Double distanceKm = null;

        // Campus address information
        String address = null;
        String district = null;
        String city = null;
        String campusName = null;

        if (entity.getCampus() != null) {
            Campus campus = entity.getCampus();
            campusName = campus.getName();
            address = campus.getAddressLine1();
            district = campus.getDistrict().getName();
            city = campus.getProvince().getName();
        }

        // Institution type information
        String institutionTypeName = null;
        String institutionTypeIcon = null;
        String institutionTypeColor = null;

        if (entity.getInstitutionType() != null) {
            InstitutionType type = entity.getInstitutionType();
            institutionTypeName = type.getDisplayName();
            institutionTypeIcon = type.getIconUrl();
            institutionTypeColor = type.getColorCode();
        }

        // Get card properties (properties marked as showInCard)
        List<InstitutionPropertyValueDto> cardProperties = entity.getPropertyValues() != null ?
                entity.getPropertyValues().stream()
                        .filter(pv -> pv.getProperty() != null &&
                                pv.getProperty().getShowInCard() != null &&
                                pv.getProperty().getShowInCard())
                        .map(this::mapToDto)
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(dto -> dto.getPropertyId(),
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList()) : new ArrayList<>();

        boolean hasActiveCampaigns = entity.getCampaignSchools() != null &&
                entity.getCampaignSchools().stream()
                        .anyMatch(cs -> cs.getIsActive() != null && cs.getIsActive());

        return SchoolSearchResultDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .logoUrl(entity.getLogoUrl())
                .coverImageUrl(entity.getCoverImageUrl())
                .description(ConversionUtils.truncateText(entity.getDescription(), 200))
                .institutionTypeName(institutionTypeName)
                .institutionTypeIcon(institutionTypeIcon)
                .institutionTypeColor(institutionTypeColor)
                .minAge(entity.getMinAge())
                .maxAge(entity.getMaxAge())
                .ageRange(ageRange)
                .monthlyFee(entity.getMonthlyFee())
                .formattedPrice(formattedPrice)
                .ratingAverage(entity.getRatingAverage())
                .ratingCount(entity.getRatingCount())
                .campusName(campusName)
                .address(address)
                .district(district)
                .city(city)
                .distanceKm(distanceKm) // Will be set by search service
                .highlights(new ArrayList<>()) // Will be set by search service
                .cardProperties(cardProperties)
                .activeCampaigns(new ArrayList<>()) // Will be set by campaign service
                .hasActiveCampaigns(hasActiveCampaigns)
                .isSubscribed(entity.getCampus() != null ? entity.getCampus().getIsSubscribed() : false)
                .isFavorite(false) // Will be set by user service based on current user
                .build();
    }

    public SchoolDetailDto mapToDetailDto(School entity) {
        if (entity == null) {
            return null;
        }

        SchoolDto schoolDto = mapToDto(entity);
        CampusDto campusDto = entity.getCampus() != null ? mapToDto(entity.getCampus()) : null;
        BrandDto brandDto = entity.getCampus() != null && entity.getCampus().getBrand() != null ?
                mapToDto(entity.getCampus().getBrand()) : null;

        List<InstitutionPropertyValueDto> allProperties = mapPropertyValuesToDto(entity.getPropertyValues());

        return SchoolDetailDto.builder()
                .school(schoolDto)
                .campus(campusDto)
                .brand(brandDto)
                .allProperties(allProperties)
                // pricings, campaigns, and statistics will be set by respective services
                .pricings(new ArrayList<>())
                .activeCampaigns(new ArrayList<>())
                .statistics(null)
                .build();
    }

    public School mapToEntity(SchoolCreateDto dto) {
        if (dto == null) {
            return null;
        }

        School entity = new School();
        entity.setName(dto.getName());
        entity.setSlug(ConversionUtils.generateSlug(dto.getName()));
        entity.setDescription(dto.getDescription());
        entity.setLogoUrl(dto.getLogoUrl());
        entity.setCoverImageUrl(dto.getCoverImageUrl());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setExtension(dto.getExtension());
        entity.setMinAge(dto.getMinAge());
        entity.setMaxAge(dto.getMaxAge());
        entity.setCapacity(dto.getCapacity());
        entity.setCurrentStudentCount(ConversionUtils.defaultIfNull(dto.getCurrentStudentCount(), 0));
        entity.setClassSizeAverage(dto.getClassSizeAverage());
        entity.setCurriculumType(dto.getCurriculumType());
        entity.setLanguageOfInstruction(dto.getLanguageOfInstruction());
        entity.setForeignLanguages(dto.getForeignLanguages());
        entity.setRegistrationFee(dto.getRegistrationFee());
        entity.setMonthlyFee(dto.getMonthlyFee());
        entity.setAnnualFee(dto.getAnnualFee());
        entity.setMetaTitle(dto.getMetaTitle());
        entity.setMetaDescription(dto.getMetaDescription());
        entity.setMetaKeywords(dto.getMetaKeywords());

        // Initialize statistics
        entity.setViewCount(0L);
        entity.setRatingAverage(0.0);
        entity.setRatingCount(0L);
        entity.setLikeCount(0L);
        entity.setPostCount(0L);

        return entity;
    }

    public void updateEntity(SchoolUpdateDto dto, School entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (StringUtils.hasText(dto.getName())) {
            entity.setName(dto.getName());
            entity.setSlug(ConversionUtils.generateSlug(dto.getName()));
        }

        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }

        if (dto.getLogoUrl() != null) {
            entity.setLogoUrl(dto.getLogoUrl());
        }

        if (dto.getCoverImageUrl() != null) {
            entity.setCoverImageUrl(dto.getCoverImageUrl());
        }

        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            entity.setPhone(dto.getPhone());
        }

        if (dto.getExtension() != null) {
            entity.setExtension(dto.getExtension());
        }

        if (dto.getMinAge() != null) {
            entity.setMinAge(dto.getMinAge());
        }

        if (dto.getMaxAge() != null) {
            entity.setMaxAge(dto.getMaxAge());
        }

        if (dto.getCapacity() != null) {
            entity.setCapacity(dto.getCapacity());
        }

        if (dto.getCurrentStudentCount() != null) {
            entity.setCurrentStudentCount(dto.getCurrentStudentCount());
        }

        if (dto.getClassSizeAverage() != null) {
            entity.setClassSizeAverage(dto.getClassSizeAverage());
        }

        if (dto.getCurriculumType() != null) {
            entity.setCurriculumType(dto.getCurriculumType());
        }

        if (dto.getLanguageOfInstruction() != null) {
            entity.setLanguageOfInstruction(dto.getLanguageOfInstruction());
        }

        if (dto.getForeignLanguages() != null) {
            entity.setForeignLanguages(dto.getForeignLanguages());
        }

        if (dto.getRegistrationFee() != null) {
            entity.setRegistrationFee(dto.getRegistrationFee());
        }

        if (dto.getMonthlyFee() != null) {
            entity.setMonthlyFee(dto.getMonthlyFee());
        }

        if (dto.getAnnualFee() != null) {
            entity.setAnnualFee(dto.getAnnualFee());
        }

        if (dto.getMetaTitle() != null) {
            entity.setMetaTitle(dto.getMetaTitle());
        }

        if (dto.getMetaDescription() != null) {
            entity.setMetaDescription(dto.getMetaDescription());
        }

        if (dto.getMetaKeywords() != null) {
            entity.setMetaKeywords(dto.getMetaKeywords());
        }
    }

    public List<SchoolDto> mapSchoolsToDto(Set<School> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .filter(school -> school.getIsActive() != null && school.getIsActive())
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(SchoolDto::getName,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public List<SchoolSummaryDto> mapSchoolsToSummaryDto(Set<School> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .filter(school -> school.getIsActive() != null && school.getIsActive())
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(SchoolSummaryDto::getName,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public List<SchoolSearchResultDto> mapSchoolsToSearchResultDto(List<School> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .filter(school -> school.getIsActive() != null && school.getIsActive())
                .map(this::mapToSearchResultDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ================== INSTITUTION FAVORITES CONVERSION ==================

    public InstitutionFavoritesDto mapToFavoritesDto(Long userId,
                                                     Set<School> favoriteSchools,
                                                     Set<Campus> favoriteCampuses,
                                                     Set<Brand> favoriteBrands) {

        List<SchoolSummaryDto> schoolSummaries = favoriteSchools != null ?
                mapSchoolsToSummaryDto(favoriteSchools) : new ArrayList<>();

        List<CampusSummaryDto> campusSummaries = favoriteCampuses != null ?
                mapCampusesToSummaryDto(favoriteCampuses) : new ArrayList<>();

        List<BrandSummaryDto> brandSummaries = favoriteBrands != null ?
                mapBrandsToSummaryDto(favoriteBrands) : new ArrayList<>();

        int totalFavorites = schoolSummaries.size() + campusSummaries.size() + brandSummaries.size();

        return InstitutionFavoritesDto.builder()
                .userId(userId)
                .favoriteSchools(schoolSummaries)
                .favoriteCampuses(campusSummaries)
                .favoriteBrands(brandSummaries)
                .totalFavorites(totalFavorites)
                .build();
    }

    // ================== INSTITUTION COMPARISON CONVERSION ==================

    public InstitutionComparisonDto mapToComparisonDto(List<School> schools,
                                                       List<String> comparisonCategories,
                                                       Map<String, Map<Long, Object>> comparisonData,
                                                       List<String> recommendations) {

        List<SchoolDto> schoolDtos = schools != null ?
                schools.stream()
                        .filter(Objects::nonNull)
                        .map(this::mapToDto)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) : new ArrayList<>();

        return InstitutionComparisonDto.builder()
                .schools(schoolDtos)
                .comparisonCategories(ConversionUtils.defaultIfNull(comparisonCategories, new ArrayList<>()))
                .comparisonData(ConversionUtils.defaultIfNull(comparisonData, new HashMap<>()))
                .recommendations(ConversionUtils.defaultIfNull(recommendations, new ArrayList<>()))
                .build();
    }

    // ================== BULK OPERATION RESULT CONVERSION ==================

    public BulkOperationResultDto mapToBulkResultDto(Boolean success,
                                                     Integer totalRecords,
                                                     Integer successfulOperations,
                                                     Integer failedOperations,
                                                     List<String> errors,
                                                     List<String> warnings,
                                                     String operationId) {

        return BulkOperationResultDto.builder()
                .success(ConversionUtils.defaultIfNull(success, false))
                .totalRecords(ConversionUtils.defaultIfNull(totalRecords, 0))
                .successfulOperations(ConversionUtils.defaultIfNull(successfulOperations, 0))
                .failedOperations(ConversionUtils.defaultIfNull(failedOperations, 0))
                .errors(ConversionUtils.defaultIfNull(errors, new ArrayList<>()))
                .warnings(ConversionUtils.defaultIfNull(warnings, new ArrayList<>()))
                .operationId(operationId)
                .operationDate(java.time.LocalDateTime.now())
                .build();
    }

    // ================== VALIDATION & HELPER METHODS ==================

    /**
     * Validates if a school entity has all required fields for DTO conversion
     */
    public boolean isValidSchoolForConversion(School school) {
        return school != null &&
                StringUtils.hasText(school.getName()) &&
                school.getCampus() != null &&
                school.getInstitutionType() != null;
    }

    /**
     * Validates if a campus entity has all required fields for DTO conversion
     */
    public boolean isValidCampusForConversion(Campus campus) {
        return campus != null &&
                StringUtils.hasText(campus.getName()) &&
                StringUtils.hasText(campus.getProvince().getName());
    }

    /**
     * Validates if a brand entity has all required fields for DTO conversion
     */
    public boolean isValidBrandForConversion(Brand brand) {
        return brand != null &&
                StringUtils.hasText(brand.getName());
    }

    /**
     * Calculates school capacity utilization percentage
     */
    public Double calculateCapacityUtilization(School school) {
        if (school == null || school.getCapacity() == null ||
                school.getCurrentStudentCount() == null || school.getCapacity() == 0) {
            return null;
        }

        return ConversionUtils.calculatePercentage(
                school.getCurrentStudentCount().longValue(),
                school.getCapacity().longValue()
        );
    }

    /**
     * Determines if school is accepting new students based on capacity
     */
    public Boolean isAcceptingNewStudents(School school) {
        if (school == null || school.getCapacity() == null ||
                school.getCurrentStudentCount() == null) {
            return true; // Assume accepting if data not available
        }

        return school.getCurrentStudentCount() < school.getCapacity();
    }

    /**
     * Generates school display summary for search results
     */
    public String generateSchoolSummary(School school) {
        if (school == null) {
            return null;
        }

        StringBuilder summary = new StringBuilder();

        if (school.getInstitutionType() != null) {
            summary.append(school.getInstitutionType().getDisplayName());
        }

        String ageRange = ConversionUtils.formatAgeRange(school.getMinAge(), school.getMaxAge());
        if (StringUtils.hasText(ageRange)) {
            if (summary.length() > 0) summary.append(" • ");
            summary.append(ageRange);
        }

        if (school.getCampus() != null && school.getCampus().getDistrict()!= null) {
            if (summary.length() > 0) summary.append(" • ");
            summary.append(school.getCampus().getDistrict());
        }

        return summary.toString();
    }

    /**
     * Creates a minimal school DTO for performance-critical operations
     */
    public SchoolSummaryDto mapToMinimalSummaryDto(School entity) {
        if (entity == null) {
            return null;
        }

        return SchoolSummaryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .logoUrl(entity.getLogoUrl())
                .monthlyFee(entity.getMonthlyFee())
                .ratingAverage(entity.getRatingAverage())
                .ratingCount(entity.getRatingCount())
                .hasActiveCampaigns(false) // Will be set separately if needed
                .build();
    }

    public SchoolSummaryDto mapSchoolToSummaryDto(School school) {
        if (school == null) {
            return null;
        }
        return SchoolSummaryDto.builder()
                .id(school.getId())
                .name(school.getName())
                .slug(school.getSlug())
                .logoUrl(school.getLogoUrl())
                .institutionTypeName(school.getInstitutionType() != null ?
                        school.getInstitutionType().getDisplayName() : null)
                .minAge(school.getMinAge())
                .maxAge(school.getMaxAge())
                .monthlyFee(school.getMonthlyFee())
                .ratingAverage(school.getRatingAverage())
                .ratingCount(school.getRatingCount())
                .hasActiveCampaigns(false) // Default to false; can be updated later
                .build();
    }

    public BrandSummaryDto mapBrandToSummaryDto(Brand brand) {
        if (brand == null) {
            return null;
        }

        long campusCount = ConversionUtils.safeSize(brand.getCampuses().stream().toList());
        long schoolCount = brand.getCampuses() != null ?
                brand.getCampuses().stream()
                        .mapToLong(campus -> ConversionUtils.safeSize(campus.getSchools().stream().toList()))
                        .sum() : 0L;

        return BrandSummaryDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .logoUrl(brand.getLogoUrl())
                .ratingAverage(brand.getRatingAverage())
                .campusCount((int) campusCount)
                .schoolCount((int) schoolCount)
                .build();
    }

    public CampusSummaryDto mapCampusToSummaryDto(Campus campus) {
        if (campus == null) {
            return null;
        }

        long schoolCount = ConversionUtils.safeSize(campus.getSchools().stream().toList());

        return CampusSummaryDto.builder()
                .id(campus.getId())
                .name(campus.getName())
                .slug(campus.getSlug())
                .logoUrl(campus.getLogoUrl())
                .province(mapToSummaryDto(campus.getProvince()))
                .district(mapToSummaryDto(campus.getDistrict()))
                .ratingAverage(campus.getRatingAverage())
                .schoolCount(schoolCount)
                .isSubscribed(campus.getIsSubscribed())
                .build();
    }


    public District mapToSummaryEntity(DistrictSummaryDto district) {
        if (district == null) {
            return null;
        }

        District entity = new District();
        entity.setId(district.getId());
        entity.setName(district.getName());
        entity.setCode(district.getCode());
        entity.setDistrictType(district.getDistrictType());
        entity.setIsCentral(district.getIsCentral());
        entity.setSchoolCount(district.getSchoolCount());
        entity.setSocioeconomicLevel(district.getSocioeconomicLevel());

        return entity;
    }

    public Province mapToSummaryEntity(ProvinceSummaryDto province) {
        if (province == null) {
            return null;
        }

        Province entity = new Province();
        entity.setId(province.getId());
        entity.setName(province.getName());
        entity.setCode(province.getCode());
        entity.setPlateCode(province.getPlateCode());
        entity.setIsMetropolitan(province.getIsMetropolitan());
        entity.setSchoolCount(province.getSchoolCount());

        return entity;
    }

    public Country mapToSummaryEntity(CountrySummaryDto country) {
        if (country == null) {
            return null;
        }

        Country entity = new Country();
        entity.setId(country.getId());
        entity.setName(country.getName());
        entity.setIsoCode2(country.getIsoCode2());
        entity.setFlagEmoji(country.getFlagEmoji());
        entity.setPhoneCode(country.getPhoneCode());
        entity.setIsSupported(country.getIsSupported());

        return entity;
    }


// ================== PROPERTY GROUP TYPE CONVERSIONS ==================

    public PropertyGroupTypeDto mapPropertyGroupTypeToDto(PropertyGroupType entity) {
        if (entity == null) {
            return null;
        }

        return PropertyGroupTypeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .institutionTypeId(entity.getInstitutionType() != null ? entity.getInstitutionType().getId() : null)
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .propertyTypes(new ArrayList<>()) // PropertyType'lar service katmanında set edilecek
                .build();
    }

    public List<PropertyGroupTypeDto> mapPropertyGroupTypesToDto(List<PropertyGroupType> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .filter(pgt -> pgt.getIsActive() != null && pgt.getIsActive())
                .map(this::mapPropertyGroupTypeToDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(PropertyGroupTypeDto::getName,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    // ================== PROPERTY TYPE CONVERSIONS ==================

    public PropertyTypeDto mapPropertyTypeToDto(PropertyType entity) {
        if (entity == null) {
            return null;
        }

        return PropertyTypeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .propertyGroupTypeId(entity.getPropertyGroupType() != null ? entity.getPropertyGroupType().getId() : null)
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<PropertyTypeDto> mapPropertyTypesToDto(List<PropertyType> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .filter(pt -> pt.getIsActive() != null && pt.getIsActive())
                .map(this::mapPropertyTypeToDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(PropertyTypeDto::getName,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    // ================== INSTITUTION TYPE GROUP CONVERSIONS ==================

    public InstitutionTypeGroupDto mapInstitutionTypeGroupToDto(InstitutionTypeGroup entity) {
        if (entity == null) {
            return null;
        }

        return InstitutionTypeGroupDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(entity.getDisplayName())
                .description(entity.getDescription())
                .iconUrl(entity.getIconUrl())
                .colorCode(entity.getColorCode())
                .sortOrder(entity.getSortOrder())
                .defaultProperties(entity.getDefaultProperties())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<InstitutionTypeGroupDto> mapInstitutionTypeGroupsToDto(List<InstitutionTypeGroup> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .filter(itg -> itg.getIsActive() != null && itg.getIsActive())
                .map(this::mapInstitutionTypeGroupToDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(InstitutionTypeGroupDto::getSortOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    // ================== INSTITUTION TYPE LIST CONVERSIONS ==================

    public InstitutionTypeListDto mapToInstitutionTypeListDto(InstitutionType institutionType,
                                                              List<PropertyGroupTypeDto> propertyGroupTypeDtos) {
        if (institutionType == null) {
            return null;
        }

        return InstitutionTypeListDto.builder()
                .institutionTypeDto(mapToDto(institutionType))
                .propertyGroupTypeDtos(propertyGroupTypeDtos != null ? propertyGroupTypeDtos : new ArrayList<>())
                .build();
    }

    public List<InstitutionTypeListDto> mapToInstitutionTypeListDtos(List<InstitutionType> institutionTypes,
                                                                     Map<Long, List<PropertyGroupTypeDto>> propertyGroupsByInstitutionType) {
        if (institutionTypes == null || institutionTypes.isEmpty()) {
            return new ArrayList<>();
        }

        return institutionTypes.stream()
                .filter(Objects::nonNull)
                .map(institutionType -> {
                    List<PropertyGroupTypeDto> propertyGroups = propertyGroupsByInstitutionType != null ?
                            propertyGroupsByInstitutionType.getOrDefault(institutionType.getId(), new ArrayList<>()) :
                            new ArrayList<>();

                    return mapToInstitutionTypeListDto(institutionType, propertyGroups);
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(dto -> dto.getInstitutionTypeDto().getSortOrder(),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    // ================== HELPER METHODS FOR PROPERTY STRUCTURE ==================

    /**
     * PropertyGroupType'a PropertyType'ları set eder
     */
    public void setPropertyTypesToGroup(PropertyGroupTypeDto propertyGroupDto, List<PropertyTypeDto> propertyTypes) {
        if (propertyGroupDto != null && propertyTypes != null) {
            propertyGroupDto.setPropertyTypes(propertyTypes);
        }
    }

    /**
     * PropertyType'ları PropertyGroupType ID'ye göre gruplar
     */
    public Map<Long, List<PropertyTypeDto>> groupPropertyTypesByGroupId(List<PropertyTypeDto> propertyTypes) {
        if (propertyTypes == null || propertyTypes.isEmpty()) {
            return new HashMap<>();
        }

        return propertyTypes.stream()
                .filter(Objects::nonNull)
                .filter(pt -> pt.getPropertyGroupTypeId() != null)
                .collect(Collectors.groupingBy(PropertyTypeDto::getPropertyGroupTypeId));
    }

    /**
     * PropertyGroupType'ları InstitutionType ID'ye göre gruplar
     */
    public Map<Long, List<PropertyGroupTypeDto>> groupPropertyGroupsByInstitutionTypeId(List<PropertyGroupTypeDto> propertyGroups) {
        if (propertyGroups == null || propertyGroups.isEmpty()) {
            return new HashMap<>();
        }

        return propertyGroups.stream()
                .filter(Objects::nonNull)
                .filter(pgt -> pgt.getInstitutionTypeId() != null)
                .collect(Collectors.groupingBy(PropertyGroupTypeDto::getInstitutionTypeId));
    }


}