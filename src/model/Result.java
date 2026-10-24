package model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A student's final result for an enrollment.
 *
 * <p>References a valid enrollment. Marks are stored as BigDecimal for precise
 * numeric processing. Percentage and grade are derived from the marks.
 */
public class Result {

    private int id;
    private int enrollmentId;
    private BigDecimal marksObtained;
    private BigDecimal percentage;
    private String grade;

    public Result() {
    }

    public Result(int id, int enrollmentId, BigDecimal marksObtained,
            BigDecimal percentage, String grade) {
        setId(id);
        setEnrollmentId(enrollmentId);
        setMarksObtained(marksObtained);
        setPercentage(percentage);
        setGrade(grade);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id cannot be negative.");
        }
        this.id = id;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        if (enrollmentId <= 0) {
            throw new IllegalArgumentException("Enrollment id must be positive.");
        }
        this.enrollmentId = enrollmentId;
    }

    public BigDecimal getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(BigDecimal marksObtained) {
        this.marksObtained = marksObtained;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Result)) {
            return false;
        }
        Result r = (Result) o;
        return enrollmentId == r.enrollmentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentId);
    }
}
