package com.studentmgmt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecordUpdateDTO {

    @Pattern(regexp = "^(PRESENT|ABSENT|LATE|EXCUSED|MEDICAL)$", message = "Invalid attendance status")
    private String status;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}