package com.studentmgmt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarksBulkCreateDTO {

    @NotNull(message = "Assessment ID is required")
    private Long assessmentId;

    @NotNull(message = "Graded by user ID is required")
    private Long gradedById;

    @NotEmpty(message = "At least one record is required")
    private List<BulkRecord> records;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BulkRecord {

        @NotNull(message = "Enrollment ID is required")
        private Long enrollmentId;

        @NotNull(message = "Obtained marks is required")
        @DecimalMin(value = "0.0", message = "Obtained marks must be non-negative")
        private Double obtainedMarks;

        private String feedback;

        private Boolean isFinalized = false;
    }
}