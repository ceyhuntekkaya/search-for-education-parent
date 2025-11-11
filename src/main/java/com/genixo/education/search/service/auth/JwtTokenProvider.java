package com.genixo.education.search.service.auth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final JwtService jwtService;

    public JwtTokenProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return bearerToken != null && bearerToken.startsWith("Bearer ") ?
                bearerToken.substring(7) : null;
    }

    public String getUsername(String token) {
        return jwtService.extractUsername(token);
    }

    public boolean validateToken(String token) {
        try {
            return jwtService.extractAllClaims(token) != null;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}