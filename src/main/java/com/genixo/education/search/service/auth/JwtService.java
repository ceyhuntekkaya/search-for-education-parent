package com.genixo.education.search.service.auth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.genixo.education.search.entity.user.Role;
import com.genixo.education.search.entity.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private static final String USER_KEY = "user";
    private static final String TOKEN_INVALID = "Token is invalid.";
    private final ObjectMapper objectMapper;
    private final long jwtExpiration;
    private final long refreshExpiration;
    private final String secret;


    public JwtService(
            @Value("${application.security.jwt.expiration}") long jwtExpiration,
            @Value("${application.security.jwt.refresh-token.expiration}") long refreshExpiration,
            @Value("${application.security.jwt.secret-key}") String secret,
            ObjectMapper objectMapper
    ) {
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
        this.secret = secret;
        this.objectMapper = objectMapper
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails, User user) {
        if (userDetails == null || user == null) {
            throw new IllegalArgumentException("UserDetails or User cannot be null.");
        }
        logger.debug("Creating token: {}", userDetails.getUsername());

        Map<String, Object> userData = new HashMap<>();

        userData.put(USER_KEY, user);
/*
        userData.put("brands", user.getBrandSet());

        userData.put("departments", user.getDepartmentSet().stream()
                .map(Department::name)
                .collect(Collectors.toList()));



        userData.put("authorities", user.getAuthoritySet().stream()
                .map(Permission::name)
                .collect(Collectors.toList()));




        userData.put("roles", user.getUserRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
  */
        String token = generateToken(userData, userDetails);
        logger.info("Token created successfully: {}", userDetails.getUsername());
        return token;
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Map<String, Object> getUserData(String token) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map<String, Object> userData = new HashMap<>();
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            User user = mapper.convertValue(claims.get(USER_KEY), User.class);
            userData.put(USER_KEY, user);
            //userData.put(USER_KEY, claims.get(USER_KEY, User.class));
            return userData;
        } catch (Exception e) {
            logger.error("Token parsing error: {}", e.getMessage());
            throw new JwtException(TOKEN_INVALID);
        }
    }

    public User getUser(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Map<String, Object> userData = getUserData(token);
            return (User) userData.get("user");
        } catch (Exception e) {
            logger.error("User parsing error: {}", e.getMessage());
            throw new JwtException(TOKEN_INVALID);
        }
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }


    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(objectMapper))  // ObjectMapper'ı buraya ekleyin
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("JWT token expired: {}", e.getMessage());
            throw new JwtException("JWT token expired.");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token.");
        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage());
            throw new JwtException("Error processing JWT token.u");
        }
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
