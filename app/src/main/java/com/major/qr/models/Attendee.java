package com.major.qr.models;

public class Attendee {
    private String addedDateTime;
    private String displayName;
    private String attendersId;
    private String email;
    private String recordId;

    public Attendee(String addedDateTime, String displayName, String attendersId, String email, String recordId) {
        this.addedDateTime = addedDateTime;
        this.displayName = displayName;
        this.attendersId = attendersId;
        this.email = email;
        this.recordId = recordId;
    }

    public Attendee() {
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getAddedDateTime() {
        return addedDateTime;
    }

    public void setAddedDateTime(String addedDateTime) {
        this.addedDateTime = addedDateTime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAttendersId() {
        return attendersId;
    }

    public void setAttendersId(String attendersId) {
        this.attendersId = attendersId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
