package com.studentmgmt.model;

public enum UserRole {
    ADMIN("ADMIN"),
    FACULTY("FACULTY"),
    STUDENT("STUDENT"),
    DEPARTMENT_HEAD("DEPARTMENT_HEAD"),
    REGISTRAR("REGISTRAR");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromValue(String value) {
        for (UserRole role : values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }
}