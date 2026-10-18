package com.studentmgmt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.studentmgmt.model.FacultyStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyDTO {

    private Long id;
    private String facultyId;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private String designation;
    private String specialization;
    private String qualification;
    private LocalDate hireDate;
    private FacultyStatus status;
    private String officeLocation;
    private String researchInterests;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Computed fields
    private Long assignedCoursesCount;
    private Long totalStudentsCount;
}