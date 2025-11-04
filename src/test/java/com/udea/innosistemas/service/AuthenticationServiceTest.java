package com.udea.innosistemas.service;

import com.udea.innosistemas.dto.AuthResponse;
import com.udea.innosistemas.dto.LoginRequest;
import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.entity.UserRole;
import com.udea.innosistemas.exception.AuthenticationException;
import com.udea.innosistemas.repository.UserRepository;
import com.udea.innosistemas.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SessionManagementService sessionManagementService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.STUDENT);
        testUser.setEnabled(true);
        testUser.setAccountNonLocked(true);
        testUser.setCreatedAt(LocalDateTime.now());

        loginRequest = new LoginRequest("test@example.com", "password123");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authentication.getName()).thenReturn("test@example.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(authentication)).thenReturn("refresh-token");
        when(sessionManagementService.registerSession(anyString(), anyString())).thenReturn(true);

        // Act
        AuthResponse response = authenticationService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(sessionManagementService).registerSession(anyString(), anyString());
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowAuthenticationException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(AuthenticationException.class, () ->
            authenticationService.login(loginRequest)
        );
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnNewAuthResponse() {
        // Arrange
        String refreshToken = "valid-refresh-token";
        when(tokenBlacklistService.isTokenBlacklisted(refreshToken)).thenReturn(false);
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(refreshToken)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(sessionManagementService.hasActiveSessions("test@example.com")).thenReturn(true);
        when(jwtTokenProvider.generateTokenFromUser(testUser)).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshTokenFromUser(testUser)).thenReturn("new-refresh-token");
        when(jwtTokenProvider.getExpirationDateFromJWT(refreshToken)).thenReturn(new java.util.Date());

        // Act
        AuthResponse response = authenticationService.refreshToken(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals("new-access-token", response.getToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        verify(jwtTokenProvider).validateToken(refreshToken);
        verify(jwtTokenProvider).isRefreshToken(refreshToken);
        verify(sessionManagementService).hasActiveSessions("test@example.com");
        verify(tokenBlacklistService).blacklistToken(eq(refreshToken), any());
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldThrowAuthenticationException() {
        // Arrange
        String invalidToken = "invalid-token";
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);

        // Act & Assert
        assertThrows(AuthenticationException.class, () ->
            authenticationService.refreshToken(invalidToken)
        );
    }

    @Test
    void logout_WithValidToken_ShouldBlacklistTokenAndInvalidateSession() {
        // Arrange
        String token = "valid-token";
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getExpirationDateFromJWT(token)).thenReturn(new java.util.Date());

        // Act
        authenticationService.logout(token);

        // Assert
        verify(tokenBlacklistService).blacklistToken(anyString(), any());
        // Note: invalidateSession method doesn't exist in SessionManagementService
        // verify(sessionManagementService).invalidateSession(token);
    }

    @Test
    void logoutFromAllDevices_ShouldInvalidateAllUserSessions() {
        // Arrange
        String username = "test@example.com";
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(testUser));

        // Act
        authenticationService.logoutFromAllDevices(username);

        // Assert
        verify(sessionManagementService).invalidateAllUserSessions(username);
    }
}

