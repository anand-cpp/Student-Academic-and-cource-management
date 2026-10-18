package com.studentmgmt.model;

public enum FacultyStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    ON_LEAVE("ON_LEAVE"),
    RETIRED("RETIRED");

    private final String value;

    FacultyStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}