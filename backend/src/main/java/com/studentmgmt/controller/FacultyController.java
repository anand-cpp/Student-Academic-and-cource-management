package com.studentmgmt.controller;

import com.studentmgmt.dto.FacultyDTO;
import com.studentmgmt.model.FacultyStatus;
import com.studentmgmt.service.FacultyService;
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
@RequestMapping("/faculty")
@RequiredArgsConstructor
@Slf4j
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<Page<FacultyDTO>> getAllFaculty(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) FacultyStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<FacultyDTO> faculty = facultyService.search(departmentId, status, search, pageable);
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<FacultyDTO> getFacultyById(@PathVariable Long id) {
        return facultyService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/faculty-id/{facultyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<FacultyDTO> getFacultyByFacultyId(@PathVariable String facultyId) {
        return facultyService.findByFacultyId(facultyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR')")
    public ResponseEntity<FacultyDTO> createFaculty(@Valid @RequestBody com.studentmgmt.dto.FacultyCreateDTO createDTO) {
        FacultyDTO faculty = facultyService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(faculty);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR')")
    public ResponseEntity<FacultyDTO> updateFaculty(@PathVariable Long id, @Valid @RequestBody com.studentmgmt.dto.FacultyUpdateDTO updateDTO) {
        FacultyDTO faculty = facultyService.update(id, updateDTO);
        return ResponseEntity.ok(faculty);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTRAR', 'DEPARTMENT_HEAD')")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam FacultyStatus status) {
        facultyService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<List<FacultyDTO>> getByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(facultyService.findByDepartmentId(departmentId));
    }
}