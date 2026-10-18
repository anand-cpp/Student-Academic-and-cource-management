package com.studentmgmt.controller;

import com.studentmgmt.dto.AttendanceRecordDTO;
import com.studentmgmt.dto.AttendanceSessionDTO;
import com.studentmgmt.model.AttendanceStatus;
import com.studentmgmt.service.AttendanceService;
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
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    // Session endpoints
    @GetMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<Page<AttendanceSessionDTO>> getAllSessions(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<AttendanceSessionDTO> sessions = attendanceService.searchSessions(courseId, startDate, endDate, pageable);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<AttendanceSessionDTO> getSessionById(@PathVariable Long id) {
        return attendanceService.findSessionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<AttendanceSessionDTO> createSession(@Valid @RequestBody com.studentmgmt.dto.AttendanceSessionCreateDTO createDTO) {
        AttendanceSessionDTO session = attendanceService.createSession(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @PutMapping("/sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<AttendanceSessionDTO> updateSession(@PathVariable Long id, @Valid @RequestBody com.studentmgmt.dto.AttendanceSessionUpdateDTO updateDTO) {
        AttendanceSessionDTO session = attendanceService.updateSession(id, updateDTO);
        return ResponseEntity.ok(session);
    }

    @DeleteMapping("/sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        attendanceService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    // Attendance record endpoints
    @GetMapping("/records")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<Page<AttendanceRecordDTO>> getAllRecords(
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Long enrollmentId,
            @RequestParam(required = false) AttendanceStatus status,
            @PageableDefault(size = 50) Pageable pageable) {

        Page<AttendanceRecordDTO> records = attendanceService.searchRecords(sessionId, enrollmentId, status, pageable);
        return ResponseEntity.ok(records);
    }

    @PostMapping("/records")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<AttendanceRecordDTO> createRecord(@Valid @RequestBody com.studentmgmt.dto.AttendanceRecordCreateDTO createDTO) {
        AttendanceRecordDTO record = attendanceService.createRecord(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @PostMapping("/records/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<AttendanceRecordDTO>> createBulkRecords(@Valid @RequestBody com.studentmgmt.dto.AttendanceBulkCreateDTO createDTO) {
        List<AttendanceRecordDTO> records = attendanceService.createBulkRecords(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(records);
    }

    @PutMapping("/records/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<AttendanceRecordDTO> updateRecord(@PathVariable Long id, @Valid @RequestBody com.studentmgmt.dto.AttendanceRecordUpdateDTO updateDTO) {
        AttendanceRecordDTO record = attendanceService.updateRecord(id, updateDTO);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/student/{studentId}/course/{courseId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT')")
    public ResponseEntity<com.studentmgmt.dto.AttendanceSummaryDTO> getStudentAttendanceSummary(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendanceSummary(studentId, courseId));
    }

    @GetMapping("/course/{courseId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR')")
    public ResponseEntity<com.studentmgmt.dto.CourseAttendanceSummaryDTO> getCourseAttendanceSummary(@PathVariable Long courseId) {
        return ResponseEntity.ok(attendanceService.getCourseAttendanceSummary(courseId));
    }
}