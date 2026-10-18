package com.studentmgmt.controller;

import com.studentmgmt.dto.GradingPolicyDTO;
import com.studentmgmt.service.GradingPolicyService;
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
@RequestMapping("/grading-policies")
@RequiredArgsConstructor
@Slf4j
public class GradingPolicyController {

    private final GradingPolicyService gradingPolicyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<List<GradingPolicyDTO>> getAllGradingPolicies() {
        return ResponseEntity.ok(gradingPolicyService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<GradingPolicyDTO> getGradingPolicyById(@PathVariable Long id) {
        return gradingPolicyService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/default")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<GradingPolicyDTO> getDefaultGradingPolicy() {
        return gradingPolicyService.findDefault()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GradingPolicyDTO> createGradingPolicy(@Valid @RequestBody com.studentmgmt.dto.GradingPolicyCreateDTO createDTO) {
        GradingPolicyDTO policy = gradingPolicyService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(policy);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GradingPolicyDTO> updateGradingPolicy(@PathVariable Long id, @Valid @RequestBody com.studentmgmt.dto.GradingPolicyUpdateDTO updateDTO) {
        GradingPolicyDTO policy = gradingPolicyService.update(id, updateDTO);
        return ResponseEntity.ok(policy);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGradingPolicy(@PathVariable Long id) {
        gradingPolicyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/set-default")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setAsDefault(@PathVariable Long id) {
        gradingPolicyService.setAsDefault(id);
        return ResponseEntity.ok().build();
    }
}