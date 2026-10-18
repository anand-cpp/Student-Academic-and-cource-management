package com.studentmgmt.service;

import com.studentmgmt.dto.StudentAcademicProfileDTO;
import com.studentmgmt.dto.CourseEnrollmentReportDTO;
import com.studentmgmt.dto.FacultyCourseAssignmentReportDTO;
import com.studentmgmt.dto.AttendanceReportDTO;
import com.studentmgmt.dto.CourseResultsReportDTO;
import com.studentmgmt.dto.StudentPerformanceSummaryDTO;

import java.util.List;

public interface ReportService {

    StudentAcademicProfileDTO getStudentAcademicProfile(Long studentId);

    CourseEnrollmentReportDTO getCourseEnrollmentReport(Long courseId);

    FacultyCourseAssignmentReportDTO getFacultyCourseAssignmentReport(Long facultyId);

    AttendanceReportDTO getAttendanceReport(Long courseId);

    AttendanceReportDTO getStudentAttendanceReport(Long studentId, Long courseId);

    CourseResultsReportDTO getCourseResultsReport(Long courseId);

    StudentPerformanceSummaryDTO getStudentPerformanceSummary(Long studentId);

    List<StudentPerformanceSummaryDTO> getDepartmentStudentPerformance(Long departmentId);
}