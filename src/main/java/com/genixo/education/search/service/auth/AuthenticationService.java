package com.genixo.education.search.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genixo.education.search.dto.institution.SchoolDto;
import com.genixo.education.search.dto.user.UserDto;
import com.genixo.education.search.dto.user.UserRoleDto;
import com.genixo.education.search.entity.user.Token;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserInstitutionAccess;
import com.genixo.education.search.enumaration.AccessType;
import com.genixo.education.search.enumaration.TokenType;
import com.genixo.education.search.repository.user.UserRepository;
import com.genixo.education.search.service.InstitutionService;
import com.genixo.education.search.service.converter.UserConverterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserConverterService converterService;
    private final InstitutionService institutionService;

    public AuthenticationResponse register(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        var savedUser = repository.save(user);
        UserDto userDto = converterService.mapToDto(savedUser);
        var jwtToken = jwtService.generateToken(user, userDto);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public User registerNewUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setIsActive(true);

            var savedUser = repository.saveAndFlush(user);
            UserDto userDto = converterService.mapToDto(user);
            var jwtToken = jwtService.generateToken(user, userDto);
            var refreshToken = jwtService.generateRefreshToken(user);
            saveUserToken(savedUser, jwtToken);
            return savedUser;
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = repository.findByEmail(request.getUsername().trim()).orElse(null);
        if (user != null) {
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {

                UserDto _userDto = converterService.mapToDto(user);
                UserDto userDto = addUserSchoolsAndSubscriptions(user, _userDto);

                var jwtToken = jwtService.generateToken(user, userDto);
                var refreshToken = jwtService.generateRefreshToken(user);


                saveUserToken(user, jwtToken);
                return AuthenticationResponse.builder()
                        .user(userDto)
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    private UserDto addUserSchoolsAndSubscriptions(User user, UserDto userDto){
        Set<UserInstitutionAccess> institutionAccess = user.getInstitutionAccess();
        Set<SchoolDto> schoolSet = new HashSet<>();
        List<UserRoleDto> userRoles = userDto.getUserRoles();
        List<UserRoleDto> newUserRoles = new ArrayList<>();
        for(UserRoleDto userRole: userRoles){
            for(UserInstitutionAccess access: institutionAccess){
                if(access.getAccessType() == AccessType.BRAND){
                    List<SchoolDto> schools = institutionService.getSchoolByBrandId(access.getEntityId());
                    for(SchoolDto school: schools){
                        schoolSet.add(shortSchool(school));
                    }
                }
                else if(access.getAccessType() == AccessType.SCHOOL){
                    SchoolDto school = institutionService.getSchoolById(access.getEntityId());
                    schoolSet.add(shortSchool(school));
                }
                else if(access.getAccessType() == AccessType.CAMPUS){
                    List<SchoolDto> schools = institutionService.getSchoolByCampusId(access.getEntityId());
                    for(SchoolDto school: schools){
                        schoolSet.add(shortSchool(school));
                    }
                }
            }
            userRole.setSchools(schoolSet);
            newUserRoles.add(userRole);
        }
        userDto.setUserRoles(newUserRoles);
        return userDto;
    }



    private SchoolDto shortSchool(SchoolDto school){
        school.setPropertyValues(null);
        school.setPricings(null);
        school.setActiveCampaigns(null);
        return school;
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        //tokenRepository.save(token);
        try{
            //repository.setLastLogin(Instant.now()+"", user.getId());
        }catch (Exception ex){
            System.out.println(ex.toString());
        }

    }




    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                UserDto userDto = converterService.mapToDto(user);
                var accessToken = jwtService.generateToken(user, userDto);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}