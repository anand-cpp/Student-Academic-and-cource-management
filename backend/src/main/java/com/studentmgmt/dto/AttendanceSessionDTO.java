package com.studentmgmt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSessionDTO {

    private Long id;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private LocalDate sessionDate;
    private Integer sessionNumber;
    private String topic;
    private Integer durationMinutes;
    private Boolean isCancelled;
    private String cancellationReason;
    private Long createdById;
    private String createdByName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Computed fields
    private Integer totalStudents;
    private Integer presentCount;
    private Integer absentCount;
    private Integer lateCount;
    private Integer excusedCount;
    private Double attendancePercentage;
}