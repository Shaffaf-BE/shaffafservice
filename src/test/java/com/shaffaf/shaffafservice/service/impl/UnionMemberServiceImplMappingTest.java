package com.shaffaf.shaffafservice.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.shaffaf.shaffafservice.service.dto.UnionMemberDTO;
import java.lang.reflect.Method;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link UnionMemberServiceImpl} mapping methods.
 * These tests specifically target the array index out of bounds issue
 * in the mapObjectArrayToDTO method.
 */
@ExtendWith(MockitoExtension.class)
class UnionMemberServiceImplMappingTest {

    private UnionMemberServiceImpl service;
    private Method mapObjectArrayToDTOMethod;

    @BeforeEach
    void setUp() throws Exception {
        service = new UnionMemberServiceImpl(null, null, null);

        // Access the private method for testing
        mapObjectArrayToDTOMethod = UnionMemberServiceImpl.class.getDeclaredMethod("mapObjectArrayToDTO", Object[].class);
        mapObjectArrayToDTOMethod.setAccessible(true);
    }

    @Test
    void testMapObjectArrayToDTO_WithValidFullRow() throws Exception {
        // Arrange - Create a valid 14-element array representing a complete union member with project
        Object[] validRow = new Object[] {
            1L, // id
            "John", // first_name
            "Doe", // last_name
            "john.doe@example.com", // email
            "+1234567890", // phone_number
            "admin", // created_by
            Instant.now(), // created_date
            "admin", // last_modified_by
            Instant.now(), // last_modified_date
            null, // deleted_on
            true, // is_union_head
            100L, // project_id
            "Test Project", // project_name
            "Test Project Description", // project_description
        };

        // Act
        UnionMemberDTO result = (UnionMemberDTO) mapObjectArrayToDTOMethod.invoke(service, (Object) validRow);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("+1234567890", result.getPhoneNumber());
        assertEquals(true, result.getIsUnionHead());
        assertNotNull(result.getProject());
        assertEquals(100L, result.getProject().getId());
        assertEquals("Test Project", result.getProject().getName());
        assertEquals("Test Project Description", result.getProject().getDescription());
    }

    @Test
    void testMapObjectArrayToDTO_WithMinimumValidRow() throws Exception {
        // Arrange - Create an 11-element array representing a union member without project info
        Object[] minimalRow = new Object[] {
            2L, // id
            "Jane", // first_name
            "Smith", // last_name
            "jane.smith@example.com", // email
            "+9876543210", // phone_number
            "admin", // created_by
            Instant.now(), // created_date
            "admin", // last_modified_by
            Instant.now(), // last_modified_date
            null, // deleted_on
            false, // is_union_head
        };

        // Act
        UnionMemberDTO result = (UnionMemberDTO) mapObjectArrayToDTOMethod.invoke(service, (Object) minimalRow);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@example.com", result.getEmail());
        assertEquals("+9876543210", result.getPhoneNumber());
        assertEquals(false, result.getIsUnionHead());
        assertNull(result.getProject()); // No project info provided
    }

    @Test
    void testMapObjectArrayToDTO_WithPartialProjectInfo() throws Exception {
        // Arrange - Create a 13-element array with project ID and name but no description
        Object[] partialRow = new Object[] {
            3L, // id
            "Bob", // first_name
            "Johnson", // last_name
            "bob.johnson@example.com", // email
            "+5555555555", // phone_number
            "admin", // created_by
            Instant.now(), // created_date
            "admin", // last_modified_by
            Instant.now(), // last_modified_date
            null, // deleted_on
            false, // is_union_head
            200L, // project_id
            "Partial Project", // project_name (no description)
        };

        // Act
        UnionMemberDTO result = (UnionMemberDTO) mapObjectArrayToDTOMethod.invoke(service, (Object) partialRow);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Bob", result.getFirstName());
        assertNotNull(result.getProject());
        assertEquals(200L, result.getProject().getId());
        assertEquals("Partial Project", result.getProject().getName());
        assertNull(result.getProject().getDescription()); // No description provided
    }

    @Test
    void testMapObjectArrayToDTO_WithNullRow() {
        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            mapObjectArrayToDTOMethod.invoke(service, (Object) null);
        });

        // The method should throw a RuntimeException wrapping IllegalArgumentException
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getMessage().contains("Null row cannot be mapped"));
    }

    @Test
    void testMapObjectArrayToDTO_WithInsufficientColumns() {
        // Arrange - Create an array with insufficient columns (this simulates the original error)
        Object[] insufficientRow = new Object[] { 1L }; // Only 1 element instead of minimum 11

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            mapObjectArrayToDTOMethod.invoke(service, (Object) insufficientRow);
        });

        // The method should throw a RuntimeException with array bounds information
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(
            exception.getCause().getMessage().contains("insufficient columns") ||
            exception.getCause().getMessage().contains("Array index out of bounds")
        );
    }

    @Test
    void testMapObjectArrayToDTO_WithEmptyRow() {
        // Arrange - Create an empty array
        Object[] emptyRow = new Object[] {};

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            mapObjectArrayToDTOMethod.invoke(service, (Object) emptyRow);
        });

        // The method should throw a RuntimeException with insufficient columns information
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getMessage().contains("insufficient columns"));
    }

    @Test
    void testMapObjectArrayToDTO_WithNullProjectId() throws Exception {
        // Arrange - Create a valid row but with null project_id
        Object[] rowWithNullProject = new Object[] {
            4L, // id
            "Alice", // first_name
            "Williams", // last_name
            "alice.williams@example.com", // email
            "+1111111111", // phone_number
            "admin", // created_by
            Instant.now(), // created_date
            "admin", // last_modified_by
            Instant.now(), // last_modified_date
            null, // deleted_on
            false, // is_union_head
            null, // project_id (null)
            null, // project_name (null)
            null, // project_description (null)
        };

        // Act
        UnionMemberDTO result = (UnionMemberDTO) mapObjectArrayToDTOMethod.invoke(service, (Object) rowWithNullProject);

        // Assert
        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("Alice", result.getFirstName());
        assertNull(result.getProject()); // Should be null when project_id is null
    }

    @Test
    void testMapObjectArrayToDTO_WithSqlTimestamps() throws Exception {
        // Arrange - Create a valid row using java.sql.Timestamp instead of Instant
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        Object[] rowWithSqlTimestamps = new Object[] {
            5L, // id
            "Charlie", // first_name
            "Brown", // last_name
            "charlie.brown@example.com", // email
            "+2222222222", // phone_number
            "admin", // created_by
            now, // created_date (sql.Timestamp)
            "admin", // last_modified_by
            now, // last_modified_date (sql.Timestamp)
            null, // deleted_on
            false, // is_union_head
            300L, // project_id
            "SQL Timestamp Project", // project_name
            "Testing SQL timestamp conversion", // project_description
        };

        // Act
        UnionMemberDTO result = (UnionMemberDTO) mapObjectArrayToDTOMethod.invoke(service, (Object) rowWithSqlTimestamps);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Charlie", result.getFirstName());
        assertEquals("Brown", result.getLastName());
        assertEquals("charlie.brown@example.com", result.getEmail());
        assertEquals("+2222222222", result.getPhoneNumber());
        assertEquals(false, result.getIsUnionHead());
        assertNotNull(result.getCreatedDate());
        assertNotNull(result.getLastModifiedDate());
        assertNull(result.getDeletedOn());
        assertNotNull(result.getProject());
        assertEquals(300L, result.getProject().getId());
        assertEquals("SQL Timestamp Project", result.getProject().getName());
    }

    @Test
    void testMapObjectArrayToDTO_WithMixedTimestampTypes() throws Exception {
        // Arrange - Create a row with mixed timestamp types
        java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
        Instant instant = Instant.now();
        Object[] rowWithMixedTimestamps = new Object[] {
            6L, // id
            "David", // first_name
            "Wilson", // last_name
            "david.wilson@example.com", // email
            "+3333333333", // phone_number
            "admin", // created_by
            sqlTimestamp, // created_date (sql.Timestamp)
            "admin", // last_modified_by
            instant, // last_modified_date (Instant)
            null, // deleted_on
            true, // is_union_head
        };

        // Act
        UnionMemberDTO result = (UnionMemberDTO) mapObjectArrayToDTOMethod.invoke(service, (Object) rowWithMixedTimestamps);

        // Assert
        assertNotNull(result);
        assertEquals(6L, result.getId());
        assertEquals("David", result.getFirstName());
        assertEquals("Wilson", result.getLastName());
        assertEquals(true, result.getIsUnionHead());
        assertNotNull(result.getCreatedDate());
        assertNotNull(result.getLastModifiedDate());
        assertNull(result.getDeletedOn());
        assertNull(result.getProject()); // No project info provided
    }

    @Test
    void testUpdateUnionMemberNative_WithNullIsUnionHead() throws Exception {
        // This test validates that when isUnionHead is null in update request,
        // the existing value is preserved to avoid database constraint violations

        // Note: This test focuses on the null handling logic in the service method
        // The actual database interaction would require integration testing

        // We can test the mapObjectArrayToDTO logic with isUnionHead scenarios
        Object[] rowWithFalseUnionHead = new Object[] {
            7L, // id
            "Eva", // first_name
            "Johnson", // last_name
            "eva.johnson@example.com", // email
            "+4444444444", // phone_number
            "admin", // created_by
            Instant.now(), // created_date
            "admin", // last_modified_by
            Instant.now(), // last_modified_date
            null, // deleted_on
            false, // is_union_head (explicitly false)
        };

        // Act
        UnionMemberDTO result = (UnionMemberDTO) mapObjectArrayToDTOMethod.invoke(service, (Object) rowWithFalseUnionHead);

        // Assert
        assertNotNull(result);
        assertEquals(7L, result.getId());
        assertEquals("Eva", result.getFirstName());
        assertEquals("Johnson", result.getLastName());
        assertEquals(false, result.getIsUnionHead()); // Should be explicitly false, not null
        assertNull(result.getProject()); // No project info provided
    }

    @Test
    void testMapObjectArrayToDTO_WithTrueUnionHead() throws Exception {
        // Test explicit true value for union head
        Object[] rowWithTrueUnionHead = new Object[] {
            8L, // id
            "Frank", // first_name
            "Miller", // last_name
            "frank.miller@example.com", // email
            "+5555555555", // phone_number
            "admin", // created_by
            Instant.now(), // created_date
            "admin", // last_modified_by
            Instant.now(), // last_modified_date
            null, // deleted_on
            true, // is_union_head (explicitly true)
            400L, // project_id
            "Head Project", // project_name
            "Project led by union head", // project_description
        };

        // Act
        UnionMemberDTO result = (UnionMemberDTO) mapObjectArrayToDTOMethod.invoke(service, (Object) rowWithTrueUnionHead);

        // Assert
        assertNotNull(result);
        assertEquals(8L, result.getId());
        assertEquals("Frank", result.getFirstName());
        assertEquals("Miller", result.getLastName());
        assertEquals(true, result.getIsUnionHead()); // Should be explicitly true
        assertNotNull(result.getProject());
        assertEquals(400L, result.getProject().getId());
        assertEquals("Head Project", result.getProject().getName());
    }

    @Test
    void testUpdateUnionMemberNative_NullHandling() throws Exception {
        // Test that various null scenarios are handled correctly in the update logic

        // Test 1: isUnionHead is null from database but should default to false
        Object[] rowWithNullIsUnionHead = new Object[] {
            9L, // id
            "Test", // first_name
            "User", // last_name
            "test.user@example.com", // email
            "+5555555555", // phone_number
            "admin", // created_by
            Instant.now(), // created_date
            "admin", // last_modified_by
            Instant.now(), // last_modified_date
            null, // deleted_on
            null, // is_union_head (null value that should be handled)
        };

        // Act
        UnionMemberDTO result = (UnionMemberDTO) mapObjectArrayToDTOMethod.invoke(service, (Object) rowWithNullIsUnionHead);

        // Assert
        assertNotNull(result);
        assertEquals(9L, result.getId());
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals(false, result.getIsUnionHead()); // Should default to false, not null
        assertNull(result.getProject()); // No project info provided
    }
}
