package model;

import java.math.BigDecimal;
import java.util.Objects;
import util.InputValidator;

/**
 * A course offered by the institution.
 *
 * <p>ENCAPSULATION: credits and marks are validated (credits positive; passing
 * marks cannot exceed maximum marks).
 */
public class Course {

    private int id;
    private String courseCode;      // unique, e.g. CS101
    private String courseName;
    private String department;
    private int semester;
    private int credits;
    private BigDecimal maximumMarks;
    private BigDecimal passingMarks;
    private Integer assignedFacultyId; // nullable

    public Course() {
    }

    public Course(int id, String courseCode, String courseName, String department,
            int semester, int credits, BigDecimal maximumMarks, BigDecimal passingMarks,
            Integer assignedFacultyId) {
        setId(id);
        setCourseCode(courseCode);
        setCourseName(courseName);
        setDepartment(department);
        setSemester(semester);
        setCredits(credits);
        setMaximumMarks(maximumMarks);
        setPassingMarks(passingMarks);
        setAssignedFacultyId(assignedFacultyId);
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

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = InputValidator.idCode(courseCode, "Course code");
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = InputValidator.required(courseName, "Course name");
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = InputValidator.required(department, "Department");
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        if (semester < 1 || semester > 12) {
            throw new IllegalArgumentException("Semester must be between 1 and 12.");
        }
        this.semester = semester;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        if (credits < 1) {
            throw new IllegalArgumentException("Credits must be positive.");
        }
        this.credits = credits;
    }

    public BigDecimal getMaximumMarks() {
        return maximumMarks;
    }

    public void setMaximumMarks(BigDecimal maximumMarks) {
        if (maximumMarks == null || maximumMarks.signum() <= 0) {
            throw new IllegalArgumentException("Maximum marks must be positive.");
        }
        this.maximumMarks = maximumMarks;
    }

    public BigDecimal getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(BigDecimal passingMarks) {
        if (passingMarks == null || passingMarks.signum() < 0) {
            throw new IllegalArgumentException("Passing marks cannot be negative.");
        }
        if (maximumMarks != null && passingMarks.compareTo(maximumMarks) > 0) {
            throw new IllegalArgumentException("Passing marks cannot exceed maximum marks.");
        }
        this.passingMarks = passingMarks;
    }

    public Integer getAssignedFacultyId() {
        return assignedFacultyId;
    }

    public void setAssignedFacultyId(Integer assignedFacultyId) {
        this.assignedFacultyId = assignedFacultyId;
    }

    public boolean isPassing(BigDecimal marksObtained) {
        if (marksObtained == null || passingMarks == null) {
            return false;
        }
        return marksObtained.compareTo(passingMarks) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        Course c = (Course) o;
        return Objects.equals(courseCode, c.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode);
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}
