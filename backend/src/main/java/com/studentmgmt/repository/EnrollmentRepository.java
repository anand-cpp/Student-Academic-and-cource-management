package com.studentmgmt.repository;

import com.studentmgmt.model.Enrollment;
import com.studentmgmt.model.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByStatus(EnrollmentStatus status);

    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);

    @Query("SELECT e FROM Enrollment e WHERE " +
           "(:studentId IS NULL OR e.student.id = :studentId) AND " +
           "(:courseId IS NULL OR e.course.id = :courseId) AND " +
           "(:status IS NULL OR e.status = :status)")
    Page<Enrollment> searchEnrollments(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("status") EnrollmentStatus status,
            Pageable pageable);

    long countByStudentId(Long studentId);

    long countByCourseId(Long courseId);

    long countByStatus(EnrollmentStatus status);

    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status = 'ENROLLED'")
    List<Enrollment> findActiveEnrollmentsByStudentId(Long studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ENROLLED'")
    List<Enrollment> findActiveEnrollmentsByCourseId(Long courseId);
}