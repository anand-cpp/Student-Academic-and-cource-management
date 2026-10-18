package com.studentmgmt.service;

import com.studentmgmt.dto.AssessmentDTO;
import com.studentmgmt.dto.AssessmentCreateDTO;
import com.studentmgmt.dto.AssessmentUpdateDTO;
import com.studentmgmt.exception.ResourceNotFoundException;
import com.studentmgmt.model.Assessment;
import com.studentmgmt.model.AssessmentType;
import com.studentmgmt.model.Course;
import com.studentmgmt.model.User;
import com.studentmgmt.repository.AssessmentRepository;
import com.studentmgmt.repository.CourseRepository;
import com.studentmgmt.repository.UserRepository;
import com.studentmgmt.util.DTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final DTOMapper dtoMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<AssessmentDTO> findById(Long id) {
        return assessmentRepository.findById(id)
                .map(dtoMapper::toAssessmentDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssessmentDTO> search(Long courseId, AssessmentType type, Boolean isPublished,
                                      LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (courseId != null) {
            return assessmentRepository.findByCourseId(courseId, pageable)
                    .map(dtoMapper::toAssessmentDTO);
        }
        return Page.empty(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentDTO> findByCourseId(Long courseId) {
        return assessmentRepository.findByCourseIdOrderByDate(courseId).stream()
                .map(dtoMapper::toAssessmentDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentDTO> findPublishedByCourseId(Long courseId) {
        return assessmentRepository.findPublishedByCourseId(courseId).stream()
                .map(dtoMapper::toAssessmentDTO)
                .toList();
    }

    @Override
    public AssessmentDTO create(AssessmentCreateDTO createDTO) {
        Course course = courseRepository.findById(createDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + createDTO.getCourseId()));

        User createdBy = userRepository.findById(createDTO.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createDTO.getCreatedById()));

        // Validate weightage
        if (createDTO.getWeightage() < 0 || createDTO.getWeightage() > 100) {
            throw new IllegalArgumentException("Weightage must be between 0 and 100");
        }

        Assessment assessment = Assessment.builder()
                .course(course)
                .title(createDTO.getTitle())
                .description(createDTO.getDescription())
                .type(createDTO.getType())
                .maxMarks(BigDecimal.valueOf(createDTO.getMaxMarks()))
                .weightage(BigDecimal.valueOf(createDTO.getWeightage()))
                .assessmentDate(createDTO.getAssessmentDate())
                .dueDate(createDTO.getDueDate())
                .isPublished(false)
                .createdBy(createdBy)
                .build();

        assessment = assessmentRepository.save(assessment);
        log.info("Created assessment: {} for course {}", assessment.getTitle(), course.getCourseCode());

        return dtoMapper.toAssessmentDTO(assessment);
    }

    @Override
    public AssessmentDTO update(Long id, AssessmentUpdateDTO updateDTO) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));

        if (updateDTO.getTitle() != null) {
            assessment.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getDescription() != null) {
            assessment.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getType() != null) {
            assessment.setType(updateDTO.getType());
        }
        if (updateDTO.getMaxMarks() != null) {
            assessment.setMaxMarks(BigDecimal.valueOf(updateDTO.getMaxMarks()));
        }
        if (updateDTO.getWeightage() != null) {
            if (updateDTO.getWeightage() < 0 || updateDTO.getWeightage() > 100) {
                throw new IllegalArgumentException("Weightage must be between 0 and 100");
            }
            assessment.setWeightage(BigDecimal.valueOf(updateDTO.getWeightage()));
        }
        if (updateDTO.getAssessmentDate() != null) {
            assessment.setAssessmentDate(updateDTO.getAssessmentDate());
        }
        if (updateDTO.getDueDate() != null) {
            assessment.setDueDate(updateDTO.getDueDate());
        }

        assessment = assessmentRepository.save(assessment);
        log.info("Updated assessment: id={}", assessment.getId());

        return dtoMapper.toAssessmentDTO(assessment);
    }

    @Override
    public void delete(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));
        assessmentRepository.delete(assessment);
        log.info("Deleted assessment: id={}", id);
    }

    @Override
    public void publish(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));
        assessment.setIsPublished(true);
        assessmentRepository.save(assessment);
        log.info("Published assessment: id={}", id);
    }

    @Override
    public void unpublish(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + id));
        assessment.setIsPublished(false);
        assessmentRepository.save(assessment);
        log.info("Unpublished assessment: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCourseId(Long courseId) {
        return assessmentRepository.countByCourseId(courseId);
    }
}