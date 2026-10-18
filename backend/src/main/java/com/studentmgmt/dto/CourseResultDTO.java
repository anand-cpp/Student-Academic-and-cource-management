package com.studentmgmt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResultDTO {
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private Integer credits;
    private Double percentage;
    private String grade;
    private Double gradePoints;
    private String status;
}