package com.udea.innosistemas.controller;

import com.udea.innosistemas.dto.AuthResponse;
import com.udea.innosistemas.dto.LoginRequest;
import com.udea.innosistemas.dto.UserInfo;
import com.udea.innosistemas.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("test@example.com", "password123");
        UserInfo userInfo = new UserInfo();
        authResponse = new AuthResponse("access-token", "refresh-token", userInfo);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
        verify(authenticationService).login(loginRequest);
    }

    @Test
    void health_ShouldReturnHealthyStatus() {
        // Act
        ResponseEntity<String> response = authController.health();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("running") || response.getBody().contains("healthy"));
    }
}

