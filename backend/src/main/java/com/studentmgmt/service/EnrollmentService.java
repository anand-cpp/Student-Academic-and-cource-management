package com.studentmgmt.service;

import com.studentmgmt.dto.EnrollmentDTO;
import com.studentmgmt.dto.EnrollmentCreateDTO;
import com.studentmgmt.dto.EnrollmentUpdateDTO;
import com.studentmgmt.model.Enrollment;
import com.studentmgmt.model.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {

    Optional<EnrollmentDTO> findById(Long id);

    Page<EnrollmentDTO> search(Long studentId, Long courseId, EnrollmentStatus status, Pageable pageable);

    EnrollmentDTO create(EnrollmentCreateDTO createDTO);

    EnrollmentDTO update(Long id, EnrollmentUpdateDTO updateDTO);

    void delete(Long id);

    void changeStatus(Long id, EnrollmentStatus status);

    long countByStudentId(Long studentId);

    long countByCourseId(Long courseId);

    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<EnrollmentDTO> findActiveByStudentId(Long studentId);

    List<EnrollmentDTO> findActiveByCourseId(Long courseId);
}