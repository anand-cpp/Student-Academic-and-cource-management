package com.studentmgmt.controller;

import com.studentmgmt.dto.CourseDTO;
import com.studentmgmt.model.CourseStatus;
import com.studentmgmt.service.CourseService;
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
@RequestMapping("/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<Page<CourseDTO>> getAllCourses(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) CourseStatus status,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<CourseDTO> courses = courseService.search(departmentId, semester, facultyId, status, academicYear, search, pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{courseCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<CourseDTO> getCourseByCode(@PathVariable String courseCode) {
        return courseService.findByCourseCode(courseCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR', 'DEPARTMENT_HEAD')")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody com.studentmgmt.dto.CourseCreateDTO createDTO) {
        CourseDTO course = courseService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR', 'DEPARTMENT_HEAD')")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @Valid @RequestBody com.studentmgmt.dto.CourseUpdateDTO updateDTO) {
        CourseDTO course = courseService.update(id, updateDTO);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR', 'DEPARTMENT_HEAD')")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam CourseStatus status) {
        courseService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/faculty/{facultyId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD')")
    public ResponseEntity<List<CourseDTO>> getActiveCoursesByFaculty(@PathVariable Long facultyId) {
        return ResponseEntity.ok(courseService.findActiveByFacultyId(facultyId));
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<List<CourseDTO>> getByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(courseService.findByDepartmentId(departmentId));
    }
}