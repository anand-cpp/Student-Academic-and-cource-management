package com.studentmgmt.repository;

import com.studentmgmt.model.Assessment;
import com.studentmgmt.model.AssessmentType;
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
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByCourseId(Long courseId);

    List<Assessment> findByCourseIdAndType(Long courseId, AssessmentType type);

    List<Assessment> findByCourseIdAndIsPublished(Long courseId, Boolean isPublished);

    Page<Assessment> findByCourseId(Long courseId, Pageable pageable);

    @Query("SELECT a FROM Assessment a WHERE a.course.id = :courseId ORDER BY a.assessmentDate")
    List<Assessment> findByCourseIdOrderByDate(Long courseId);

    @Query("SELECT a FROM Assessment a WHERE a.course.id = :courseId AND a.assessmentDate BETWEEN :startDate AND :endDate")
    List<Assessment> findByCourseIdAndDateRange(Long courseId, LocalDate startDate, LocalDate endDate);

    long countByCourseId(Long courseId);

    long countByCourseIdAndIsPublished(Long courseId, Boolean isPublished);

    @Query("SELECT a FROM Assessment a WHERE a.course.id = :courseId AND a.isPublished = true")
    List<Assessment> findPublishedByCourseId(Long courseId);
}