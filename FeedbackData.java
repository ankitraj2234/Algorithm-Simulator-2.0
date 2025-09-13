package com.simulator;

import java.io.File;
import java.util.List;

/**
 * 📊 FEEDBACK DATA MODEL
 * Contains all feedback information for email sending
 */
public class FeedbackData {

    private String name;
    private String email;
    private String location;
    private String operatingSystem;
    private String issueType;
    private String description;
    private List<File> attachments;

    // System information
    private String appVersion;
    private String javaVersion;
    private String javaVendor;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }

    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<File> getAttachments() { return attachments; }
    public void setAttachments(List<File> attachments) { this.attachments = attachments; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    public String getJavaVersion() { return javaVersion; }
    public void setJavaVersion(String javaVersion) { this.javaVersion = javaVersion; }

    public String getJavaVendor() { return javaVendor; }
    public void setJavaVendor(String javaVendor) { this.javaVendor = javaVendor; }
}
