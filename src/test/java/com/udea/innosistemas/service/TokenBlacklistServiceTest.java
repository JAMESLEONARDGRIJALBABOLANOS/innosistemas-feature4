package com.udea.innosistemas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TokenBlacklistServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void blacklistToken_WithValidToken_ShouldAddToBlacklist() {
        // Arrange
        String token = "valid-token";
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000); // 1 hour

        // Act
        tokenBlacklistService.blacklistToken(token, expirationDate);

        // Assert
        verify(valueOperations).set(anyString(), eq("revoked"), anyLong(), any());
    }

    @Test
    void blacklistToken_WithExpiredToken_ShouldNotAddToBlacklist() {
        // Arrange
        String token = "expired-token";
        Date expirationDate = new Date(System.currentTimeMillis() - 3600000); // 1 hour ago

        // Act
        tokenBlacklistService.blacklistToken(token, expirationDate);

        // Assert
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void isTokenBlacklisted_WithBlacklistedToken_ShouldReturnTrue() {
        // Arrange
        String token = "blacklisted-token";
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        // Act
        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        // Assert
        assertTrue(result);
        verify(redisTemplate).hasKey(anyString());
    }

    @Test
    void isTokenBlacklisted_WithNonBlacklistedToken_ShouldReturnFalse() {
        // Arrange
        String token = "valid-token";
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // Act
        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        // Assert
        assertFalse(result);
    }

    @Test
    void isTokenBlacklisted_WhenRedisUnavailable_ShouldReturnFalse() {
        // Arrange
        TokenBlacklistService serviceWithoutRedis = new TokenBlacklistService();

        // Act
        boolean result = serviceWithoutRedis.isTokenBlacklisted("any-token");

        // Assert
        assertFalse(result, "Should return false when Redis is not available");
    }

    @Test
    void removeTokenFromBlacklist_ShouldDeleteToken() {
        // Arrange
        String token = "token-to-remove";

        // Act
        tokenBlacklistService.removeTokenFromBlacklist(token);

        // Assert
        verify(redisTemplate).delete(anyString());
    }
}
