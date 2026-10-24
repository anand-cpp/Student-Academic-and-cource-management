package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Centralized date parsing and formatting helpers.
 *
 * <p>All dates in the application use {@link LocalDate} and the ISO-8601
 * format (yyyy-MM-dd). Keeping conversion logic in one place avoids
 * duplicated SimpleDateFormat/DateTimeFormatter code across the UI and DAO
 * layers.
 */
public final class DateUtils {

    public static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private DateUtils() {
        // Utility class - not instantiable
    }

    /**
     * Parse a user-supplied date string in ISO format.
     *
     * @param value the date string
     * @return the parsed LocalDate
     * @throws AppException if the value is blank or not a valid date
     */
    public static LocalDate parse(String value) throws AppException {
        if (value == null || value.trim().isEmpty()) {
            throw new AppException("DATE_REQUIRED", "Date is required.");
        }
        try {
            return LocalDate.parse(value.trim(), ISO);
        } catch (DateTimeParseException ex) {
            throw new AppException("DATE_INVALID",
                    "Invalid date '" + value + "'. Use yyyy-MM-dd (for example 2024-08-15).", ex);
        }
    }

    /** Format a LocalDate for display. */
    public static String display(LocalDate date) {
        if (date == null) {
            return "-";
        }
        return date.format(DISPLAY);
    }

    /** Format a LocalDate for database storage (ISO). */
    public static String db(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(ISO);
    }

    /** Ensure a date is not in the future. */
    public static void notFuture(LocalDate date, String fieldName) throws AppException {
        if (date != null && date.isAfter(LocalDate.now())) {
            throw new AppException("DATE_FUTURE", fieldName + " cannot be a future date.");
        }
    }
}
