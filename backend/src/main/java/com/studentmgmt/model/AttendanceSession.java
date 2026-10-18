package com.studentmgmt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "attendance_sessions", indexes = {
    @Index(name = "idx_sessions_course", columnList = "course_id"),
    @Index(name = "idx_sessions_date", columnList = "session_date"),
    @Index(name = "idx_sessions_cancelled", columnList = "is_cancelled")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_course_session", columnNames = {"course_id", "session_date", "session_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "session_number", nullable = false)
    private Integer sessionNumber;

    @Column(name = "topic", length = 255)
    private String topic;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 60;

    @Column(name = "is_cancelled", nullable = false)
    private Boolean isCancelled = false;

    @Column(name = "cancellation_reason", length = 255)
    private String cancellationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Convenience methods
    public String getCourseCode() {
        return course != null ? course.getCourseCode() : null;
    }

    public String getCourseTitle() {
        return course != null ? course.getTitle() : null;
    }

    public boolean isActive() {
        return !isCancelled;
    }
}