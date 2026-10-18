package com.studentmgmt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.studentmgmt.model.CourseStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {

    private Long id;
    private String courseCode;
    private String title;
    private String description;
    private Integer credits;
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private Integer semester;
    private Long facultyId;
    private String facultyName;
    private Integer maxEnrollment;
    private String prerequisites;
    private String syllabusUrl;
    private CourseStatus status;
    private String academicYear;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Computed fields
    private Long currentEnrollmentCount;
    private Long availableSeats;
}