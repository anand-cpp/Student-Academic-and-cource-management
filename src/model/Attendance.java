package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * An attendance record for an enrollment on a specific date.
 *
 * <p>Status is one of PRESENT, ABSENT, LATE. The application rule (documented
 * in {@link service.AttendanceService}) treats LATE as attended for the
 * percentage calculation.
 */
public class Attendance {

    private int id;
    private int enrollmentId;
    private LocalDate classDate;
    private String status; // PRESENT, ABSENT, LATE

    public Attendance() {
    }

    public Attendance(int id, int enrollmentId, LocalDate classDate, String status) {
        setId(id);
        setEnrollmentId(enrollmentId);
        setClassDate(classDate);
        setStatus(status);
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

    public LocalDate getClassDate() {
        return classDate;
    }

    public void setClassDate(LocalDate classDate) {
        this.classDate = classDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        String s = (status == null) ? "PRESENT" : status.trim().toUpperCase();
        if (!"PRESENT".equals(s) && !"ABSENT".equals(s) && !"LATE".equals(s)) {
            throw new IllegalArgumentException("Status must be PRESENT, ABSENT, or LATE.");
        }
        this.status = s;
    }

    public boolean isPresent() {
        return "PRESENT".equals(status);
    }

    public boolean isLate() {
        return "LATE".equals(status);
    }

    /** Attended means present or late (student was in the session). */
    public boolean isAttended() {
        return "PRESENT".equals(status) || "LATE".equals(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attendance)) {
            return false;
        }
        Attendance a = (Attendance) o;
        return enrollmentId == a.enrollmentId
                && java.util.Objects.equals(classDate, a.classDate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(enrollmentId, classDate);
    }
}
