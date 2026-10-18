package com.studentmgmt.model;

public enum FacultyDesignation {
    PROFESSOR("PROFESSOR"),
    ASSOCIATE_PROFESSOR("ASSOCIATE_PROFESSOR"),
    ASSISTANT_PROFESSOR("ASSISTANT_PROFESSOR"),
    LECTURER("LECTURER"),
    ADJUNCT("ADJUNCT"),
    VISITING("VISITING");

    private final String value;

    FacultyDesignation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}