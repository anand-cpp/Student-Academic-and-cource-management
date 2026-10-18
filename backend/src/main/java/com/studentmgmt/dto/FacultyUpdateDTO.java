package com.studentmgmt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyUpdateDTO {

    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Pattern(regexp = "^(PROFESSOR|ASSOCIATE_PROFESSOR|ASSISTANT_PROFESSOR|LECTURER|ADJUNCT|VISITING)$", message = "Invalid designation")
    private String designation;

    @Size(max = 255, message = "Specialization must not exceed 255 characters")
    private String specialization;

    private String qualification;

    @Size(max = 255, message = "Office location must not exceed 255 characters")
    private String officeLocation;

    private String researchInterests;

    private Long departmentId;
}