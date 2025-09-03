package com.genixo.education.search.service.converter;

import com.genixo.education.search.dto.user.*;
import com.genixo.education.search.entity.user.Permission;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserInstitutionAccess;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserConverterService {

    @Autowired
    private LocationConverterService locationConverterService;

    @Autowired
    private PasswordEncoder passwordEncoder;



    public UserRoleDto mapToDto(UserRole entity) {
        if (entity == null) {
            return null;
        }
        return UserRoleDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .role(entity.getRole())
                .departments(entity.getDepartments())
                .permissions(entity.getPermissions())
                .roleLevel(entity.getRoleLevel())
                .expiresAt(entity.getExpiresAt())
                .build();
    }




    public UserDto mapToDto(User entity) {
        if (entity == null) {
            return null;
        }

        List<String> roles = entity.getUserRoles() == null ? new ArrayList<>() :
                entity.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().name())
                        .distinct()
                        .collect(Collectors.toList());

        List<String> authorities = entity.getUserRoles() == null ? new ArrayList<>() :
                entity.getUserRoles().stream()
                        .filter(userRole -> userRole.getRole() != null && userRole.getRole().getPermissions() != null)
                        .flatMap(userRole -> userRole.getRole().getPermissions().stream())
                        .map(Permission::name)
                        .distinct()
                        .collect(Collectors.toList());


        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .fullName(buildFullName(entity.getFirstName(), entity.getLastName()))
                .userType(entity.getUserType())
                .isEmailVerified(ConversionUtils.defaultIfNull(entity.getIsEmailVerified(), false))
                .isPhoneVerified(ConversionUtils.defaultIfNull(entity.getIsPhoneVerified(), false))
                .lastLoginAt(entity.getLastLoginAt())
                .profileImageUrl(entity.getProfileImageUrl())
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .createdAt(entity.getCreatedAt())
                .country(entity.getCountry() != null ? locationConverterService.mapToSummaryDto(entity.getCountry()) : null)
                .province(entity.getProvince() != null ? locationConverterService.mapToSummaryDto(entity.getProvince()) : null)
                .district(entity.getDistrict() != null ? locationConverterService.mapToSummaryDto(entity.getDistrict()) : null)
                .neighborhood(entity.getNeighborhood() != null ? locationConverterService.mapToSummaryDto(entity.getNeighborhood()) : null)
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .postalCode(entity.getPostalCode())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .userRoles(entity.getUserRoles() == null ? new ArrayList<>() :
                        entity.getUserRoles().stream()
                                .map(this::mapToDto)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()))
                .institutionAccess(mapInstitutionAccessToDto(entity.getInstitutionAccess()))
                .roles(roles)
                .authorities(authorities)
                .build();
    }

    public UserSummaryDto mapToSummaryDto(User entity) {
        if (entity == null) {
            return null;
        }

        return UserSummaryDto.builder()
                .id(entity.getId())
                .fullName(buildFullName(entity.getFirstName(), entity.getLastName()))
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .userType(entity.getUserType())
                .profileImageUrl(entity.getProfileImageUrl())
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .build();
    }

    public UserListDto mapToListDto(User entity) {
        if (entity == null) {
            return null;
        }

        // Get primary role information
        String primaryRole = null;
        com.genixo.education.search.enumaration.RoleLevel primaryRoleLevel = null;
        int institutionAccessCount = 0;
        String primaryInstitution = null;

        if (entity.getUserRoles() != null && !entity.getUserRoles().isEmpty()) {
            UserRole firstRole = entity.getUserRoles().iterator().next();
            if (firstRole != null) {
                primaryRole = firstRole.getRole().name();
                primaryRoleLevel = firstRole.getRoleLevel();
            }
        }

        if (entity.getInstitutionAccess() != null) {
            institutionAccessCount = entity.getInstitutionAccess().size();
            // Get first institution name (this would need to be resolved from entity ID)
            // For now, we'll set it based on access type
            UserInstitutionAccess firstAccess = entity.getInstitutionAccess().stream()
                    .findFirst().orElse(null);
            if (firstAccess != null) {
                primaryInstitution = firstAccess.getAccessType().name();
            }
        }

        return UserListDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .fullName(buildFullName(entity.getFirstName(), entity.getLastName()))
                .userType(entity.getUserType())
                .isActive(ConversionUtils.defaultIfNull(entity.getIsActive(), true))
                .isEmailVerified(ConversionUtils.defaultIfNull(entity.getIsEmailVerified(), false))
                .isPhoneVerified(ConversionUtils.defaultIfNull(entity.getIsPhoneVerified(), false))
                .lastLoginAt(entity.getLastLoginAt())
                .createdAt(entity.getCreatedAt())
                .profileImageUrl(entity.getProfileImageUrl())
                .primaryRole(primaryRole)
                .primaryRoleLevel(primaryRoleLevel)
                .institutionAccessCount(institutionAccessCount)
                .primaryInstitution(primaryInstitution)
                .build();
    }

    public UserProfileDto mapToProfileDto(User entity) {
        if (entity == null) {
            return null;
        }

        return UserProfileDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .profileImageUrl(entity.getProfileImageUrl())
                .countryId(entity.getCountry() != null ? entity.getCountry().getId() : null)
                .countryName(entity.getCountry() != null ? entity.getCountry().getName() : null)
                .provinceId(entity.getProvince() != null ? entity.getProvince().getId() : null)
                .provinceName(entity.getProvince() != null ? entity.getProvince().getName() : null)
                .districtId(entity.getDistrict() != null ? entity.getDistrict().getId() : null)
                .districtName(entity.getDistrict() != null ? entity.getDistrict().getName() : null)
                .neighborhoodId(entity.getNeighborhood() != null ? entity.getNeighborhood().getId() : null)
                .neighborhoodName(entity.getNeighborhood() != null ? entity.getNeighborhood().getName() : null)
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .postalCode(entity.getPostalCode())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .emailNotifications(true) // Default values - these would be stored elsewhere
                .smsNotifications(true)
                .marketingEmails(false)
                .preferredLanguage("tr")
                .timezone("Europe/Istanbul")
                .build();
    }


    public User mapToEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User entity = new User();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail().toLowerCase().trim());
        entity.setPhone(cleanPhoneNumber(dto.getPhone()));
        entity.setFirstName(ConversionUtils.capitalizeWords(dto.getFirstName().trim()));
        entity.setLastName(ConversionUtils.capitalizeWords(dto.getLastName().trim()));
        entity.setUserType(dto.getUserType());
        entity.setIsEmailVerified(false);
        entity.setIsPhoneVerified(false);
        entity.setIsActive(true);
        entity.setAddressLine1(dto.getAddressLine1());
        entity.setAddressLine2(dto.getAddressLine2());
        entity.setPostalCode(dto.getPostalCode());

        // Generate email verification token
        entity.setEmailVerificationToken(generateVerificationToken());

        // Generate phone verification code if phone provided
        if (StringUtils.hasText(dto.getPhone())) {
            entity.setPhoneVerificationCode(generatePhoneVerificationCode());
        }

        return entity;
    }



    public User mapToEntity(UserRegistrationDto dto) {
        if (dto == null) {
            return null;
        }

        User entity = new User();
        entity.setEmail(dto.getEmail().toLowerCase().trim());
        entity.setPhone(cleanPhoneNumber(dto.getPhone()));
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setFirstName(ConversionUtils.capitalizeWords(dto.getFirstName().trim()));
        entity.setLastName(ConversionUtils.capitalizeWords(dto.getLastName().trim()));
        entity.setUserType(dto.getUserType());
        entity.setIsEmailVerified(false);
        entity.setIsPhoneVerified(false);
        entity.setIsActive(true);
        entity.setAddressLine1(dto.getAddressLine1());
        entity.setAddressLine2(dto.getAddressLine2());
        entity.setPostalCode(dto.getPostalCode());

        // Generate email verification token
        entity.setEmailVerificationToken(generateVerificationToken());

        // Generate phone verification code if phone provided
        if (StringUtils.hasText(dto.getPhone())) {
            entity.setPhoneVerificationCode(generatePhoneVerificationCode());
        }

        return entity;
    }

    public void updateEntity(UserUpdateDto dto, User entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (StringUtils.hasText(dto.getFirstName())) {
            entity.setFirstName(ConversionUtils.capitalizeWords(dto.getFirstName().trim()));
        }

        if (StringUtils.hasText(dto.getLastName())) {
            entity.setLastName(ConversionUtils.capitalizeWords(dto.getLastName().trim()));
        }

        if (StringUtils.hasText(dto.getPhone())) {
            String cleanPhone = cleanPhoneNumber(dto.getPhone());
            if (!cleanPhone.equals(entity.getPhone())) {
                entity.setPhone(cleanPhone);
                entity.setIsPhoneVerified(false);
                entity.setPhoneVerificationCode(generatePhoneVerificationCode());
            }
        }

        if (StringUtils.hasText(dto.getProfileImageUrl())) {
            entity.setProfileImageUrl(dto.getProfileImageUrl());
        }

        // Location updates
        if (dto.getCountryId() != null) {
            // Country would be set by service layer
        }

        if (dto.getProvinceId() != null) {
            // Province would be set by service layer
        }

        if (dto.getDistrictId() != null) {
            // District would be set by service layer
        }

        if (dto.getNeighborhoodId() != null) {
            // Neighborhood would be set by service layer
        }

        if (StringUtils.hasText(dto.getAddressLine1())) {
            entity.setAddressLine1(dto.getAddressLine1().trim());
        }

        if (StringUtils.hasText(dto.getAddressLine2())) {
            entity.setAddressLine2(dto.getAddressLine2().trim());
        }

        if (StringUtils.hasText(dto.getPostalCode())) {
            entity.setPostalCode(dto.getPostalCode().trim());
        }

        if (dto.getLatitude() != null) {
            entity.setLatitude(dto.getLatitude());
        }

        if (dto.getLongitude() != null) {
            entity.setLongitude(dto.getLongitude());
        }
    }

    // ================== ROLE CONVERTERS ==================




    // ================== PERMISSION CONVERTERS ==================



    // ================== USER INSTITUTION ACCESS CONVERTERS ==================

    public UserInstitutionAccessDto mapToDto(UserInstitutionAccess entity) {
        if (entity == null) {
            return null;
        }

        // Entity name would need to be resolved by service layer
        String entityName = resolveEntityName(entity.getAccessType(), entity.getEntityId());

        return UserInstitutionAccessDto.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .userFullName(entity.getUser() != null ?
                        buildFullName(entity.getUser().getFirstName(), entity.getUser().getLastName()) : null)
                .accessType(entity.getAccessType())
                .entityId(entity.getEntityId())
                .entityName(entityName)
                .grantedAt(entity.getGrantedAt())
                .expiresAt(entity.getExpiresAt())
                .isActive(isInstitutionAccessActive(entity))
                .build();
    }

    public UserInstitutionAccess mapToEntity(UserInstitutionAccessGrantDto dto, User user) {
        if (dto == null || user == null) {
            return null;
        }

        UserInstitutionAccess entity = new UserInstitutionAccess();
        entity.setUser(user);
        entity.setAccessType(dto.getAccessType());
        entity.setEntityId(dto.getEntityId());
        entity.setGrantedAt(LocalDateTime.now());
        entity.setExpiresAt(dto.getExpiresAt());
        entity.setIsActive(true);

        return entity;
    }

    // ================== AUTHENTICATION CONVERTERS ==================

    public LoginResponseDto mapToLoginResponse(User user, String accessToken, String refreshToken,
                                               Long expiresIn, List<String> permissions) {
        if (user == null) {
            return null;
        }

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(mapToDto(user))
                .permissions(ConversionUtils.defaultIfNull(permissions, new ArrayList<>()))
                .build();
    }

    public ApiResponseDto<UserDto> mapToApiResponse(User user, String message) {
        return ApiResponseDto.<UserDto>builder()
                .success(true)
                .message(message)
                .data(mapToDto(user))
                .timestamp(LocalDateTime.now().toString())
                .status(200)
                .build();
    }

    public <T> ApiResponseDto<T> mapToErrorResponse(String message, int status) {
        return ApiResponseDto.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now().toString())
                .status(status)
                .build();
    }

    // ================== STATISTICS CONVERTERS ==================

    public UserStatisticsDto createUserStatistics(Map<String, Long> counts) {
        if (counts == null) {
            counts = new HashMap<>();
        }

        return UserStatisticsDto.builder()
                .totalUsers(counts.getOrDefault("totalUsers", 0L))
                .activeUsers(counts.getOrDefault("activeUsers", 0L))
                .inactiveUsers(counts.getOrDefault("inactiveUsers", 0L))
                .institutionUsers(counts.getOrDefault("institutionUsers", 0L))
                .parentUsers(counts.getOrDefault("parentUsers", 0L))
                .usersRegisteredToday(counts.getOrDefault("usersRegisteredToday", 0L))
                .usersRegisteredThisWeek(counts.getOrDefault("usersRegisteredThisWeek", 0L))
                .usersRegisteredThisMonth(counts.getOrDefault("usersRegisteredThisMonth", 0L))
                .usersLoggedInToday(counts.getOrDefault("usersLoggedInToday", 0L))
                .usersLoggedInThisWeek(counts.getOrDefault("usersLoggedInThisWeek", 0L))
                .unverifiedEmailUsers(counts.getOrDefault("unverifiedEmailUsers", 0L))
                .unverifiedPhoneUsers(counts.getOrDefault("unverifiedPhoneUsers", 0L))
                .build();
    }

    // ================== ACTIVITY CONVERTERS ==================

    public UserActivityDto createUserActivity(User user, String activity, String description,
                                              String ipAddress, String userAgent, String deviceType,
                                              String location) {
        return UserActivityDto.builder()
                .userId(user != null ? user.getId() : null)
                .userFullName(user != null ? buildFullName(user.getFirstName(), user.getLastName()) : null)
                .activity(activity)
                .description(description)
                .activityTime(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceType(deviceType)
                .location(location)
                .build();
    }

    // ================== COLLECTION CONVERTERS ==================

    public List<UserDto> mapToDto(List<User> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<UserSummaryDto> mapToSummaryDto(List<User> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::mapToSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<UserListDto> mapToListDto(List<User> entities) {
        if (ConversionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::mapToListDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }



    public List<UserInstitutionAccessDto> mapInstitutionAccessToDto(Set<UserInstitutionAccess> institutionAccess) {
        if (institutionAccess == null || institutionAccess.isEmpty()) {
            return new ArrayList<>();
        }

        return institutionAccess.stream()
                .filter(Objects::nonNull)
                .map(this::mapToDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ================== PAGINATED RESPONSE CONVERTER ==================

    public <T> PaginatedResponseDto<T> mapToPaginatedResponse(List<T> content, int page, int size,
                                                              long totalElements, int totalPages) {
        boolean first = page == 0;
        boolean last = page >= totalPages - 1;
        boolean hasNext = !last;
        boolean hasPrevious = !first;

        return PaginatedResponseDto.<T>builder()
                .content(ConversionUtils.defaultIfNull(content, new ArrayList<>()))
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(first)
                .last(last)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }

    // ================== HELPER METHODS ==================

    private String buildFullName(String firstName, String lastName) {
        List<String> nameParts = new ArrayList<>();

        if (StringUtils.hasText(firstName)) {
            nameParts.add(firstName.trim());
        }

        if (StringUtils.hasText(lastName)) {
            nameParts.add(lastName.trim());
        }

        return nameParts.isEmpty() ? null : String.join(" ", nameParts);
    }

    private String cleanPhoneNumber(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }

        // Remove all non-digits
        String digits = phone.replaceAll("\\D", "");

        // Handle Turkish numbers
        if (digits.startsWith("90")) {
            return digits;
        } else if (digits.startsWith("0") && digits.length() == 11) {
            return "90" + digits.substring(1);
        } else if (digits.length() == 10 && digits.startsWith("5")) {
            return "90" + digits;
        }

        return digits;
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }

    private String generatePhoneVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private boolean isUserRoleActive(UserRole userRole) {
        if (userRole == null || !ConversionUtils.defaultIfNull(userRole.getIsActive(), true)) {
            return false;
        }

        if (userRole.getExpiresAt() != null && userRole.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    private boolean isInstitutionAccessActive(UserInstitutionAccess access) {
        if (access == null || !ConversionUtils.defaultIfNull(access.getIsActive(), true)) {
            return false;
        }

        if (access.getExpiresAt() != null && access.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    private String resolveEntityName(com.genixo.education.search.enumaration.AccessType accessType, Long entityId) {
        // This would be implemented by the service layer to resolve actual names
        // For now, return a placeholder
        if (accessType == null || entityId == null) {
            return null;
        }

        return accessType.name() + " #" + entityId;
    }

    // ================== VALIDATION HELPERS ==================

    public boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.toLowerCase().matches(emailRegex);
    }

    public boolean isValidPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }

        String cleanPhone = cleanPhoneNumber(phone);
        return cleanPhone != null && cleanPhone.length() >= 10;
    }

    public boolean isStrongPassword(String password) {
        if (!StringUtils.hasText(password)) {
            return false;
        }

        // At least 8 characters, contains uppercase, lowercase, digit and special character
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordRegex);
    }

    public UserSummaryDto mapUserToSummaryDto(User userById) {

        if (userById == null) {
            return null;
        }
        return UserSummaryDto.builder()
                .id(userById.getId())
                .fullName(buildFullName(userById.getFirstName(), userById.getLastName()))
                .email(userById.getEmail())
                .phone(userById.getPhone())
                .userType(userById.getUserType())
                .profileImageUrl(userById.getProfileImageUrl())
                .isActive(ConversionUtils.defaultIfNull(userById.getIsActive(), true))
                .build();
    }
}