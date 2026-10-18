package com.studentmgmt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAttendanceSummaryDTO {
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private Long totalSessions;
    private Long totalStudents;
    private Long presentCount;
    private Long absentCount;
    private Long lateCount;
    private Long excusedCount;
    private Double percentage;
}