package com.studentmgmt.model;

public enum StudentStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    GRADUATED("GRADUATED"),
    SUSPENDED("SUSPENDED"),
    WITHDRAWN("WITHDRAWN");

    private final String value;

    StudentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}