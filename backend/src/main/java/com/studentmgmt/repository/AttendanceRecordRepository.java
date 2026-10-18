package com.studentmgmt.repository;

import com.studentmgmt.model.AttendanceRecord;
import com.studentmgmt.model.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findBySessionIdAndEnrollmentId(Long sessionId, Long enrollmentId);

    List<AttendanceRecord> findBySessionId(Long sessionId);

    List<AttendanceRecord> findByEnrollmentId(Long enrollmentId);

    List<AttendanceRecord> findByEnrollmentIdAndStatus(Long enrollmentId, AttendanceStatus status);

    Page<AttendanceRecord> findBySessionId(Long sessionId, Pageable pageable);

    Page<AttendanceRecord> findByEnrollmentId(Long enrollmentId, Pageable pageable);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.enrollment.id = :enrollmentId ORDER BY ar.session.sessionDate, ar.session.sessionNumber")
    List<AttendanceRecord> findByEnrollmentIdOrderBySessionDate(Long enrollmentId);

    long countByEnrollmentId(Long enrollmentId);

    long countByEnrollmentIdAndStatus(Long enrollmentId, AttendanceStatus status);

    long countByEnrollmentIdAndStatusIn(Long enrollmentId, List<AttendanceStatus> statuses);

    boolean existsBySessionIdAndEnrollmentId(Long sessionId, Long enrollmentId);

    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar WHERE ar.enrollment.id = :enrollmentId AND ar.status IN ('PRESENT', 'LATE')")
    long countPresentByEnrollmentId(Long enrollmentId);

    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar WHERE ar.enrollment.id = :enrollmentId AND ar.session.isCancelled = false")
    long countEligibleSessionsByEnrollmentId(Long enrollmentId);
}