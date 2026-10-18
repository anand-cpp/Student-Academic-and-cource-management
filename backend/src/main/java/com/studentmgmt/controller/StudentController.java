package com.studentmgmt.controller;

import com.studentmgmt.dto.StudentCreateDTO;
import com.studentmgmt.dto.StudentDTO;
import com.studentmgmt.dto.StudentDetailDTO;
import com.studentmgmt.dto.StudentUpdateDTO;
import com.studentmgmt.model.StudentStatus;
import com.studentmgmt.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<Page<StudentDTO>> getAllStudents(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) StudentStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<StudentDTO> students = studentService.search(departmentId, status, search, pageable);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        return studentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student-id/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<StudentDTO> getStudentByStudentId(@PathVariable String studentId) {
        return studentService.findByStudentId(studentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/detail")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<StudentDetailDTO> getStudentDetail(@PathVariable Long id) {
        // This would need a separate service method to fetch detail with enrollments, attendance, academic summary
        return ResponseEntity.ok().build(); // Placeholder
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR')")
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentCreateDTO createDTO) {
        StudentDTO student = studentService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR')")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentUpdateDTO updateDTO) {
        StudentDTO student = studentService.update(id, updateDTO);
        return ResponseEntity.ok(student);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR')")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam StudentStatus status) {
        studentService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT_HEAD')")
    public ResponseEntity<Long> countByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(studentService.countByDepartmentId(departmentId));
    }

    @GetMapping("/count/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR')")
    public ResponseEntity<Long> countByStatus(@PathVariable StudentStatus status) {
        return ResponseEntity.ok(studentService.countByStatus(status));
    }
}