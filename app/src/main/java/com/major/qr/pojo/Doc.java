package com.major.qr.pojo;

public class Doc {
    String documentType;
    String documentReference;
    String documentId;

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
