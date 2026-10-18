package com.studentmgmt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentUpdateDTO {

    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private String description;

    private Boolean isActive;

    private Long headFacultyId;
}