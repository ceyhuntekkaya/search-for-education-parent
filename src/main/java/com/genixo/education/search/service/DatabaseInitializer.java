package com.genixo.education.search.service;


import com.genixo.education.search.common.exception.IdNotAvailableException;
import com.genixo.education.search.entity.user.Role;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.entity.user.UserRole;
import com.genixo.education.search.enumaration.UserType;
import com.genixo.education.search.service.auth.AuthenticationRequest;
import com.genixo.education.search.service.auth.AuthenticationResponse;
import com.genixo.education.search.service.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class DatabaseInitializer {
    private final AuthenticationService authenticationService;

    @Value("${storage.base}")
    private String TEST_OUTPUT_PATH;


    @EventListener(ApplicationReadyEvent.class)
    public void initializeCourseData() throws IdNotAvailableException, IOException {
        //User user = createUser();

    }



    User createUser() {




        User userAdmin = new User();
        userAdmin.setEmail("ankara@genixo.ai");
        userAdmin.setPassword("genixo123");
        userAdmin.setFirstName("Sistem");
        userAdmin.setLastName("Yöneticisi");
        userAdmin.setUserType(UserType.INSTITUTION_USER);

        authenticationService.register(userAdmin);




        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("admin@genixo.ai");
        request.setPassword("admin@genixo.ai");
        AuthenticationResponse authenticate = authenticationService.authenticate(request);

        return null;

    }



}
