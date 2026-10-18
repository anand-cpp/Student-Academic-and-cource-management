package com.studentmgmt.repository;

import com.studentmgmt.model.Course;
import com.studentmgmt.model.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByDepartmentId(Long departmentId);

    List<Course> findBySemester(Integer semester);

    List<Course> findByFacultyId(Long facultyId);

    List<Course> findByStatus(CourseStatus status);

    List<Course> findByAcademicYear(String academicYear);

    Page<Course> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Course> findByFacultyId(Long facultyId, Pageable pageable);

    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE " +
           "(:departmentId IS NULL OR c.department.id = :departmentId) AND " +
           "(:semester IS NULL OR c.semester = :semester) AND " +
           "(:facultyId IS NULL OR c.faculty.id = :facultyId) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:academicYear IS NULL OR c.academicYear = :academicYear) AND " +
           "(:search IS NULL OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Course> searchCourses(
            @Param("departmentId") Long departmentId,
            @Param("semester") Integer semester,
            @Param("facultyId") Long facultyId,
            @Param("status") CourseStatus status,
            @Param("academicYear") String academicYear,
            @Param("search") String search,
            Pageable pageable);

    long countByDepartmentId(Long departmentId);

    long countByFacultyId(Long facultyId);

    long countByStatus(CourseStatus status);

    boolean existsByCourseCode(String courseCode);

    @Query("SELECT c FROM Course c WHERE c.faculty.id = :facultyId AND c.status = 'ACTIVE'")
    List<Course> findActiveCoursesByFacultyId(Long facultyId);
}