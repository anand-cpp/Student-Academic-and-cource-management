package com.studentmgmt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpdateDTO {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits must not exceed 10")
    private Integer credits;

    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 12, message = "Semester must not exceed 12")
    private Integer semester;

    @Min(value = 1, message = "Max enrollment must be at least 1")
    @Max(value = 500, message = "Max enrollment must not exceed 500")
    private Integer maxEnrollment;

    private String prerequisites;

    @Size(max = 500, message = "Syllabus URL must not exceed 500 characters")
    private String syllabusUrl;

    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    private String academicYear;

    private Long departmentId;

    private Long facultyId;
}