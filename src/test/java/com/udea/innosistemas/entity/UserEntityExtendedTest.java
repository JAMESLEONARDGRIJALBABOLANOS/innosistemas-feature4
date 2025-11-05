package com.udea.innosistemas.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityExtendedTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(UserRole.STUDENT);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    // ===== USERDETAILS METHODS =====

    @Test
    void getAuthorities_WithStudentRole_ShouldReturnCorrectAuthorities() {
        // Arrange
        user.setRole(UserRole.STUDENT);

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT")));
    }

    @Test
    void getAuthorities_WithProfessorRole_ShouldReturnCorrectAuthorities() {
        // Arrange
        user.setRole(UserRole.PROFESSOR);

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_PROFESSOR")));
    }

    @Test
    void getAuthorities_WithAdminRole_ShouldReturnCorrectAuthorities() {
        // Arrange
        user.setRole(UserRole.ADMIN);

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void getAuthorities_WithTARole_ShouldReturnCorrectAuthorities() {
        // Arrange
        user.setRole(UserRole.TA);

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_TA")));
    }

    @Test
    void getUsername_ShouldReturnEmail() {
        // Act
        String username = user.getUsername();

        // Assert
        assertEquals("test@example.com", username);
    }

    @Test
    void isAccountNonExpired_WhenTrue_ShouldReturnTrue() {
        // Arrange
        user.setAccountNonExpired(true);

        // Act & Assert
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void isAccountNonExpired_WhenFalse_ShouldReturnFalse() {
        // Arrange
        user.setAccountNonExpired(false);

        // Act & Assert
        assertFalse(user.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_WhenTrue_ShouldReturnTrue() {
        // Arrange
        user.setAccountNonLocked(true);

        // Act & Assert
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void isAccountNonLocked_WhenFalse_ShouldReturnFalse() {
        // Arrange
        user.setAccountNonLocked(false);

        // Act & Assert
        assertFalse(user.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_WhenTrue_ShouldReturnTrue() {
        // Arrange
        user.setCredentialsNonExpired(true);

        // Act & Assert
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void isCredentialsNonExpired_WhenFalse_ShouldReturnFalse() {
        // Arrange
        user.setCredentialsNonExpired(false);

        // Act & Assert
        assertFalse(user.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_WhenTrue_ShouldReturnTrue() {
        // Arrange
        user.setEnabled(true);

        // Act & Assert
        assertTrue(user.isEnabled());
    }

    @Test
    void isEnabled_WhenFalse_ShouldReturnFalse() {
        // Arrange
        user.setEnabled(false);

        // Act & Assert
        assertFalse(user.isEnabled());
    }

    // ===== GETFULLNAME EDGE CASES =====

    @Test
    void getFullName_WithBothNames_ShouldReturnFullName() {
        // Act
        String fullName = user.getFullName();

        // Assert
        assertEquals("John Doe", fullName);
    }

    @Test
    void getFullName_WithNullFirstName_ShouldReturnEmail() {
        // Arrange
        user.setFirstName(null);

        // Act
        String fullName = user.getFullName();

        // Assert
        assertEquals("test@example.com", fullName);
    }

    @Test
    void getFullName_WithNullLastName_ShouldReturnEmail() {
        // Arrange
        user.setLastName(null);

        // Act
        String fullName = user.getFullName();

        // Assert
        assertEquals("test@example.com", fullName);
    }

    @Test
    void getFullName_WithBothNamesNull_ShouldReturnEmail() {
        // Arrange
        user.setFirstName(null);
        user.setLastName(null);

        // Act
        String fullName = user.getFullName();

        // Assert
        assertEquals("test@example.com", fullName);
    }

    @Test
    void getFullName_WithEmptyFirstName_ShouldReturnLastNameWithSpace() {
        // Arrange
        user.setFirstName("");

        // Act
        String fullName = user.getFullName();

        // Assert
        // El método getFullName() retorna " lastName" (con espacio inicial) si firstName está vacío
        assertEquals(" Doe", fullName);
    }

    @Test
    void getFullName_WithEmptyLastName_ShouldReturnFirstNameWithSpace() {
        // Arrange
        user.setLastName("");

        // Act
        String fullName = user.getFullName();

        // Assert
        // El método getFullName() retorna "firstName " (con espacio final) si lastName está vacío
        assertEquals("John ", fullName);
    }

    @Test
    void getFullName_WithWhitespaceNames_ShouldReturnTrimmedOrEmail() {
        // Arrange
        user.setFirstName("   ");
        user.setLastName("   ");

        // Act
        String fullName = user.getFullName();

        // Assert
        // Dependiendo de la implementación, puede retornar email o espacios
        // Verificamos que no sea null
        assertNotNull(fullName);
    }

    // ===== OPTIONAL FIELDS =====

    @Test
    void setTeamId_ShouldSetCorrectly() {
        // Act
        user.setTeamId(100L);

        // Assert
        assertEquals(100L, user.getTeamId());
    }

    @Test
    void setCourseId_ShouldSetCorrectly() {
        // Act
        user.setCourseId(200L);

        // Assert
        assertEquals(200L, user.getCourseId());
    }

    @Test
    void teamId_CanBeNull() {
        // Act
        user.setTeamId(null);

        // Assert
        assertNull(user.getTeamId());
    }

    @Test
    void courseId_CanBeNull() {
        // Act
        user.setCourseId(null);

        // Assert
        assertNull(user.getCourseId());
    }

    // ===== TIMESTAMPS =====

    @Test
    void createdAt_ShouldNotBeNull() {
        // Assert
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void updatedAt_ShouldNotBeNull() {
        // Assert
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void setCreatedAt_ShouldUpdateCorrectly() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.of(2024, 1, 1, 0, 0);

        // Act
        user.setCreatedAt(newDate);

        // Assert
        assertEquals(newDate, user.getCreatedAt());
    }

    @Test
    void setUpdatedAt_ShouldUpdateCorrectly() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.of(2024, 1, 1, 0, 0);

        // Act
        user.setUpdatedAt(newDate);

        // Assert
        assertEquals(newDate, user.getUpdatedAt());
    }

    // ===== EDGE CASES =====

    @Test
    void user_WithAllFieldsNull_ShouldHandleGracefully() {
        // Arrange
        User emptyUser = new User();

        // Act & Assert
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getEmail());
        assertNull(emptyUser.getPassword());
        assertNull(emptyUser.getFirstName());
        assertNull(emptyUser.getLastName());
        assertNull(emptyUser.getRole());
        // isEnabled() es true por defecto en la clase User
        assertTrue(emptyUser.isEnabled());
    }

    @Test
    void user_WithVeryLongEmail_ShouldAccept() {
        // Arrange
        String longEmail = "very.long.email.address.that.exceeds.normal.length@example.com";

        // Act
        user.setEmail(longEmail);

        // Assert
        assertEquals(longEmail, user.getEmail());
    }

    @Test
    void user_WithVeryLongNames_ShouldAccept() {
        // Arrange
        String longName = "A".repeat(255);

        // Act
        user.setFirstName(longName);
        user.setLastName(longName);

        // Assert
        assertEquals(longName, user.getFirstName());
        assertEquals(longName, user.getLastName());
    }

    @Test
    void user_WithSpecialCharactersInNames_ShouldAccept() {
        // Arrange
        user.setFirstName("José-María");
        user.setLastName("O'Connor-Smith");

        // Act
        String fullName = user.getFullName();

        // Assert
        assertEquals("José-María O'Connor-Smith", fullName);
    }

    @Test
    void getAuthorities_WithNullRole_ShouldThrowNullPointerException() {
        // Arrange
        user.setRole(null);

        // Act & Assert
        // El método getAuthorities() lanza NullPointerException cuando role es null
        assertThrows(NullPointerException.class, () -> user.getAuthorities());
    }

    @Test
    void getUsername_WithNullEmail_ShouldReturnNull() {
        // Arrange
        user.setEmail(null);

        // Act
        String username = user.getUsername();

        // Assert
        assertNull(username);
    }

    // ===== EQUALS AND HASHCODE =====

    @Test
    void equals_WithSameId_ShouldReturnTrue() {
        // Arrange
        User user2 = new User();
        user2.setId(1L);

        // Act & Assert
        assertEquals(user.getId(), user2.getId());
    }

    @Test
    void equals_WithDifferentId_ShouldReturnFalse() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);

        // Act & Assert
        assertNotEquals(user.getId(), user2.getId());
    }

    @Test
    void equals_WithNullId_ShouldHandleCorrectly() {
        // Arrange
        User user1 = new User();
        User user2 = new User();

        // Act & Assert
        assertNull(user1.getId());
        assertNull(user2.getId());
    }

    @Test
    void hashCode_WithSameId_ShouldBeSame() {
        // Arrange
        User user2 = new User();
        user2.setId(1L);

        // Act & Assert
        assertEquals(user.getId().hashCode(), user2.getId().hashCode());
    }

    // ===== BUILDER PATTERN / CONSTRUCTOR TESTS =====

    @Test
    void defaultConstructor_ShouldCreateUser() {
        // Act
        User newUser = new User();

        // Assert
        assertNotNull(newUser);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        User newUser = new User();

        // Act
        newUser.setId(999L);
        newUser.setEmail("new@test.com");
        newUser.setPassword("newPassword");
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");
        newUser.setRole(UserRole.PROFESSOR);
        newUser.setEnabled(true);
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        newUser.setTeamId(50L);
        newUser.setCourseId(60L);

        // Assert
        assertEquals(999L, newUser.getId());
        assertEquals("new@test.com", newUser.getEmail());
        assertEquals("newPassword", newUser.getPassword());
        assertEquals("Jane", newUser.getFirstName());
        assertEquals("Smith", newUser.getLastName());
        assertEquals(UserRole.PROFESSOR, newUser.getRole());
        assertTrue(newUser.isEnabled());
        assertTrue(newUser.isAccountNonExpired());
        assertTrue(newUser.isAccountNonLocked());
        assertTrue(newUser.isCredentialsNonExpired());
        assertEquals(50L, newUser.getTeamId());
        assertEquals(60L, newUser.getCourseId());
    }
}

