package com.major.qr.pojo;

public class Attendees {
    String addedDateTime;
    String displayName;
    String attendersId;
    String email;

    public Attendees(String addedDateTime, String displayName, String attendersId, String email) {
        this.addedDateTime = addedDateTime;
        this.displayName = displayName;
        this.attendersId = attendersId;
        this.email = email;
    }

    public Attendees() {
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
