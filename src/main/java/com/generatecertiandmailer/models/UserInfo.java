package com.generatecertiandmailer.models;

public class UserInfo {
    private String emailId;
    private String name;
    private String grade;
    private String certificateId;

    public UserInfo(String emailId, String name, String grade, String certificateId) {
        this.emailId = emailId;
        this.name = name;
        this.grade = grade;
        this.certificateId = certificateId;
    }

    public String getEmailId() { return emailId; }
    public String getName() { return name; }
    public String getGrade() { return grade; }
    public String getCertificateId() { return certificateId; }
}
