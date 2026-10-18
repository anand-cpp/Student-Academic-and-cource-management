package com.studentmgmt.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessments", indexes = {
    @Index(name = "idx_assessments_course", columnList = "course_id"),
    @Index(name = "idx_assessments_type", columnList = "type"),
    @Index(name = "idx_assessments_date", columnList = "assessment_date"),
    @Index(name = "idx_assessments_published", columnList = "is_published")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private AssessmentType type;

    @Column(name = "max_marks", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxMarks;

    @Column(name = "weightage", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightage;

    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Convenience methods
    public String getCourseCode() {
        return course != null ? course.getCourseCode() : null;
    }

    public double getMaxMarksDouble() {
        return maxMarks != null ? maxMarks.doubleValue() : 0.0;
    }

    public double getWeightageDouble() {
        return weightage != null ? weightage.doubleValue() : 0.0;
    }
}