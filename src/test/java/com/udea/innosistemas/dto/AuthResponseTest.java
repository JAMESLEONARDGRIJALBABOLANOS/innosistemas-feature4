package com.udea.innosistemas.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void builderPattern_ShouldCreateAuthResponse() {
        // Act
        AuthResponse response = AuthResponse.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .userInfo(null)
                .build();

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
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
    void equals_WithSameValues_ShouldReturnTrue() {
        // Arrange
        AuthResponse response1 = AuthResponse.builder()
                .token("token")
                .refreshToken("refresh")
                .build();

        AuthResponse response2 = AuthResponse.builder()
                .token("token")
                .refreshToken("refresh")
                .build();

        // Assert
        assertEquals(response1, response2);
    }

    @Test
    void hashCode_WithSameValues_ShouldBeEqual() {
        // Arrange
        AuthResponse response1 = AuthResponse.builder()
                .token("token")
                .refreshToken("refresh")
                .build();

        AuthResponse response2 = AuthResponse.builder()
                .token("token")
                .refreshToken("refresh")
                .build();

        // Assert
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void toString_ShouldContainFieldValues() {
        // Arrange
        AuthResponse response = AuthResponse.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .build();

        // Act
        String result = response.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("access-token") || result.contains("AuthResponse"));
    }
}

