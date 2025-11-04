package com.udea.innosistemas.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void constructor_WithAllParameters_ShouldCreateAuthResponse() {
        // Arrange
        UserInfo userInfo = new UserInfo();

        // Act
        AuthResponse response = new AuthResponse("access-token", "refresh-token", userInfo);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertNotNull(response.getUserInfo());
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        AuthResponse response = new AuthResponse();
        UserInfo userInfo = new UserInfo();

        // Act
        response.setToken("access-token");
        response.setRefreshToken("refresh-token");
        response.setUserInfo(userInfo);

        // Assert
        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertNotNull(response.getUserInfo());
    }

    @Test
    void constructor_WithTokenAndUserInfo_ShouldCreateAuthResponse() {
        // Arrange
        UserInfo userInfo = new UserInfo();

        // Act
        AuthResponse response = new AuthResponse("token", userInfo);

        // Assert
        assertEquals("token", response.getToken());
        assertNotNull(response.getUserInfo());
        assertNull(response.getRefreshToken());
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyAuthResponse() {
        // Act
        AuthResponse response = new AuthResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getToken());
        assertNull(response.getRefreshToken());
        assertNull(response.getUserInfo());
    }
}

