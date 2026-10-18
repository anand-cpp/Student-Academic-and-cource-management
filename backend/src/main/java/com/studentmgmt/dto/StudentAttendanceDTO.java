package com.studentmgmt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAttendanceDTO {
    private Long studentId;
    private String studentName;
    private String studentCode;
    private Long totalSessions;
    private Long presentCount;
    private Long lateCount;
    private Long absentCount;
    private Long excusedCount;
    private Double attendancePercentage;
}