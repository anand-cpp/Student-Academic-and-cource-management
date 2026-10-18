package com.studentmgmt.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResultDTO {
    private Long studentId;
    private String studentName;
    private String studentCode;
    private Double percentage;
    private String grade;
    private Double gradePoints;
    private List<MarksDTO> marks;
}