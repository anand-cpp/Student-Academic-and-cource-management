package com.studentmgmt.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grading_policies", indexes = {
    @Index(name = "idx_grading_policies_default", columnList = "is_default")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingPolicy extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "grade_boundaries", columnDefinition = "JSON", nullable = false)
    private JsonNode gradeBoundaries;

    public String getGradeForPercentage(double percentage) {
        if (gradeBoundaries == null || gradeBoundaries.isEmpty()) {
            return "F";
        }

        // Iterate through boundaries (assuming they're ordered from highest to lowest)
        // JSON structure: {"A+": 90, "A": 85, ...}
        java.util.Iterator<String> fieldNames = gradeBoundaries.fieldNames();
        while (fieldNames.hasNext()) {
            String grade = fieldNames.next();
            double threshold = gradeBoundaries.get(grade).asDouble();
            if (percentage >= threshold) {
                return grade;
            }
        }
        return "F";
    }

    public double getMinPercentageForGrade(String grade) {
        if (gradeBoundaries == null || !gradeBoundaries.has(grade)) {
            return 0.0;
        }
        return gradeBoundaries.get(grade).asDouble();
    }
}