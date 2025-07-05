package com.shaffaf.shaffafservice.security;

import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Security utility class for SQL injection prevention and input validation.
 * This class provides methods to validate and sanitize user inputs to prevent security vulnerabilities.
 */
@Component
public class SqlSecurityUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SqlSecurityUtil.class);

    // Pattern to detect potential SQL injection attempts
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i).*('|(\\-\\-)|(;)|(\\|)|(\\*)|(%)|(union)|(select)|(insert)|(delete)|(update)|(drop)|(create)|(alter)|(exec)|(execute)|(sp_)|(xp_)).*",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    // Pattern for safe alphanumeric input with underscores
    private static final Pattern SAFE_ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    // Pattern for safe sort field names
    private static final Pattern SAFE_SORT_FIELD_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");

    /**
     * Checks if the input contains potential SQL injection patterns.
     *
     * @param input the input string to check
     * @return true if potential SQL injection is detected, false otherwise
     */
    public static boolean containsSqlInjection(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        boolean hasSqlInjection = SQL_INJECTION_PATTERN.matcher(input.trim()).matches();

        if (hasSqlInjection) {
            LOG.warn("Potential SQL injection detected in input: {}", input);
        }

        return hasSqlInjection;
    }

    /**
     * Validates that a sort field name is safe for use in SQL ORDER BY clauses.
     *
     * @param fieldName the field name to validate
     * @return true if the field name is safe, false otherwise
     */
    public static boolean isSafeSortField(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return false;
        }

        String trimmed = fieldName.trim();

        // Check length limit
        if (trimmed.length() > 50) {
            LOG.warn("Sort field name too long: {} characters", trimmed.length());
            return false;
        }

        // Check pattern
        boolean isSafe = SAFE_SORT_FIELD_PATTERN.matcher(trimmed).matches();

        if (!isSafe) {
            LOG.warn("Unsafe sort field name detected: {}", trimmed);
        }

        return isSafe;
    }

    /**
     * Validates that an input string contains only safe alphanumeric characters.
     *
     * @param input the input to validate
     * @return true if safe, false otherwise
     */
    public static boolean isSafeAlphanumeric(String input) {
        if (input == null || input.trim().isEmpty()) {
            return true; // Empty input is considered safe
        }

        String trimmed = input.trim();

        // Check for SQL injection patterns first
        if (containsSqlInjection(trimmed)) {
            return false;
        }

        // Check alphanumeric pattern
        return SAFE_ALPHANUMERIC_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Sanitizes input by removing potentially dangerous characters.
     * This method should be used as a last resort - prefer parameterized queries.
     *
     * @param input the input to sanitize
     * @return sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        String sanitized = input
            .trim()
            .replaceAll("[<>\"'&;\\-\\-]", "") // Remove dangerous chars
            .replaceAll("(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute|sp_|xp_)", "") // Remove SQL keywords
            .replaceAll("/\\*.*?\\*/", "") // Remove block comments
            .replaceAll("\\s+", " "); // Normalize whitespace

        // Limit length
        if (sanitized.length() > 255) {
            LOG.warn("Input too long, truncating from {} to 255 characters", sanitized.length());
            sanitized = sanitized.substring(0, 255);
        }

        return sanitized;
    }

    /**
     * Validates pagination parameters to prevent potential abuse.
     *
     * @param pageNumber the page number
     * @param pageSize the page size
     * @param maxPageSize the maximum allowed page size
     * @return true if valid, false otherwise
     */
    public static boolean isValidPagination(int pageNumber, int pageSize, int maxPageSize) {
        if (pageNumber < 0) {
            LOG.warn("Invalid page number: {}", pageNumber);
            return false;
        }

        if (pageSize <= 0 || pageSize > maxPageSize) {
            LOG.warn("Invalid page size: {} (max allowed: {})", pageSize, maxPageSize);
            return false;
        }

        // Check for potential integer overflow in offset calculation
        try {
            long offset = (long) pageNumber * pageSize;
            if (offset > Integer.MAX_VALUE) {
                LOG.warn("Page offset too large: {}", offset);
                return false;
            }
        } catch (ArithmeticException e) {
            LOG.warn("Arithmetic overflow in pagination calculation", e);
            return false;
        }

        return true;
    }
}
