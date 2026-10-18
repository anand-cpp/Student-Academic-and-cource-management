package com.studentmgmt.dto;

import com.studentmgmt.model.EnrollmentStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentUpdateDTO {

    @Pattern(regexp = "^(ENROLLED|DROPPED|COMPLETED|WITHDRAWN|FAILED)$", message = "Invalid status")
    private String status;

    @Size(max = 5, message = "Grade must not exceed 5 characters")
    private String grade;

    @DecimalMin(value = "0.0", message = "Grade points must be non-negative")
    @DecimalMax(value = "10.0", message = "Grade points must not exceed 10.0")
    private Double gradePoints;
}