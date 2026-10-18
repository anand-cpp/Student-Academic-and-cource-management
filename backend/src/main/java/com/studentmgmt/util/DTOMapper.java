package com.studentmgmt.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentmgmt.dto.*;
import com.studentmgmt.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DTOMapper {

    private final ObjectMapper objectMapper;

    // Student mappings
    public StudentDTO toStudentDTO(Student student) {
        if (student == null) return null;

        StudentDTO dto = StudentDTO.builder()
                .id(student.getId())
                .studentId(student.getStudentId())
                .userId(student.getUser() != null ? student.getUser().getId() : null)
                .username(student.getUser() != null ? student.getUser().getUsername() : null)
                .email(student.getUser() != null ? student.getUser().getEmail() : null)
                .fullName(student.getUser() != null ? student.getUser().getFullName() : null)
                .phone(student.getUser() != null ? student.getUser().getPhone() : null)
                .avatarUrl(student.getUser() != null ? student.getUser().getAvatarUrl() : null)
                .departmentId(student.getDepartment() != null ? student.getDepartment().getId() : null)
                .departmentCode(student.getDepartment() != null ? student.getDepartment().getCode() : null)
                .departmentName(student.getDepartment() != null ? student.getDepartment().getName() : null)
                .admissionYear(student.getAdmissionYear())
                .currentSemester(student.getCurrentSemester())
                .status(student.getStatus())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender() != null ? student.getGender().name() : null)
                .address(student.getAddress())
                .emergencyContactName(student.getEmergencyContactName())
                .emergencyContactPhone(student.getEmergencyContactPhone())
                .bloodGroup(student.getBloodGroup())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();

        return dto;
    }

    public StudentDetailDTO toStudentDetailDTO(Student student, List<EnrollmentDTO> enrollments,
                                               AttendanceSummaryDTO attendanceSummary,
                                               AcademicSummaryDTO academicSummary) {
        StudentDTO base = toStudentDTO(student);
        return StudentDetailDTO.builder()
                .id(base.getId())
                .studentId(base.getStudentId())
                .userId(base.getUserId())
                .username(base.getUsername())
                .email(base.getEmail())
                .fullName(base.getFullName())
                .phone(base.getPhone())
                .avatarUrl(base.getAvatarUrl())
                .departmentId(base.getDepartmentId())
                .departmentCode(base.getDepartmentCode())
                .departmentName(base.getDepartmentName())
                .admissionYear(base.getAdmissionYear())
                .currentSemester(base.getCurrentSemester())
                .status(base.getStatus())
                .dateOfBirth(base.getDateOfBirth())
                .gender(base.getGender())
                .address(base.getAddress())
                .emergencyContactName(base.getEmergencyContactName())
                .emergencyContactPhone(base.getEmergencyContactPhone())
                .bloodGroup(base.getBloodGroup())
                .createdAt(base.getCreatedAt())
                .updatedAt(base.getUpdatedAt())
                .enrollments(enrollments)
                .attendanceSummary(attendanceSummary)
                .academicSummary(academicSummary)
                .build();
    }

    // Faculty mappings
    public FacultyDTO toFacultyDTO(Faculty faculty) {
        if (faculty == null) return null;

        return FacultyDTO.builder()
                .id(faculty.getId())
                .facultyId(faculty.getFacultyId())
                .userId(faculty.getUser() != null ? faculty.getUser().getId() : null)
                .username(faculty.getUser() != null ? faculty.getUser().getUsername() : null)
                .email(faculty.getUser() != null ? faculty.getUser().getEmail() : null)
                .fullName(faculty.getUser() != null ? faculty.getUser().getFullName() : null)
                .phone(faculty.getUser() != null ? faculty.getUser().getPhone() : null)
                .avatarUrl(faculty.getUser() != null ? faculty.getUser().getAvatarUrl() : null)
                .departmentId(faculty.getDepartment() != null ? faculty.getDepartment().getId() : null)
                .departmentCode(faculty.getDepartment() != null ? faculty.getDepartment().getCode() : null)
                .departmentName(faculty.getDepartment() != null ? faculty.getDepartment().getName() : null)
                .designation(faculty.getDesignation() != null ? faculty.getDesignation().name() : null)
                .specialization(faculty.getSpecialization())
                .qualification(faculty.getQualification())
                .hireDate(faculty.getHireDate())
                .status(faculty.getStatus())
                .officeLocation(faculty.getOfficeLocation())
                .researchInterests(faculty.getResearchInterests())
                .createdAt(faculty.getCreatedAt())
                .updatedAt(faculty.getUpdatedAt())
                .build();
    }

    // Course mappings
    public CourseDTO toCourseDTO(Course course) {
        if (course == null) return null;

        return CourseDTO.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .title(course.getTitle())
                .description(course.getDescription())
                .credits(course.getCredits())
                .departmentId(course.getDepartment() != null ? course.getDepartment().getId() : null)
                .departmentCode(course.getDepartment() != null ? course.getDepartment().getCode() : null)
                .departmentName(course.getDepartment() != null ? course.getDepartment().getName() : null)
                .semester(course.getSemester())
                .facultyId(course.getFaculty() != null ? course.getFaculty().getId() : null)
                .facultyName(course.getFaculty() != null ? course.getFaculty().getFullName() : null)
                .maxEnrollment(course.getMaxEnrollment())
                .prerequisites(course.getPrerequisites())
                .syllabusUrl(course.getSyllabusUrl())
                .status(course.getStatus())
                .academicYear(course.getAcademicYear())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    // Enrollment mappings
    public EnrollmentDTO toEnrollmentDTO(Enrollment enrollment) {
        if (enrollment == null) return null;

        return EnrollmentDTO.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent() != null ? enrollment.getStudent().getId() : null)
                .studentName(enrollment.getStudent() != null ? enrollment.getStudent().getFullName() : null)
                .studentCode(enrollment.getStudent() != null ? enrollment.getStudent().getStudentId() : null)
                .courseId(enrollment.getCourse() != null ? enrollment.getCourse().getId() : null)
                .courseCode(enrollment.getCourse() != null ? enrollment.getCourse().getCourseCode() : null)
                .courseTitle(enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null)
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .grade(enrollment.getGrade())
                .gradePoints(enrollment.getGradePoints() != null ? enrollment.getGradePoints().doubleValue() : null)
                .completionDate(enrollment.getCompletionDate())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }

    // Attendance mappings
    public AttendanceSessionDTO toAttendanceSessionDTO(AttendanceSession session) {
        if (session == null) return null;

        return AttendanceSessionDTO.builder()
                .id(session.getId())
                .courseId(session.getCourse() != null ? session.getCourse().getId() : null)
                .courseCode(session.getCourse() != null ? session.getCourse().getCourseCode() : null)
                .courseTitle(session.getCourse() != null ? session.getCourse().getTitle() : null)
                .sessionDate(session.getSessionDate())
                .sessionNumber(session.getSessionNumber())
                .topic(session.getTopic())
                .durationMinutes(session.getDurationMinutes())
                .isCancelled(session.getIsCancelled())
                .cancellationReason(session.getCancellationReason())
                .createdById(session.getCreatedBy() != null ? session.getCreatedBy().getId() : null)
                .createdByName(session.getCreatedBy() != null ? session.getCreatedBy().getFullName() : null)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    public AttendanceRecordDTO toAttendanceRecordDTO(AttendanceRecord record) {
        if (record == null) return null;

        return AttendanceRecordDTO.builder()
                .id(record.getId())
                .sessionId(record.getSession() != null ? record.getSession().getId() : null)
                .sessionDate(record.getSession() != null ? record.getSession().getSessionDate() : null)
                .sessionNumber(record.getSession() != null ? record.getSession().getSessionNumber() : null)
                .enrollmentId(record.getEnrollment() != null ? record.getEnrollment().getId() : null)
                .studentId(record.getEnrollment() != null && record.getEnrollment().getStudent() != null
                        ? record.getEnrollment().getStudent().getId() : null)
                .studentName(record.getEnrollment() != null && record.getEnrollment().getStudent() != null
                        ? record.getEnrollment().getStudent().getFullName() : null)
                .studentCode(record.getEnrollment() != null && record.getEnrollment().getStudent() != null
                        ? record.getEnrollment().getStudent().getStudentId() : null)
                .status(record.getStatus())
                .remarks(record.getRemarks())
                .recordedById(record.getRecordedBy() != null ? record.getRecordedBy().getId() : null)
                .recordedByName(record.getRecordedBy() != null ? record.getRecordedBy().getFullName() : null)
                .recordedAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    // Assessment mappings
    public AssessmentDTO toAssessmentDTO(Assessment assessment) {
        if (assessment == null) return null;

        return AssessmentDTO.builder()
                .id(assessment.getId())
                .courseId(assessment.getCourse() != null ? assessment.getCourse().getId() : null)
                .courseCode(assessment.getCourse() != null ? assessment.getCourse().getCourseCode() : null)
                .title(assessment.getTitle())
                .description(assessment.getDescription())
                .type(assessment.getType() != null ? assessment.getType().name() : null)
                .maxMarks(assessment.getMaxMarks() != null ? assessment.getMaxMarks().doubleValue() : 0.0)
                .weightage(assessment.getWeightage() != null ? assessment.getWeightage().doubleValue() : 0.0)
                .assessmentDate(assessment.getAssessmentDate())
                .dueDate(assessment.getDueDate())
                .isPublished(assessment.getIsPublished())
                .createdById(assessment.getCreatedBy() != null ? assessment.getCreatedBy().getId() : null)
                .createdAt(assessment.getCreatedAt())
                .updatedAt(assessment.getUpdatedAt())
                .build();
    }

    // Marks mappings
    public MarksDTO toMarksDTO(Marks marks) {
        if (marks == null) return null;

        return MarksDTO.builder()
                .id(marks.getId())
                .assessmentId(marks.getAssessment() != null ? marks.getAssessment().getId() : null)
                .assessmentTitle(marks.getAssessment() != null ? marks.getAssessment().getTitle() : null)
                .assessmentType(marks.getAssessment() != null && marks.getAssessment().getType() != null
                        ? marks.getAssessment().getType().name() : null)
                .maxMarks(marks.getAssessment() != null && marks.getAssessment().getMaxMarks() != null
                        ? marks.getAssessment().getMaxMarks().doubleValue() : 0.0)
                .weightage(marks.getAssessment() != null && marks.getAssessment().getWeightage() != null
                        ? marks.getAssessment().getWeightage().doubleValue() : 0.0)
                .enrollmentId(marks.getEnrollment() != null ? marks.getEnrollment().getId() : null)
                .studentId(marks.getEnrollment() != null && marks.getEnrollment().getStudent() != null
                        ? marks.getEnrollment().getStudent().getId() : null)
                .studentName(marks.getEnrollment() != null && marks.getEnrollment().getStudent() != null
                        ? marks.getEnrollment().getStudent().getFullName() : null)
                .obtainedMarks(marks.getObtainedMarks() != null ? marks.getObtainedMarks().doubleValue() : 0.0)
                .percentage(marks.getPercentage())
                .weightedScore(marks.getWeightedScore())
                .gradedById(marks.getGradedBy() != null ? marks.getGradedBy().getId() : null)
                .gradedAt(marks.getGradedAt())
                .feedback(marks.getFeedback())
                .isFinalized(marks.getIsFinalized())
                .createdAt(marks.getCreatedAt())
                .updatedAt(marks.getUpdatedAt())
                .build();
    }

    // Department mappings
    public DepartmentDTO toDepartmentDTO(Department department) {
        if (department == null) return null;

        return DepartmentDTO.builder()
                .id(department.getId())
                .code(department.getCode())
                .name(department.getName())
                .description(department.getDescription())
                .headFacultyId(department.getHeadFaculty() != null ? department.getHeadFaculty().getId() : null)
                .headFacultyName(department.getHeadFaculty() != null ? department.getHeadFaculty().getFullName() : null)
                .isActive(department.getIsActive())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }

    // GradingPolicy mappings
    public GradingPolicyDTO toGradingPolicyDTO(GradingPolicy policy) {
        if (policy == null) return null;

        Map<String, Double> boundaries = new java.util.HashMap<>();
        if (policy.getGradeBoundaries() != null) {
            policy.getGradeBoundaries().fields().forEachRemaining(entry ->
                    boundaries.put(entry.getKey(), entry.getValue().asDouble()));
        }

        return GradingPolicyDTO.builder()
                .id(policy.getId())
                .name(policy.getName())
                .description(policy.getDescription())
                .isDefault(policy.getIsDefault())
                .gradeBoundaries(boundaries)
                .createdAt(policy.getCreatedAt())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }

    // Dashboard mappings
    public DashboardStatsDTO toDashboardStatsDTO(long totalStudents, long totalFaculty, long totalCourses,
                                                 long activeEnrollments, double avgAttendance,
                                                 double avgPerformance, long pendingApprovals) {
        return DashboardStatsDTO.builder()
                .totalStudents(totalStudents)
                .totalFaculty(totalFaculty)
                .totalCourses(totalCourses)
                .activeEnrollments(activeEnrollments)
                .averageAttendancePercentage(avgAttendance)
                .averagePerformancePercentage(avgPerformance)
                .pendingApprovals(pendingApprovals)
                .build();
    }

    // Helper method to convert JsonNode to Map
    public Map<String, Double> jsonNodeToMap(JsonNode node) {
        if (node == null) return Map.of();
        return objectMapper.convertValue(node, Map.class);
    }
}