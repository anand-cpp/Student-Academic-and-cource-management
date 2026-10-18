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

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.id = :courseId")
    Optional<Enrollment> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId")
    List<Enrollment> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId")
    List<Enrollment> findByCourseId(@Param("courseId") Long courseId);

    List<Enrollment> findByStatus(EnrollmentStatus status);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId")
    Page<Enrollment> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId")
    Page<Enrollment> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    @Query("SELECT e FROM Enrollment e WHERE " +
           "(:studentId IS NULL OR e.student.id = :studentId) AND " +
           "(:courseId IS NULL OR e.course.id = :courseId) AND " +
           "(:status IS NULL OR e.status = :status)")
    Page<Enrollment> searchEnrollments(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("status") EnrollmentStatus status,
            Pageable pageable);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    long countByStatus(EnrollmentStatus status);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status")
    long countByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") EnrollmentStatus status);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Enrollment e WHERE e.student.id = :studentId AND e.course.id = :courseId")
    boolean existsByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status = 'ENROLLED'")
    List<Enrollment> findActiveEnrollmentsByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ENROLLED'")
    List<Enrollment> findActiveEnrollmentsByCourseId(@Param("courseId") Long courseId);
}