package util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Centralized input validation.
 *
 * <p>Every user-supplied value is validated here before it reaches the service
 * or DAO layers. This keeps validation logic in one place, demonstrates
 * encapsulation of business rules, and provides helpful, field-specific error
 * messages instead of cryptic database failures.
 */
public final class InputValidator {

    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9][0-9\\s\\-]{6,14}$");
    private static final Pattern ID_CODE = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9\\-]{1,19}$");

    private InputValidator() {
        // Utility class - not instantiable
    }

    /** Required non-blank text. */
    public static String required(String value, String fieldName) throws AppException {
        if (value == null || value.trim().isEmpty()) {
            throw new AppException("REQUIRED", fieldName + " is required.");
        }
        return value.trim();
    }

    /** Required name (letters, spaces, hyphens, apostrophes). */
    public static String name(String value, String fieldName) throws AppException {
        String v = required(value, fieldName);
        if (!Pattern.matches("^[A-Za-z][A-Za-z .'-]{1,99}$", v)) {
            throw new AppException("INVALID_NAME", fieldName + " must be a valid name.");
        }
        return v;
    }

    public static String email(String value) throws AppException {
        String v = required(value, "Email");
        if (!EMAIL.matcher(v).matches()) {
            throw new AppException("INVALID_EMAIL", "Enter a valid email address (for example name@college.edu).");
        }
        return v.toLowerCase();
    }

    public static String phone(String value) throws AppException {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        String v = value.trim();
        if (!PHONE.matcher(v).matches()) {
            throw new AppException("INVALID_PHONE",
                    "Enter a valid phone number (6-15 digits, optional leading +).");
        }
        return v;
    }

    /** Non-negative integer (used for credits, semesters). */
    public static int positiveInt(String value, String fieldName, int min) throws AppException {
        String v = required(value, fieldName);
        try {
            int n = Integer.parseInt(v);
        } catch (NumberFormatException ex) {
            throw new AppException("INVALID_NUMBER", fieldName + " must be a whole number.", ex);
        }
        int n = Integer.parseInt(v);
        if (n < min) {
            throw new AppException("OUT_OF_RANGE", fieldName + " must be at least " + min + ".");
        }
        return n;
    }

    public static BigDecimal decimal(String value, String fieldName) throws AppException {
        String v = required(value, fieldName);
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException ex) {
            throw new AppException("INVALID_NUMBER", fieldName + " must be a valid number.", ex);
        }
    }

    /** Marks within [0, max]. */
    public static BigDecimal marks(BigDecimal marks, BigDecimal max) throws AppException {
        if (marks == null) {
            throw new AppException("REQUIRED", "Marks obtained is required.");
        }
        if (marks.signum() < 0) {
            throw new AppException("OUT_OF_RANGE", "Marks cannot be negative.");
        }
        if (marks.compareTo(max) > 0) {
            throw new AppException("OUT_OF_RANGE",
                    "Marks cannot exceed the course maximum of " + max.stripTrailingZeros().toPlainString() + ".");
        }
        return marks;
    }

    public static String idCode(String value, String fieldName) throws AppException {
        String v = required(value, fieldName);
        if (!ID_CODE.matcher(v).matches()) {
            throw new AppException("INVALID_CODE", fieldName + " must be 2-20 alphanumeric characters or hyphens.");
        }
        return v.toUpperCase();
    }

    public static String attendanceStatus(String value) throws AppException {
        String v = required(value, "Attendance status");
        String upper = v.toUpperCase();
        if (!"PRESENT".equals(upper) && !"ABSENT".equals(upper) && !"LATE".equals(upper)) {
            throw new AppException("INVALID_STATUS", "Attendance status must be Present, Absent, or Late.");
        }
        return upper;
    }
}
