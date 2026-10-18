package com.studentmgmt.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAcademicProfileDTO {

    private StudentDTO student;
    private List<EnrollmentDTO> enrollments;
    private AttendanceSummaryDTO attendanceSummary;
    private AcademicSummaryDTO academicSummary;
}