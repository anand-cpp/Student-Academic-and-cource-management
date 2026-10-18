package com.studentmgmt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "course_grading_policies", indexes = {
    @Index(name = "idx_cgp_course", columnList = "course_id"),
    @Index(name = "idx_cgp_policy", columnList = "grading_policy_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_course_policy", columnNames = {"course_id", "grading_policy_id", "effective_from"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseGradingPolicy extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grading_policy_id", nullable = false)
    private GradingPolicy gradingPolicy;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    // Convenience methods
    public String getCourseCode() {
        return course != null ? course.getCourseCode() : null;
    }

    public String getPolicyName() {
        return gradingPolicy != null ? gradingPolicy.getName() : null;
    }

    public boolean isEffectiveOn(LocalDate date) {
        if (date == null) return false;
        if (effectiveFrom != null && date.isBefore(effectiveFrom)) return false;
        if (effectiveTo != null && date.isAfter(effectiveTo)) return false;
        return true;
    }
}