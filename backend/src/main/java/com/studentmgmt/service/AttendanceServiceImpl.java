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
import com.studentmgmt.exception.BusinessRuleViolationException;
import com.studentmgmt.exception.ResourceNotFoundException;
import com.studentmgmt.model.AttendanceRecord;
import com.studentmgmt.model.AttendanceSession;
import com.studentmgmt.model.AttendanceStatus;
import com.studentmgmt.model.Course;
import com.studentmgmt.model.Enrollment;
import com.studentmgmt.model.User;
import com.studentmgmt.repository.AttendanceRecordRepository;
import com.studentmgmt.repository.AttendanceSessionRepository;
import com.studentmgmt.repository.CourseRepository;
import com.studentmgmt.repository.EnrollmentRepository;
import com.studentmgmt.repository.UserRepository;
import com.studentmgmt.util.DTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceSessionRepository sessionRepository;
    private final AttendanceRecordRepository recordRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final DTOMapper dtoMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<AttendanceSessionDTO> findSessionById(Long id) {
        return sessionRepository.findById(id)
                .map(dtoMapper::toAttendanceSessionDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceSessionDTO> searchSessions(Long courseId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (courseId != null && startDate != null && endDate != null) {
            return sessionRepository.findByCourseIdAndSessionDateBetween(courseId, startDate, endDate, pageable)
                    .map(dtoMapper::toAttendanceSessionDTO);
        } else if (courseId != null) {
            return sessionRepository.findByCourseId(courseId, pageable)
                    .map(dtoMapper::toAttendanceSessionDTO);
        }
        return Page.empty(pageable);
    }

    @Override
    public AttendanceSessionDTO createSession(AttendanceSessionCreateDTO createDTO) {
        Course course = courseRepository.findById(createDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + createDTO.getCourseId()));

        User createdBy = userRepository.findById(createDTO.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createDTO.getCreatedById()));

        if (sessionRepository.existsByCourseIdAndSessionDateAndSessionNumber(
                course.getId(), createDTO.getSessionDate(), createDTO.getSessionNumber())) {
            throw new BusinessRuleViolationException("Session already exists for this course on this date with this number");
        }

        AttendanceSession session = AttendanceSession.builder()
                .course(course)
                .sessionDate(createDTO.getSessionDate())
                .sessionNumber(createDTO.getSessionNumber())
                .topic(createDTO.getTopic())
                .durationMinutes(createDTO.getDurationMinutes() != null ? createDTO.getDurationMinutes() : 60)
                .isCancelled(false)
                .createdBy(createdBy)
                .build();

        session = sessionRepository.save(session);
        log.info("Created attendance session: course={}, date={}, number={}", course.getCourseCode(), session.getSessionDate(), session.getSessionNumber());

        return dtoMapper.toAttendanceSessionDTO(session);
    }

    @Override
    public AttendanceSessionDTO updateSession(Long id, AttendanceSessionUpdateDTO updateDTO) {
        AttendanceSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance session not found with id: " + id));

        if (updateDTO.getTopic() != null) {
            session.setTopic(updateDTO.getTopic());
        }
        if (updateDTO.getDurationMinutes() != null) {
            session.setDurationMinutes(updateDTO.getDurationMinutes());
        }
        if (updateDTO.getIsCancelled() != null) {
            session.setIsCancelled(updateDTO.getIsCancelled());
            if (updateDTO.getIsCancelled() && updateDTO.getCancellationReason() != null) {
                session.setCancellationReason(updateDTO.getCancellationReason());
            }
        }

        session = sessionRepository.save(session);
        log.info("Updated attendance session: id={}", session.getId());

        return dtoMapper.toAttendanceSessionDTO(session);
    }

    @Override
    public void deleteSession(Long id) {
        AttendanceSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance session not found with id: " + id));
        sessionRepository.delete(session);
        log.info("Deleted attendance session: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceRecordDTO> searchRecords(Long sessionId, Long enrollmentId, AttendanceStatus status, Pageable pageable) {
        if (sessionId != null) {
            return recordRepository.findBySessionId(sessionId, pageable)
                    .map(dtoMapper::toAttendanceRecordDTO);
        } else if (enrollmentId != null) {
            return recordRepository.findByEnrollmentId(enrollmentId, pageable)
                    .map(dtoMapper::toAttendanceRecordDTO);
        }
        return Page.empty(pageable);
    }

    @Override
    public AttendanceRecordDTO createRecord(AttendanceRecordCreateDTO createDTO) {
        AttendanceSession session = sessionRepository.findById(createDTO.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + createDTO.getSessionId()));

        Enrollment enrollment = enrollmentRepository.findById(createDTO.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + createDTO.getEnrollmentId()));

        User recordedBy = userRepository.findById(createDTO.getRecordedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createDTO.getRecordedById()));

        if (recordRepository.existsBySessionIdAndEnrollmentId(session.getId(), enrollment.getId())) {
            throw new BusinessRuleViolationException("Attendance record already exists for this session and enrollment");
        }

        // Validate enrollment belongs to the course of the session
        if (!enrollment.getCourse().getId().equals(session.getCourse().getId())) {
            throw new BusinessRuleViolationException("Enrollment does not belong to the course of this session");
        }

        AttendanceRecord record = AttendanceRecord.builder()
                .session(session)
                .enrollment(enrollment)
                .status(AttendanceStatus.valueOf(createDTO.getStatus()))
                .remarks(createDTO.getRemarks())
                .recordedBy(recordedBy)
                .build();

        record = recordRepository.save(record);
        log.info("Created attendance record: session={}, student={}, status={}", session.getId(), enrollment.getStudent().getStudentId(), record.getStatus());

        return dtoMapper.toAttendanceRecordDTO(record);
    }

    @Override
    public List<AttendanceRecordDTO> createBulkRecords(AttendanceBulkCreateDTO createDTO) {
        AttendanceSession session = sessionRepository.findById(createDTO.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + createDTO.getSessionId()));

        User recordedBy = userRepository.findById(createDTO.getRecordedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createDTO.getRecordedById()));

        List<Long> enrollmentIds = createDTO.getRecords().stream()
                .map(AttendanceBulkCreateDTO.BulkRecord::getEnrollmentId)
                .toList();

        List<Enrollment> enrollments = enrollmentRepository.findAllById(enrollmentIds);

        return createDTO.getRecords().stream()
                .map(bulkRecord -> {
                    Enrollment enrollment = enrollments.stream()
                            .filter(e -> e.getId().equals(bulkRecord.getEnrollmentId()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + bulkRecord.getEnrollmentId()));

                    if (!enrollment.getCourse().getId().equals(session.getCourse().getId())) {
                        throw new BusinessRuleViolationException("Enrollment does not belong to the course of this session");
                    }

                    AttendanceRecord record;
                    if (recordRepository.existsBySessionIdAndEnrollmentId(session.getId(), enrollment.getId())) {
                        record = recordRepository.findBySessionIdAndEnrollmentId(session.getId(), enrollment.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));
                        record.setStatus(AttendanceStatus.valueOf(bulkRecord.getStatus()));
                        record.setRemarks(bulkRecord.getRemarks());
                    } else {
                        record = AttendanceRecord.builder()
                                .session(session)
                                .enrollment(enrollment)
                                .status(AttendanceStatus.valueOf(bulkRecord.getStatus()))
                                .remarks(bulkRecord.getRemarks())
                                .recordedBy(recordedBy)
                                .build();
                    }

                    record = recordRepository.save(record);
                    return dtoMapper.toAttendanceRecordDTO(record);
                })
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceRecordDTO updateRecord(Long id, AttendanceRecordUpdateDTO updateDTO) {
        AttendanceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));

        if (updateDTO.getStatus() != null) {
            record.setStatus(AttendanceStatus.valueOf(updateDTO.getStatus()));
        }
        if (updateDTO.getRemarks() != null) {
            record.setRemarks(updateDTO.getRemarks());
        }

        record = recordRepository.save(record);
        log.info("Updated attendance record: id={}", record.getId());

        return dtoMapper.toAttendanceRecordDTO(record);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryDTO getStudentAttendanceSummary(Long studentId, Long courseId) {
        // Get student's enrollment for this course
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not enrolled in this course"));

        List<AttendanceRecord> records = recordRepository.findByEnrollmentIdOrderBySessionDate(enrollment.getId());

        long totalSessions = records.size();
        long presentCount = records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
        long lateCount = records.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE).count();
        long absentCount = records.stream().filter(r -> r.getStatus() == AttendanceStatus.ABSENT).count();
        long excusedCount = records.stream().filter(r -> r.getStatus() == AttendanceStatus.EXCUSED).count();
        long medicalCount = records.stream().filter(r -> r.getStatus() == AttendanceStatus.MEDICAL).count();

        long eligibleSessions = totalSessions - excusedCount - medicalCount;
        double percentage = eligibleSessions > 0 ? ((double) (presentCount + lateCount) / eligibleSessions) * 100.0 : 0.0;

        return AttendanceSummaryDTO.builder()
                .totalSessions(totalSessions)
                .totalPresent(presentCount)
                .totalAbsent(absentCount)
                .totalLate(lateCount)
                .totalExcused(excusedCount + medicalCount)
                .overallPercentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseAttendanceSummaryDTO getCourseAttendanceSummary(Long courseId) {
        List<AttendanceSession> sessions = sessionRepository.findActiveSessionsByCourseId(courseId);
        List<Enrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByCourseId(courseId);

        return CourseAttendanceSummaryDTO.builder()
                .courseId(courseId)
                .totalSessions((long) sessions.size())
                .totalStudents((long) enrollments.size())
                .build();
    }
}