package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A student's enrollment in a course.
 *
 * <p>Links one student to one course with an enrollment date and status
 * (Active, Completed, Dropped).
 */
public class Enrollment {

    private int id;
    private int studentId;
    private int courseId;
    private LocalDate enrollmentDate;
    private String status; // Active, Completed, Dropped

    public Enrollment() {
    }

    public Enrollment(int id, int studentId, int courseId, LocalDate enrollmentDate, String status) {
        setId(id);
        setStudentId(studentId);
        setCourseId(courseId);
        setEnrollmentDate(enrollmentDate);
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

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Student id must be positive.");
        }
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        if (courseId <= 0) {
            throw new IllegalArgumentException("Course id must be positive.");
        }
        this.courseId = courseId;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        String s = (status == null) ? "Active" : status.trim();
        if (!"Active".equals(s) && !"Completed".equals(s) && !"Dropped".equals(s)) {
            throw new IllegalArgumentException("Status must be Active, Completed, or Dropped.");
        }
        this.status = s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Enrollment)) {
            return false;
        }
        Enrollment e = (Enrollment) o;
        return studentId == e.studentId && courseId == e.courseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId);
    }

    @Override
    public String toString() {
        return "Enrollment[id=" + id + ", student=" + studentId + ", course=" + courseId
                + ", status=" + status + "]";
    }
}
