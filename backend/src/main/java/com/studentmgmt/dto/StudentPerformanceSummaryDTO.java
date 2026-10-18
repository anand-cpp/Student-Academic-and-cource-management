package com.studentmgmt.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPerformanceSummaryDTO {

    private StudentDTO student;
    private List<CoursePerformanceDTO> coursePerformances;
    private Double overallPercentage;
    private String overallGrade;
    private Double gpa;
    private Double totalCreditsEarned;
    private Double totalCreditsAttempted;
}