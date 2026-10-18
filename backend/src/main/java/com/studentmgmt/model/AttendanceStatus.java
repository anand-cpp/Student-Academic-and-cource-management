package com.studentmgmt.model;

public enum AttendanceStatus {
    PRESENT("PRESENT"),
    ABSENT("ABSENT"),
    LATE("LATE"),
    EXCUSED("EXCUSED"),
    MEDICAL("MEDICAL");

    private final String value;

    AttendanceStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean countsAsPresent() {
        return this == PRESENT || this == LATE;
    }

    public boolean countsAsAbsent() {
        return this == ABSENT;
    }

    public boolean isExcused() {
        return this == EXCUSED || this == MEDICAL;
    }
}