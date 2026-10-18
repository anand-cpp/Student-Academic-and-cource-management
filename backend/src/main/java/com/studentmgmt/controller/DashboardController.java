package com.studentmgmt.controller;

import com.studentmgmt.dto.DashboardStatsDTO;
import com.studentmgmt.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/stats/faculty")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<DashboardStatsDTO> getFacultyDashboardStats() {
        return ResponseEntity.ok(dashboardService.getFacultyDashboardStats());
    }

    @GetMapping("/stats/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<DashboardStatsDTO> getStudentDashboardStats() {
        return ResponseEntity.ok(dashboardService.getStudentDashboardStats());
    }
}