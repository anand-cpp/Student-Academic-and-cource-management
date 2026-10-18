package com.studentmgmt.service;

import com.studentmgmt.dto.CourseResultDTO;
import com.studentmgmt.dto.MarksDTO;
import com.studentmgmt.dto.MarksCreateDTO;
import com.studentmgmt.dto.MarksUpdateDTO;
import com.studentmgmt.dto.MarksBulkCreateDTO;
import com.studentmgmt.exception.BusinessRuleViolationException;
import com.studentmgmt.exception.ResourceNotFoundException;
import com.studentmgmt.model.Assessment;
import com.studentmgmt.model.Enrollment;
import com.studentmgmt.model.Marks;
import com.studentmgmt.model.User;
import com.studentmgmt.repository.AssessmentRepository;
import com.studentmgmt.repository.EnrollmentRepository;
import com.studentmgmt.repository.MarksRepository;
import com.studentmgmt.repository.UserRepository;
import com.studentmgmt.util.DTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MarksServiceImpl implements MarksService {

    private final MarksRepository marksRepository;
    private final AssessmentRepository assessmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final DTOMapper dtoMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<MarksDTO> findById(Long id) {
        return marksRepository.findById(id)
                .map(dtoMapper::toMarksDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MarksDTO> search(Long assessmentId, Long enrollmentId, Boolean isFinalized, Pageable pageable) {
        if (assessmentId != null) {
            return marksRepository.findByAssessmentId(assessmentId, pageable)
                    .map(dtoMapper::toMarksDTO);
        } else if (enrollmentId != null) {
            return marksRepository.findByEnrollmentId(enrollmentId, pageable)
                    .map(dtoMapper::toMarksDTO);
        }
        return Page.empty(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarksDTO> findByAssessmentId(Long assessmentId) {
        return marksRepository.findByAssessmentId(assessmentId).stream()
                .map(dtoMapper::toMarksDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarksDTO> findByEnrollmentId(Long enrollmentId) {
        return marksRepository.findByEnrollmentId(enrollmentId).stream()
                .map(dtoMapper::toMarksDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarksDTO> findByStudentAndCourse(Long studentId, Long courseId) {
        return marksRepository.findFinalizedByCourseIdAndStudentId(courseId, studentId).stream()
                .map(dtoMapper::toMarksDTO)
                .toList();
    }

    @Override
    public MarksDTO create(MarksCreateDTO createDTO) {
        Assessment assessment = assessmentRepository.findById(createDTO.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + createDTO.getAssessmentId()));

        Enrollment enrollment = enrollmentRepository.findById(createDTO.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + createDTO.getEnrollmentId()));

        User gradedBy = userRepository.findById(createDTO.getGradedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createDTO.getGradedById()));

        if (marksRepository.existsByAssessmentIdAndEnrollmentId(assessment.getId(), enrollment.getId())) {
            throw new BusinessRuleViolationException("Marks already exist for this assessment and enrollment");
        }

        // Validate marks
        if (createDTO.getObtainedMarks() < 0 || createDTO.getObtainedMarks() > assessment.getMaxMarksDouble()) {
            throw new BusinessRuleViolationException("Obtained marks must be between 0 and max marks (" + assessment.getMaxMarksDouble() + ")");
        }

        Marks marks = Marks.builder()
                .assessment(assessment)
                .enrollment(enrollment)
                .obtainedMarks(BigDecimal.valueOf(createDTO.getObtainedMarks()))
                .gradedBy(gradedBy)
                .feedback(createDTO.getFeedback())
                .isFinalized(createDTO.getIsFinalized() != null ? createDTO.getIsFinalized() : false)
                .build();

        marks = marksRepository.save(marks);
        log.info("Created marks: assessment={}, student={}, obtained={}", assessment.getTitle(), enrollment.getStudent().getStudentId(), marks.getObtainedMarks());

        return dtoMapper.toMarksDTO(marks);
    }

    @Override
    public List<MarksDTO> createBulk(MarksBulkCreateDTO createDTO) {
        Assessment assessment = assessmentRepository.findById(createDTO.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + createDTO.getAssessmentId()));

        User gradedBy = userRepository.findById(createDTO.getGradedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createDTO.getGradedById()));

        List<Long> enrollmentIds = createDTO.getRecords().stream()
                .map(MarksBulkCreateDTO.BulkRecord::getEnrollmentId)
                .toList();

        List<Enrollment> enrollments = enrollmentRepository.findAllById(enrollmentIds);

        return createDTO.getRecords().stream()
                .map(bulkRecord -> {
                    Enrollment enrollment = enrollments.stream()
                            .filter(e -> e.getId().equals(bulkRecord.getEnrollmentId()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + bulkRecord.getEnrollmentId()));

                    if (!enrollment.getCourse().getId().equals(assessment.getCourse().getId())) {
                        throw new BusinessRuleViolationException("Enrollment does not belong to the course of this assessment");
                    }

                    if (bulkRecord.getObtainedMarks() < 0 || bulkRecord.getObtainedMarks() > assessment.getMaxMarksDouble()) {
                        throw new BusinessRuleViolationException("Obtained marks must be between 0 and max marks (" + assessment.getMaxMarksDouble() + ")");
                    }

                    Marks marks;
                    if (marksRepository.existsByAssessmentIdAndEnrollmentId(assessment.getId(), enrollment.getId())) {
                        marks = marksRepository.findByAssessmentIdAndEnrollmentId(assessment.getId(), enrollment.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Marks not found"));
                        marks.setObtainedMarks(BigDecimal.valueOf(bulkRecord.getObtainedMarks()));
                        marks.setFeedback(bulkRecord.getFeedback());
                        marks.setIsFinalized(bulkRecord.getIsFinalized() != null ? bulkRecord.getIsFinalized() : false);
                    } else {
                        marks = Marks.builder()
                                .assessment(assessment)
                                .enrollment(enrollment)
                                .obtainedMarks(BigDecimal.valueOf(bulkRecord.getObtainedMarks()))
                                .gradedBy(gradedBy)
                                .feedback(bulkRecord.getFeedback())
                                .isFinalized(bulkRecord.getIsFinalized() != null ? bulkRecord.getIsFinalized() : false)
                                .build();
                    }

                    marks = marksRepository.save(marks);
                    return dtoMapper.toMarksDTO(marks);
                })
                .collect(Collectors.toList());
    }

    @Override
    public MarksDTO update(Long id, MarksUpdateDTO updateDTO) {
        Marks marks = marksRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marks not found with id: " + id));

        if (updateDTO.getObtainedMarks() != null) {
            Assessment assessment = marks.getAssessment();
            if (updateDTO.getObtainedMarks() < 0 || updateDTO.getObtainedMarks() > assessment.getMaxMarksDouble()) {
                throw new BusinessRuleViolationException("Obtained marks must be between 0 and max marks (" + assessment.getMaxMarksDouble() + ")");
            }
            marks.setObtainedMarks(BigDecimal.valueOf(updateDTO.getObtainedMarks()));
        }
        if (updateDTO.getFeedback() != null) {
            marks.setFeedback(updateDTO.getFeedback());
        }
        if (updateDTO.getIsFinalized() != null) {
            marks.setIsFinalized(updateDTO.getIsFinalized());
        }

        marks = marksRepository.save(marks);
        log.info("Updated marks: id={}", marks.getId());

        return dtoMapper.toMarksDTO(marks);
    }

    @Override
    public void finalizeMarks(Long id) {
        Marks marks = marksRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marks not found with id: " + id));
        marks.setIsFinalized(true);
        marksRepository.save(marks);
        log.info("Finalized marks: id={}", id);
    }

    @Override
    public void unfinalizeMarks(Long id) {
        Marks marks = marksRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marks not found with id: " + id));
        marks.setIsFinalized(false);
        marksRepository.save(marks);
        log.info("Unfinalized marks: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResultDTO getCourseResult(Long studentId, Long courseId) {
        List<Marks> finalizedMarks = marksRepository.findFinalizedByCourseIdAndStudentId(courseId, studentId);

        if (finalizedMarks.isEmpty()) {
            return CourseResultDTO.builder()
                    .courseId(courseId)
                    .build();
        }

        double totalWeightedScore = finalizedMarks.stream()
                .mapToDouble(Marks::getWeightedScore)
                .sum();

        BigDecimal totalWeightage = finalizedMarks.stream()
                .map(m -> m.getAssessment().getWeightage())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double percentage = totalWeightage.compareTo(BigDecimal.ZERO) > 0
                ? totalWeightedScore / totalWeightage.doubleValue() * 100.0
                : 0.0;

        // Get grading policy for course
        // For now, use default grading
        String grade = calculateGrade(percentage);

        return CourseResultDTO.builder()
                .courseId(courseId)
                .percentage(Math.round(percentage * 100.0) / 100.0)
                .grade(grade)
                .gradePoints(calculateGradePoints(grade))
                .build();
    }

    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 85) return "A";
        if (percentage >= 80) return "A-";
        if (percentage >= 75) return "B+";
        if (percentage >= 70) return "B";
        if (percentage >= 65) return "B-";
        if (percentage >= 60) return "C+";
        if (percentage >= 55) return "C";
        if (percentage >= 50) return "C-";
        if (percentage >= 40) return "D";
        return "F";
    }

    private double calculateGradePoints(String grade) {
        return switch (grade) {
            case "A+" -> 10.0;
            case "A" -> 9.5;
            case "A-" -> 9.0;
            case "B+" -> 8.5;
            case "B" -> 8.0;
            case "B-" -> 7.5;
            case "C+" -> 7.0;
            case "C" -> 6.5;
            case "C-" -> 6.0;
            case "D" -> 5.0;
            default -> 0.0;
        };
    }
}