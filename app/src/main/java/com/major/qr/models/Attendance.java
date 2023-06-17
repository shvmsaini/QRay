package com.major.qr.models;

public class Attendance {
    private String id;
    private String name;
    private String totalAttenders;
    private String creationDate;

    public Attendance(String id, String name, String totalAttenders, String creationDate) {
        this.id = id;
        this.name = name;
        this.totalAttenders = totalAttenders;
        this.creationDate = creationDate;
    }

    public Attendance() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalAttenders() {
        return totalAttenders;
    }

    public void setTotalAttenders(String totalAttenders) {
        this.totalAttenders = totalAttenders;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
