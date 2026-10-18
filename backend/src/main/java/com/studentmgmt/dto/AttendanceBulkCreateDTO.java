package com.studentmgmt.dto;

import com.studentmgmt.model.AttendanceStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceBulkCreateDTO {

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Recorded by user ID is required")
    private Long recordedById;

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

        @NotNull(message = "Status is required")
        @Pattern(regexp = "^(PRESENT|ABSENT|LATE|EXCUSED|MEDICAL)$", message = "Invalid attendance status")
        private String status;

        @Size(max = 500, message = "Remarks must not exceed 500 characters")
        private String remarks;
    }
}