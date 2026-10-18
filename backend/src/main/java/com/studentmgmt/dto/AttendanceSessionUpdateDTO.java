package com.studentmgmt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSessionUpdateDTO {

    @Size(max = 255, message = "Topic must not exceed 255 characters")
    private String topic;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration must not exceed 480 minutes")
    private Integer durationMinutes;

    private Boolean isCancelled;

    @Size(max = 255, message = "Cancellation reason must not exceed 255 characters")
    private String cancellationReason;
}