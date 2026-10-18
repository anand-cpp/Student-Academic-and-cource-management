package com.studentmgmt.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSummaryDTO {

    private Long totalSessions;
    private Long totalPresent;
    private Long totalAbsent;
    private Long totalLate;
    private Long totalExcused;
    private Double overallPercentage;
    private List<CourseAttendanceSummaryDTO> byCourse;
}