package com.studentmgmt.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResultsReportDTO {

    private CourseDTO course;
    private List<AssessmentDTO> assessments;
    private List<StudentResultDTO> studentResults;
}