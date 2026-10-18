package com.studentmgmt.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicSummaryDTO {

    private Double overallPercentage;
    private String overallGrade;
    private Double totalCreditsEarned;
    private Double totalCreditsAttempted;
    private Double gpa;
    private List<CourseResultDTO> courseResults;
}