package com.udea.innosistemas.security;

import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.entity.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();

        // Set private fields using reflection
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret",
            "CHANGE_THIS_SECRET_KEY_IN_PRODUCTION_USE_ENVIRONMENT_VARIABLE_MIN_256_BITS");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 3600L); // 1 hour
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshExpirationInMs", 86400L); // 24 hours

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(UserRole.STUDENT);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void generateToken_WithAuthentication_ShouldReturnValidToken() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);

        // Act
        String token = jwtTokenProvider.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void generateTokenFromUser_ShouldReturnValidToken() {
        // Act
        String token = jwtTokenProvider.generateTokenFromUser(testUser);

        // Assert
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void getUsernameFromJWT_WithValidToken_ShouldReturnUsername() {
        // Arrange
        String token = jwtTokenProvider.generateTokenFromUser(testUser);

        // Act
        String username = jwtTokenProvider.getUsernameFromJWT(token);

        // Assert
        assertEquals("test@example.com", username);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtTokenProvider.generateTokenFromUser(testUser);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // Arrange
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", -1L);
        String expiredToken = jwtTokenProvider.generateTokenFromUser(testUser);

        // Reset expiration
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 3600L);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void getExpirationDateFromJWT_ShouldReturnDate() {
        // Arrange
        String token = jwtTokenProvider.generateTokenFromUser(testUser);

        // Act
        Date expirationDate = jwtTokenProvider.getExpirationDateFromJWT(token);

        // Assert
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void isTokenExpired_WithNonExpiredToken_ShouldReturnFalse() {
        // Arrange
        String token = jwtTokenProvider.generateTokenFromUser(testUser);

        // Act
        boolean isExpired = jwtTokenProvider.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void generatedToken_ShouldContainUserClaims() {
        // Arrange
        testUser.setTeamId(5L);
        testUser.setCourseId(10L);

        // Act
        String token = jwtTokenProvider.generateTokenFromUser(testUser);

        // Assert
        assertNotNull(token);
        String username = jwtTokenProvider.getUsernameFromJWT(token);
        assertEquals("test@example.com", username);
    }
}

