package com.studentmgmt.repository;

import com.studentmgmt.model.Marks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {

    Optional<Marks> findByAssessmentIdAndEnrollmentId(Long assessmentId, Long enrollmentId);

    List<Marks> findByAssessmentId(Long assessmentId);

    List<Marks> findByEnrollmentId(Long enrollmentId);

    List<Marks> findByEnrollmentIdAndIsFinalized(Long enrollmentId, Boolean isFinalized);

    Page<Marks> findByAssessmentId(Long assessmentId, Pageable pageable);

    Page<Marks> findByEnrollmentId(Long enrollmentId, Pageable pageable);

    @Query("SELECT m FROM Marks m WHERE m.enrollment.id = :enrollmentId AND m.isFinalized = true ORDER BY m.assessment.assessmentDate")
    List<Marks> findFinalizedByEnrollmentId(Long enrollmentId);

    @Query("SELECT m FROM Marks m WHERE m.assessment.course.id = :courseId AND m.enrollment.student.id = :studentId AND m.isFinalized = true")
    List<Marks> findFinalizedByCourseIdAndStudentId(Long courseId, Long studentId);

    long countByAssessmentId(Long assessmentId);

    long countByEnrollmentId(Long enrollmentId);

    boolean existsByAssessmentIdAndEnrollmentId(Long assessmentId, Long enrollmentId);

    @Query("SELECT SUM(m.obtainedMarks) FROM Marks m WHERE m.enrollment.id = :enrollmentId AND m.isFinalized = true")
    BigDecimal sumObtainedMarksByEnrollmentId(Long enrollmentId);

    @Query("SELECT SUM(m.obtainedMarks * m.assessment.weightage / 100) FROM Marks m WHERE m.enrollment.id = :enrollmentId AND m.isFinalized = true")
    BigDecimal sumWeightedMarksByEnrollmentId(Long enrollmentId);
}