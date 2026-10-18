package com.studentmgmt.controller;

import com.studentmgmt.dto.EnrollmentDTO;
import com.studentmgmt.model.EnrollmentStatus;
import com.studentmgmt.service.EnrollmentService;
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
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@Slf4j
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<Page<EnrollmentDTO>> getAllEnrollments(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) EnrollmentStatus status,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<EnrollmentDTO> enrollments = enrollmentService.search(studentId, courseId, status, pageable);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<EnrollmentDTO> getEnrollmentById(@PathVariable Long id) {
        return enrollmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<EnrollmentDTO> createEnrollment(@Valid @RequestBody com.studentmgmt.dto.EnrollmentCreateDTO createDTO) {
        EnrollmentDTO enrollment = enrollmentService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR')")
    public ResponseEntity<EnrollmentDTO> updateEnrollment(@PathVariable Long id, @Valid @RequestBody com.studentmgmt.dto.EnrollmentUpdateDTO updateDTO) {
        EnrollmentDTO enrollment = enrollmentService.update(id, updateDTO);
        return ResponseEntity.ok(enrollment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR', 'FACULTY')")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam EnrollmentStatus status) {
        enrollmentService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/student/{studentId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<List<EnrollmentDTO>> getActiveByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.findActiveByStudentId(studentId));
    }

    @GetMapping("/course/{courseId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<List<EnrollmentDTO>> getActiveByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.findActiveByCourseId(courseId));
    }
}