package com.studentmgmt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {

    private Long totalStudents;
    private Long totalFaculty;
    private Long totalCourses;
    private Long activeEnrollments;
    private Double averageAttendancePercentage;
    private Double averagePerformancePercentage;
    private Long pendingApprovals;
}