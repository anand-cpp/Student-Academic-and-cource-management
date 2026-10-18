package com.studentmgmt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    public UserRole getUserRole() {
        return UserRole.fromValue(name);
    }

    public void setUserRole(UserRole userRole) {
        this.name = userRole.getValue();
    }
}