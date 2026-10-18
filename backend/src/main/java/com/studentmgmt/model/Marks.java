package com.studentmgmt.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "marks", indexes = {
    @Index(name = "idx_marks_assessment", columnList = "assessment_id"),
    @Index(name = "idx_marks_enrollment", columnList = "enrollment_id"),
    @Index(name = "idx_marks_finalized", columnList = "is_finalized")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_assessment_enrollment", columnNames = {"assessment_id", "enrollment_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marks extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(name = "obtained_marks", nullable = false, precision = 5, scale = 2)
    private BigDecimal obtainedMarks = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    private User gradedBy;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "is_finalized", nullable = false)
    private Boolean isFinalized = false;

    // Convenience methods
    public String getStudentId() {
        return enrollment != null && enrollment.getStudent() != null ? enrollment.getStudent().getStudentId() : null;
    }

    public String getStudentName() {
        return enrollment != null && enrollment.getStudent() != null ? enrollment.getStudent().getFullName() : null;
    }

    public String getAssessmentTitle() {
        return assessment != null ? assessment.getTitle() : null;
    }

    public double getObtainedMarksDouble() {
        return obtainedMarks != null ? obtainedMarks.doubleValue() : 0.0;
    }

    public double getPercentage() {
        if (assessment == null || assessment.getMaxMarks() == null || assessment.getMaxMarks().doubleValue() == 0) {
            return 0.0;
        }
        return (getObtainedMarksDouble() / assessment.getMaxMarksDouble()) * 100.0;
    }

    public double getWeightedScore() {
        if (assessment == null || assessment.getWeightage() == null) {
            return 0.0;
        }
        return getPercentage() * (assessment.getWeightageDouble() / 100.0);
    }
}