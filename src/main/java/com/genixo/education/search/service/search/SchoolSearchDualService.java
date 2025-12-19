package com.genixo.education.search.service.search;

import com.genixo.education.search.dto.institution.SchoolSearchDto;
import com.genixo.education.search.dto.institution.SchoolSearchResultDto;
import com.genixo.education.search.dto.search.SchoolSearchByNameDto;
import com.genixo.education.search.entity.search.SchoolSearchViewWithProperties;
import com.genixo.education.search.repository.search.SchoolSearchDualRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * School Search Service - DUAL MODE
 * Frontend (ID) ve AI (Name) aramalarını destekler
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolSearchDualService {

    private final SchoolSearchDualRepository repository;

    // ============================================================================
    // FRONTEND SEARCH (ID BAZLI)
    // ============================================================================

    /**
     * Frontend için ID bazlı arama
     */
    public Page<SchoolSearchResultDto> searchByIds(SchoolSearchDto dto) {
        log.info("Frontend search - institutionTypeIds: {}, provinceId: {}",
                dto.getInstitutionTypeIds(), dto.getProvinceId());

        // Validasyon
        if (dto.getProvinceId() == null) {
            throw new IllegalArgumentException("provinceId is required");
        }
        if (dto.getInstitutionTypeIds() == null || dto.getInstitutionTypeIds().isEmpty()) {
            throw new IllegalArgumentException("institutionTypeIds is required");
        }

        // Boş string'leri null'a çevir
        normalizeEmptyStrings(dto);

        // Her institution type için arama yap
        Long institutionTypeId = dto.getInstitutionTypeIds().get(0); // İlk tip için arama

        Pageable pageable = PageRequest.of(
                dto.getPage() != null ? dto.getPage() : 0,
                dto.getSize() != null ? dto.getSize() : 12
        );

        // Property ID'lerini array'e çevir
        Long[] propertyIds = null;
        if (dto.getPropertyFilters() != null && !dto.getPropertyFilters().isEmpty()) {
            propertyIds = dto.getPropertyFilters().toArray(new Long[0]);
        }

        // Sort parametrelerini normalize et
        String sortBy = normalizeSortBy(dto.getSortBy());
        String sortDirection = normalizeSortDirection(dto.getSortDirection());

        // Repository'den ara
        Page<SchoolSearchViewWithProperties> results = repository.searchByIds(
                institutionTypeId,
                dto.getProvinceId(),
                dto.getDistrictId(),
                dto.getNeighborhoodId(),
                dto.getMinAge(),
                dto.getMaxAge(),
                dto.getMinFee(),
                dto.getMaxFee(),
                dto.getCurriculumType(),
                dto.getLanguageOfInstruction(),
                dto.getMinRating(),
                dto.getIsSubscribed(),
                dto.getSearchTerm(),
                propertyIds,
                sortBy,
                sortDirection,
                pageable
        );

        log.info("Found {} schools", results.getTotalElements());

        // DTO'ya çevir
        return results.map(this::toResultDto);
    }

    /**
     * Coğrafi arama - ID bazlı
     */
    public Page<SchoolSearchResultDto> searchNearbyByIds(SchoolSearchDto dto) {
        log.info("Nearby search - lat: {}, lon: {}, radius: {}",
                dto.getLatitude(), dto.getLongitude(), dto.getRadiusKm());

        // Validasyon
        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            throw new IllegalArgumentException("latitude and longitude are required");
        }
        if (dto.getInstitutionTypeIds() == null || dto.getInstitutionTypeIds().isEmpty()) {
            throw new IllegalArgumentException("institutionTypeIds is required");
        }

        Long institutionTypeId = dto.getInstitutionTypeIds().get(0);
        Double radiusKm = dto.getRadiusKm() != null ? dto.getRadiusKm() : 10.0;

        List<SchoolSearchViewWithProperties> results = repository.findNearbySchoolsById(
                institutionTypeId,
                dto.getLatitude(),
                dto.getLongitude(),
                radiusKm
        );

        // Manuel pagination
        int page = dto.getPage() != null ? dto.getPage() : 0;
        int size = dto.getSize() != null ? dto.getSize() : 12;

        int start = page * size;
        int end = Math.min(start + size, results.size());
        List<SchoolSearchViewWithProperties> pageContent = results.subList(start, end);

        Page<SchoolSearchViewWithProperties> pagedResults = new PageImpl<>(
                pageContent,
                PageRequest.of(page, size),
                results.size()
        );

        return pagedResults.map(this::toResultDto);
    }

    // ============================================================================
    // AI SEARCH (NAME BAZLI)
    // ============================================================================

    /**
     * AI için Name bazlı arama
     */
    public Page<SchoolSearchResultDto> searchByNames(SchoolSearchByNameDto dto) {
        log.info("AI search - institutionType: {}, province: {}",
                dto.getInstitutionTypeName(), dto.getProvinceName());

        // Validasyon
        if (!dto.hasRequiredFields()) {
            throw new IllegalArgumentException("institutionTypeName and provinceName are required");
        }

        // Boş string'leri null'a çevir
        dto.normalizeEmptyStrings();

        Pageable pageable = PageRequest.of(
                dto.getPage() != null ? dto.getPage() : 0,
                dto.getSize() != null ? dto.getSize() : 12
        );

        // Property name'lerini array'e çevir
        String[] propertyNames = null;
        if (dto.getPropertyFilters() != null && !dto.getPropertyFilters().isEmpty()) {
            propertyNames = dto.getPropertyFilters().toArray(new String[0]);
        }

        // Sort parametreleri (AI'dan küçük harfle gelebilir)
        String sortBy = dto.getSortBy() != null ? dto.getSortBy().toLowerCase() : "name";
        String sortDirection = dto.getSortDirection() != null ? dto.getSortDirection().toLowerCase() : "asc";

        // Repository'den ara
        Page<SchoolSearchViewWithProperties> results = repository.searchByNames(
                dto.getInstitutionTypeName(),
                dto.getProvinceName(),
                dto.getDistrictName(),
                dto.getNeighborhoodName(),
                dto.getMinAge(),
                dto.getMaxAge(),
                dto.getMinFee(),
                dto.getMaxFee(),
                dto.getCurriculumType(),
                dto.getLanguageOfInstruction(),
                dto.getMinRating(),
                dto.getIsSubscribed(),
                dto.getSearchTerm(),
                propertyNames,
                sortBy,
                sortDirection,
                pageable
        );

        log.info("AI found {} schools", results.getTotalElements());

        // DTO'ya çevir
        return results.map(this::toResultDto);
    }

    /**
     * Coğrafi arama - Name bazlı (AI için)
     */
    public Page<SchoolSearchResultDto> searchNearbyByNames(SchoolSearchByNameDto dto,
                                                           Double latitude,
                                                           Double longitude,
                                                           Double radiusKm) {
        log.info("AI nearby search - lat: {}, lon: {}, radius: {}", latitude, longitude, radiusKm);

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("latitude and longitude are required");
        }
        if (dto.getInstitutionTypeName() == null || dto.getInstitutionTypeName().trim().isEmpty()) {
            throw new IllegalArgumentException("institutionTypeName is required");
        }

        Double radius = radiusKm != null ? radiusKm : 10.0;

        List<SchoolSearchViewWithProperties> results = repository.findNearbySchoolsByName(
                dto.getInstitutionTypeName(),
                latitude,
                longitude,
                radius
        );

        // Manuel pagination
        int page = dto.getPage() != null ? dto.getPage() : 0;
        int size = dto.getSize() != null ? dto.getSize() : 12;

        int start = page * size;
        int end = Math.min(start + size, results.size());
        List<SchoolSearchViewWithProperties> pageContent = results.subList(start, end);

        Page<SchoolSearchViewWithProperties> pagedResults = new PageImpl<>(
                pageContent,
                PageRequest.of(page, size),
                results.size()
        );

        return pagedResults.map(this::toResultDto);
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Entity'yi Result DTO'ya çevir
     */
    private SchoolSearchResultDto toResultDto(SchoolSearchViewWithProperties entity) {
        return SchoolSearchResultDto.builder()
                .id(entity.getSchoolId())
                .name(entity.getSchoolName())
                .slug(entity.getSchoolSlug())
                .description(entity.getDescription())

                // Kampüs
                .campusId(entity.getCampusId())
                .campusName(entity.getCampusName())
                .campusSlug(entity.getCampusSlug())
                .campusIsSubscribed(entity.getCampusIsSubscribed())

                // Lokasyon
                .neighborhood(entity.getNeighborhoodName())
                .district(entity.getDistrictName())
                .province(entity.getProvinceName())
                .fullLocation(entity.getFullLocation())
                .address(entity.getAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())

                // Görsel
                .logoUrl(entity.getEffectiveLogoUrl())
                .coverImageUrl(entity.getEffectiveCoverImageUrl())

                // Rating
                .ratingAverage(entity.getRatingAverage())
                .ratingCount(entity.getRatingCount())
                .ratingStars(entity.getRatingStars())

                // Tip
                .institutionTypeDisplayName(entity.getInstitutionTypeDisplayName())
                .institutionTypeColor(entity.getInstitutionTypeColor())
                .institutionTypeIcon(entity.getInstitutionTypeIcon())
                .curriculumType(entity.getCurriculumType())
                .languageOfInstruction(entity.getLanguageOfInstruction())

                // Marka
                .brandName(entity.getBrandName())
                .brandSlug(entity.getBrandSlug())
                .brandLogo(entity.getBrandLogo())

                // İstatistikler
                .viewCount(entity.getViewCount())
                .studentCapacity(entity.getStudentCapacity())
                .currentStudentCount(entity.getCurrentStudentCount())
                .occupancyRate(entity.getOccupancyRate())

                // Yaş
                .minAge(entity.getMinAge())
                .maxAge(entity.getMaxAge())
                .ageRangeText(entity.getAgeRangeText())

                // Ücret
                .monthlyFee(entity.getMonthlyFee())
                .annualFee(entity.getAnnualFee())
                .feeRangeText(entity.getFeeRangeText())

                // İletişim
                .phone(entity.getEffectivePhone())
                .email(entity.getEffectiveEmail())
                .websiteUrl(entity.getWebsiteUrl())

                // Scores
                .popularityScore(entity.getPopularityScore())
                .trustScore(entity.getTrustScore())
                .qualityScore(entity.getQualityScore())
                .trustLevel(entity.getTrustLevel())

                // Diğer
                .foundedYear(entity.getFoundedYear())
                .propertyCount(entity.getPropertyCount())

                .build();
    }

    /**
     * Boş string'leri null'a çevir
     */
    private void normalizeEmptyStrings(SchoolSearchDto dto) {
        if (dto.getCurriculumType() != null && dto.getCurriculumType().trim().isEmpty()) {
            dto.setCurriculumType(null);
        }
        if (dto.getLanguageOfInstruction() != null && dto.getLanguageOfInstruction().trim().isEmpty()) {
            dto.setLanguageOfInstruction(null);
        }
        if (dto.getSearchTerm() != null && dto.getSearchTerm().trim().isEmpty()) {
            dto.setSearchTerm(null);
        }
    }

    /**
     * Sort by normalize et
     */
    private String normalizeSortBy(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "NAME";
        }
        return sortBy.toUpperCase();
    }

    /**
     * Sort direction normalize et
     */
    private String normalizeSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            return "ASC";
        }
        return sortDirection.toUpperCase();
    }
}