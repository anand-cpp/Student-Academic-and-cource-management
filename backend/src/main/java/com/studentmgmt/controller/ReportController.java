package com.studentmgmt.controller;

import com.studentmgmt.dto.StudentAcademicProfileDTO;
import com.studentmgmt.dto.CourseEnrollmentReportDTO;
import com.studentmgmt.dto.FacultyCourseAssignmentReportDTO;
import com.studentmgmt.dto.AttendanceReportDTO;
import com.studentmgmt.dto.CourseResultsReportDTO;
import com.studentmgmt.dto.StudentPerformanceSummaryDTO;
import com.studentmgmt.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/student-academic-profile/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<StudentAcademicProfileDTO> getStudentAcademicProfile(@PathVariable Long studentId) {
        return ResponseEntity.ok(reportService.getStudentAcademicProfile(studentId));
    }

    @GetMapping("/course-enrollment/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<CourseEnrollmentReportDTO> getCourseEnrollmentReport(@PathVariable Long courseId) {
        return ResponseEntity.ok(reportService.getCourseEnrollmentReport(courseId));
    }

    @GetMapping("/faculty-course-assignment/{facultyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<FacultyCourseAssignmentReportDTO> getFacultyCourseAssignmentReport(@PathVariable Long facultyId) {
        return ResponseEntity.ok(reportService.getFacultyCourseAssignmentReport(facultyId));
    }

    @GetMapping("/attendance/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<AttendanceReportDTO> getAttendanceReport(@PathVariable Long courseId) {
        return ResponseEntity.ok(reportService.getAttendanceReport(courseId));
    }

    @GetMapping("/attendance/student/{studentId}/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<AttendanceReportDTO> getStudentAttendanceReport(@PathVariable Long studentId, @PathVariable Long courseId) {
        return ResponseEntity.ok(reportService.getStudentAttendanceReport(studentId, courseId));
    }

    @GetMapping("/course-results/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<CourseResultsReportDTO> getCourseResultsReport(@PathVariable Long courseId) {
        return ResponseEntity.ok(reportService.getCourseResultsReport(courseId));
    }

    @GetMapping("/student-performance/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<StudentPerformanceSummaryDTO> getStudentPerformanceSummary(@PathVariable Long studentId) {
        return ResponseEntity.ok(reportService.getStudentPerformanceSummary(studentId));
    }

    @GetMapping("/department/{departmentId}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<List<StudentPerformanceSummaryDTO>> getDepartmentStudentPerformance(@PathVariable Long departmentId) {
        return ResponseEntity.ok(reportService.getDepartmentStudentPerformance(departmentId));
    }
}