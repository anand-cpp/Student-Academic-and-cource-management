package com.studentmgmt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarksDTO {

    private Long id;
    private Long assessmentId;
    private String assessmentTitle;
    private String assessmentType;
    private Double maxMarks;
    private Double weightage;
    private Long enrollmentId;
    private Long studentId;
    private String studentName;
    private Double obtainedMarks;
    private Double percentage;
    private Double weightedScore;
    private Long gradedById;
    private LocalDateTime gradedAt;
    private String feedback;
    private Boolean isFinalized;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}