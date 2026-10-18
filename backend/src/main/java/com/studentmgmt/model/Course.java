package com.studentmgmt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses", indexes = {
    @Index(name = "idx_courses_code", columnList = "course_code"),
    @Index(name = "idx_courses_department", columnList = "department_id"),
    @Index(name = "idx_courses_semester", columnList = "semester"),
    @Index(name = "idx_courses_faculty", columnList = "faculty_id"),
    @Index(name = "idx_courses_status", columnList = "status"),
    @Index(name = "idx_courses_academic_year", columnList = "academic_year")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseEntity {

    @Column(name = "course_code", nullable = false, unique = true, length = 20)
    private String courseCode;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "credits", nullable = false)
    private Integer credits = 3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "semester", nullable = false)
    private Integer semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @Column(name = "max_enrollment", nullable = false)
    private Integer maxEnrollment = 60;

    @Column(name = "prerequisites", columnDefinition = "TEXT")
    private String prerequisites;

    @Column(name = "syllabus_url", length = 500)
    private String syllabusUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CourseStatus status = CourseStatus.ACTIVE;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    // Convenience methods
    public String getDepartmentCode() {
        return department != null ? department.getCode() : null;
    }

    public String getFacultyName() {
        return faculty != null ? faculty.getFullName() : null;
    }

    public boolean isActive() {
        return status == CourseStatus.ACTIVE;
    }

    public String getDisplayName() {
        return courseCode + " - " + title;
    }
}