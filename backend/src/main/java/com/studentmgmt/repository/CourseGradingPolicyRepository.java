package com.studentmgmt.repository;

import com.studentmgmt.model.CourseGradingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseGradingPolicyRepository extends JpaRepository<CourseGradingPolicy, Long> {

    List<CourseGradingPolicy> findByCourseId(Long courseId);

    Optional<CourseGradingPolicy> findByCourseIdAndGradingPolicyId(Long courseId, Long gradingPolicyId);

    @Query("SELECT cgp FROM CourseGradingPolicy cgp WHERE cgp.course.id = :courseId AND cgp.effectiveFrom <= :date AND (cgp.effectiveTo IS NULL OR cgp.effectiveTo >= :date) ORDER BY cgp.effectiveFrom DESC")
    List<CourseGradingPolicy> findEffectiveByCourseIdAndDate(Long courseId, LocalDate date);

    @Query("SELECT cgp FROM CourseGradingPolicy cgp WHERE cgp.course.id = :courseId AND cgp.effectiveFrom <= :date AND (cgp.effectiveTo IS NULL OR cgp.effectiveTo >= :date) ORDER BY cgp.effectiveFrom DESC LIMIT 1")
    Optional<CourseGradingPolicy> findCurrentEffectiveByCourseId(Long courseId, LocalDate date);

    boolean existsByCourseIdAndGradingPolicyIdAndEffectiveFrom(Long courseId, Long gradingPolicyId, LocalDate effectiveFrom);
}