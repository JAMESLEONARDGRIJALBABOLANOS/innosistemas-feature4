package com.udea.innosistemas.dto;

import com.udea.innosistemas.entity.User;
import com.udea.innosistemas.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserInfoTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(UserRole.STUDENT);
        testUser.setTeamId(10L);
        testUser.setCourseId(20L);
    }

    @Test
    void constructor_WithUser_ShouldCreateUserInfo() {
        // Act
        UserInfo userInfo = new UserInfo(testUser);

        // Assert
        assertNotNull(userInfo);
        assertEquals(1L, userInfo.getId());
        assertEquals("test@example.com", userInfo.getEmail());
        assertEquals("John", userInfo.getFirstName());
        assertEquals("Doe", userInfo.getLastName());
        assertEquals(UserRole.STUDENT, userInfo.getRole());
        assertEquals(10L, userInfo.getTeamId());
        assertEquals(20L, userInfo.getCourseId());
    }

    @Test
    void constructor_WithParameters_ShouldCreateUserInfo() {
        // Act
        UserInfo userInfo = new UserInfo(1L, "test@example.com", UserRole.STUDENT, 10L, 20L);

        // Assert
        assertNotNull(userInfo);
        assertEquals(1L, userInfo.getId());
        assertEquals("test@example.com", userInfo.getEmail());
        assertEquals(UserRole.STUDENT, userInfo.getRole());
        assertEquals(10L, userInfo.getTeamId());
        assertEquals(20L, userInfo.getCourseId());
    }

    @Test
    void getFullName_WithBothNames_ShouldReturnFullName() {
        // Arrange
        UserInfo userInfo = new UserInfo(testUser);

        // Act
        String fullName = userInfo.getFullName();

        // Assert
        assertEquals("John Doe", fullName);
    }

    @Test
    void getFullName_WithNullFirstName_ShouldReturnEmail() {
        // Arrange
        testUser.setFirstName(null);
        UserInfo userInfo = new UserInfo(testUser);

        // Act
        String fullName = userInfo.getFullName();

        // Assert
        assertEquals("test@example.com", fullName);
    }

    @Test
    void getFullName_WithNullLastName_ShouldReturnEmail() {
        // Arrange
        testUser.setLastName(null);
        UserInfo userInfo = new UserInfo(testUser);

        // Act
        String fullName = userInfo.getFullName();

        // Assert
        assertEquals("test@example.com", fullName);
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        UserInfo userInfo = new UserInfo();

        // Act
        userInfo.setId(2L);
        userInfo.setEmail("new@example.com");
        userInfo.setFirstName("Jane");
        userInfo.setLastName("Smith");
        userInfo.setRole(UserRole.PROFESSOR);
        userInfo.setTeamId(15L);
        userInfo.setCourseId(25L);
        userInfo.setFullName("Jane Smith");

        // Assert
        assertEquals(2L, userInfo.getId());
        assertEquals("new@example.com", userInfo.getEmail());
        assertEquals("Jane", userInfo.getFirstName());
        assertEquals("Smith", userInfo.getLastName());
        assertEquals(UserRole.PROFESSOR, userInfo.getRole());
        assertEquals(15L, userInfo.getTeamId());
        assertEquals(25L, userInfo.getCourseId());
        assertEquals("Jane Smith", userInfo.getFullName());
    }

    @Test
    void toString_ShouldContainUserData() {
        // Arrange
        UserInfo userInfo = new UserInfo(testUser);

        // Act
        String result = userInfo.toString();

        // Assert
        assertNotNull(result);
    }

    @Test
    void equals_WithSameData_ShouldReturnEqual() {
        // Arrange
        UserInfo userInfo1 = new UserInfo(testUser);
        UserInfo userInfo2 = new UserInfo(testUser);

        // Act & Assert
        assertEquals(userInfo1.getId(), userInfo2.getId());
        assertEquals(userInfo1.getEmail(), userInfo2.getEmail());
    }

    @Test
    void constructor_EmptyConstructor_ShouldCreateInstance() {
        // Act
        UserInfo userInfo = new UserInfo();

        // Assert
        assertNotNull(userInfo);
    }

    @Test
    void setRole_WithValidRole_ShouldSetRole() {
        // Arrange
        UserInfo userInfo = new UserInfo();

        // Act
        userInfo.setRole(UserRole.ADMIN);

        // Assert
        assertEquals(UserRole.ADMIN, userInfo.getRole());
    }

    @Test
    void setTeamId_WithValidId_ShouldSetTeamId() {
        // Arrange
        UserInfo userInfo = new UserInfo();

        // Act
        userInfo.setTeamId(100L);

        // Assert
        assertEquals(100L, userInfo.getTeamId());
    }

    @Test
    void setCourseId_WithValidId_ShouldSetCourseId() {
        // Arrange
        UserInfo userInfo = new UserInfo();

        // Act
        userInfo.setCourseId(200L);

        // Assert
        assertEquals(200L, userInfo.getCourseId());
    }
}

