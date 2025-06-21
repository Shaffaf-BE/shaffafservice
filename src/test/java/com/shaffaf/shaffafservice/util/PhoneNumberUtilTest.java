package com.shaffaf.shaffafservice.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PhoneNumberUtil}.
 */
class PhoneNumberUtilTest {

    @Test
    void testValidPakistaniMobileNumbers() {
        // Valid Pakistani mobile numbers
        assertTrue(PhoneNumberUtil.isValidPakistaniMobile("+923001234567"));
        assertTrue(PhoneNumberUtil.isValidPakistaniMobile("+923111234567"));
        assertTrue(PhoneNumberUtil.isValidPakistaniMobile("+923211234567"));
        assertTrue(PhoneNumberUtil.isValidPakistaniMobile("+923311234567"));
        assertTrue(PhoneNumberUtil.isValidPakistaniMobile("+923451234567"));
    }

    @Test
    void testInvalidPakistaniMobileNumbers() {
        // Invalid formats
        assertFalse(PhoneNumberUtil.isValidPakistaniMobile("923001234567")); // Missing +
        assertFalse(PhoneNumberUtil.isValidPakistaniMobile("03001234567")); // Local format
        assertFalse(PhoneNumberUtil.isValidPakistaniMobile("+92001234567")); // Missing 3
        assertFalse(PhoneNumberUtil.isValidPakistaniMobile("+9230012345678")); // Too long
        assertFalse(PhoneNumberUtil.isValidPakistaniMobile("+92300123456")); // Too short
        assertFalse(PhoneNumberUtil.isValidPakistaniMobile("+1234567890")); // Wrong country code
        assertFalse(PhoneNumberUtil.isValidPakistaniMobile("")); // Empty
        assertFalse(PhoneNumberUtil.isValidPakistaniMobile(null)); // Null
        assertFalse(PhoneNumberUtil.isValidPakistaniMobile("   ")); // Whitespace only
    }

    @Test
    void testValidateMethod() {
        // Should not throw for valid number
        assertDoesNotThrow(() -> PhoneNumberUtil.validatePakistaniMobile("+923001234567", "testField"));

        // Should throw for invalid number
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            PhoneNumberUtil.validatePakistaniMobile("invalid", "testField")
        );
        assertTrue(exception.getMessage().contains("testField"));
        assertTrue(exception.getMessage().contains(PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE));
    }

    @Test
    void testFormatMethod() {
        // Should return formatted number for valid input
        String validNumber = "+923001234567";
        assertEquals(validNumber, PhoneNumberUtil.formatPakistaniMobile(validNumber));

        // Should handle whitespace
        assertEquals(validNumber, PhoneNumberUtil.formatPakistaniMobile("  " + validNumber + "  "));

        // Should throw for invalid number
        assertThrows(IllegalArgumentException.class, () -> PhoneNumberUtil.formatPakistaniMobile("invalid"));
    }

    @Test
    void testAuthenticationValidation() {
        assertTrue(PhoneNumberUtil.isValidForAuthentication("+923001234567"));
        assertFalse(PhoneNumberUtil.isValidForAuthentication("invalid"));
    }

    @Test
    void testConstants() {
        assertNotNull(PhoneNumberUtil.PAKISTANI_MOBILE_REGEX);
        assertNotNull(PhoneNumberUtil.EXAMPLE_PHONE_NUMBER);
        assertNotNull(PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE);

        // Example number should be valid according to our validation
        assertTrue(PhoneNumberUtil.isValidPakistaniMobile(PhoneNumberUtil.EXAMPLE_PHONE_NUMBER));
    }

    @Test
    void testUtilityMethods() {
        assertNotNull(PhoneNumberUtil.getValidationRegex());
        assertNotNull(PhoneNumberUtil.getFormatDescription());

        // Regex should match our constant
        assertEquals(PhoneNumberUtil.PAKISTANI_MOBILE_REGEX, PhoneNumberUtil.getValidationRegex());
    }

    @Test
    void testUtilityClassCannotBeInstantiated() {
        // Verify that the utility class cannot be instantiated
        assertThrows(UnsupportedOperationException.class, () -> {
            // Use reflection to try to call the private constructor
            var constructor = PhoneNumberUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }

    @Test
    void testNormalization() {
        // Test already valid format
        assertEquals("+923001234567", PhoneNumberUtil.normalize("+923001234567"));

        // Test local format (03xxxxxxxxx)
        assertEquals("+923001234567", PhoneNumberUtil.normalize("03001234567"));

        // Test international without + (923xxxxxxxxx)
        assertEquals("+923001234567", PhoneNumberUtil.normalize("923001234567"));

        // Test with spaces and formatting
        assertEquals("+923001234567", PhoneNumberUtil.normalize("0300 123 4567"));
        assertEquals("+923001234567", PhoneNumberUtil.normalize("0300-123-4567"));
        assertEquals("+923001234567", PhoneNumberUtil.normalize("(0300) 123-4567"));

        // Test normalization with validation
        assertTrue(PhoneNumberUtil.isValidForAuthenticationWithNormalization("03001234567"));
        assertTrue(PhoneNumberUtil.isValidForAuthenticationWithNormalization("923001234567"));
        assertTrue(PhoneNumberUtil.isValidForAuthenticationWithNormalization("+923001234567"));

        // Test invalid formats that can't be normalized
        assertFalse(PhoneNumberUtil.isValidForAuthenticationWithNormalization("1234567890"));
        assertFalse(PhoneNumberUtil.isValidForAuthenticationWithNormalization(""));
        assertFalse(PhoneNumberUtil.isValidForAuthenticationWithNormalization(null));
    }
}
