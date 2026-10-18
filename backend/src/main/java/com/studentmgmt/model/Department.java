package com.studentmgmt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departments", indexes = {
    @Index(name = "idx_departments_code", columnList = "code"),
    @Index(name = "idx_departments_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_faculty_id")
    private Faculty headFaculty;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}