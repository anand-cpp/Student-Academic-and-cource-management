package com.studentmgmt.service;

import com.studentmgmt.dto.AttendanceRecordDTO;
import com.studentmgmt.dto.AttendanceSessionDTO;
import com.studentmgmt.dto.AttendanceSummaryDTO;
import com.studentmgmt.dto.CourseAttendanceSummaryDTO;
import com.studentmgmt.dto.AttendanceSessionCreateDTO;
import com.studentmgmt.dto.AttendanceSessionUpdateDTO;
import com.studentmgmt.dto.AttendanceRecordCreateDTO;
import com.studentmgmt.dto.AttendanceRecordUpdateDTO;
import com.studentmgmt.dto.AttendanceBulkCreateDTO;
import com.studentmgmt.model.AttendanceRecord;
import com.studentmgmt.model.AttendanceSession;
import com.studentmgmt.model.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {

    Optional<AttendanceSessionDTO> findSessionById(Long id);

    Page<AttendanceSessionDTO> searchSessions(Long courseId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    AttendanceSessionDTO createSession(AttendanceSessionCreateDTO createDTO);

    AttendanceSessionDTO updateSession(Long id, AttendanceSessionUpdateDTO updateDTO);

    void deleteSession(Long id);

    Page<AttendanceRecordDTO> searchRecords(Long sessionId, Long enrollmentId, AttendanceStatus status, Pageable pageable);

    AttendanceRecordDTO createRecord(AttendanceRecordCreateDTO createDTO);

    List<AttendanceRecordDTO> createBulkRecords(AttendanceBulkCreateDTO createDTO);

    AttendanceRecordDTO updateRecord(Long id, AttendanceRecordUpdateDTO updateDTO);

    AttendanceSummaryDTO getStudentAttendanceSummary(Long studentId, Long courseId);

    CourseAttendanceSummaryDTO getCourseAttendanceSummary(Long courseId);
}