package com.major.qr.models;

import java.util.ArrayList;

public class Qr {
    private String lastSeen;
    private ArrayList<Doc> docs;
    private String sessionName;
    private String creationDate;
    private String qrId;
    private String token;

    public Qr(String lastSeen, ArrayList<Doc> docs, String sessionName, String creationDate,
              String qrId, String token) {
        this.lastSeen = lastSeen;
        this.docs = docs;
        this.sessionName = sessionName;
        this.creationDate = creationDate;
        this.qrId = qrId;
        this.token = token;
    }

    public Qr() {
    }

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<Doc> getDocs() {
        return docs;
    }

    public void setDocs(ArrayList<Doc> docs) {
        this.docs = docs;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
