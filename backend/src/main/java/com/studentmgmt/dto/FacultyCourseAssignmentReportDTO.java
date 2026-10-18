package com.studentmgmt.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyCourseAssignmentReportDTO {

    private FacultyDTO faculty;
    private Integer totalCourses;
    private Long activeCourses;
    private Long totalStudents;
    private List<CourseDTO> courses;
}