package com.studentmgmt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingPolicyCreateDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    private String description;

    private Boolean isDefault = false;

    @NotNull(message = "Grade boundaries are required")
    private Map<String, Double> gradeBoundaries;
}