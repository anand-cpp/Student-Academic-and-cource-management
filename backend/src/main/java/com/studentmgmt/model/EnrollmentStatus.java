package com.studentmgmt.model;

public enum EnrollmentStatus {
    ENROLLED("ENROLLED"),
    DROPPED("DROPPED"),
    COMPLETED("COMPLETED"),
    WITHDRAWN("WITHDRAWN"),
    FAILED("FAILED");

    private final String value;

    EnrollmentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}