package com.genixo.education.search.api.rest;

import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.service.UserService;
import com.genixo.education.search.service.auth.AuthenticationRequest;
import com.genixo.education.search.service.auth.AuthenticationResponse;
import com.genixo.education.search.service.auth.AuthenticationService;
import com.genixo.education.search.service.auth.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService service;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody User user
    ) {
        return ResponseEntity.ok(service.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse response = service.authenticate(request);
        System.out.println("authenticate");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout");
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
        // CEYHUN
    }



    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String username = jwtTokenProvider.getUsername(token);
            User user = userService.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
