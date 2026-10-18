package com.studentmgmt.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseEnrollmentReportDTO {

    private CourseDTO course;
    private Integer totalEnrollments;
    private Long enrolledCount;
    private Long droppedCount;
    private Long completedCount;
    private Long withdrawnCount;
    private Long failedCount;
    private List<EnrollmentDTO> enrollments;
}