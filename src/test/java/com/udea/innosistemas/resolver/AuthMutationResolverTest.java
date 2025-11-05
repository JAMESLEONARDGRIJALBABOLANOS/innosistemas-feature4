package com.udea.innosistemas.resolver;

import com.udea.innosistemas.dto.*;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.entity.UserRole;
import com.udea.innosistemas.exception.AuthenticationException;
import com.udea.innosistemas.exception.BusinessException;
import com.udea.innosistemas.security.JwtTokenProvider;
import com.udea.innosistemas.service.AuthenticationService;
import com.udea.innosistemas.service.UserRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthMutationResolverTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthMutationResolver authMutationResolver;

    private LoginRequest validLoginRequest;
    private RegisterUserInput validRegisterInput;
    private AuthResponse validAuthResponse;
    private UserInfo validUserInfo;

    @BeforeEach
    void setUp() {
        validLoginRequest = new LoginRequest("test@example.com", "password123");

        validRegisterInput = new RegisterUserInput();
        validRegisterInput.setEmail("new@example.com");
        validRegisterInput.setPassword("password123");
        validRegisterInput.setFirstName("John");
        validRegisterInput.setLastName("Doe");
        validRegisterInput.setRole("STUDENT");

        validUserInfo = new UserInfo(1L, "test@example.com", UserRole.STUDENT);

        validAuthResponse = new AuthResponse(
            "access-token",
            "refresh-token",
            validUserInfo
        );
    }

    // ===== LOGIN TESTS =====

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class))).thenReturn(validAuthResponse);

        // Act
        AuthResponse result = authMutationResolver.login("test@example.com", "password123");

        // Assert
        assertNotNull(result);
        assertEquals("access-token", result.getToken());
        assertEquals("refresh-token", result.getRefreshToken());
        assertNotNull(result.getUserInfo());
        verify(authenticationService).login(any(LoginRequest.class));
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () ->
            authMutationResolver.login("test@example.com", "wrongpassword")
        );
    }

    @Test
    void login_WithNullEmail_ShouldThrowException() {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class)))
            .thenThrow(new IllegalArgumentException("Email cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            authMutationResolver.login(null, "password123")
        );
    }

    @Test
    void login_WithEmptyPassword_ShouldThrowException() {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class)))
            .thenThrow(new IllegalArgumentException("Password cannot be empty"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            authMutationResolver.login("test@example.com", "")
        );
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class)))
            .thenThrow(new AuthenticationException("User not found"));

        // Act & Assert
        assertThrows(AuthenticationException.class, () ->
            authMutationResolver.login("nonexistent@example.com", "password123")
        );
    }

    @Test
    void login_WithLockedAccount_ShouldThrowException() {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class)))
            .thenThrow(new AuthenticationException("Account is locked"));

        // Act & Assert
        assertThrows(AuthenticationException.class, () ->
            authMutationResolver.login("locked@example.com", "password123")
        );
    }

    // ===== REGISTER TESTS =====

    @Test
    void registerUser_WithValidInput_ShouldReturnUserInfo() {
        // Arrange
        when(userRegistrationService.registerUser(any(RegisterUserInput.class)))
            .thenReturn(validUserInfo);

        // Act
        UserInfo result = authMutationResolver.registerUser(validRegisterInput);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals(UserRole.STUDENT, result.getRole());
        verify(userRegistrationService).registerUser(validRegisterInput);
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(userRegistrationService.registerUser(any(RegisterUserInput.class)))
            .thenThrow(new BusinessException("Email already exists"));

        // Act & Assert
        assertThrows(BusinessException.class, () ->
            authMutationResolver.registerUser(validRegisterInput)
        );
    }

    @Test
    void registerUser_WithInvalidEmail_ShouldThrowException() {
        // Arrange
        validRegisterInput.setEmail("invalid-email");
        when(userRegistrationService.registerUser(any(RegisterUserInput.class)))
            .thenThrow(new IllegalArgumentException("Invalid email format"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            authMutationResolver.registerUser(validRegisterInput)
        );
    }

    @Test
    void registerUser_WithWeakPassword_ShouldThrowException() {
        // Arrange
        validRegisterInput.setPassword("123");
        when(userRegistrationService.registerUser(any(RegisterUserInput.class)))
            .thenThrow(new IllegalArgumentException("Password too weak"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            authMutationResolver.registerUser(validRegisterInput)
        );
    }

    @Test
    void registerUser_WithInvalidRole_ShouldThrowException() {
        // Arrange
        validRegisterInput.setRole("INVALID_ROLE");
        when(userRegistrationService.registerUser(any(RegisterUserInput.class)))
            .thenThrow(new BusinessException("Invalid role"));

        // Act & Assert
        assertThrows(BusinessException.class, () ->
            authMutationResolver.registerUser(validRegisterInput)
        );
    }

    @Test
    void registerUser_WithNullFirstName_ShouldThrowException() {
        // Arrange
        validRegisterInput.setFirstName(null);
        when(userRegistrationService.registerUser(any(RegisterUserInput.class)))
            .thenThrow(new IllegalArgumentException("First name is required"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            authMutationResolver.registerUser(validRegisterInput)
        );
    }

    // ===== REFRESH TOKEN TESTS =====

    @Test
    void refreshToken_WithValidToken_ShouldReturnNewAuthResponse() {
        // Arrange
        when(authenticationService.refreshToken(anyString()))
            .thenReturn(validAuthResponse);

        // Act
        AuthResponse result = authMutationResolver.refreshToken("valid-refresh-token");

        // Assert
        assertNotNull(result);
        assertEquals("access-token", result.getToken());
        verify(authenticationService).refreshToken("valid-refresh-token");
    }

    @Test
    void refreshToken_WithExpiredToken_ShouldThrowException() {
        // Arrange
        when(authenticationService.refreshToken(anyString()))
            .thenThrow(new AuthenticationException("Refresh token expired"));

        // Act & Assert
        assertThrows(AuthenticationException.class, () ->
            authMutationResolver.refreshToken("expired-token")
        );
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldThrowException() {
        // Arrange
        when(authenticationService.refreshToken(anyString()))
            .thenThrow(new AuthenticationException("Invalid refresh token"));

        // Act & Assert
        assertThrows(AuthenticationException.class, () ->
            authMutationResolver.refreshToken("invalid-token")
        );
    }

    @Test
    void refreshToken_WithBlacklistedToken_ShouldThrowException() {
        // Arrange
        when(authenticationService.refreshToken(anyString()))
            .thenThrow(new AuthenticationException("Token is blacklisted"));

        // Act & Assert
        assertThrows(AuthenticationException.class, () ->
            authMutationResolver.refreshToken("blacklisted-token")
        );
    }

    @Test
    void refreshToken_WithNullToken_ShouldThrowException() {
        // Arrange
        when(authenticationService.refreshToken(isNull()))
            .thenThrow(new IllegalArgumentException("Token cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            authMutationResolver.refreshToken(null)
        );
    }

    // ===== LOGOUT TESTS =====

    @Test
    void logout_WithValidToken_ShouldReturnLogoutResponse() {
        // Arrange
        LogoutResponse expectedResponse = new LogoutResponse(true, "Logout successful");
        when(authenticationService.logout(anyString())).thenReturn(expectedResponse);

        // Act
        LogoutResponse result = authMutationResolver.logout("valid-token");

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Logout successful", result.getMessage());
        verify(authenticationService).logout("valid-token");
    }

    @Test
    void logout_WithExpiredToken_ShouldStillSucceed() {
        // Arrange
        LogoutResponse expectedResponse = new LogoutResponse(true, "Logout successful");
        when(authenticationService.logout(anyString())).thenReturn(expectedResponse);

        // Act
        LogoutResponse result = authMutationResolver.logout("expired-token");

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    void logout_WithInvalidToken_ShouldThrowException() {
        // Arrange
        when(authenticationService.logout(anyString()))
            .thenThrow(new AuthenticationException("Invalid token"));

        // Act & Assert
        assertThrows(AuthenticationException.class, () ->
            authMutationResolver.logout("invalid-token")
        );
    }

    @Test
    void logout_WithNullToken_ShouldReturnFailureResponse() {
        // Act - cuando token es null, el resolver retorna una respuesta de fallo
        LogoutResponse result = authMutationResolver.logout(null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
    }
}

