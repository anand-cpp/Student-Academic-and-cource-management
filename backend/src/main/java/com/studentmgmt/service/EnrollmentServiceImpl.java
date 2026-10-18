package com.studentmgmt.service;

import com.studentmgmt.dto.EnrollmentDTO;
import com.studentmgmt.dto.EnrollmentCreateDTO;
import com.studentmgmt.dto.EnrollmentUpdateDTO;
import com.studentmgmt.exception.BusinessRuleViolationException;
import com.studentmgmt.exception.ResourceNotFoundException;
import com.studentmgmt.model.Course;
import com.studentmgmt.model.Enrollment;
import com.studentmgmt.model.EnrollmentStatus;
import com.studentmgmt.model.Student;
import com.studentmgmt.repository.CourseRepository;
import com.studentmgmt.repository.EnrollmentRepository;
import com.studentmgmt.repository.StudentRepository;
import com.studentmgmt.util.DTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DTOMapper dtoMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<EnrollmentDTO> findById(Long id) {
        return enrollmentRepository.findById(id)
                .map(dtoMapper::toEnrollmentDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentDTO> search(Long studentId, Long courseId, EnrollmentStatus status, Pageable pageable) {
        return enrollmentRepository.searchEnrollments(studentId, courseId, status, pageable)
                .map(dtoMapper::toEnrollmentDTO);
    }

    @Override
    public EnrollmentDTO create(EnrollmentCreateDTO createDTO) {
        Student student = studentRepository.findById(createDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + createDTO.getStudentId()));

        Course course = courseRepository.findById(createDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + createDTO.getCourseId()));

        // Validate business rules
        if (!student.isActive()) {
            throw new BusinessRuleViolationException("Cannot enroll inactive student");
        }
        if (!course.isActive()) {
            throw new BusinessRuleViolationException("Cannot enroll in inactive course");
        }
        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new BusinessRuleViolationException("Student is already enrolled in this course");
        }

        // Check enrollment capacity
        long currentEnrollment = enrollmentRepository.countByCourseIdAndStatus(course.getId(), EnrollmentStatus.ENROLLED);
        if (currentEnrollment >= course.getMaxEnrollment()) {
            throw new BusinessRuleViolationException("Course has reached maximum enrollment capacity");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrollmentDate(createDTO.getEnrollmentDate() != null ? createDTO.getEnrollmentDate() : LocalDate.now())
                .status(EnrollmentStatus.ENROLLED)
                .build();

        enrollment = enrollmentRepository.save(enrollment);
        log.info("Created enrollment: student={}, course={}", student.getStudentId(), course.getCourseCode());

        return dtoMapper.toEnrollmentDTO(enrollment);
    }

    @Override
    public EnrollmentDTO update(Long id, EnrollmentUpdateDTO updateDTO) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));

        if (updateDTO.getStatus() != null) {
            enrollment.setStatus(EnrollmentStatus.valueOf(updateDTO.getStatus()));
            if (enrollment.getStatus() == EnrollmentStatus.COMPLETED && enrollment.getCompletionDate() == null) {
                enrollment.setCompletionDate(LocalDate.now());
            }
        }
        if (updateDTO.getGrade() != null) {
            enrollment.setGrade(updateDTO.getGrade());
        }
        if (updateDTO.getGradePoints() != null) {
            enrollment.setGradePoints(java.math.BigDecimal.valueOf(updateDTO.getGradePoints()));
        }

        enrollment = enrollmentRepository.save(enrollment);
        log.info("Updated enrollment: id={}", enrollment.getId());

        return dtoMapper.toEnrollmentDTO(enrollment);
    }

    @Override
    public void delete(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        enrollment.setStatus(EnrollmentStatus.WITHDRAWN);
        enrollmentRepository.save(enrollment);
        log.info("Withdrew enrollment: id={}", enrollment.getId());
    }

    @Override
    public void changeStatus(Long id, EnrollmentStatus status) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        enrollment.setStatus(status);
        if (status == EnrollmentStatus.COMPLETED && enrollment.getCompletionDate() == null) {
            enrollment.setCompletionDate(LocalDate.now());
        }
        enrollmentRepository.save(enrollment);
        log.info("Changed status for enrollment {} to {}", enrollment.getId(), status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStudentId(Long studentId) {
        return enrollmentRepository.countByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCourseId(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status) {
        return enrollmentRepository.countByCourseIdAndStatus(courseId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentIdAndCourseId(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> findActiveByStudentId(Long studentId) {
        return enrollmentRepository.findActiveEnrollmentsByStudentId(studentId).stream()
                .map(dtoMapper::toEnrollmentDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> findActiveByCourseId(Long courseId) {
        return enrollmentRepository.findActiveEnrollmentsByCourseId(courseId).stream()
                .map(dtoMapper::toEnrollmentDTO)
                .toList();
    }
}