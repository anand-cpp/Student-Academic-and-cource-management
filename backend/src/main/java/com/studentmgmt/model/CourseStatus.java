package com.studentmgmt.model;

public enum CourseStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    ARCHIVED("ARCHIVED");

    private final String value;

    CourseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}