package com.studentmgmt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.studentmgmt.model.AssessmentType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentDTO {

    private Long id;
    private Long courseId;
    private String courseCode;
    private String title;
    private String description;
    private String type;
    private Double maxMarks;
    private Double weightage;
    private LocalDate assessmentDate;
    private LocalDateTime dueDate;
    private Boolean isPublished;
    private Long createdById;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Computed fields
    private Long submittedCount;
    private Long gradedCount;
    private Double averageScore;
    private Double averagePercentage;
}