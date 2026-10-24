package model;

import java.time.LocalDate;
import java.util.Objects;
import util.InputValidator;

/**
 * A student record. Extends {@link Person} (INHERITANCE).
 *
 * <p>Demonstrates POLYMORPHISM by overriding {@link #getRole()} and
 * {@link #getProfileSummary()}. Fields are private with validated setters
 * (ENCAPSULATION).
 */
public class Student extends Person {

    private String studentId;   // business id, e.g. STU0001 (unique)
    private String rollNumber;  // unique roll number
    private String department;
    private int semester;
    private LocalDate admissionDate;

    public Student() {
    }

    public Student(int id, String studentId, String rollNumber, String fullName,
            String email, String phone, String address, LocalDate dateOfBirth,
            String department, int semester, LocalDate admissionDate) {
        super(id, fullName, email, phone, address, dateOfBirth);
        setStudentId(studentId);
        setRollNumber(rollNumber);
        setDepartment(department);
        setSemester(semester);
        setAdmissionDate(admissionDate);
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = InputValidator.idCode(studentId, "Student ID");
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = InputValidator.idCode(rollNumber, "Roll number");
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

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        if (admissionDate != null) {
            util.DateUtils.notFuture(admissionDate, "Admission date");
        }
        this.admissionDate = admissionDate;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String getProfileSummary() {
        return studentId + " | " + rollNumber + " | " + department + " - Semester " + semester;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student)) {
            return false;
        }
        Student s = (Student) o;
        return Objects.equals(studentId, s.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
}
