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

    // ===== CASOS TRISTES (SAD PATH) =====

    @Test
    void validateToken_WithNullToken_ShouldReturnFalse() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithMalformedToken_ShouldReturnFalse() {
        // Arrange
        String malformedToken = "this.is.not.a.valid.jwt.token.format";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithTokenWithoutSignature_ShouldReturnFalse() {
        // Arrange
        String tokenWithoutSignature = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0In0";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(tokenWithoutSignature);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithTokenWithWrongSignature_ShouldReturnFalse() {
        // Arrange
        String tokenWithWrongSignature = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0In0.wrong_signature";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(tokenWithWrongSignature);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void getUsernameFromJWT_WithNullToken_ShouldThrowException() {
        // Act & Assert
        assertThrows(Exception.class, () ->
            jwtTokenProvider.getUsernameFromJWT(null)
        );
    }

    @Test
    void getUsernameFromJWT_WithMalformedToken_ShouldThrowException() {
        // Act & Assert
        assertThrows(Exception.class, () ->
            jwtTokenProvider.getUsernameFromJWT("invalid.token")
        );
    }

    @Test
    void getExpirationDateFromJWT_WithMalformedToken_ShouldThrowException() {
        // Act & Assert
        assertThrows(Exception.class, () ->
            jwtTokenProvider.getExpirationDateFromJWT("invalid.token")
        );
    }

    @Test
    void isTokenExpired_WithMalformedToken_ShouldThrowException() {
        // Act & Assert
        assertThrows(Exception.class, () ->
            jwtTokenProvider.isTokenExpired("invalid.token")
        );
    }

    @Test
    void generateToken_WithNullAuthentication_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () ->
            jwtTokenProvider.generateToken(null)
        );
    }

    @Test
    void generateTokenFromUser_WithNullUser_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () ->
            jwtTokenProvider.generateTokenFromUser(null)
        );
    }


    @Test
    void generateToken_WithDifferentRoles_ShouldGenerateValidTokens() {
        // Test PROFESSOR role
        testUser.setRole(UserRole.PROFESSOR);
        String professorToken = jwtTokenProvider.generateTokenFromUser(testUser);
        assertNotNull(professorToken);
        assertTrue(jwtTokenProvider.validateToken(professorToken));

        // Test ADMIN role
        testUser.setRole(UserRole.ADMIN);
        String adminToken = jwtTokenProvider.generateTokenFromUser(testUser);
        assertNotNull(adminToken);
        assertTrue(jwtTokenProvider.validateToken(adminToken));

        // Test TA role
        testUser.setRole(UserRole.TA);
        String taToken = jwtTokenProvider.generateTokenFromUser(testUser);
        assertNotNull(taToken);
        assertTrue(jwtTokenProvider.validateToken(taToken));
    }

    @Test
    void validateToken_WithVeryLongToken_ShouldHandleCorrectly() {
        // Arrange - create user with very long names
        testUser.setFirstName("VeryLongFirstNameThatExceedsNormalLength" + "A".repeat(100));
        testUser.setLastName("VeryLongLastNameThatExceedsNormalLength" + "B".repeat(100));

        // Act
        String token = jwtTokenProvider.generateTokenFromUser(testUser);

        // Assert
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void generateToken_WithUserWithNullOptionalFields_ShouldSucceed() {
        // Arrange
        testUser.setTeamId(null);
        testUser.setCourseId(null);
        testUser.setFirstName(null);
        testUser.setLastName(null);

        // Act
        String token = jwtTokenProvider.generateTokenFromUser(testUser);

        // Assert
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("test@example.com", jwtTokenProvider.getUsernameFromJWT(token));
    }

    @Test
    void validateToken_AfterSecretChange_ShouldReturnFalse() {
        // Arrange
        String token = jwtTokenProvider.generateTokenFromUser(testUser);

        // Change secret
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret",
            "DIFFERENT_SECRET_KEY_THAT_SHOULD_INVALIDATE_TOKEN_MIN_256_BITS_REQUIRED");

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertFalse(isValid);
    }
}


