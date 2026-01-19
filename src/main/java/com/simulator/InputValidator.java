package com.simulator;

/**
 * Input validation utility class for consistent validation across the
 * application.
 * Reduces duplicate validation code in controllers.
 */
public class InputValidator {

    /**
     * Validate if a string is a valid integer within the given range.
     * 
     * @param input The input string to validate
     * @param min   Minimum allowed value (inclusive)
     * @param max   Maximum allowed value (inclusive)
     * @return true if valid, false otherwise
     */
    public static boolean isValidInteger(String input, int min, int max) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            int value = Integer.parseInt(input.trim());
            return value >= min && value <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate if a string is a valid integer (any value).
     */
    public static boolean isValidInteger(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parse a string to integer safely, returning a default value if invalid.
     * 
     * @param input        The input string to parse
     * @param defaultValue The default value to return if parsing fails
     * @return The parsed integer or default value
     */
    public static int parseIntegerSafe(String input, int defaultValue) {
        if (input == null || input.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parse a string to integer safely with range clamping.
     */
    public static int parseIntegerSafe(String input, int defaultValue, int min, int max) {
        int value = parseIntegerSafe(input, defaultValue);
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Validate if a string is non-empty and within the max length.
     */
    public static boolean isValidString(String input, int maxLength) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return input.trim().length() <= maxLength;
    }

    /**
     * Check if a string is non-null and non-empty (after trimming).
     */
    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * Sanitize input string by trimming whitespace and removing special characters.
     * 
     * @param input The input string to sanitize
     * @return Sanitized string
     */
    public static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }

    /**
     * Sanitize and limit input to a maximum length.
     */
    public static String sanitize(String input, int maxLength) {
        String sanitized = sanitize(input);
        if (sanitized.length() > maxLength) {
            return sanitized.substring(0, maxLength);
        }
        return sanitized;
    }

    /**
     * Get a validation message for invalid input.
     */
    public static String getIntegerValidationMessage(int min, int max) {
        return String.format("Please enter a valid integer between %d and %d.", min, max);
    }

    /**
     * Get a validation message for empty input.
     */
    public static String getEmptyInputMessage(String fieldName) {
        return String.format("Please enter a %s.", fieldName);
    }
}
