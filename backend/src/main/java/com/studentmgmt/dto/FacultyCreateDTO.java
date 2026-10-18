package com.studentmgmt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyCreateDTO {

    @NotBlank(message = "Faculty ID is required")
    @Size(max = 50, message = "Faculty ID must not exceed 50 characters")
    private String facultyId;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Designation is required")
    @Pattern(regexp = "^(PROFESSOR|ASSOCIATE_PROFESSOR|ASSISTANT_PROFESSOR|LECTURER|ADJUNCT|VISITING)$", message = "Invalid designation")
    private String designation;

    @Size(max = 255, message = "Specialization must not exceed 255 characters")
    private String specialization;

    private String qualification;

    @NotNull(message = "Hire date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    @Size(max = 255, message = "Office location must not exceed 255 characters")
    private String officeLocation;

    private String researchInterests;
}