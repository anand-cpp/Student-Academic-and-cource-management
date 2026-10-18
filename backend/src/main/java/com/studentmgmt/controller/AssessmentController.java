package com.studentmgmt.controller;

import com.studentmgmt.dto.AssessmentDTO;
import com.studentmgmt.model.AssessmentType;
import com.studentmgmt.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/assessments")
@RequiredArgsConstructor
@Slf4j
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<Page<AssessmentDTO>> getAllAssessments(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) AssessmentType type,
            @RequestParam(required = false) Boolean isPublished,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<AssessmentDTO> assessments = assessmentService.search(courseId, type, isPublished, startDate, endDate, pageable);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<AssessmentDTO> getAssessmentById(@PathVariable Long id) {
        return assessmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<List<AssessmentDTO>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(assessmentService.findByCourseId(courseId));
    }

    @GetMapping("/course/{courseId}/published")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<List<AssessmentDTO>> getPublishedByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(assessmentService.findPublishedByCourseId(courseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<AssessmentDTO> createAssessment(@Valid @RequestBody com.studentmgmt.dto.AssessmentCreateDTO createDTO) {
        AssessmentDTO assessment = assessmentService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(assessment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<AssessmentDTO> updateAssessment(@PathVariable Long id, @Valid @RequestBody com.studentmgmt.dto.AssessmentUpdateDTO updateDTO) {
        AssessmentDTO assessment = assessmentService.update(id, updateDTO);
        return ResponseEntity.ok(assessment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        assessmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Void> publishAssessment(@PathVariable Long id) {
        assessmentService.publish(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/unpublish")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Void> unpublishAssessment(@PathVariable Long id) {
        assessmentService.unpublish(id);
        return ResponseEntity.ok().build();
    }
}