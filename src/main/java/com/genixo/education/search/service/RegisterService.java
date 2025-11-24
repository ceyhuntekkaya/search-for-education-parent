package com.genixo.education.search.service;

import com.genixo.education.search.dto.register.*;
import com.genixo.education.search.dto.user.UserDto;
import com.genixo.education.search.entity.institution.Brand;
import com.genixo.education.search.entity.institution.Campus;
import com.genixo.education.search.entity.location.Country;
import com.genixo.education.search.entity.location.District;
import com.genixo.education.search.entity.location.Province;
import com.genixo.education.search.entity.user.Role;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserInstitutionAccess;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.enumaration.AccessType;
import com.genixo.education.search.enumaration.RoleLevel;
import com.genixo.education.search.enumaration.UserType;
import com.genixo.education.search.repository.user.UserInstitutionAccessRepository;
import com.genixo.education.search.repository.user.UserRepository;
import com.genixo.education.search.repository.user.UserRoleRepository;
import com.genixo.education.search.service.converter.UserConverterService;
import com.genixo.education.search.util.ConversionUtils;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverterService converterService;
    private final EmailService emailService;
    private final LocationService locationService;
    private final InstitutionService institutionService;
    private static final SecureRandom secureRandom = new SecureRandom();
    private final UserService userService;
    private final UserInstitutionAccessRepository userInstitutionAccessRepository;

    @Transactional
    public UserDto registerCredential(RegisterCredentialDto registerCredentialDto) {
        User user = new User();
        user.setEmail(registerCredentialDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerCredentialDto.getPassword()));


        user.setIsEmailVerified(false);
        user.setIsPhoneVerified(false);
        user.setEmail(registerCredentialDto.getEmail());
        user.setEmail(registerCredentialDto.getEmail());
        user.setEmailVerificationToken(null);
        user.setPhoneVerificationCode(null);
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);
        user.setLastLoginAt(LocalDateTime.now());
        user.setProfileImageUrl(null);
        user.setCountry(null);
        user.setProvince(null);
        user.setDistrict(null);
        user.setNeighborhood(null);

        Set<UserInstitutionAccess> userInstitutionAccess = new HashSet<>();
        user.setInstitutionAccess(userInstitutionAccess);


        UUID uuid = UUID.randomUUID();
        user.setPhone(uuid.toString());
        user.setFirstName("");
        user.setLastName("");
        user.setUserType(UserType.INSTITUTION_USER);


        user.setAddressLine1(null);
        user.setAddressLine2(null);
        user.setPostalCode(null);
        user.setLatitude(null);
        user.setLongitude(null);


        User createdUser = userRepository.saveAndFlush(user);

        UserRole userRole = new UserRole();
        userRole.setUser(createdUser);
        userRole.setRole(Role.COMPANY);
        userRole.setRoleLevel(RoleLevel.CAMPUS);
        userRoleRepository.saveAndFlush(userRole);


        User result = userRepository.findById(createdUser.getId()).orElse(null);
        return converterService.mapToDto(result);
    }

    public UserDto registerIdentity(RegisterIdentityDto registerIdentityDto) throws Exception {
        User user = userRepository.findById(registerIdentityDto.getUserId()).orElse(null);
        if (user == null) {
            return null;
        }

        user.setFirstName(registerIdentityDto.getFirstName());
        user.setLastName(registerIdentityDto.getLastName());
        user.setPhone(registerIdentityDto.getPhone());

        //String code = generate4DigitCode();
        String code = "1234";
        user.setPhoneVerificationCode(code);
        user.setEmailVerificationToken(code);
        emailService.sendCode(code, user.getEmail(), user.getFirstName(), user.getLastName());

        userRepository.saveAndFlush(user);

        User result = userRepository.findById(user.getId()).orElse(null);
        return converterService.mapToDto(result);
    }

    public UserDto registerConfirm(RegisterConfirmDto registerConfirmDto) {
        User user = userRepository.findById(registerConfirmDto.getUserId()).orElse(null);
        if (user == null) {
            return null;
        }
        if (user.getPhoneVerificationCode().equals(registerConfirmDto.getCode())) {
            user.setIsEmailVerified(true);
            user.setIsPhoneVerified(true);
            user.setPhoneVerificationCode(null);
            user.setEmailVerificationToken(null);
        }
        userRepository.saveAndFlush(user);


        User result = userRepository.findById(user.getId()).orElse(null);
        return converterService.mapToDto(result);
    }

    public UserDto registerCampus(RegisterCampusDto registerCampusDto) {
        User user = userRepository.findById(registerCampusDto.getUserId()).orElse(null);
        if (user == null) {
            return null;
        }


        Brand brand = institutionService.getBrandClassById(registerCampusDto.getBrandId());
        District district = locationService.getDistrictClassById(registerCampusDto.getDistrictId());
        Country country = locationService.getCountryClassById(registerCampusDto.getCountryId());
        Province province = locationService.getProvinceClassById(registerCampusDto.getProvinceId());

        Campus campus = new Campus();
        campus.setName(registerCampusDto.getName());
        String slug = ConversionUtils.generateSlug(registerCampusDto.getName());
        campus.setSlug(slug);
        campus.setDescription("");
        campus.setEmail(registerCampusDto.getEmail());
        campus.setLogoUrl("default_logo.jpg");
        campus.setCoverImageUrl("default_cover.jpg");
        campus.setPhone(registerCampusDto.getPhone());
        campus.setFax("");


        campus.setAddressLine1(registerCampusDto.getAddressLine1());
        campus.setAddressLine2(registerCampusDto.getAddressLine2());
        campus.setPostalCode(registerCampusDto.getPostalCode());
        campus.setDistrict(district);
        campus.setNeighborhood(null);
        campus.setCountry(country);
        campus.setProvince(province);

        campus.setLatitude(0d);
        campus.setLatitude(0d);
        campus.setEstablishedYear(0);
        campus.setBrand(brand);
        campus.setCreatedBy(user.getId());
        Campus createdCampus = institutionService.createCampusByClass(campus);

        UserInstitutionAccess access = new UserInstitutionAccess();
        access.setAccessType(AccessType.CAMPUS);
        access.setUser(user);
        access.setEntityId(createdCampus.getId());
        access.setIsActive(true);
        access.setGrantedAt(LocalDateTime.now());
        userInstitutionAccessRepository.saveAndFlush(access);

        User result = userRepository.findById(user.getId()).orElse(null);
        return converterService.mapToDto(result);
    }

    public UserDto registerSubscription(RegisterSubscriptionDto registerSubscriptionDto) {
        User user = userRepository.findById(registerSubscriptionDto.getUserId()).orElse(null);
        if (user == null) {
            return null;
        }


        User result = userRepository.findById(user.getId()).orElse(null);
        return converterService.mapToDto(result);
    }

    public UserDto registerPayment(RegisterPaymentDto registerPaymentDto) {
        User user = userRepository.findById(registerPaymentDto.getUserId()).orElse(null);
        if (user == null) {
            return null;
        }


        User result = userRepository.findById(user.getId()).orElse(null);
        return converterService.mapToDto(result);
    }

    public UserDto registerVerification(RegisterVerificationCodeDto registerVerificationCodeDto) {
        User user = userRepository.findById(registerVerificationCodeDto.getUserId()).orElse(null);
        if (user == null) {
            return null;
        }


        User result = userRepository.findById(user.getId()).orElse(null);
        return converterService.mapToDto(result);
    }


    public static String generate4DigitCode() {
        int code = secureRandom.nextInt(10_000); // 0..9999
        return String.format("%04d", code);
    }


}
