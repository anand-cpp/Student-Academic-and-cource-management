package service;

import java.math.BigDecimal;

/**
 * Configurable grading scale.
 *
 * <p>Documented example scale (not an official institutional policy):<br>
 * A: 90-100% | B: 80-89.99% | C: 70-79.99% | D: 60-69.99% | E: 50-59.99% |
 * F: below 50%.
 *
 * <p>The scale is centralized here so it is easy to modify in one place without
 * touching the UI or DAO layers. Percentages are compared with BigDecimal for
 * precise boundaries.
 */
public class GradeService {

    private static final BigDecimal A = new BigDecimal("90");
    private static final BigDecimal B = new BigDecimal("80");
    private static final BigDecimal C = new BigDecimal("70");
    private static final BigDecimal D = new BigDecimal("60");
    private static final BigDecimal E = new BigDecimal("50");

    public GradeService() {
    }

    /**
     * Calculate the letter grade for a percentage.
     *
     * @param percentage the percentage (0-100)
     * @return the letter grade
     */
    public String calculateGrade(BigDecimal percentage) {
        if (percentage == null) {
            return "F";
        }
        if (percentage.compareTo(A) >= 0) {
            return "A";
        }
        if (percentage.compareTo(B) >= 0) {
            return "B";
        }
        if (percentage.compareTo(C) >= 0) {
            return "C";
        }
        if (percentage.compareTo(D) >= 0) {
            return "D";
        }
        if (percentage.compareTo(E) >= 0) {
            return "E";
        }
        return "F";
    }

    /** Minimum percentage required to pass (grade D or better). */
    public static BigDecimal passingThreshold() {
        return D;
    }

    /** Human-readable label for a grade. */
    public static String label(String grade) {
        switch (grade) {
            case "A": return "Excellent";
            case "B": return "Very Good";
            case "C": return "Good";
            case "D": return "Pass";
            case "E": return "Below Average";
            case "F": return "Fail";
            default: return "Unknown";
        }
    }
}
