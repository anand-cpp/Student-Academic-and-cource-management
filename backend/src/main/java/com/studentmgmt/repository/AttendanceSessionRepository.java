package com.studentmgmt.repository;

import com.studentmgmt.model.AttendanceSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {

    List<AttendanceSession> findByCourseId(Long courseId);

    List<AttendanceSession> findByCourseIdAndSessionDateBetween(Long courseId, LocalDate startDate, LocalDate endDate);

    Page<AttendanceSession> findByCourseIdAndSessionDateBetween(Long courseId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Optional<AttendanceSession> findByCourseIdAndSessionDateAndSessionNumber(Long courseId, LocalDate sessionDate, Integer sessionNumber);

    Page<AttendanceSession> findByCourseId(Long courseId, Pageable pageable);

    @Query("SELECT s FROM AttendanceSession s WHERE s.course.id = :courseId AND s.isCancelled = false ORDER BY s.sessionDate, s.sessionNumber")
    List<AttendanceSession> findActiveSessionsByCourseId(Long courseId);

    long countByCourseId(Long courseId);

    boolean existsByCourseIdAndSessionDateAndSessionNumber(Long courseId, LocalDate sessionDate, Integer sessionNumber);
}