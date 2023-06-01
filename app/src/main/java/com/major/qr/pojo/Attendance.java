package com.major.qr.pojo;

public class Attendance {
    String id;
    String name;
    String totalAttenders;
    String creationDate;

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

    public String getName() {
        return name;
    }

    public String getTotalAttenders() {
        return totalAttenders;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalAttenders(String totalAttenders) {
        this.totalAttenders = totalAttenders;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
