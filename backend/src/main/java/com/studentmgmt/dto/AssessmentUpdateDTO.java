package com.studentmgmt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.studentmgmt.model.AssessmentType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentUpdateDTO {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    private AssessmentType type;

    @DecimalMin(value = "0.01", message = "Max marks must be positive")
    @DecimalMax(value = "1000.0", message = "Max marks must not exceed 1000")
    private Double maxMarks;

    @DecimalMin(value = "0.0", message = "Weightage must be non-negative")
    @DecimalMax(value = "100.0", message = "Weightage must not exceed 100")
    private Double weightage;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate assessmentDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate;
}