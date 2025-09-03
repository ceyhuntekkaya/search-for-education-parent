package com.genixo.education.search.api.rest;

import com.genixo.education.search.dto.user.*;
import com.genixo.education.search.service.UserService;
import com.genixo.education.search.enumaration.AccessType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for user registration, authentication, profile management, and access control")
public class UserController {

    private final UserService userService;



    /*
        @RestController
        @RequestMapping("/api/admin")
        @PreAuthorize("hasRole('ADMIN')") // Rol kontrolü
        public class AdminController {

        @GetMapping("/users")
        @PreAuthorize("hasAuthority('USER_READ')") // Spesifik yetki kontrolü
        public ResponseEntity<?> getUsers() {
            // ...
        }

        @PostMapping("/users")
        @PreAuthorize("hasRole('ADMIN') and hasAuthority('USER_CREATE')") // Rol ve yetki kontrolü
        public ResponseEntity<?> createUser() {
            // ...
    }
}
     */
    // ================================ USER REGISTRATION & AUTHENTICATION ================================

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid registration data or user already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation errors")
    })
    public ResponseEntity<ApiResponse<UserDto>> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto,
            HttpServletRequest request) {

        log.info("User registration request for email: {}", registrationDto.getEmail());

        try {
            UserDto user = userService.registerUser(registrationDto);

            ApiResponse<UserDto> response = ApiResponse.success(user, "User registered successfully. Please check your email for verification.");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (com.genixo.education.search.common.exception.ValidationException e) {
            ApiResponse<UserDto> response = ApiResponse.error(e.getMessage());
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
/*
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email/phone and password")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials or inactive account"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid login data")
    })
    public ResponseEntity<ApiResponse<UserDto>> authenticateUser(
            @Valid @RequestBody UserLoginDto loginDto,
            HttpServletRequest request) {

        log.info("Login attempt for: {}", loginDto.getEmailOrPhone());

        try {
            UserDto user = userService.authenticateUser(loginDto);

            ApiResponse<UserDto> response = ApiResponse.success(user, "Login successful");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (javax.security.sasl.AuthenticationException e) {
            ApiResponse<UserDto> response = ApiResponse.error(e.getMessage());
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

 */

    // ================================ EMAIL & PHONE VERIFICATION ================================

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify user email with verification token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired verification token")
    })
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody EmailVerificationDto verificationDto,
            HttpServletRequest request) {

        log.info("Email verification request with token: {}", verificationDto.getToken());

        try {
            userService.verifyEmail(verificationDto);

            ApiResponse<Void> response = ApiResponse.success(null, "Email verified successfully");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (com.genixo.education.search.common.exception.ValidationException e) {
            ApiResponse<Void> response = ApiResponse.error(e.getMessage());
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/verify-phone")
    @Operation(summary = "Verify phone", description = "Verify user phone with verification code")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Phone verified successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid phone number or verification code")
    })
    public ResponseEntity<ApiResponse<Void>> verifyPhone(
            @Valid @RequestBody PhoneVerificationDto verificationDto,
            HttpServletRequest request) {

        log.info("Phone verification request for: {}", verificationDto.getPhone());

        try {
            userService.verifyPhone(verificationDto);

            ApiResponse<Void> response = ApiResponse.success(null, "Phone verified successfully");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (com.genixo.education.search.common.exception.ValidationException e) {
            ApiResponse<Void> response = ApiResponse.error(e.getMessage());
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ================================ PASSWORD MANAGEMENT ================================

    @PostMapping("/password/reset")
    @Operation(summary = "Request password reset", description = "Request password reset via email or SMS")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset email/SMS sent"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(
            @Valid @RequestBody PasswordResetDto passwordResetDto,
            HttpServletRequest request) {

        log.info("Password reset request for: {}", passwordResetDto.getEmailOrPhone());

        try {
            userService.requestPasswordReset(passwordResetDto);

            ApiResponse<Void> response = ApiResponse.success(null,
                    "Password reset instructions have been sent to your email/phone");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            ApiResponse<Void> response = ApiResponse.error("User not found with provided email/phone");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/password/reset/confirm")
    @Operation(summary = "Confirm password reset", description = "Confirm password reset with token and new password")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired token, or password validation failed")
    })
    public ResponseEntity<ApiResponse<Void>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmDto confirmDto,
            HttpServletRequest request) {

        log.info("Password reset confirmation with token: {}", confirmDto.getToken());

        try {
            userService.confirmPasswordReset(confirmDto);

            ApiResponse<Void> response = ApiResponse.success(null, "Password reset successful");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (com.genixo.education.search.common.exception.ValidationException e) {
            ApiResponse<Void> response = ApiResponse.error(e.getMessage());
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/{userId}/password/change")
    @Operation(summary = "Change password", description = "Change user password (requires authentication)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid current password or new password validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('USER') and (#userId == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Valid @RequestBody PasswordChangeDto passwordChangeDto,
            HttpServletRequest request) {

        log.info("Password change request for user: {}", userId);

        try {
            userService.changePassword(userId, passwordChangeDto);

            ApiResponse<Void> response = ApiResponse.success(null, "Password changed successfully");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (com.genixo.education.search.common.exception.ValidationException e) {
            ApiResponse<Void> response = ApiResponse.error(e.getMessage());
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (EntityNotFoundException e) {
            ApiResponse<Void> response = ApiResponse.error("User not found");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ================================ USER PROFILE MANAGEMENT ================================

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Get user information by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('USER') and (#userId == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long userId,
            HttpServletRequest request) {

        log.debug("Get user request: {}", userId);

        try {
            UserDto user = userService.getUserById(userId);

            ApiResponse<UserDto> response = ApiResponse.success(user, "User retrieved successfully");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            ApiResponse<UserDto> response = ApiResponse.error("User not found");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get user profile", description = "Get detailed user profile information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('USER') and (#userId == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfile(
            @Parameter(description = "User ID") @PathVariable Long userId,
            HttpServletRequest request) {

        log.debug("Get user profile request: {}", userId);

        try {
            UserProfileDto profile = userService.getUserProfile(userId);

            ApiResponse<UserProfileDto> response = ApiResponse.success(profile, "User profile retrieved successfully");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            ApiResponse<UserProfileDto> response = ApiResponse.error("User not found");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "Update user profile", description = "Update user profile information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User profile updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid profile data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('USER') and (#userId == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateUserProfile(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDto updateDto,
            HttpServletRequest request) {

        log.info("Update user profile request for user: {}", userId);

        try {
            UserProfileDto profile = userService.updateUserProfile(userId, updateDto);

            ApiResponse<UserProfileDto> response = ApiResponse.success(profile, "User profile updated successfully");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            ApiResponse<UserProfileDto> response = ApiResponse.error("User not found");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (com.genixo.education.search.common.exception.ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    // ================================ USER SEARCH & ADMINISTRATION ================================

    @PostMapping("/search")
    @Operation(summary = "Search users", description = "Search users with various filters (Admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User search completed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponseDto<UserListDto>>> searchUsers(
            @Valid @RequestBody UserSearchDto searchDto,
            HttpServletRequest request) {

        log.debug("User search request");

        PaginatedResponseDto<UserListDto> users = userService.searchUsers(searchDto);

        ApiResponse<PaginatedResponseDto<UserListDto>> response = ApiResponse.success(users,
                "User search completed successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get user statistics", description = "Get comprehensive user statistics (Admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User statistics retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<UserStatisticsDto>> getUserStatistics(
            HttpServletRequest request) {

        log.debug("Get user statistics request");

        UserStatisticsDto statistics = userService.getUserStatistics();

        ApiResponse<UserStatisticsDto> response = ApiResponse.success(statistics,
                "User statistics retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ INSTITUTION ACCESS MANAGEMENT ================================

    @PostMapping("/{userId}/institution-access")
    @Operation(summary = "Grant institution access", description = "Grant institution access to user (Admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Institution access granted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid access data or access already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<UserInstitutionAccessDto>> grantInstitutionAccess(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Valid @RequestBody InstitutionAccessRequest accessRequest,
            HttpServletRequest request) {

        log.info("Grant institution access request for user: {} to entity: {}",
                userId, accessRequest.getEntityId());

        UserInstitutionAccessGrantDto grantDto = UserInstitutionAccessGrantDto.builder()
                .userId(userId)
                .accessType(accessRequest.getAccessType())
                .entityId(accessRequest.getEntityId())
                .expiresAt(accessRequest.getExpiresAt())
                .build();

        try {
            UserInstitutionAccessDto access = userService.grantInstitutionAccess(grantDto);

            ApiResponse<UserInstitutionAccessDto> response = ApiResponse.success(access,
                    "Institution access granted successfully");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EntityNotFoundException | com.genixo.education.search.common.exception.ValidationException e) {
            ApiResponse<UserInstitutionAccessDto> response = ApiResponse.error("User not found");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{userId}/institution-access")
    @Operation(summary = "Revoke institution access", description = "Revoke institution access from user (Admin only)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Institution access revoked successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User or institution access not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> revokeInstitutionAccess(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Access type") @RequestParam AccessType accessType,
            @Parameter(description = "Entity ID") @RequestParam Long entityId,
            HttpServletRequest request) {

        log.info("Revoke institution access request for user: {} from entity: {}", userId, entityId);

        try {
            userService.revokeInstitutionAccess(userId, accessType, entityId);

            ApiResponse<Void> response = ApiResponse.success(null, "Institution access revoked successfully");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            ApiResponse<Void> response = ApiResponse.error("Institution access not found");
            response.setPath(request.getRequestURI());
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{userId}/institution-access")
    @Operation(summary = "Get user institution access", description = "Get all institution access for user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Institution access retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('USER') and (#userId == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<ApiResponse<List<UserInstitutionAccessDto>>> getUserInstitutionAccess(
            @Parameter(description = "User ID") @PathVariable Long userId,
            HttpServletRequest request) {

        log.debug("Get institution access for user: {}", userId);

        List<UserInstitutionAccessDto> accessList = userService.getUserInstitutionAccess(userId);

        ApiResponse<List<UserInstitutionAccessDto>> response = ApiResponse.success(accessList,
                "Institution access retrieved successfully");
        response.setPath(request.getRequestURI());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    // ================================ REQUEST DTOs ================================

    public static class InstitutionAccessRequest {
        private AccessType accessType;
        private Long entityId;
        private LocalDateTime expiresAt;

        public AccessType getAccessType() {
            return accessType;
        }

        public void setAccessType(AccessType accessType) {
            this.accessType = accessType;
        }

        public Long getEntityId() {
            return entityId;
        }

        public void setEntityId(Long entityId) {
            this.entityId = entityId;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
        }
    }

    // ================================ EXCEPTION CLASSES (Placeholder) ================================

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }
    }

    public static class EntityNotFoundException extends RuntimeException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }
}