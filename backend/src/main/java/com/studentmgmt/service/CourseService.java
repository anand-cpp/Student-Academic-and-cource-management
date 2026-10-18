package com.studentmgmt.service;

import com.studentmgmt.dto.CourseDTO;
import com.studentmgmt.dto.CourseCreateDTO;
import com.studentmgmt.dto.CourseUpdateDTO;
import com.studentmgmt.model.Course;
import com.studentmgmt.model.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    Optional<CourseDTO> findById(Long id);

    Optional<CourseDTO> findByCourseCode(String courseCode);

    Page<CourseDTO> search(Long departmentId, Integer semester, Long facultyId,
                           CourseStatus status, String academicYear, String search, Pageable pageable);

    CourseDTO create(CourseCreateDTO createDTO);

    CourseDTO update(Long id, CourseUpdateDTO updateDTO);

    void delete(Long id);

    void changeStatus(Long id, CourseStatus status);

    long countByDepartmentId(Long departmentId);

    long countByFacultyId(Long facultyId);

    long countByStatus(CourseStatus status);

    boolean existsByCourseCode(String courseCode);

    List<CourseDTO> findByDepartmentId(Long departmentId);

    List<CourseDTO> findActiveByFacultyId(Long facultyId);
}