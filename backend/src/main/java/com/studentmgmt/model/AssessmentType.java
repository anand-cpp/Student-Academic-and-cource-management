package com.studentmgmt.model;

public enum AssessmentType {
    QUIZ("QUIZ"),
    MIDTERM("MIDTERM"),
    FINAL("FINAL"),
    ASSIGNMENT("ASSIGNMENT"),
    PROJECT("PROJECT"),
    LAB("LAB"),
    PRESENTATION("PRESENTATION"),
    PARTICIPATION("PARTICIPATION"),
    OTHER("OTHER");

    private final String value;

    AssessmentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}