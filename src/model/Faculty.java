package model;

import java.time.LocalDate;
import java.util.Objects;
import util.InputValidator;

/**
 * A faculty member. Extends {@link Person} (INHERITANCE).
 *
 * <p>Demonstrates POLYMORPHISM by overriding {@link #getRole()} and
 * {@link #getProfileSummary()}.
 */
public class Faculty extends Person {

    private String facultyId;   // business id, e.g. FAC0001 (unique)
    private String employeeId;  // unique employee id
    private String department;
    private String designation;
    private String specialization;

    public Faculty() {
    }

    public Faculty(int id, String facultyId, String employeeId, String fullName,
            String email, String phone, String address, LocalDate dateOfBirth,
            String department, String designation, String specialization) {
        super(id, fullName, email, phone, address, dateOfBirth);
        setFacultyId(facultyId);
        setEmployeeId(employeeId);
        setDepartment(department);
        setDesignation(designation);
        setSpecialization(specialization);
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = InputValidator.idCode(facultyId, "Faculty ID");
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = InputValidator.idCode(employeeId, "Employee ID");
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = InputValidator.required(department, "Department");
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = InputValidator.required(designation, "Designation");
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = InputValidator.required(specialization, "Specialization");
    }

    @Override
    public String getRole() {
        return "Faculty";
    }

    @Override
    public String getProfileSummary() {
        return facultyId + " | " + employeeId + " | " + department + " - " + designation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Faculty)) {
            return false;
        }
        Faculty f = (Faculty) o;
        return Objects.equals(facultyId, f.facultyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facultyId);
    }
}
