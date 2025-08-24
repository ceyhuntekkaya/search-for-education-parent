package com.genixo.education.search.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now().toString());
            body.put("status", HttpStatus.FORBIDDEN.value());
            body.put("error", "Forbidden");
            body.put("message", "Access Denied: Insufficient permissions");
            body.put("path", request.getRequestURI());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getOutputStream(), body);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/auth/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/course/**")).permitAll()

                        .requestMatchers(AntPathRequestMatcher.antMatcher("/exam-part-question-part/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/course-part-material/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/exam-part-question/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/exam-part/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/question-content-section/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/course-part/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/curriculum-content/**")).permitAll()


                        .requestMatchers(AntPathRequestMatcher.antMatcher("/course-lesson/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/exam/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/user/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/storage/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/health")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.OPTIONS)).permitAll()

                        // Protected endpoints with role-based authorization
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/security4/**")).hasRole("ADMIN")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/security1/**")).hasAnyRole("ADMIN", "USER_MANAGER")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/security3/**")).hasAuthority("DEVICE_CONTROL")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/security2/**")).hasAnyAuthority("DEVICE_CONTROL", "DEVICE_ADD")


                        // Catch-all: require authentication for any other endpoint
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider);

        return httpSecurity.build();
    }
}