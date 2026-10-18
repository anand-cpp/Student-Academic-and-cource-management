package com.studentmgmt.controller;

import com.studentmgmt.dto.MarksDTO;
import com.studentmgmt.service.MarksService;
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
import java.util.List;

@RestController
@RequestMapping("/marks")
@RequiredArgsConstructor
@Slf4j
public class MarksController {

    private final MarksService marksService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<Page<MarksDTO>> getAllMarks(
            @RequestParam(required = false) Long assessmentId,
            @RequestParam(required = false) Long enrollmentId,
            @RequestParam(required = false) Boolean isFinalized,
            @PageableDefault(size = 50) Pageable pageable) {

        Page<MarksDTO> marks = marksService.search(assessmentId, enrollmentId, isFinalized, pageable);
        return ResponseEntity.ok(marks);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<MarksDTO> getMarksById(@PathVariable Long id) {
        return marksService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/assessment/{assessmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<List<MarksDTO>> getByAssessment(@PathVariable Long assessmentId) {
        return ResponseEntity.ok(marksService.findByAssessmentId(assessmentId));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<List<MarksDTO>> getByEnrollment(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(marksService.findByEnrollmentId(enrollmentId));
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<List<MarksDTO>> getByStudentAndCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        return ResponseEntity.ok(marksService.findByStudentAndCourse(studentId, courseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<MarksDTO> createMarks(@Valid @RequestBody com.studentmgmt.dto.MarksCreateDTO createDTO) {
        MarksDTO marks = marksService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(marks);
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<MarksDTO>> createBulkMarks(@Valid @RequestBody com.studentmgmt.dto.MarksBulkCreateDTO createDTO) {
        List<MarksDTO> marks = marksService.createBulk(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(marks);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<MarksDTO> updateMarks(@PathVariable Long id, @Valid @RequestBody com.studentmgmt.dto.MarksUpdateDTO updateDTO) {
        MarksDTO marks = marksService.update(id, updateDTO);
        return ResponseEntity.ok(marks);
    }

    @PatchMapping("/{id}/finalize")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Void> finalizeMarks(@PathVariable Long id) {
        marksService.finalizeMarks(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/unfinalize")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Void> unfinalizeMarks(@PathVariable Long id) {
        marksService.unfinalizeMarks(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/student/{studentId}/course/{courseId}/result")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<com.studentmgmt.dto.CourseResultDTO> getCourseResult(@PathVariable Long studentId, @PathVariable Long courseId) {
        return ResponseEntity.ok(marksService.getCourseResult(studentId, courseId));
    }
}