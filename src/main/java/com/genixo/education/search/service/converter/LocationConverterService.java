package com.genixo.education.search.service.converter;


import com.genixo.education.search.dto.location.*;
import com.genixo.education.search.entity.location.Country;
import com.genixo.education.search.entity.location.District;
import com.genixo.education.search.entity.location.Neighborhood;
import com.genixo.education.search.entity.location.Province;
import com.genixo.education.search.util.ConversionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationConverterService {


    public CountryDto mapToDto(Country entity) {
        if (entity == null) {
            return null;
        }

        return CountryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nameEn(entity.getNameEn())
                .isoCode2(entity.getIsoCode2())
                .isoCode3(entity.getIsoCode3())
                .phoneCode(entity.getPhoneCode())
                .currencyCode(entity.getCurrencyCode())
                .currencySymbol(entity.getCurrencySymbol())
                .flagEmoji(entity.getFlagEmoji())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .timezone(entity.getTimezone())
                .isSupported(ConversionUtils.defaultIfNull(entity.getIsSupported(), true))
                .sortOrder(ConversionUtils.defaultIfNull(entity.getSortOrder(), 0))
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
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
                .isSupported(ConversionUtils.defaultIfNull(entity.getIsSupported(), true))
                .build();
    }

    public Country mapToEntity(LocationCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Country entity = new Country();
        entity.setName(dto.getName());
        entity.setNameEn(dto.getNameEn());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setIsActive(true);
        entity.setSortOrder(0);

        return entity;
    }

    public void updateEntity(LocationUpdateDto dto, Country entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (StringUtils.hasText(dto.getName())) {
            entity.setName(dto.getName().trim());
        }

        if (StringUtils.hasText(dto.getNameEn())) {
            entity.setNameEn(dto.getNameEn().trim());
        }

        if (dto.getLatitude() != null) {
            entity.setLatitude(dto.getLatitude());
        }

        if (dto.getLongitude() != null) {
            entity.setLongitude(dto.getLongitude());
        }

        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
    }

    // ================== PROVINCE CONVERTERS ==================

    public ProvinceDto mapToDto(Province entity) {
        if (entity == null) {
            return null;
        }

        return ProvinceDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nameEn(entity.getNameEn())
                .code(entity.getCode())
                .plateCode(entity.getPlateCode())
                .region(entity.getRegion())
                .areaCode(entity.getAreaCode())
                .postalCodePrefix(entity.getPostalCodePrefix())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .population(entity.getPopulation())
                .areaKm2(entity.getAreaKm2())
                .elevationM(entity.getElevationM())
                .timeZone(entity.getTimeZone())
                .isMetropolitan(ConversionUtils.defaultIfNull(entity.getIsMetropolitan(), false))
                .sortOrder(ConversionUtils.defaultIfNull(entity.getSortOrder(), 0))
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .gdpPerCapita(entity.getGdpPerCapita())
                .unemploymentRate(entity.getUnemploymentRate())
                .educationIndex(entity.getEducationIndex())
                .hasAirport(ConversionUtils.defaultIfNull(entity.getHasAirport(), false))
                .hasUniversity(ConversionUtils.defaultIfNull(entity.getHasUniversity(), false))
                .hasMetro(ConversionUtils.defaultIfNull(entity.getHasMetro(), false))
                .trafficDensity(entity.getTrafficDensity())
                .schoolCount(ConversionUtils.defaultIfNull(entity.getSchoolCount(), 0L))
                .studentCount(ConversionUtils.defaultIfNull(entity.getStudentCount(), 0L))
                .teacherCount(ConversionUtils.defaultIfNull(entity.getTeacherCount(), 0L))
                .literacyRate(entity.getLiteracyRate())
                .country(mapToSummaryDto(entity.getCountry()))
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
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
                .isMetropolitan(ConversionUtils.defaultIfNull(entity.getIsMetropolitan(), false))
                .schoolCount(ConversionUtils.defaultIfNull(entity.getSchoolCount(), 0L))
                .build();
    }

    public Province mapToEntity(LocationCreateDto dto, Country country) {
        if (dto == null || country == null) {
            return null;
        }

        Province entity = new Province();
        entity.setCountry(country);
        entity.setName(dto.getName());
        entity.setNameEn(dto.getNameEn());
        entity.setCode(dto.getCode());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setDescription(dto.getDescription());
        entity.setIsActive(true);
        entity.setSortOrder(0);
        entity.setSchoolCount(0L);
        entity.setStudentCount(0L);
        entity.setTeacherCount(0L);

        // Generate slug
        if (StringUtils.hasText(entity.getName())) {
            entity.setSlug(ConversionUtils.generateSlug(entity.getName()));
        }

        return entity;
    }

    public void updateEntity(LocationUpdateDto dto, Province entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (StringUtils.hasText(dto.getName())) {
            entity.setName(dto.getName().trim());
            entity.setSlug(ConversionUtils.generateSlug(dto.getName().trim()));
        }

        if (StringUtils.hasText(dto.getNameEn())) {
            entity.setNameEn(dto.getNameEn().trim());
        }

        if (dto.getLatitude() != null) {
            entity.setLatitude(dto.getLatitude());
        }

        if (dto.getLongitude() != null) {
            entity.setLongitude(dto.getLongitude());
        }

        if (StringUtils.hasText(dto.getDescription())) {
            entity.setDescription(dto.getDescription().trim());
        }

        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
    }

    // ================== DISTRICT CONVERTERS ==================

    public DistrictDto mapToDto(District entity) {
        if (entity == null) {
            return null;
        }

        return DistrictDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nameEn(entity.getNameEn())
                .code(entity.getCode())
                .districtType(entity.getDistrictType())
                .postalCode(entity.getPostalCode())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .population(entity.getPopulation())
                .areaKm2(entity.getAreaKm2())
                .elevationM(entity.getElevationM())
                .densityPerKm2(entity.getDensityPerKm2())
                .isCentral(ConversionUtils.defaultIfNull(entity.getIsCentral(), false))
                .isCoastal(ConversionUtils.defaultIfNull(entity.getIsCoastal(), false))
                .sortOrder(ConversionUtils.defaultIfNull(entity.getSortOrder(), 0))
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .averageIncome(entity.getAverageIncome())
                .propertyPriceIndex(entity.getPropertyPriceIndex())
                .costOfLivingIndex(entity.getCostOfLivingIndex())
                .socioeconomicLevel(entity.getSocioeconomicLevel())
                .hasMetroStation(ConversionUtils.defaultIfNull(entity.getHasMetroStation(), false))
                .hasBusTerminal(ConversionUtils.defaultIfNull(entity.getHasBusTerminal(), false))
                .hasTrainStation(ConversionUtils.defaultIfNull(entity.getHasTrainStation(), false))
                .distanceToAirportKm(entity.getDistanceToAirportKm())
                .distanceToCityCenterKm(entity.getDistanceToCityCenterKm())
                .publicTransportScore(entity.getPublicTransportScore())
                .trafficCongestionLevel(entity.getTrafficCongestionLevel())
                .schoolCount(ConversionUtils.defaultIfNull(entity.getSchoolCount(), 0L))
                .privateSchoolCount(ConversionUtils.defaultIfNull(entity.getPrivateSchoolCount(), 0L))
                .publicSchoolCount(ConversionUtils.defaultIfNull(entity.getPublicSchoolCount(), 0L))
                .universityCount(ConversionUtils.defaultIfNull(entity.getUniversityCount(), 0L))
                .educationQualityIndex(entity.getEducationQualityIndex())
                .literacyRate(entity.getLiteracyRate())
                .youthPopulationPercentage(entity.getYouthPopulationPercentage())
                .elderlyPopulationPercentage(entity.getElderlyPopulationPercentage())
                .averageFamilySize(entity.getAverageFamilySize())
                .birthRate(entity.getBirthRate())
                .hospitalCount(ConversionUtils.defaultIfNull(entity.getHospitalCount(), 0))
                .shoppingMallCount(ConversionUtils.defaultIfNull(entity.getShoppingMallCount(), 0))
                .parkCount(ConversionUtils.defaultIfNull(entity.getParkCount(), 0))
                .culturalCenterCount(ConversionUtils.defaultIfNull(entity.getCulturalCenterCount(), 0))
                .sportsFacilityCount(ConversionUtils.defaultIfNull(entity.getSportsFacilityCount(), 0))
                .safetyIndex(entity.getSafetyIndex())
                .airQualityIndex(entity.getAirQualityIndex())
                .noiseLevel(entity.getNoiseLevel())
                .climateType(entity.getClimateType())
                .averageTemperature(entity.getAverageTemperature())
                .annualRainfallMm(entity.getAnnualRainfallMm())
                .humidityPercentage(entity.getHumidityPercentage())
                .province(mapToSummaryDto(entity.getProvince()))
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
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
                .isCentral(ConversionUtils.defaultIfNull(entity.getIsCentral(), false))
                .schoolCount(ConversionUtils.defaultIfNull(entity.getSchoolCount(), 0L))
                .socioeconomicLevel(entity.getSocioeconomicLevel())
                .build();
    }

    public District mapToEntity(LocationCreateDto dto, Province province) {
        if (dto == null || province == null) {
            return null;
        }

        District entity = new District();
        entity.setProvince(province);
        entity.setName(dto.getName());
        entity.setNameEn(dto.getNameEn());
        entity.setCode(dto.getCode());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setDescription(dto.getDescription());
        entity.setIsActive(true);
        entity.setSortOrder(0);
        entity.setIsCentral(false);
        entity.setIsCoastal(false);
        entity.setSchoolCount(0L);
        entity.setPrivateSchoolCount(0L);
        entity.setPublicSchoolCount(0L);
        entity.setUniversityCount(0L);
        entity.setHospitalCount(0);
        entity.setShoppingMallCount(0);
        entity.setParkCount(0);
        entity.setCulturalCenterCount(0);
        entity.setSportsFacilityCount(0);

        // Generate slug
        if (StringUtils.hasText(entity.getName())) {
            entity.setSlug(ConversionUtils.generateSlug(entity.getName()));
        }

        return entity;
    }

    public void updateEntity(LocationUpdateDto dto, District entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (StringUtils.hasText(dto.getName())) {
            entity.setName(dto.getName().trim());
            entity.setSlug(ConversionUtils.generateSlug(dto.getName().trim()));
        }

        if (StringUtils.hasText(dto.getNameEn())) {
            entity.setNameEn(dto.getNameEn().trim());
        }

        if (dto.getLatitude() != null) {
            entity.setLatitude(dto.getLatitude());
        }

        if (dto.getLongitude() != null) {
            entity.setLongitude(dto.getLongitude());
        }

        if (StringUtils.hasText(dto.getDescription())) {
            entity.setDescription(dto.getDescription().trim());
        }

        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
    }

    // ================== NEIGHBORHOOD CONVERTERS ==================

    public NeighborhoodDto mapToDto(Neighborhood entity) {
        if (entity == null) {
            return null;
        }

        return NeighborhoodDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nameEn(entity.getNameEn())
                .code(entity.getCode())
                .neighborhoodType(entity.getNeighborhoodType())
                .postalCode(entity.getPostalCode())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .population(entity.getPopulation())
                .areaKm2(entity.getAreaKm2())
                .elevationM(entity.getElevationM())
                .densityPerKm2(entity.getDensityPerKm2())
                .sortOrder(ConversionUtils.defaultIfNull(entity.getSortOrder(), 0))
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .housingType(entity.getHousingType())
                .developmentLevel(entity.getDevelopmentLevel())
                .isGatedCommunity(ConversionUtils.defaultIfNull(entity.getIsGatedCommunity(), false))
                .isHistorical(ConversionUtils.defaultIfNull(entity.getIsHistorical(), false))
                .isCommercialCenter(ConversionUtils.defaultIfNull(entity.getIsCommercialCenter(), false))
                .isResidential(ConversionUtils.defaultIfNull(entity.getIsResidential(), true))
                .isIndustrial(ConversionUtils.defaultIfNull(entity.getIsIndustrial(), false))
                .averageRentPrice(entity.getAverageRentPrice())
                .averagePropertyPrice(entity.getAveragePropertyPrice())
                .propertyPricePerM2(entity.getPropertyPricePerM2())
                .incomeLevel(entity.getIncomeLevel())
                .metroAccessibilityMinutes(entity.getMetroAccessibilityMinutes())
                .busAccessibilityMinutes(entity.getBusAccessibilityMinutes())
                .mainRoadAccessibilityMinutes(entity.getMainRoadAccessibilityMinutes())
                .highwayAccessibilityMinutes(entity.getHighwayAccessibilityMinutes())
                .publicTransportFrequency(entity.getPublicTransportFrequency())
                .parkingAvailability(entity.getParkingAvailability())
                .walkabilityScore(entity.getWalkabilityScore())
                .hasMetroStation(ConversionUtils.defaultIfNull(entity.getHasMetroStation(), false))
                .hasHospital(ConversionUtils.defaultIfNull(entity.getHasHospital(), false))
                .hasShoppingCenter(ConversionUtils.defaultIfNull(entity.getHasShoppingCenter(), false))
                .hasPark(ConversionUtils.defaultIfNull(entity.getHasPark(), false))
                .hasLibrary(ConversionUtils.defaultIfNull(entity.getHasLibrary(), false))
                .hasSportsFacility(ConversionUtils.defaultIfNull(entity.getHasSportsFacility(), false))
                .hasCulturalCenter(ConversionUtils.defaultIfNull(entity.getHasCulturalCenter(), false))
                .hasKindergarten(ConversionUtils.defaultIfNull(entity.getHasKindergarten(), false))
                .restaurantCount(ConversionUtils.defaultIfNull(entity.getRestaurantCount(), 0))
                .cafeCount(ConversionUtils.defaultIfNull(entity.getCafeCount(), 0))
                .bankCount(ConversionUtils.defaultIfNull(entity.getBankCount(), 0))
                .pharmacyCount(ConversionUtils.defaultIfNull(entity.getPharmacyCount(), 0))
                .supermarketCount(ConversionUtils.defaultIfNull(entity.getSupermarketCount(), 0))
                .schoolCount(ConversionUtils.defaultIfNull(entity.getSchoolCount(), 0L))
                .privateSchoolCount(ConversionUtils.defaultIfNull(entity.getPrivateSchoolCount(), 0L))
                .publicSchoolCount(ConversionUtils.defaultIfNull(entity.getPublicSchoolCount(), 0L))
                .preschoolCount(ConversionUtils.defaultIfNull(entity.getPreschoolCount(), 0L))
                .educationAccessibilityScore(entity.getEducationAccessibilityScore())
                .schoolQualityIndex(entity.getSchoolQualityIndex())
                .safetyScore(entity.getSafetyScore())
                .noiseLevel(entity.getNoiseLevel())
                .airQualityScore(entity.getAirQualityScore())
                .greenSpacePercentage(entity.getGreenSpacePercentage())
                .cleanlinessScore(entity.getCleanlinessScore())
                .socialLifeScore(entity.getSocialLifeScore())
                .averageAge(entity.getAverageAge())
                .familyWithChildrenPercentage(entity.getFamilyWithChildrenPercentage())
                .youngProfessionalPercentage(entity.getYoungProfessionalPercentage())
                .elderlyPercentage(entity.getElderlyPercentage())
                .studentPercentage(entity.getStudentPercentage())
                .propertyDemandLevel(entity.getPropertyDemandLevel())
                .developmentPotential(entity.getDevelopmentPotential())
                .investmentAttractiveness(entity.getInvestmentAttractiveness())
                .schoolPreferenceScore(entity.getSchoolPreferenceScore())
                .commuteToBusinessDistrictsMinutes(entity.getCommuteToBusinessDistrictsMinutes())
                .familyFriendlinessScore(entity.getFamilyFriendlinessScore())
                .district(mapToSummaryDto(entity.getDistrict()))
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
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
                .schoolCount(ConversionUtils.defaultIfNull(entity.getSchoolCount(), 0L))
                .incomeLevel(entity.getIncomeLevel())
                .schoolPreferenceScore(entity.getSchoolPreferenceScore())
                .build();
    }

    public Neighborhood mapToEntity(LocationCreateDto dto, District district) {
        if (dto == null || district == null) {
            return null;
        }

        Neighborhood entity = new Neighborhood();
        entity.setDistrict(district);
        entity.setName(dto.getName());
        entity.setNameEn(dto.getNameEn());
        entity.setCode(dto.getCode());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setDescription(dto.getDescription());
        entity.setIsActive(true);
        entity.setSortOrder(0);
        entity.setIsGatedCommunity(false);
        entity.setIsHistorical(false);
        entity.setIsCommercialCenter(false);
        entity.setIsResidential(true);
        entity.setIsIndustrial(false);
        entity.setHasMetroStation(false);
        entity.setHasHospital(false);
        entity.setHasShoppingCenter(false);
        entity.setHasPark(false);
        entity.setHasLibrary(false);
        entity.setHasSportsFacility(false);
        entity.setHasCulturalCenter(false);
        entity.setHasKindergarten(false);
        entity.setRestaurantCount(0);
        entity.setCafeCount(0);
        entity.setBankCount(0);
        entity.setPharmacyCount(0);
        entity.setSupermarketCount(0);
        entity.setSchoolCount(0L);
        entity.setPrivateSchoolCount(0L);
        entity.setPublicSchoolCount(0L);
        entity.setPreschoolCount(0L);

        // Generate slug
        if (StringUtils.hasText(entity.getName())) {
            entity.setSlug(ConversionUtils.generateSlug(entity.getName()));
        }

        return entity;
    }

    public void updateEntity(LocationUpdateDto dto, Neighborhood entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (StringUtils.hasText(dto.getName())) {
            entity.setName(dto.getName().trim());
            entity.setSlug(ConversionUtils.generateSlug(dto.getName().trim()));
        }

        if (StringUtils.hasText(dto.getNameEn())) {
            entity.setNameEn(dto.getNameEn().trim());
        }

        if (dto.getLatitude() != null) {
            entity.setLatitude(dto.getLatitude());
        }

        if (dto.getLongitude() != null) {
            entity.setLongitude(dto.getLongitude());
        }

        if (StringUtils.hasText(dto.getDescription())) {
            entity.setDescription(dto.getDescription().trim());
        }

        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }

        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
    }

    // ================== HIERARCHY CONVERTERS ==================

    public LocationHierarchyDto mapToHierarchyDto(Country country, Province province, District district, Neighborhood neighborhood) {
        return LocationHierarchyDto.builder()
                .country(mapToSummaryDto(country))
                .province(mapToSummaryDto(province))
                .district(mapToSummaryDto(district))
                .neighborhood(mapToSummaryDto(neighborhood))
                .build();
    }

    public LocationHierarchyDto mapToHierarchyDto(Neighborhood neighborhood) {
        if (neighborhood == null) {
            return null;
        }

        District district = neighborhood.getDistrict();
        Province province = district != null ? district.getProvince() : null;
        Country country = province != null ? province.getCountry() : null;

        return mapToHierarchyDto(country, province, district, neighborhood);
    }

    // ================== COLLECTION CONVERTERS ==================

    public List<CountryDto> mapToDto(List<Country> countries) {
        if (ConversionUtils.isEmpty(countries)) {
            return new ArrayList<>();
        }

        return countries.stream()
                .filter(Objects::nonNull)
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<CountrySummaryDto> mapToSummaryDto(List<Country> countries) {
        if (ConversionUtils.isEmpty(countries)) {
            return new ArrayList<>();
        }

        return countries.stream()
                .filter(Objects::nonNull)
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<ProvinceDto> mapProvincesToDto(List<Province> provinces) {
        if (ConversionUtils.isEmpty(provinces)) {
            return new ArrayList<>();
        }

        return provinces.stream()
                .filter(Objects::nonNull)
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<ProvinceSummaryDto> mapProvincesToSummaryDto(List<Province> provinces) {
        if (ConversionUtils.isEmpty(provinces)) {
            return new ArrayList<>();
        }

        return provinces.stream()
                .filter(Objects::nonNull)
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<DistrictDto> mapDistrictsToDto(List<District> districts) {
        if (ConversionUtils.isEmpty(districts)) {
            return new ArrayList<>();
        }

        return districts.stream()
                .filter(Objects::nonNull)
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<DistrictSummaryDto> mapDistrictsToSummaryDto(List<District> districts) {
        if (ConversionUtils.isEmpty(districts)) {
            return new ArrayList<>();
        }

        return districts.stream()
                .filter(Objects::nonNull)
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<NeighborhoodDto> mapNeighborhoodsToDto(List<Neighborhood> neighborhoods) {
        if (ConversionUtils.isEmpty(neighborhoods)) {
            return new ArrayList<>();
        }

        return neighborhoods.stream()
                .filter(Objects::nonNull)
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<NeighborhoodSummaryDto> mapNeighborhoodsToSummaryDto(List<Neighborhood> neighborhoods) {
        if (ConversionUtils.isEmpty(neighborhoods)) {
            return new ArrayList<>();
        }

        return neighborhoods.stream()
                .filter(Objects::nonNull)
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ================== SEARCH & SUGGESTION CONVERTERS ==================

    public LocationSuggestionDto mapToSuggestionDto(Country entity) {
        if (entity == null) {
            return null;
        }

        return LocationSuggestionDto.builder()
                .id("country_" + entity.getId())
                .name(entity.getName())
                .type("COUNTRY")
                .fullName(entity.getName())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .schoolCount(0L) // Countries don't have direct school counts
                .relevanceScore("1.0")
                .build();
    }

    public LocationSuggestionDto mapToSuggestionDto(Province entity) {
        if (entity == null) {
            return null;
        }

        String fullName = entity.getName();
        if (entity.getCountry() != null) {
            fullName = entity.getName() + ", " + entity.getCountry().getName();
        }

        return LocationSuggestionDto.builder()
                .id("province_" + entity.getId())
                .name(entity.getName())
                .type("PROVINCE")
                .fullName(fullName)
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .schoolCount(ConversionUtils.defaultIfNull(entity.getSchoolCount(), 0L))
                .relevanceScore("0.8")
                .build();
    }

    public LocationSuggestionDto mapToSuggestionDto(District entity) {
        if (entity == null) {
            return null;
        }

        String fullName = entity.getName();
        if (entity.getProvince() != null) {
            fullName = entity.getName() + ", " + entity.getProvince().getName();
            if (entity.getProvince().getCountry() != null) {
                fullName += ", " + entity.getProvince().getCountry().getName();
            }
        }

        return LocationSuggestionDto.builder()
                .id("district_" + entity.getId())
                .name(entity.getName())
                .type("DISTRICT")
                .fullName(fullName)
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .schoolCount(ConversionUtils.defaultIfNull(entity.getSchoolCount(), 0L))
                .relevanceScore("0.9")
                .build();
    }

    public LocationSuggestionDto mapToSuggestionDto(Neighborhood entity) {
        if (entity == null) {
            return null;
        }

        String fullName = entity.getName();
        if (entity.getDistrict() != null) {
            fullName = entity.getName() + ", " + entity.getDistrict().getName();
            if (entity.getDistrict().getProvince() != null) {
                fullName += ", " + entity.getDistrict().getProvince().getName();
            }
        }

        return LocationSuggestionDto.builder()
                .id("neighborhood_" + entity.getId())
                .name(entity.getName())
                .type("NEIGHBORHOOD")
                .fullName(fullName)
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .schoolCount(ConversionUtils.defaultIfNull(entity.getSchoolCount(), 0L))
                .relevanceScore("1.0")
                .build();
    }

    // ================== DISTANCE & NEARBY CONVERTERS ==================

    public LocationDistanceDto mapToDistanceDto(LocationSuggestionDto location, Double distanceKm) {
        if (location == null || distanceKm == null) {
            return null;
        }

        // Calculate estimated travel time (assuming 50 km/h average)
        int estimatedTravelTime = (int) Math.ceil(distanceKm / 50.0 * 60);

        // Determine transportation method based on distance
        String transportationMethod = "WALKING";
        if (distanceKm > 2.0) {
            transportationMethod = "CAR";
        } else if (distanceKm > 0.5) {
            transportationMethod = "PUBLIC_TRANSPORT";
        }

        return LocationDistanceDto.builder()
                .location(location)
                .distanceKm(distanceKm)
                .estimatedTravelTimeMinutes(estimatedTravelTime)
                .transportationMethod(transportationMethod)
                .build();
    }

    public NearbyLocationsDto mapToNearbyLocationsDto(CoordinatesDto center, Double radiusKm,
                                                      List<LocationDistanceDto> locations) {
        if (center == null || radiusKm == null) {
            return null;
        }

        return NearbyLocationsDto.builder()
                .center(center)
                .radiusKm(radiusKm)
                .locations(ConversionUtils.defaultIfNull(locations, new ArrayList<>()))
                .totalCount(ConversionUtils.safeSize(locations))
                .build();
    }

    // ================== COORDINATES CONVERTER ==================

    public CoordinatesDto mapToCoordinatesDto(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }

        return CoordinatesDto.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    // ================== IMPORT RESULT CONVERTERS ==================

    public LocationImportResultDto createImportResult(Boolean success, Integer totalRecords,
                                                      Integer successfulImports, Integer failedImports,
                                                      Integer skippedRecords, List<String> errors,
                                                      List<String> warnings, String importId) {
        return LocationImportResultDto.builder()
                .success(ConversionUtils.defaultIfNull(success, false))
                .totalRecords(ConversionUtils.defaultIfNull(totalRecords, 0))
                .successfulImports(ConversionUtils.defaultIfNull(successfulImports, 0))
                .failedImports(ConversionUtils.defaultIfNull(failedImports, 0))
                .skippedRecords(ConversionUtils.defaultIfNull(skippedRecords, 0))
                .errors(ConversionUtils.defaultIfNull(errors, new ArrayList<>()))
                .warnings(ConversionUtils.defaultIfNull(warnings, new ArrayList<>()))
                .importId(importId)
                .importDate(java.time.LocalDateTime.now())
                .build();
    }

    // ================== STATISTICS CONVERTERS ==================

    public LocationStatisticsDto createLocationStatistics(Map<String, Long> counts) {
        if (counts == null) {
            counts = new HashMap<>();
        }

        return LocationStatisticsDto.builder()
                .totalCountries(counts.getOrDefault("totalCountries", 0L))
                .supportedCountries(counts.getOrDefault("supportedCountries", 0L))
                .totalProvinces(counts.getOrDefault("totalProvinces", 0L))
                .metropolitanProvinces(counts.getOrDefault("metropolitanProvinces", 0L))
                .totalDistricts(counts.getOrDefault("totalDistricts", 0L))
                .centralDistricts(counts.getOrDefault("centralDistricts", 0L))
                .totalNeighborhoods(counts.getOrDefault("totalNeighborhoods", 0L))
                .totalSchools(counts.getOrDefault("totalSchools", 0L))
                .totalStudents(counts.getOrDefault("totalStudents", 0L))
                .totalTeachers(counts.getOrDefault("totalTeachers", 0L))
                .build();
    }

    // ================== VALIDATION HELPERS ==================

    public boolean isValidLocationHierarchy(Country country, Province province, District district, Neighborhood neighborhood) {
        // Country is required
        if (country == null) {
            return false;
        }

        // Province must belong to country
        if (province != null && (province.getCountry() == null ||
                !province.getCountry().getId().equals(country.getId()))) {
            return false;
        }

        // District must belong to province
        if (district != null && (district.getProvince() == null ||
                !district.getProvince().getId().equals(province.getId()))) {
            return false;
        }

        // Neighborhood must belong to district
        if (neighborhood != null && (neighborhood.getDistrict() == null ||
                !neighborhood.getDistrict().getId().equals(district.getId()))) {
            return false;
        }

        return true;
    }

    // ================== BULK OPERATIONS ==================

    public BulkLocationImportDto mapToBulkImportDto(String fileUrl, String fileType,
                                                    Boolean validateOnly, Boolean overwriteExisting,
                                                    String mappingConfiguration) {
        return BulkLocationImportDto.builder()
                .fileUrl(fileUrl)
                .fileType(ConversionUtils.defaultIfEmpty(fileType, "CSV"))
                .validateOnly(ConversionUtils.defaultIfNull(validateOnly, false))
                .overwriteExisting(ConversionUtils.defaultIfNull(overwriteExisting, false))
                .mappingConfiguration(mappingConfiguration)
                .build();
    }

    // ================== HELPER METHODS ==================

    private String buildFullLocationName(Country country, Province province, District district, Neighborhood neighborhood) {
        List<String> parts = new ArrayList<>();

        if (neighborhood != null && StringUtils.hasText(neighborhood.getName())) {
            parts.add(neighborhood.getName());
        }

        if (district != null && StringUtils.hasText(district.getName())) {
            parts.add(district.getName());
        }

        if (province != null && StringUtils.hasText(province.getName())) {
            parts.add(province.getName());
        }

        if (country != null && StringUtils.hasText(country.getName())) {
            parts.add(country.getName());
        }

        return String.join(", ", parts);
    }

    private Double calculateRelevanceScore(String locationType, Long schoolCount) {
        double baseScore = 0.5;

        // Location type scoring
        switch (locationType.toUpperCase()) {
            case "NEIGHBORHOOD":
                baseScore = 1.0;
                break;
            case "DISTRICT":
                baseScore = 0.9;
                break;
            case "PROVINCE":
                baseScore = 0.8;
                break;
            case "COUNTRY":
                baseScore = 0.7;
                break;
        }

        // School count bonus
        if (schoolCount != null && schoolCount > 0) {
            baseScore += Math.min(schoolCount * 0.01, 0.3); // Max 0.3 bonus
        }

        return Math.min(baseScore, 1.0);
    }
}
