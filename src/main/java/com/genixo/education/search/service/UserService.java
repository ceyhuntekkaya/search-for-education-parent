package com.genixo.education.search.service;



import com.genixo.education.search.common.exception.*;
import com.genixo.education.search.dto.user.*;
import com.genixo.education.search.entity.institution.School;
import com.genixo.education.search.enumaration.AccessType;
import com.genixo.education.search.enumaration.PermissionCategory;
import com.genixo.education.search.enumaration.UserType;
import com.genixo.education.search.entity.user.*;
import com.genixo.education.search.repository.insitution.SchoolRepository;
import com.genixo.education.search.repository.location.CountryRepository;
import com.genixo.education.search.repository.location.DistrictRepository;
import com.genixo.education.search.repository.location.NeighborhoodRepository;
import com.genixo.education.search.repository.location.ProvinceRepository;
import com.genixo.education.search.repository.user.*;
import com.genixo.education.search.service.converter.UserConverterService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.security.sasl.AuthenticationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users, authentication, and authorization
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserInstitutionAccessRepository userInstitutionAccessRepository;
    private final CountryRepository countryRepository;
    private final SchoolRepository schoolRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final NeighborhoodRepository neighborhoodRepository;

    private final UserConverterService converterService;
    private final PasswordEncoder passwordEncoder;



    public UserDto registerUser(UserRegistrationDto registrationDto) throws ValidationException {

        // Validate registration data
        validateRegistrationData(registrationDto);

        // Check if user already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new ValidationException("User with this email already exists");
        }

        if (StringUtils.hasText(registrationDto.getPhone()) &&
                userRepository.existsByPhone(registrationDto.getPhone())) {
            throw new ValidationException("User with this phone number already exists");
        }

        // Create new user entity
        User user = new User();
        user.setEmail(registrationDto.getEmail().toLowerCase().trim());
        user.setPhone(registrationDto.getPhone());
        user.setFirstName(registrationDto.getFirstName().trim());
        user.setLastName(registrationDto.getLastName().trim());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setUserType(registrationDto.getUserType());
        user.setIsEmailVerified(false);
        user.setIsPhoneVerified(false);

        // Set location if provided
        setUserLocation(user, registrationDto.getCountryId(), registrationDto.getProvinceId(),
                registrationDto.getDistrictId(), registrationDto.getNeighborhoodId());

        user.setAddressLine1(registrationDto.getAddressLine1());
        user.setAddressLine2(registrationDto.getAddressLine2());
        user.setPostalCode(registrationDto.getPostalCode());

        // Generate verification tokens
        user.setEmailVerificationToken(generateVerificationToken());
        user.setPhoneVerificationCode(generatePhoneVerificationCode());

        // Save user
        User savedUser = userRepository.save(user);

        // Send verification emails/SMS
        // emailService.sendEmailVerification(savedUser.getEmail(), savedUser.getEmailVerificationToken());
        // if (StringUtils.hasText(savedUser.getPhone())) {
        //     smsService.sendPhoneVerification(savedUser.getPhone(), savedUser.getPhoneVerificationCode());
        // }

        return converterService.mapToDto(savedUser);
    }

    /**
     * Verify user email with token
     */
    public void verifyEmail(EmailVerificationDto verificationDto) throws ValidationException {

        User user = userRepository.findByEmailVerificationToken(verificationDto.getToken())
                .orElseThrow(() -> new ValidationException("Invalid or expired verification token"));

        user.setIsEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);

    }

    /**
     * Verify user phone with code
     */
    public void verifyPhone(PhoneVerificationDto verificationDto) throws ValidationException {

        User user = userRepository.findByPhoneAndPhoneVerificationCode(
                        verificationDto.getPhone(), verificationDto.getVerificationCode())
                .orElseThrow(() -> new ValidationException("Invalid phone number or verification code"));

        user.setIsPhoneVerified(true);
        user.setPhoneVerificationCode(null);
        userRepository.save(user);

    }

    /**
     * Authenticate user login
     */
    @Transactional(readOnly = true)
    public UserDto authenticateUser(UserLoginDto loginDto) throws AuthenticationException {

        User user = userRepository.findByEmailOrPhone(loginDto.getEmailOrPhone(), loginDto.getEmailOrPhone())
                .orElseThrow(() -> new AuthenticationException("Invalid email/phone or password"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid email/phone or password");
        }

        if (!user.getIsActive()) {
            throw new AuthenticationException("User account is inactive");
        }

        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return converterService.mapToDto(user);
    }

    // ========================= USER MANAGEMENT =========================

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return converterService.mapToDto(user);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    /**
     * Get user profile
     */
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return converterService.mapToProfileDto(user);
    }

    /**
     * Update user profile
     */
    public UserProfileDto updateUserProfile(Long userId, UserUpdateDto updateDto) throws ValidationException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Update basic information
        if (StringUtils.hasText(updateDto.getFirstName())) {
            user.setFirstName(updateDto.getFirstName().trim());
        }
        if (StringUtils.hasText(updateDto.getLastName())) {
            user.setLastName(updateDto.getLastName().trim());
        }
        if (StringUtils.hasText(updateDto.getPhone())) {
            // Check if phone is already taken by another user
            if (userRepository.existsByPhoneAndIdNot(updateDto.getPhone(), userId)) {
                throw new ValidationException("Phone number is already taken");
            }
            user.setPhone(updateDto.getPhone());
            user.setIsPhoneVerified(false); // Re-verify phone if changed
            user.setPhoneVerificationCode(generatePhoneVerificationCode());
        }

        // Update location
        setUserLocation(user, updateDto.getCountryId(), updateDto.getProvinceId(),
                updateDto.getDistrictId(), updateDto.getNeighborhoodId());

        user.setAddressLine1(updateDto.getAddressLine1());
        user.setAddressLine2(updateDto.getAddressLine2());
        user.setPostalCode(updateDto.getPostalCode());
        user.setLatitude(updateDto.getLatitude());
        user.setLongitude(updateDto.getLongitude());
        user.setProfileImageUrl(updateDto.getProfileImageUrl());

        User updatedUser = userRepository.save(user);

        return converterService.mapToProfileDto(updatedUser);
    }

    /**
     * Change user password
     */
    public void changePassword(Long userId, PasswordChangeDto passwordChangeDto) throws ValidationException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new ValidationException("Current password is incorrect");
        }

        // Validate new password
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new ValidationException("New password and confirmation do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);

    }

    /**
     * Reset password request
     */
    public void requestPasswordReset(PasswordResetDto passwordResetDto) {

        User user = userRepository.findByEmailOrPhone(passwordResetDto.getEmailOrPhone(), passwordResetDto.getEmailOrPhone())
                .orElseThrow(() -> new EntityNotFoundException("User not found with provided email/phone"));

        // Generate reset token
        String resetToken = generatePasswordResetToken();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetExpiresAt(LocalDateTime.now().plusHours(1)); // 1 hour expiry

        userRepository.save(user);

        // Send reset email/SMS
        // if (passwordResetDto.getEmailOrPhone().contains("@")) {
        //     emailService.sendPasswordReset(user.getEmail(), resetToken);
        // } else {
        //     smsService.sendPasswordReset(user.getPhone(), resetToken);
        // }

    }

    /**
     * Confirm password reset
     */
    public void confirmPasswordReset(PasswordResetConfirmDto confirmDto) throws ValidationException {

        User user = userRepository.findByPasswordResetToken(confirmDto.getToken())
                .orElseThrow(() -> new ValidationException("Invalid or expired reset token"));

        // Check token expiry
        if (user.getPasswordResetExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Reset token has expired");
        }

        // Validate new password
        if (!confirmDto.getNewPassword().equals(confirmDto.getConfirmPassword())) {
            throw new ValidationException("Password and confirmation do not match");
        }

        // Update password and clear reset token
        user.setPassword(passwordEncoder.encode(confirmDto.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);

        userRepository.save(user);

    }

    // ========================= USER SEARCH & LISTING =========================

    /**
     * Search users with filters
     */
    @Transactional(readOnly = true)
    public PaginatedResponseDto<UserListDto> searchUsers(UserSearchDto searchDto) {

        // Build sort
        Sort sort = buildUserSort(searchDto.getSortBy(), searchDto.getSortDirection());
        Pageable pageable = PageRequest.of(
                searchDto.getPage() != null ? searchDto.getPage() : 0,
                searchDto.getSize() != null ? searchDto.getSize() : 20,
                sort
        );

        // Search users based on criteria

        /*
        Page<User> userPage = userRepository.searchUsers(
                searchDto.getSearchTerm(),
                searchDto.getUserType(),
                searchDto.getIsActive(),
                searchDto.getRoleId(),
                searchDto.getRoleLevel(),
                searchDto.getInstitutionId(),
                searchDto.getAccessType(),
                searchDto.getCreatedAfter(),
                searchDto.getCreatedBefore(),
                searchDto.getLastLoginAfter(),
                searchDto.getLastLoginBefore(),
                searchDto.getCountryId(),
                searchDto.getProvinceId(),
                searchDto.getDistrictId(),
                searchDto.getNeighborhoodId(),
                pageable
        );

        List<UserListDto> userDtos = userPage.getContent().stream()
                .map(converterService::toUserListDto)
                .collect(Collectors.toList());

                return PaginatedResponseDto.<UserListDto>builder()
                .content(userDtos)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
         */
// ceyhun
        return null;
    }

    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public UserStatisticsDto getUserStatistics() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusWeeks(1);
        LocalDateTime monthStart = now.minusMonths(1);

        return UserStatisticsDto.builder()
                .totalUsers(userRepository.count())
                .activeUsers(userRepository.countByIsActive(true))
                .inactiveUsers(userRepository.countByIsActive(false))
                .institutionUsers(userRepository.countByUserType(UserType.INSTITUTION_USER))
                .parentUsers(userRepository.countByUserType(UserType.PARENT))
                .usersRegisteredToday(userRepository.countByCreatedAtAfter(todayStart))
                .usersRegisteredThisWeek(userRepository.countByCreatedAtAfter(weekStart))
                .usersRegisteredThisMonth(userRepository.countByCreatedAtAfter(monthStart))
                .usersLoggedInToday(userRepository.countByLastLoginAtAfter(todayStart))
                .usersLoggedInThisWeek(userRepository.countByLastLoginAtAfter(weekStart))
                .unverifiedEmailUsers(userRepository.countByIsEmailVerified(false))
                .unverifiedPhoneUsers(userRepository.countByIsPhoneVerified(false))
                .build();
    }

    // ========================= ROLE MANAGEMENT =========================





    // ========================= INSTITUTION ACCESS MANAGEMENT =========================

    /**
     * Grant institution access to user
     */
    public UserInstitutionAccessDto grantInstitutionAccess(UserInstitutionAccessGrantDto grantDto) throws ValidationException {

        User user = userRepository.findById(grantDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Check if access already exists
        if (userInstitutionAccessRepository.existsByUserIdAndAccessTypeAndEntityIdAndIsActiveTrue(
                grantDto.getUserId(), grantDto.getAccessType(), grantDto.getEntityId())) {
            throw new ValidationException("User already has access to this entity");
        }

        UserInstitutionAccess access = new UserInstitutionAccess();
        access.setUser(user);
        access.setAccessType(grantDto.getAccessType());
        access.setEntityId(grantDto.getEntityId());
        access.setGrantedAt(LocalDateTime.now());
        access.setExpiresAt(grantDto.getExpiresAt());

        UserInstitutionAccess savedAccess = userInstitutionAccessRepository.save(access);

        return converterService.mapToDto(savedAccess);
    }

    /**
     * Revoke institution access from user
     */
    public void revokeInstitutionAccess(Long userId, AccessType accessType, Long entityId) {

        UserInstitutionAccess access = userInstitutionAccessRepository
                .findByUserIdAndAccessTypeAndEntityIdAndIsActiveTrue(userId, accessType, entityId)
                .orElseThrow(() -> new EntityNotFoundException("Institution access not found"));

        access.setIsActive(false);
        userInstitutionAccessRepository.save(access);

    }

    /**
     * Get user institution access
     */
    @Transactional(readOnly = true)
    public List<UserInstitutionAccessDto> getUserInstitutionAccess(Long userId) {
        List<UserInstitutionAccess> accessList = userInstitutionAccessRepository
                .findByUserIdAndIsActiveTrueOrderByGrantedAtDesc(userId);
        return accessList.stream()
                .map(converterService::mapToDto)
                .collect(Collectors.toList());
    }

    // ========================= UTILITY METHODS =========================

    private void validateRegistrationData(UserRegistrationDto registrationDto) throws ValidationException {
        if (!StringUtils.hasText(registrationDto.getEmail())) {
            throw new ValidationException("Email is required");
        }
        if (!isValidEmail(registrationDto.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        if (!StringUtils.hasText(registrationDto.getFirstName())) {
            throw new ValidationException("First name is required");
        }
        if (!StringUtils.hasText(registrationDto.getLastName())) {
            throw new ValidationException("Last name is required");
        }
        if (!StringUtils.hasText(registrationDto.getPassword())) {
            throw new ValidationException("Password is required");
        }
        if (registrationDto.getPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new ValidationException("Password and confirmation do not match");
        }
        if (registrationDto.getUserType() == null) {
            throw new ValidationException("User type is required");
        }
    }

    private void setUserLocation(User user, Long countryId, Long provinceId, Long districtId, Long neighborhoodId) {
        if (countryId != null) {
            user.setCountry(countryRepository.findById(countryId).orElse(null));
        }
        if (provinceId != null) {
            user.setProvince(provinceRepository.findById(provinceId).orElse(null));
        }
        if (districtId != null) {
            user.setDistrict(districtRepository.findById(districtId).orElse(null));
        }
        if (neighborhoodId != null) {
            user.setNeighborhood(neighborhoodRepository.findById(neighborhoodId).orElse(null));
        }
    }

    private Sort buildUserSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        String sortField = "createdAt"; // default
        if (StringUtils.hasText(sortBy)) {
            switch (sortBy.toLowerCase()) {
                case "name":
                    sortField = "firstName";
                    break;
                case "email":
                    sortField = "email";
                    break;
                case "phone":
                    sortField = "phone";
                    break;
                case "created":
                    sortField = "createdAt";
                    break;
                case "lastlogin":
                    sortField = "lastLoginAt";
                    break;
                default:
                    sortField = "createdAt";
            }
        }

        return Sort.by(direction, sortField);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String generatePhoneVerificationCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    private String generatePasswordResetToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    public List<UserSummaryDto> getUserByCampus(Long campusId) {
        List<School> schools = schoolRepository.getSchoolsByCampus(campusId);
        Set<Long> schoolIds = schools.stream()
                .map(School::getId)
                .collect(Collectors.toSet());
        List<User> users = userRepository.findByCampus(campusId, schoolIds);
        return converterService.mapToSummaryDto(users);
    }
}
