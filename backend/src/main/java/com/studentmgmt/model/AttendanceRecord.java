package com.studentmgmt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attendance_records", indexes = {
    @Index(name = "idx_attendance_session", columnList = "session_id"),
    @Index(name = "idx_attendance_enrollment", columnList = "enrollment_id"),
    @Index(name = "idx_attendance_status", columnList = "status")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_session_enrollment", columnNames = {"session_id", "enrollment_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AttendanceSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AttendanceStatus status = AttendanceStatus.ABSENT;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", nullable = false)
    private User recordedBy;

    // Convenience methods
    public String getStudentId() {
        return enrollment != null && enrollment.getStudent() != null ? enrollment.getStudent().getStudentId() : null;
    }

    public String getStudentName() {
        return enrollment != null && enrollment.getStudent() != null ? enrollment.getStudent().getFullName() : null;
    }

    public String getCourseCode() {
        return session != null && session.getCourse() != null ? session.getCourse().getCourseCode() : null;
    }

    public boolean isPresent() {
        return status == AttendanceStatus.PRESENT || status == AttendanceStatus.LATE;
    }

    public boolean isExcused() {
        return status.isExcused();
    }
}