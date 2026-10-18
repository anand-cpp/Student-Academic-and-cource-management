package com.studentmgmt.service;

import com.studentmgmt.dto.CourseDTO;
import com.studentmgmt.dto.CourseCreateDTO;
import com.studentmgmt.dto.CourseUpdateDTO;
import com.studentmgmt.exception.ResourceNotFoundException;
import com.studentmgmt.model.Course;
import com.studentmgmt.model.CourseStatus;
import com.studentmgmt.model.Department;
import com.studentmgmt.model.Faculty;
import com.studentmgmt.repository.CourseRepository;
import com.studentmgmt.repository.DepartmentRepository;
import com.studentmgmt.repository.FacultyRepository;
import com.studentmgmt.util.DTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final DTOMapper dtoMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseDTO> findById(Long id) {
        return courseRepository.findById(id)
                .map(dtoMapper::toCourseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseDTO> findByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode)
                .map(dtoMapper::toCourseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> search(Long departmentId, Integer semester, Long facultyId,
                                  CourseStatus status, String academicYear, String search, Pageable pageable) {
        return courseRepository.searchCourses(departmentId, semester, facultyId, status, academicYear, search, pageable)
                .map(dtoMapper::toCourseDTO);
    }

    @Override
    public CourseDTO create(CourseCreateDTO createDTO) {
        if (courseRepository.existsByCourseCode(createDTO.getCourseCode())) {
            throw new IllegalArgumentException("Course code already exists: " + createDTO.getCourseCode());
        }

        Department department = departmentRepository.findById(createDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + createDTO.getDepartmentId()));

        Faculty faculty = null;
        if (createDTO.getFacultyId() != null) {
            faculty = facultyRepository.findById(createDTO.getFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + createDTO.getFacultyId()));
        }

        Course course = Course.builder()
                .courseCode(createDTO.getCourseCode())
                .title(createDTO.getTitle())
                .description(createDTO.getDescription())
                .credits(createDTO.getCredits() != null ? createDTO.getCredits() : 3)
                .department(department)
                .semester(createDTO.getSemester())
                .faculty(faculty)
                .maxEnrollment(createDTO.getMaxEnrollment() != null ? createDTO.getMaxEnrollment() : 60)
                .prerequisites(createDTO.getPrerequisites())
                .syllabusUrl(createDTO.getSyllabusUrl())
                .status(CourseStatus.ACTIVE)
                .academicYear(createDTO.getAcademicYear())
                .build();

        course = courseRepository.save(course);
        log.info("Created course: {} ({})", course.getCourseCode(), course.getId());

        return dtoMapper.toCourseDTO(course);
    }

    @Override
    public CourseDTO update(Long id, CourseUpdateDTO updateDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        if (updateDTO.getTitle() != null) {
            course.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getDescription() != null) {
            course.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getCredits() != null) {
            course.setCredits(updateDTO.getCredits());
        }
        if (updateDTO.getSemester() != null) {
            course.setSemester(updateDTO.getSemester());
        }
        if (updateDTO.getMaxEnrollment() != null) {
            course.setMaxEnrollment(updateDTO.getMaxEnrollment());
        }
        if (updateDTO.getPrerequisites() != null) {
            course.setPrerequisites(updateDTO.getPrerequisites());
        }
        if (updateDTO.getSyllabusUrl() != null) {
            course.setSyllabusUrl(updateDTO.getSyllabusUrl());
        }
        if (updateDTO.getAcademicYear() != null) {
            course.setAcademicYear(updateDTO.getAcademicYear());
        }
        if (updateDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(updateDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + updateDTO.getDepartmentId()));
            course.setDepartment(department);
        }
        if (updateDTO.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(updateDTO.getFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + updateDTO.getFacultyId()));
            course.setFaculty(faculty);
        } else if (updateDTO.getFacultyId() != null && updateDTO.getFacultyId() == 0L) {
            course.setFaculty(null);
        }

        course = courseRepository.save(course);
        log.info("Updated course: {} ({})", course.getCourseCode(), course.getId());

        return dtoMapper.toCourseDTO(course);
    }

    @Override
    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.setStatus(CourseStatus.ARCHIVED);
        courseRepository.save(course);
        log.info("Archived course: {} ({})", course.getCourseCode(), course.getId());
    }

    @Override
    public void changeStatus(Long id, CourseStatus status) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.setStatus(status);
        courseRepository.save(course);
        log.info("Changed status for course {} to {}", course.getCourseCode(), status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByDepartmentId(Long departmentId) {
        return courseRepository.countByDepartmentId(departmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByFacultyId(Long facultyId) {
        return courseRepository.countByFacultyId(facultyId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(CourseStatus status) {
        return courseRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCourseCode(String courseCode) {
        return courseRepository.existsByCourseCode(courseCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findByDepartmentId(Long departmentId) {
        return courseRepository.findByDepartmentId(departmentId).stream()
                .map(dtoMapper::toCourseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findActiveByFacultyId(Long facultyId) {
        return courseRepository.findActiveCoursesByFacultyId(facultyId).stream()
                .map(dtoMapper::toCourseDTO)
                .toList();
    }
}