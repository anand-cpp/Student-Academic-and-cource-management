package com.studentmgmt.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "enrollments", indexes = {
    @Index(name = "idx_enrollments_student", columnList = "student_id"),
    @Index(name = "idx_enrollments_course", columnList = "course_id"),
    @Index(name = "idx_enrollments_status", columnList = "status"),
    @Index(name = "idx_enrollments_date", columnList = "enrollment_date")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_student_course", columnNames = {"student_id", "course_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(name = "grade", length = 5)
    private String grade;

    @Column(name = "grade_points", precision = 3, scale = 2)
    private BigDecimal gradePoints;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    // Convenience methods
    public String getStudentId() {
        return student != null ? student.getStudentId() : null;
    }

    public String getStudentName() {
        return student != null ? student.getFullName() : null;
    }

    public String getCourseCode() {
        return course != null ? course.getCourseCode() : null;
    }

    public String getCourseTitle() {
        return course != null ? course.getTitle() : null;
    }

    public boolean isActive() {
        return status == EnrollmentStatus.ENROLLED;
    }

    public boolean isCompleted() {
        return status == EnrollmentStatus.COMPLETED;
    }
}