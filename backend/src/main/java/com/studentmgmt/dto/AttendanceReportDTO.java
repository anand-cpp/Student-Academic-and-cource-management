package com.studentmgmt.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceReportDTO {

    private CourseDTO course;
    private Integer totalSessions;
    private List<StudentAttendanceDTO> studentAttendances;
}