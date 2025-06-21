package com.shaffaf.shaffafservice.util;

import java.util.regex.Pattern;

/**
 * Utility class for phone number validation and formatting.
 * Provides centralized validation for Pakistani mobile numbers throughout the application.
 */
public final class PhoneNumberUtil {/**
 * Pakistani mobile number pattern: +923xxxxxxxxx
 * - Starts with +92 (Pakistan country code)
 * - Followed by 3 (Pakistani mobile prefix)
 * - Followed by exactly 9 digits
 * Total length: 13 characters
 */

    public static final String PAKISTANI_MOBILE_REGEX = "^\\+923[0-9]{9}$";

    /**
     * Compiled pattern for better performance when used multiple times
     */
    private static final Pattern PAKISTANI_MOBILE_PATTERN = Pattern.compile(PAKISTANI_MOBILE_REGEX);

    /**
     * Example of valid Pakistani mobile number
     */
    public static final String EXAMPLE_PHONE_NUMBER = "+923311234569";

    /**
     * Error message for invalid phone number format
     */
    public static final String INVALID_PHONE_ERROR_MESSAGE =
        "Phone number must be in format +923311234569 (Pakistani mobile number with +92 country code)";

    // Private constructor to prevent instantiation
    private PhoneNumberUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Validates if the given phone number matches Pakistani mobile number format.
     *
     * @param phoneNumber the phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean isValidPakistaniMobile(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return PAKISTANI_MOBILE_PATTERN.matcher(phoneNumber.trim()).matches();
    }

    /**
     * Validates phone number and throws exception if invalid.
     * Useful for validation in REST controllers and services.
     *
     * @param phoneNumber the phone number to validate
     * @param fieldName the name of the field being validated (for error messages)
     * @throws IllegalArgumentException if the phone number is invalid
     */
    public static void validatePakistaniMobile(String phoneNumber, String fieldName) {
        if (!isValidPakistaniMobile(phoneNumber)) {
            throw new IllegalArgumentException(String.format("%s: %s", fieldName, INVALID_PHONE_ERROR_MESSAGE));
        }
    }

    /**
     * Formats and validates a phone number.
     * Currently returns the input as-is since we enforce exact format,
     * but can be extended for normalization if needed.
     *
     * @param phoneNumber the phone number to format
     * @return the formatted phone number
     * @throws IllegalArgumentException if the phone number is invalid
     */
    public static String formatPakistaniMobile(String phoneNumber) {
        validatePakistaniMobile(phoneNumber, "Phone number");
        return phoneNumber.trim();
    }

    /**
     * Checks if a phone number is suitable for authentication.
     * Same as isValidPakistaniMobile but with explicit naming for authentication context.
     *
     * @param phoneNumber the phone number used for authentication
     * @return true if valid for authentication, false otherwise
     */
    public static boolean isValidForAuthentication(String phoneNumber) {
        return isValidPakistaniMobile(phoneNumber);
    }

    /**
     * Gets the regex pattern used for validation.
     * Useful for @Pattern annotations in DTOs.
     *
     * @return the regex pattern string
     */
    public static String getValidationRegex() {
        return PAKISTANI_MOBILE_REGEX;
    }

    /**
     * Gets a user-friendly description of the expected phone number format.
     *
     * @return description of the expected format
     */
    public static String getFormatDescription() {
        return "Pakistani mobile number in format +923xxxxxxxxx (e.g., " + EXAMPLE_PHONE_NUMBER + ")";
    }

    /**
     * Normalizes a phone number to the standard Pakistani mobile format.
     * Handles various input formats including local format (03xxxxxxxxx) and international without + sign.
     *
     * @param phoneNumber the input phone number in various formats
     * @return the normalized phone number in +923xxxxxxxxx format, or the original if it cannot be normalized
     */
    public static String normalize(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }

        String normalized = phoneNumber.trim();

        // If it's already in the correct format, return as-is
        if (isValidPakistaniMobile(normalized)) {
            return normalized;
        }

        // Remove any spaces, dashes, parentheses, or other formatting
        normalized = normalized.replaceAll("[\\s\\-\\(\\)]", "");

        // Handle local format (03xxxxxxxxx) - convert to international
        if (normalized.startsWith("03") && normalized.length() == 11) {
            normalized = "+92" + normalized.substring(1);
        }
        // Handle international format without + (923xxxxxxxxx)
        else if (normalized.startsWith("923") && normalized.length() == 12) {
            normalized = "+" + normalized;
        }
        // Handle country code without + (92xxxxxxxxxx)
        else if (normalized.startsWith("92") && normalized.length() == 12) {
            normalized = "+" + normalized;
        }

        return normalized;
    }

    /**
     * Checks if a phone number is suitable for authentication after normalization.
     * This method first normalizes the phone number and then validates it.
     *
     * @param phoneNumber the phone number used for authentication
     * @return true if valid for authentication after normalization, false otherwise
     */
    public static boolean isValidForAuthenticationWithNormalization(String phoneNumber) {
        String normalizedPhone = normalize(phoneNumber);
        return isValidPakistaniMobile(normalizedPhone);
    }
}
