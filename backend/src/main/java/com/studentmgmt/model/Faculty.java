package com.studentmgmt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "faculty", indexes = {
    @Index(name = "idx_faculty_faculty_id", columnList = "faculty_id"),
    @Index(name = "idx_faculty_department", columnList = "department_id"),
    @Index(name = "idx_faculty_status", columnList = "status"),
    @Index(name = "idx_faculty_designation", columnList = "designation")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Faculty extends BaseEntity {

    @Column(name = "faculty_id", nullable = false, unique = true, length = 50)
    private String facultyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "designation", nullable = false, length = 30)
    private FacultyDesignation designation;

    @Column(name = "specialization", length = 255)
    private String specialization;

    @Column(name = "qualification", columnDefinition = "TEXT")
    private String qualification;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FacultyStatus status = FacultyStatus.ACTIVE;

    @Column(name = "office_location", length = 255)
    private String officeLocation;

    @Column(name = "research_interests", columnDefinition = "TEXT")
    private String researchInterests;

    // Convenience methods
    public String getFullName() {
        return user != null ? user.getFullName() : null;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    public String getPhone() {
        return user != null ? user.getPhone() : null;
    }

    public boolean isActive() {
        return status == FacultyStatus.ACTIVE;
    }
}