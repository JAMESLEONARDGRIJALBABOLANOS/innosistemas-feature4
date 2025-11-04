package com.udea.innosistemas.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(UserRole.STUDENT);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getUsername_ShouldReturnEmail() {
        assertEquals("test@example.com", user.getUsername());
    }

    @Test
    void getAuthorities_ShouldReturnRoleAuthority() {
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT")));
    }

    @Test
    void getFullName_ShouldCombineFirstAndLastName() {
        assertEquals("John Doe", user.getFullName());
    }

    @Test
    void getFullName_WithNullNames_ShouldReturnEmpty() {
        user.setFirstName(null);
        user.setLastName(null);

        assertEquals("", user.getFullName());
    }

    @Test
    void isAccountNonExpired_ShouldAlwaysReturnTrue() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_ShouldReturnConfiguredValue() {
        user.setAccountNonLocked(true);
        assertTrue(user.isAccountNonLocked());

        user.setAccountNonLocked(false);
        assertFalse(user.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_ShouldAlwaysReturnTrue() {
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_ShouldReturnConfiguredValue() {
        user.setEnabled(true);
        assertTrue(user.isEnabled());

        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    void differentRoles_ShouldHaveDifferentAuthorities() {
        user.setRole(UserRole.ADMIN);
        assertTrue(user.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));

        user.setRole(UserRole.PROFESSOR);
        assertTrue(user.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROFESSOR")));
    }

    @Test
    void setTeamIdAndCourseId_ShouldStoreValues() {
        user.setTeamId(10L);
        user.setCourseId(20L);

        assertEquals(10L, user.getTeamId());
        assertEquals(20L, user.getCourseId());
    }

    @Test
    void createdAtAndUpdatedAt_ShouldTrackTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }
}

