package com.genixo.education.search.service;




import com.genixo.education.search.dto.institution.*;
import com.genixo.education.search.dto.location.*;
import com.genixo.education.search.dto.user.*;
import com.genixo.education.search.entity.user.UserInstitutionAccess;
import com.genixo.education.search.enumaration.PropertyDataType;
import com.genixo.education.search.entity.institution.*;
import com.genixo.education.search.entity.location.*;
import com.genixo.education.search.entity.user.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Universal converter service for Entity ↔ DTO conversions
 * Handles all conversion operations for the application
 */
@Service
public class ConverterService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ========================= USER CONVERSIONS =========================

    public UserDto toUserDto(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(getFullName(user.getFirstName(), user.getLastName()))
                .userType(user.getUserType())
                .isEmailVerified(user.getIsEmailVerified())
                .isPhoneVerified(user.getIsPhoneVerified())
                .lastLoginAt(user.getLastLoginAt())
                .profileImageUrl(user.getProfileImageUrl())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .country(user.getCountry() != null ? user.getCountry().getName() : null)
                .province(user.getProvince() != null ? user.getProvince().getName() : null)
                .district(user.getDistrict() != null ? user.getDistrict().getName() : null)
                .neighborhood(user.getNeighborhood() != null ? user.getNeighborhood().getName() : null)
                .addressLine1(user.getAddressLine1())
                .addressLine2(user.getAddressLine2())
                .postalCode(user.getPostalCode())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .build();
    }

    public UserSummaryDto toUserSummaryDto(User user) {
        if (user == null) return null;

        return UserSummaryDto.builder()
                .id(user.getId())
                .fullName(getFullName(user.getFirstName(), user.getLastName()))
                .email(user.getEmail())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .profileImageUrl(user.getProfileImageUrl())
                .isActive(user.getIsActive())
                .build();
    }

    public UserListDto toUserListDto(User user) {
        if (user == null) return null;

        return UserListDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .fullName(getFullName(user.getFirstName(), user.getLastName()))
                .userType(user.getUserType())
                .isActive(user.getIsActive())
                .isEmailVerified(user.getIsEmailVerified())
                .isPhoneVerified(user.getIsPhoneVerified())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public UserProfileDto toUserProfileDto(User user) {
        if (user == null) return null;

        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profileImageUrl(user.getProfileImageUrl())
                .countryId(user.getCountry() != null ? user.getCountry().getId() : null)
                .countryName(user.getCountry() != null ? user.getCountry().getName() : null)
                .provinceId(user.getProvince() != null ? user.getProvince().getId() : null)
                .provinceName(user.getProvince() != null ? user.getProvince().getName() : null)
                .districtId(user.getDistrict() != null ? user.getDistrict().getId() : null)
                .districtName(user.getDistrict() != null ? user.getDistrict().getName() : null)
                .neighborhoodId(user.getNeighborhood() != null ? user.getNeighborhood().getId() : null)
                .neighborhoodName(user.getNeighborhood() != null ? user.getNeighborhood().getName() : null)
                .addressLine1(user.getAddressLine1())
                .addressLine2(user.getAddressLine2())
                .postalCode(user.getPostalCode())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .build();
    }






    public UserInstitutionAccessDto toUserInstitutionAccessDto(UserInstitutionAccess access) {
        if (access == null) return null;

        return UserInstitutionAccessDto.builder()
                .id(access.getId())
                .userId(access.getUser().getId())
                .userFullName(getFullName(access.getUser().getFirstName(), access.getUser().getLastName()))
                .accessType(access.getAccessType())
                .entityId(access.getEntityId())
                .grantedAt(access.getGrantedAt())
                .expiresAt(access.getExpiresAt())
                .isActive(access.getIsActive())
                .build();
    }

    // ========================= LOCATION CONVERSIONS =========================

    public CountryDto toCountryDto(Country country) {
        if (country == null) return null;

        return CountryDto.builder()
                .id(country.getId())
                .name(country.getName())
                .nameEn(country.getNameEn())
                .isoCode2(country.getIsoCode2())
                .isoCode3(country.getIsoCode3())
                .phoneCode(country.getPhoneCode())
                .currencyCode(country.getCurrencyCode())
                .currencySymbol(country.getCurrencySymbol())
                .flagEmoji(country.getFlagEmoji())
                .latitude(country.getLatitude())
                .longitude(country.getLongitude())
                .timezone(country.getTimezone())
                .isSupported(country.getIsSupported())
                .sortOrder(country.getSortOrder())
                .isActive(country.getIsActive())
                .createdAt(country.getCreatedAt())
                .build();
    }

    public CountrySummaryDto toCountrySummaryDto(Country country) {
        if (country == null) return null;

        return CountrySummaryDto.builder()
                .id(country.getId())
                .name(country.getName())
                .isoCode2(country.getIsoCode2())
                .flagEmoji(country.getFlagEmoji())
                .phoneCode(country.getPhoneCode())
                .isSupported(country.getIsSupported())
                .build();
    }

    public ProvinceDto toProvinceDto(Province province) {
        if (province == null) return null;

        return ProvinceDto.builder()
                .id(province.getId())
                .name(province.getName())
                .nameEn(province.getNameEn())
                .code(province.getCode())
                .plateCode(province.getPlateCode())
                .region(province.getRegion())
                .areaCode(province.getAreaCode())
                .postalCodePrefix(province.getPostalCodePrefix())
                .latitude(province.getLatitude())
                .longitude(province.getLongitude())
                .population(province.getPopulation())
                .areaKm2(province.getAreaKm2())
                .elevationM(province.getElevationM())
                .timeZone(province.getTimeZone())
                .isMetropolitan(province.getIsMetropolitan())
                .sortOrder(province.getSortOrder())
                .slug(province.getSlug())
                .description(province.getDescription())
                .gdpPerCapita(province.getGdpPerCapita())
                .unemploymentRate(province.getUnemploymentRate())
                .educationIndex(province.getEducationIndex())
                .hasAirport(province.getHasAirport())
                .hasUniversity(province.getHasUniversity())
                .hasMetro(province.getHasMetro())
                .trafficDensity(province.getTrafficDensity())
                .schoolCount(province.getSchoolCount())
                .studentCount(province.getStudentCount())
                .teacherCount(province.getTeacherCount())
                .literacyRate(province.getLiteracyRate())
                .country(toCountrySummaryDto(province.getCountry()))
                .isActive(province.getIsActive())
                .createdAt(province.getCreatedAt())
                .build();
    }

    public ProvinceSummaryDto toProvinceSummaryDto(Province province) {
        if (province == null) return null;

        return ProvinceSummaryDto.builder()
                .id(province.getId())
                .name(province.getName())
                .code(province.getCode())
                .plateCode(province.getPlateCode())
                .isMetropolitan(province.getIsMetropolitan())
                .schoolCount(province.getSchoolCount())
                .build();
    }

    public DistrictDto toDistrictDto(District district) {
        if (district == null) return null;

        return DistrictDto.builder()
                .id(district.getId())
                .name(district.getName())
                .nameEn(district.getNameEn())
                .code(district.getCode())
                .districtType(district.getDistrictType())
                .postalCode(district.getPostalCode())
                .latitude(district.getLatitude())
                .longitude(district.getLongitude())
                .population(district.getPopulation())
                .areaKm2(district.getAreaKm2())
                .elevationM(district.getElevationM())
                .densityPerKm2(district.getDensityPerKm2())
                .isCentral(district.getIsCentral())
                .isCoastal(district.getIsCoastal())
                .sortOrder(district.getSortOrder())
                .slug(district.getSlug())
                .description(district.getDescription())
                .averageIncome(district.getAverageIncome())
                .propertyPriceIndex(district.getPropertyPriceIndex())
                .costOfLivingIndex(district.getCostOfLivingIndex())
                .socioeconomicLevel(district.getSocioeconomicLevel())
                .hasMetroStation(district.getHasMetroStation())
                .hasBusTerminal(district.getHasBusTerminal())
                .hasTrainStation(district.getHasTrainStation())
                .distanceToAirportKm(district.getDistanceToAirportKm())
                .distanceToCityCenterKm(district.getDistanceToCityCenterKm())
                .publicTransportScore(district.getPublicTransportScore())
                .trafficCongestionLevel(district.getTrafficCongestionLevel())
                .schoolCount(district.getSchoolCount())
                .privateSchoolCount(district.getPrivateSchoolCount())
                .publicSchoolCount(district.getPublicSchoolCount())
                .universityCount(district.getUniversityCount())
                .educationQualityIndex(district.getEducationQualityIndex())
                .literacyRate(district.getLiteracyRate())
                .youthPopulationPercentage(district.getYouthPopulationPercentage())
                .elderlyPopulationPercentage(district.getElderlyPopulationPercentage())
                .averageFamilySize(district.getAverageFamilySize())
                .birthRate(district.getBirthRate())
                .hospitalCount(district.getHospitalCount())
                .shoppingMallCount(district.getShoppingMallCount())
                .parkCount(district.getParkCount())
                .sportsFacilityCount(district.getSportsFacilityCount())
                .safetyIndex(district.getSafetyIndex())
                .airQualityIndex(district.getAirQualityIndex())
                .noiseLevel(district.getNoiseLevel())
                .climateType(district.getClimateType())
                .averageTemperature(district.getAverageTemperature())
                .annualRainfallMm(district.getAnnualRainfallMm())
                .humidityPercentage(district.getHumidityPercentage())
                .province(toProvinceSummaryDto(district.getProvince()))
                .isActive(district.getIsActive())
                .createdAt(district.getCreatedAt())
                .build();
    }

    public DistrictSummaryDto toDistrictSummaryDto(District district) {
        if (district == null) return null;

        return DistrictSummaryDto.builder()
                .id(district.getId())
                .name(district.getName())
                .code(district.getCode())
                .districtType(district.getDistrictType())
                .isCentral(district.getIsCentral())
                .schoolCount(district.getSchoolCount())
                .socioeconomicLevel(district.getSocioeconomicLevel())
                .build();
    }

    public NeighborhoodDto toNeighborhoodDto(Neighborhood neighborhood) {
        if (neighborhood == null) return null;

        return NeighborhoodDto.builder()
                .id(neighborhood.getId())
                .name(neighborhood.getName())
                .nameEn(neighborhood.getNameEn())
                .code(neighborhood.getCode())
                .neighborhoodType(neighborhood.getNeighborhoodType())
                .postalCode(neighborhood.getPostalCode())
                .latitude(neighborhood.getLatitude())
                .longitude(neighborhood.getLongitude())
                .population(neighborhood.getPopulation())
                .areaKm2(neighborhood.getAreaKm2())
                .elevationM(neighborhood.getElevationM())
                .densityPerKm2(neighborhood.getDensityPerKm2())
                .sortOrder(neighborhood.getSortOrder())
                .slug(neighborhood.getSlug())
                .description(neighborhood.getDescription())
                .housingType(neighborhood.getHousingType())
                .developmentLevel(neighborhood.getDevelopmentLevel())
                .isGatedCommunity(neighborhood.getIsGatedCommunity())
                .isHistorical(neighborhood.getIsHistorical())
                .isCommercialCenter(neighborhood.getIsCommercialCenter())
                .isResidential(neighborhood.getIsResidential())
                .isIndustrial(neighborhood.getIsIndustrial())
                .averageRentPrice(neighborhood.getAverageRentPrice())
                .averagePropertyPrice(neighborhood.getAveragePropertyPrice())
                .propertyPricePerM2(neighborhood.getPropertyPricePerM2())
                .incomeLevel(neighborhood.getIncomeLevel())
                .metroAccessibilityMinutes(neighborhood.getMetroAccessibilityMinutes())
                .busAccessibilityMinutes(neighborhood.getBusAccessibilityMinutes())
                .mainRoadAccessibilityMinutes(neighborhood.getMainRoadAccessibilityMinutes())
                .highwayAccessibilityMinutes(neighborhood.getHighwayAccessibilityMinutes())
                .publicTransportFrequency(neighborhood.getPublicTransportFrequency())
                .parkingAvailability(neighborhood.getParkingAvailability())
                .walkabilityScore(neighborhood.getWalkabilityScore())
                .hasMetroStation(neighborhood.getHasMetroStation())
                .hasHospital(neighborhood.getHasHospital())
                .hasShoppingCenter(neighborhood.getHasShoppingCenter())
                .hasPark(neighborhood.getHasPark())
                .hasLibrary(neighborhood.getHasLibrary())
                .hasSportsFacility(neighborhood.getHasSportsFacility())
                .hasCulturalCenter(neighborhood.getHasCulturalCenter())
                .hasKindergarten(neighborhood.getHasKindergarten())
                .restaurantCount(neighborhood.getRestaurantCount())
                .cafeCount(neighborhood.getCafeCount())
                .bankCount(neighborhood.getBankCount())
                .pharmacyCount(neighborhood.getPharmacyCount())
                .supermarketCount(neighborhood.getSupermarketCount())
                .schoolCount(neighborhood.getSchoolCount())
                .privateSchoolCount(neighborhood.getPrivateSchoolCount())
                .publicSchoolCount(neighborhood.getPublicSchoolCount())
                .preschoolCount(neighborhood.getPreschoolCount())
                .educationAccessibilityScore(neighborhood.getEducationAccessibilityScore())
                .schoolQualityIndex(neighborhood.getSchoolQualityIndex())
                .safetyScore(neighborhood.getSafetyScore())
                .noiseLevel(neighborhood.getNoiseLevel())
                .airQualityScore(neighborhood.getAirQualityScore())
                .greenSpacePercentage(neighborhood.getGreenSpacePercentage())
                .cleanlinessScore(neighborhood.getCleanlinessScore())
                .socialLifeScore(neighborhood.getSocialLifeScore())
                .averageAge(neighborhood.getAverageAge())
                .familyWithChildrenPercentage(neighborhood.getFamilyWithChildrenPercentage())
                .youngProfessionalPercentage(neighborhood.getYoungProfessionalPercentage())
                .elderlyPercentage(neighborhood.getElderlyPercentage())
                .studentPercentage(neighborhood.getStudentPercentage())
                .propertyDemandLevel(neighborhood.getPropertyDemandLevel())
                .developmentPotential(neighborhood.getDevelopmentPotential())
                .investmentAttractiveness(neighborhood.getInvestmentAttractiveness())
                .schoolPreferenceScore(neighborhood.getSchoolPreferenceScore())
                .commuteToBusinessDistrictsMinutes(neighborhood.getCommuteToBusinessDistrictsMinutes())
                .familyFriendlinessScore(neighborhood.getFamilyFriendlinessScore())
                .district(toDistrictSummaryDto(neighborhood.getDistrict()))
                .isActive(neighborhood.getIsActive())
                .createdAt(neighborhood.getCreatedAt())
                .build();
    }

    public NeighborhoodSummaryDto toNeighborhoodSummaryDto(Neighborhood neighborhood) {
        if (neighborhood == null) return null;

        return NeighborhoodSummaryDto.builder()
                .id(neighborhood.getId())
                .name(neighborhood.getName())
                .code(neighborhood.getCode())
                .neighborhoodType(neighborhood.getNeighborhoodType())
                .schoolCount(neighborhood.getSchoolCount())
                .incomeLevel(neighborhood.getIncomeLevel())
                .schoolPreferenceScore(neighborhood.getSchoolPreferenceScore())
                .build();
    }

    public LocationHierarchyDto toLocationHierarchyDto(Country country, Province province, District district, Neighborhood neighborhood) {
        return LocationHierarchyDto.builder()
                .country(toCountrySummaryDto(country))
                .province(toProvinceSummaryDto(province))
                .district(toDistrictSummaryDto(district))
                .neighborhood(toNeighborhoodSummaryDto(neighborhood))
                .build();
    }

    // ========================= INSTITUTION CONVERSIONS =========================

    public BrandDto toBrandDto(Brand brand) {
        if (brand == null) return null;

        return BrandDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .description(brand.getDescription())
                .logoUrl(brand.getLogoUrl())
                .coverImageUrl(brand.getCoverImageUrl())
                .websiteUrl(brand.getWebsiteUrl())
                .email(brand.getEmail())
                .phone(brand.getPhone())
                .foundedYear(brand.getFoundedYear())
                .facebookUrl(brand.getFacebookUrl())
                .twitterUrl(brand.getTwitterUrl())
                .instagramUrl(brand.getInstagramUrl())
                .linkedinUrl(brand.getLinkedinUrl())
                .youtubeUrl(brand.getYoutubeUrl())
                .metaTitle(brand.getMetaTitle())
                .metaDescription(brand.getMetaDescription())
                .metaKeywords(brand.getMetaKeywords())
                .viewCount(brand.getViewCount())
                .ratingAverage(brand.getRatingAverage())
                .ratingCount(brand.getRatingCount())
                .isActive(brand.getIsActive())
                .createdAt(brand.getCreatedAt())
                .build();
    }

    public BrandSummaryDto toBrandSummaryDto(Brand brand) {
        if (brand == null) return null;

        return BrandSummaryDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .logoUrl(brand.getLogoUrl())
                .ratingAverage(brand.getRatingAverage())
                .campusCount((long) (brand.getCampuses() != null ? brand.getCampuses().size() : 0))
                .build();
    }

    public CampusDto toCampusDto(Campus campus) {
        if (campus == null) return null;

        return CampusDto.builder()
                .id(campus.getId())
                .name(campus.getName())
                .slug(campus.getSlug())
                .description(campus.getDescription())
                .logoUrl(campus.getLogoUrl())
                .coverImageUrl(campus.getCoverImageUrl())
                .email(campus.getEmail())
                .phone(campus.getPhone())
                .fax(campus.getFax())
                .websiteUrl(campus.getWebsiteUrl())
                .addressLine1(campus.getAddressLine1())
                .addressLine2(campus.getAddressLine2())
                .district(toDistrictSummaryDto(campus.getDistrict()))
                .province(toProvinceSummaryDto(campus.getProvince()))
                .postalCode(campus.getPostalCode())
                .country(toCountrySummaryDto(campus.getCountry()))
                .latitude(campus.getLatitude())
                .longitude(campus.getLongitude())
                .location(toLocationHierarchyDto(
                        campus.getCountry(),
                        campus.getProvince(),
                        campus.getDistrict(),
                        campus.getNeighborhood()
                ))
                .facebookUrl(campus.getFacebookUrl())
                .twitterUrl(campus.getTwitterUrl())
                .instagramUrl(campus.getInstagramUrl())
                .linkedinUrl(campus.getLinkedinUrl())
                .youtubeUrl(campus.getYoutubeUrl())
                .metaTitle(campus.getMetaTitle())
                .metaDescription(campus.getMetaDescription())
                .metaKeywords(campus.getMetaKeywords())
                .viewCount(campus.getViewCount())
                .ratingAverage(campus.getRatingAverage())
                .ratingCount(campus.getRatingCount())
                .establishedYear(campus.getEstablishedYear())
                .isSubscribed(campus.getIsSubscribed())
                .brand(toBrandSummaryDto(campus.getBrand()))
                .isActive(campus.getIsActive())
                .createdAt(campus.getCreatedAt())
                .build();
    }

    public CampusSummaryDto toCampusSummaryDto(Campus campus) {
        if (campus == null) return null;

        return CampusSummaryDto.builder()
                .id(campus.getId())
                .name(campus.getName())
                .slug(campus.getSlug())
                .logoUrl(campus.getLogoUrl())
                .province(toProvinceSummaryDto(campus.getProvince()))
                .district(toDistrictSummaryDto(campus.getDistrict()))
                .ratingAverage(campus.getRatingAverage())
                .schoolCount((long) (campus.getSchools() != null ? campus.getSchools().size() : 0))
                .isSubscribed(campus.getIsSubscribed())
                .build();
    }

    public SchoolDto toSchoolDto(School school) {
        if (school == null) return null;

        return SchoolDto.builder()
                .id(school.getId())
                .name(school.getName())
                .slug(school.getSlug())
                .description(school.getDescription())
                .logoUrl(school.getLogoUrl())
                .coverImageUrl(school.getCoverImageUrl())
                .email(school.getEmail())
                .phone(school.getPhone())
                .extension(school.getExtension())
                .minAge(school.getMinAge())
                .maxAge(school.getMaxAge())
                .capacity(school.getCapacity())
                .currentStudentCount(school.getCurrentStudentCount())
                .classSizeAverage(school.getClassSizeAverage())
                .curriculumType(school.getCurriculumType())
                .languageOfInstruction(school.getLanguageOfInstruction())
                .foreignLanguages(school.getForeignLanguages())
                .registrationFee(school.getRegistrationFee())
                .monthlyFee(school.getMonthlyFee())
                .annualFee(school.getAnnualFee())
                .metaTitle(school.getMetaTitle())
                .metaDescription(school.getMetaDescription())
                .metaKeywords(school.getMetaKeywords())
                .viewCount(school.getViewCount())
                .ratingAverage(school.getRatingAverage())
                .ratingCount(school.getRatingCount())
                .likeCount(school.getLikeCount())
                .postCount(school.getPostCount())
                .campus(toCampusSummaryDto(school.getCampus()))
                .institutionType(toInstitutionTypeDto(school.getInstitutionType()))
                .isActive(school.getIsActive())
                .createdAt(school.getCreatedAt())
                .build();
    }

    public SchoolSummaryDto toSchoolSummaryDto(School school) {
        if (school == null) return null;

        return SchoolSummaryDto.builder()
                .id(school.getId())
                .name(school.getName())
                .slug(school.getSlug())
                .logoUrl(school.getLogoUrl())
                .institutionTypeName(school.getInstitutionType() != null ? school.getInstitutionType().getDisplayName() : null)
                .minAge(school.getMinAge())
                .maxAge(school.getMaxAge())
                .monthlyFee(school.getMonthlyFee())
                .ratingAverage(school.getRatingAverage())
                .ratingCount(school.getRatingCount())
                .hasActiveCampaigns(false) // This will be set by service layer
                .build();
    }

    public InstitutionTypeDto toInstitutionTypeDto(InstitutionType institutionType) {
        if (institutionType == null) return null;

        return InstitutionTypeDto.builder()
                .id(institutionType.getId())
                .name(institutionType.getName())
                .displayName(institutionType.getDisplayName())
                .description(institutionType.getDescription())
                .iconUrl(institutionType.getIconUrl())
                .colorCode(institutionType.getColorCode())
                .sortOrder(institutionType.getSortOrder())
                .defaultProperties(institutionType.getDefaultProperties())
                .isActive(institutionType.getIsActive())
                .createdAt(institutionType.getCreatedAt())
                .build();
    }

    public InstitutionTypeSummaryDto toInstitutionTypeSummaryDto(InstitutionType institutionType) {
        if (institutionType == null) return null;

        return InstitutionTypeSummaryDto.builder()
                .id(institutionType.getId())
                .name(institutionType.getName())
                .displayName(institutionType.getDisplayName())
                .iconUrl(institutionType.getIconUrl())
                .colorCode(institutionType.getColorCode())
                .schoolCount((long) (institutionType.getSchools() != null ? institutionType.getSchools().size() : 0))
                .build();
    }

    public InstitutionPropertyDto toInstitutionPropertyDto(InstitutionProperty property) {
        if (property == null) return null;

        return InstitutionPropertyDto.builder()
                .id(property.getId())
                .name(property.getName())
                .displayName(property.getDisplayName())
                .description(property.getDescription())
                .dataType(property.getDataType())
                .isRequired(property.getIsRequired())
                .isSearchable(property.getIsSearchable())
                .isFilterable(property.getIsFilterable())
                .showInCard(property.getShowInCard())
                .showInProfile(property.getShowInProfile())
                .sortOrder(property.getSortOrder())
                .options(property.getOptions())
                .defaultValue(property.getDefaultValue())
                .minValue(property.getMinValue())
                .maxValue(property.getMaxValue())
                .minLength(property.getMinLength())
                .maxLength(property.getMaxLength())
                .regexPattern(property.getRegexPattern())
                .institutionType(toInstitutionTypeSummaryDto(property.getInstitutionType()))
                .isActive(property.getIsActive())
                .createdAt(property.getCreatedAt())
                .build();
    }

    public InstitutionPropertyValueDto toInstitutionPropertyValueDto(InstitutionPropertyValue propertyValue) {
        if (propertyValue == null) return null;

        InstitutionProperty property = propertyValue.getProperty();

        return InstitutionPropertyValueDto.builder()
                .id(propertyValue.getId())
                .propertyId(property.getId())
                .propertyName(property.getName())
                .propertyDisplayName(property.getDisplayName())
                .dataType(property.getDataType())
                .showInCard(property.getShowInCard())
                .showInProfile(property.getShowInProfile())
                .textValue(propertyValue.getTextValue())
                .numberValue(propertyValue.getNumberValue())
                .booleanValue(propertyValue.getBooleanValue())
                .dateValue(propertyValue.getDateValue() != null ? propertyValue.getDateValue().format(DATE_FORMATTER) : null)
                .datetimeValue(propertyValue.getDatetimeValue() != null ? propertyValue.getDatetimeValue().format(DATETIME_FORMATTER) : null)
                .jsonValue(propertyValue.getJsonValue())
                .fileUrl(propertyValue.getFileUrl())
                .fileName(propertyValue.getFileName())
                .displayValue(formatPropertyValue(propertyValue))
                .formattedValue(formatPropertyValueForDisplay(propertyValue))
                .build();
    }

    // ========================= LIST CONVERSIONS =========================

    public List<UserDto> toUserDtoList(List<User> users) {
        return users != null ? users.stream().map(this::toUserDto).collect(Collectors.toList()) : null;
    }

    public List<UserSummaryDto> toUserSummaryDtoList(List<User> users) {
        return users != null ? users.stream().map(this::toUserSummaryDto).collect(Collectors.toList()) : null;
    }

    public List<UserListDto> toUserListDtoList(List<User> users) {
        return users != null ? users.stream().map(this::toUserListDto).collect(Collectors.toList()) : null;
    }



    public List<CountryDto> toCountryDtoList(List<Country> countries) {
        return countries != null ? countries.stream().map(this::toCountryDto).collect(Collectors.toList()) : null;
    }

    public List<CountrySummaryDto> toCountrySummaryDtoList(List<Country> countries) {
        return countries != null ? countries.stream().map(this::toCountrySummaryDto).collect(Collectors.toList()) : null;
    }

    public List<ProvinceDto> toProvinceDtoList(List<Province> provinces) {
        return provinces != null ? provinces.stream().map(this::toProvinceDto).collect(Collectors.toList()) : null;
    }

    public List<ProvinceSummaryDto> toProvinceSummaryDtoList(List<Province> provinces) {
        return provinces != null ? provinces.stream().map(this::toProvinceSummaryDto).collect(Collectors.toList()) : null;
    }

    public List<DistrictDto> toDistrictDtoList(List<District> districts) {
        return districts != null ? districts.stream().map(this::toDistrictDto).collect(Collectors.toList()) : null;
    }

    public List<DistrictSummaryDto> toDistrictSummaryDtoList(List<District> districts) {
        return districts != null ? districts.stream().map(this::toDistrictSummaryDto).collect(Collectors.toList()) : null;
    }

    public List<NeighborhoodDto> toNeighborhoodDtoList(List<Neighborhood> neighborhoods) {
        return neighborhoods != null ? neighborhoods.stream().map(this::toNeighborhoodDto).collect(Collectors.toList()) : null;
    }

    public List<NeighborhoodSummaryDto> toNeighborhoodSummaryDtoList(List<Neighborhood> neighborhoods) {
        return neighborhoods != null ? neighborhoods.stream().map(this::toNeighborhoodSummaryDto).collect(Collectors.toList()) : null;
    }

    public List<BrandDto> toBrandDtoList(List<Brand> brands) {
        return brands != null ? brands.stream().map(this::toBrandDto).collect(Collectors.toList()) : null;
    }

    public List<BrandSummaryDto> toBrandSummaryDtoList(List<Brand> brands) {
        return brands != null ? brands.stream().map(this::toBrandSummaryDto).collect(Collectors.toList()) : null;
    }

    public List<CampusDto> toCampusDtoList(List<Campus> campuses) {
        return campuses != null ? campuses.stream().map(this::toCampusDto).collect(Collectors.toList()) : null;
    }

    public List<CampusSummaryDto> toCampusSummaryDtoList(List<Campus> campuses) {
        return campuses != null ? campuses.stream().map(this::toCampusSummaryDto).collect(Collectors.toList()) : null;
    }

    public List<SchoolDto> toSchoolDtoList(List<School> schools) {
        return schools != null ? schools.stream().map(this::toSchoolDto).collect(Collectors.toList()) : null;
    }

    public List<SchoolSummaryDto> toSchoolSummaryDtoList(List<School> schools) {
        return schools != null ? schools.stream().map(this::toSchoolSummaryDto).collect(Collectors.toList()) : null;
    }

    public List<InstitutionPropertyValueDto> toInstitutionPropertyValueDtoList(List<InstitutionPropertyValue> propertyValues) {
        return propertyValues != null ? propertyValues.stream().map(this::toInstitutionPropertyValueDto).collect(Collectors.toList()) : null;
    }

    // ========================= UTILITY METHODS =========================

    private String getFullName(String firstName, String lastName) {
        if (!StringUtils.hasText(firstName) && !StringUtils.hasText(lastName)) {
            return null;
        }
        if (!StringUtils.hasText(firstName)) {
            return lastName;
        }
        if (!StringUtils.hasText(lastName)) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    private String formatPropertyValue(InstitutionPropertyValue propertyValue) {
        if (propertyValue == null) return null;

        PropertyDataType dataType = propertyValue.getProperty().getDataType();

        switch (dataType) {
            case TEXT:
            case TEXTAREA:
            case EMAIL:
            case PHONE:
            case URL:
                return propertyValue.getTextValue();

            case NUMBER:
            case DECIMAL:
                return propertyValue.getNumberValue() != null ? propertyValue.getNumberValue().toString() : null;

            case BOOLEAN:
                return propertyValue.getBooleanValue() != null ? (propertyValue.getBooleanValue() ? "Evet" : "Hayır") : null;

            case DATE:
                return propertyValue.getDateValue() != null ? propertyValue.getDateValue().format(DATE_FORMATTER) : null;

            case DATETIME:
                return propertyValue.getDatetimeValue() != null ? propertyValue.getDatetimeValue().format(DATETIME_FORMATTER) : null;

            case SELECT:
            case MULTISELECT:
                return propertyValue.getJsonValue();

            case FILE:
            case IMAGE:
                return propertyValue.getFileName();

            default:
                return propertyValue.getTextValue();
        }
    }

    private String formatPropertyValueForDisplay(InstitutionPropertyValue propertyValue) {
        String value = formatPropertyValue(propertyValue);
        if (value == null) return null;

        PropertyDataType dataType = propertyValue.getProperty().getDataType();

        switch (dataType) {
            case NUMBER:
                return formatNumber(propertyValue.getNumberValue());

            case DECIMAL:
                return formatDecimal(propertyValue.getNumberValue());

            case PHONE:
                return formatPhone(value);

            case EMAIL:
                return value.toLowerCase();

            case URL:
                return formatUrl(value);

            default:
                return value;
        }
    }

    private String formatNumber(Double number) {
        if (number == null) return null;
        return String.format("%.0f", number);
    }

    private String formatDecimal(Double decimal) {
        if (decimal == null) return null;
        return String.format("%.2f", decimal);
    }

    private String formatPhone(String phone) {
        if (phone == null) return null;
        // Simple Turkish phone formatting
        phone = phone.replaceAll("[^0-9]", "");
        if (phone.length() == 10) {
            return String.format("(%s) %s %s %s",
                    phone.substring(0, 3),
                    phone.substring(3, 6),
                    phone.substring(6, 8),
                    phone.substring(8, 10));
        }
        return phone;
    }

    private String formatUrl(String url) {
        if (url == null) return null;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "https://" + url;
        }
        return url;
    }
}