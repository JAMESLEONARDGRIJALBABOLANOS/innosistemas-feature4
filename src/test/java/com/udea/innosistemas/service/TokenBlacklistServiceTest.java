package com.udea.innosistemas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

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
    }

    @Test
    void loadUserByUsername_WithExistingUser_ShouldReturnUserDetails() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonLocked());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_WithNonExistingUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
            userDetailsService.loadUserByUsername("nonexistent@example.com")
        );
    }

    @Test
    void loadUserById_WithExistingUser_ShouldReturnUserDetails() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserById(1L);

        // Assert
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void loadUserById_WithNonExistingUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
            userDetailsService.loadUserById(999L)
        );
    }
}

