package com.major.qr.models;

public class Doc {
    private String documentType;
    private String documentReference;
    private String documentId;

    public Doc(String s1, String s2, String s3) {
        this.documentType = s1;
        this.documentReference = s2;
        this.documentId = s3;
    }

    public Doc() {
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(String documentReference) {
        this.documentReference = documentReference;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
