package com.genixo.education.search.service;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.entity.content.Gallery;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserInstitutionAccess;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.enumaration.AccessType;
import com.genixo.education.search.enumaration.RoleLevel;
import com.genixo.education.search.repository.insitution.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAccessValidator {

    private final SchoolRepository schoolRepository;
    private final CampusRepository campusRepository;
    private final BrandRepository brandRepository;

    /**
     * Validate if user can access a specific school
     * @param user The user to check
     * @param schoolId The school ID to validate access for
     * @throws BusinessException if user doesn't have access
     */
    public void validateUserCanAccessSchool(User user, Long schoolId) {
        if (user == null || schoolId == null) {
            throw new BusinessException("Invalid user or school ID provided");
        }


        // System administrators have access to everything
        if (hasSystemRole(user)) {
            return;
        }

        // Check if user has direct or inherited access to the school
        if (!hasAccessToSchool(user, schoolId)) {
            log.warn("User {} denied access to school {}", user.getId(), schoolId);
            throw new BusinessException("User does not have access to this school")
                    .withErrorCode("ACCESS_DENIED")
                    .addDetail("School ID: " + schoolId)
                    .addDetail("User ID: " + user.getId());
        }

    }

    /**
     * Validate if user can access a specific campus
     * @param user The user to check
     * @param campusId The campus ID to validate access for
     * @throws BusinessException if user doesn't have access
     */
    public void validateUserCanAccessCampus(User user, Long campusId) {
        if (user == null || campusId == null) {
            throw new BusinessException("Invalid user or campus ID provided");
        }


        if (hasSystemRole(user)) {
            return;
        }

        if (!hasAccessToCampus(user, campusId)) {
            log.warn("User {} denied access to campus {}", user.getId(), campusId);
            throw new BusinessException("User does not have access to this campus")
                    .withErrorCode("ACCESS_DENIED")
                    .addDetail("Campus ID: " + campusId)
                    .addDetail("User ID: " + user.getId());
        }

    }

    /**
     * Validate if user can access a specific brand
     * @param user The user to check
     * @param brandId The brand ID to validate access for
     * @throws BusinessException if user doesn't have access
     */
    public void validateUserCanAccessBrand(User user, Long brandId) {
        if (user == null || brandId == null) {
            throw new BusinessException("Invalid user or brand ID provided");
        }


        if (hasSystemRole(user)) {
            return;
        }

        if (!hasAccessToBrand(user, brandId)) {
            log.warn("User {} denied access to brand {}", user.getId(), brandId);
            throw new BusinessException("User does not have access to this brand")
                    .withErrorCode("ACCESS_DENIED")
                    .addDetail("Brand ID: " + brandId)
                    .addDetail("User ID: " + user.getId());
        }

    }

    /**
     * Validate if user can manage (edit/delete) a specific school
     * @param user The user to check
     * @param schoolId The school ID to validate manage access for
     * @throws BusinessException if user doesn't have manage access
     */
    public void validateUserCanManageSchool(User user, Long schoolId) {
        // First check basic access
        validateUserCanAccessSchool(user, schoolId);

        // Then check for manage permissions
        if (!hasManageAccessToSchool(user, schoolId)) {
            log.warn("User {} denied manage access to school {}", user.getId(), schoolId);
            throw new BusinessException("User does not have management permissions for this school")
                    .withErrorCode("MANAGE_ACCESS_DENIED")
                    .addDetail("School ID: " + schoolId)
                    .addDetail("User ID: " + user.getId());
        }

    }

    /**
     * Validate if user can manage a specific campus
     */
    public void validateUserCanManageCampus(User user, Long campusId) {
        validateUserCanAccessCampus(user, campusId);

        if (!hasManageAccessToCampus(user, campusId)) {
            throw new BusinessException("User does not have management permissions for this campus")
                    .withErrorCode("MANAGE_ACCESS_DENIED");
        }
    }

    /**
     * Validate if user can manage a specific brand
     */
    public void validateUserCanManageBrand(User user, Long brandId) {
        validateUserCanAccessBrand(user, brandId);

        if (!hasManageAccessToBrand(user, brandId)) {
            throw new BusinessException("User does not have management permissions for this brand")
                    .withErrorCode("MANAGE_ACCESS_DENIED");
        }
    }

    /**
     * Validate if user can create new brands
     */
    public void validateUserCanCreateBrand(User user) {
        if (!hasSystemRole(user)) {
            throw new BusinessException("Only system administrators can create brands")
                    .withErrorCode("INSUFFICIENT_PERMISSIONS");
        }
    }

    /**
     * Validate if user can moderate content
     */
    public void validateUserCanModerateContent(User user) {
        boolean canModerate = user.getUserRoles().stream()
                .anyMatch(userRole -> {
                    String roleName = userRole.getRole().name();
                    RoleLevel roleLevel = userRole.getRoleLevel();

                    return roleName.contains("MODERATOR") ||
                            roleName.contains("ADMIN") ||
                            roleLevel == RoleLevel.SYSTEM;
                });

        if (!canModerate) {
            throw new BusinessException("User does not have content moderation permissions")
                    .withErrorCode("MODERATION_ACCESS_DENIED");
        }
    }

    /**
     * Validate subscription limits for a school
     */
    public void validateSchoolSubscriptionLimits(User user, Long schoolId, String featureType) {
        // First validate access
        validateUserCanAccessSchool(user, schoolId);

        // Check if school's campus is subscribed
        boolean isSubscribed = schoolRepository.findByIdAndIsActiveTrueAndCampusIsSubscribedTrue(schoolId)
                .isPresent();

        if (!isSubscribed) {
            throw new BusinessException("Active subscription required to access this feature")
                    .withErrorCode("SUBSCRIPTION_REQUIRED")
                    .addDetail("Feature: " + featureType)
                    .addDetail("School ID: " + schoolId);
        }
    }

    // ================================ PRIVATE HELPER METHODS ================================

    /**
     * Check if user has system-level role
     */
    private boolean hasSystemRole(User user) {
        return user.getUserRoles().stream()
                .anyMatch(userRole -> {
                    // Check if role is still valid (not expired)
                    if (userRole.getExpiresAt() != null &&
                            userRole.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }
                    return userRole.getRoleLevel() == RoleLevel.SYSTEM;
                });
    }

    /**
     * Check if user has access to a specific school
     * This includes direct school access, campus access, or brand access
     */
    private boolean hasAccessToSchool(User user, Long schoolId) {
        return user.getInstitutionAccess().stream()
                .anyMatch(access -> {
                    // Check if access is still valid (not expired)
                    if (access.getExpiresAt() != null &&
                            access.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }

                    switch (access.getAccessType()) {
                        case SCHOOL:
                            // Direct school access
                            return access.getEntityId().equals(schoolId);
                        case CAMPUS:
                            // Campus access - check if school belongs to this campus
                            return schoolRepository.existsByIdAndCampusId(schoolId, access.getEntityId());
                        case BRAND:
                            // Brand access - check if school belongs to this brand
                            return schoolRepository.existsByIdAndCampusBrandId(schoolId, access.getEntityId());
                        default:
                            return false;
                    }
                });
    }

    /**
     * Check if user has access to a specific campus
     */
    private boolean hasAccessToCampus(User user, Long campusId) {
        return user.getInstitutionAccess().stream()
                .anyMatch(access -> {
                    if (access.getExpiresAt() != null &&
                            access.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }

                    switch (access.getAccessType()) {
                        case CAMPUS:
                            return access.getEntityId().equals(campusId);
                        case BRAND:
                            return campusRepository.existsByIdAndBrandId(campusId, access.getEntityId());
                        default:
                            return false;
                    }
                });
    }

    /**
     * Check if user has access to a specific brand
     */
    private boolean hasAccessToBrand(User user, Long brandId) {
        return user.getInstitutionAccess().stream()
                .anyMatch(access -> access.getAccessType() == AccessType.BRAND &&
                        access.getEntityId().equals(brandId) &&
                        (access.getExpiresAt() == null ||
                                access.getExpiresAt().isAfter(LocalDateTime.now())));
    }

    /**
     * Check if user has management permissions for a school
     */
    private boolean hasManageAccessToSchool(User user, Long schoolId) {
        // System admins can manage everything
        if (hasSystemRole(user)) {
            return true;
        }

        // Check if user has management roles for this school
        return user.getUserRoles().stream()
                .anyMatch(userRole -> {
                    if (userRole.getExpiresAt() != null &&
                            userRole.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }

                    String roleName = userRole.getRole().name();
                    RoleLevel roleLevel = userRole.getRoleLevel();

                    // Check for management roles
                    boolean hasManagementRole = roleName.contains("ADMIN") ||
                            roleName.contains("MANAGER") ||
                            roleName.contains("DIRECTOR") ||
                            roleLevel == RoleLevel.SCHOOL ||
                            roleLevel == RoleLevel.CAMPUS ||
                            roleLevel == RoleLevel.BRAND;

                    return hasManagementRole && hasAccessToSchool(user, schoolId);
                });
    }

    /**
     * Check if user has management permissions for a campus
     */
    private boolean hasManageAccessToCampus(User user, Long campusId) {
        if (hasSystemRole(user)) {
            return true;
        }

        return user.getUserRoles().stream()
                .anyMatch(userRole -> {
                    if (userRole.getExpiresAt() != null &&
                            userRole.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }

                    RoleLevel roleLevel = userRole.getRoleLevel();
                    String roleName = userRole.getRole().name();

                    boolean hasManagementRole = roleName.contains("ADMIN") ||
                            roleName.contains("MANAGER") ||
                            roleLevel == RoleLevel.CAMPUS ||
                            roleLevel == RoleLevel.BRAND;

                    return hasManagementRole && hasAccessToCampus(user, campusId);
                });
    }

    /**
     * Check if user has management permissions for a brand
     */
    private boolean hasManageAccessToBrand(User user, Long brandId) {
        if (hasSystemRole(user)) {
            return true;
        }

        return user.getUserRoles().stream()
                .anyMatch(userRole -> {
                    if (userRole.getExpiresAt() != null &&
                            userRole.getExpiresAt().isBefore(LocalDateTime.now())) {
                        return false;
                    }

                    RoleLevel roleLevel = userRole.getRoleLevel();
                    String roleName = userRole.getRole().name();

                    boolean hasManagementRole = roleName.contains("ADMIN") ||
                            roleName.contains("MANAGER") ||
                            roleLevel == RoleLevel.BRAND;

                    return hasManagementRole && hasAccessToBrand(user, brandId);
                });
    }

    // ================================ PUBLIC UTILITY METHODS ================================

    /**
     * Get all school IDs that user has access to
     */
    public List<Long> getUserAccessibleSchoolIds(User user) {
        if (hasSystemRole(user)) {
            return schoolRepository.findAllActiveSchoolIds();
        }

        return user.getInstitutionAccess().stream()
                .filter(access -> access.getExpiresAt() == null ||
                        access.getExpiresAt().isAfter(LocalDateTime.now()))
                .flatMap(access -> {
                    return switch (access.getAccessType()) {
                        case SCHOOL -> List.of(access.getEntityId()).stream();
                        case CAMPUS -> schoolRepository.findIdsByCampusId(access.getEntityId()).stream();
                        case BRAND -> schoolRepository.findIdsByCampus_Brand_Id(access.getEntityId()).stream();
                        default -> List.<Long>of().stream();
                    };
                })
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Get all campus IDs that user has access to
     */
    public List<Long> getUserAccessibleCampusIds(User user) {
        if (hasSystemRole(user)) {
            return campusRepository.findAllActiveCampusIds();
        }

        return user.getInstitutionAccess().stream()
                .filter(access -> access.getExpiresAt() == null ||
                        access.getExpiresAt().isAfter(LocalDateTime.now()))
                .flatMap(access -> {
                    return switch (access.getAccessType()) {
                        case CAMPUS -> Stream.of(access.getEntityId());
                        case BRAND -> campusRepository.findIdsByBrandId(access.getEntityId()).stream();
                        default -> List.<Long>of().stream();
                    };
                })
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Get all brand IDs that user has access to
     */
    public List<Long> getUserAccessibleBrandIds(User user) {
        if (hasSystemRole(user)) {
            return brandRepository.findAllActiveBrandIds();
        }

        return user.getInstitutionAccess().stream()
                .filter(access -> access.getAccessType() == AccessType.BRAND)
                .filter(access -> access.getExpiresAt() == null ||
                        access.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(UserInstitutionAccess::getEntityId)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Check if user can access any entity of given type
     */
    public boolean hasAnyAccessOfType(User user, AccessType accessType) {
        if (hasSystemRole(user)) {
            return true;
        }

        return user.getInstitutionAccess().stream()
                .anyMatch(access -> access.getAccessType() == accessType &&
                        (access.getExpiresAt() == null ||
                                access.getExpiresAt().isAfter(LocalDateTime.now())));
    }

    /**
     * Get user's role level for a specific entity
     */
    public RoleLevel getUserHighestRoleLevel(User user, Long entityId, AccessType accessType) {
        if (hasSystemRole(user)) {
            return RoleLevel.SYSTEM;
        }

        return user.getUserRoles().stream()
                .filter(userRole -> userRole.getExpiresAt() == null ||
                        userRole.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(userRole -> {
                    // Check if this role applies to the entity
                    return user.getInstitutionAccess().stream()
                            .anyMatch(access -> access.getAccessType() == accessType &&
                                    access.getEntityId().equals(entityId) &&
                                    (access.getExpiresAt() == null ||
                                            access.getExpiresAt().isAfter(LocalDateTime.now())));
                })
                .map(UserRole::getRoleLevel)
                .max(RoleLevel::compareTo) // Assuming RoleLevel implements Comparable
                .orElse(null);
    }

    public void validateUserCanAccessGallery(User user, Gallery gallery) {
        if (gallery == null) {
            throw new BusinessException("Gallery not found")
                    .withErrorCode("GALLERY_NOT_FOUND");
        }
/* ceyhun
        // System admins can access everything
        if (hasSystemRole(user)) {
            return;
        }

        // Check access based on gallery's associated entity
        switch (gallery.getAccessType()) {
            case SCHOOL:
                validateUserCanAccessSchool(user, gallery.getEntityId());
                break;
            case CAMPUS:
                validateUserCanAccessCampus(user, gallery.getEntityId());
                break;
            case BRAND:
                validateUserCanAccessBrand(user, gallery.getEntityId());
                break;
            default:
                throw new BusinessException("Invalid gallery access type")
                        .withErrorCode("INVALID_ACCESS_TYPE");
        }

 */
    }
}

