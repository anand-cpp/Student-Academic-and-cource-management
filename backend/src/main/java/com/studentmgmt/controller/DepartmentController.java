package com.studentmgmt.controller;

import com.studentmgmt.dto.DepartmentDTO;
import com.studentmgmt.service.DepartmentService;
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
@RequestMapping("/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<Page<DepartmentDTO>> getAllDepartments(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<DepartmentDTO> departments = departmentService.search(search, pageable);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        return departmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<DepartmentDTO> getDepartmentByCode(@PathVariable String code) {
        return departmentService.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<List<DepartmentDTO>> getActiveDepartments() {
        return ResponseEntity.ok(departmentService.findActive());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody com.studentmgmt.dto.DepartmentCreateDTO createDTO) {
        DepartmentDTO department = departmentService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(department);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @Valid @RequestBody com.studentmgmt.dto.DepartmentUpdateDTO updateDTO) {
        DepartmentDTO department = departmentService.update(id, updateDTO);
        return ResponseEntity.ok(department);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}