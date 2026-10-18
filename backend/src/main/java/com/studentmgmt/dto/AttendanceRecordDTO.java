package com.studentmgmt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.studentmgmt.model.AttendanceStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecordDTO {

    private Long id;
    private Long sessionId;
    private LocalDate sessionDate;
    private Integer sessionNumber;
    private Long enrollmentId;
    private Long studentId;
    private String studentName;
    private String studentCode;
    private AttendanceStatus status;
    private String remarks;
    private Long recordedById;
    private String recordedByName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recordedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}