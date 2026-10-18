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
public class AttendanceSessionCreateDTO {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Session date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate sessionDate;

    @NotNull(message = "Session number is required")
    @Min(value = 1, message = "Session number must be at least 1")
    private Integer sessionNumber;

    @Size(max = 255, message = "Topic must not exceed 255 characters")
    private String topic;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration must not exceed 480 minutes")
    private Integer durationMinutes = 60;

    @NotNull(message = "Created by user ID is required")
    private Long createdById;
}