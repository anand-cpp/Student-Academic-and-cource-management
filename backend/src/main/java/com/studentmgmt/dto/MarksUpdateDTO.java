package com.studentmgmt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarksUpdateDTO {

    @DecimalMin(value = "0.0", message = "Obtained marks must be non-negative")
    private Double obtainedMarks;

    private String feedback;

    private Boolean isFinalized;
}