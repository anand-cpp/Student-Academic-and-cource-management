package com.studentmgmt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarksCreateDTO {

    @NotNull(message = "Assessment ID is required")
    private Long assessmentId;

    @NotNull(message = "Enrollment ID is required")
    private Long enrollmentId;

    @NotNull(message = "Obtained marks is required")
    @DecimalMin(value = "0.0", message = "Obtained marks must be non-negative")
    private Double obtainedMarks;

    private String feedback;

    @NotNull(message = "Graded by user ID is required")
    private Long gradedById;

    private Boolean isFinalized = false;
}