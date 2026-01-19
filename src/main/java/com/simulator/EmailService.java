package com.simulator;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * 📧 EMAIL SERVICE
 * Professional email sending service with SMTP and attachments
 * 
 * SECURITY NOTE: Email credentials should be configured via:
 * 1. Environment variables: EMAIL_SENDER, EMAIL_PASSWORD, EMAIL_RECIPIENT
 * 2. Or config/email.properties file (not committed to version control)
 */
public class EmailService {

    private static EmailService instance;
    private final Properties properties;
    private final String smtpHost;
    private final String smtpPort;
    private final String senderEmail;
    private final String senderPassword;
    private final String recipientEmail;
    private boolean isConfigured = false;

    private EmailService() {
        // Load configuration from environment variables or properties file
        this.smtpHost = "smtp.gmail.com";
        this.smtpPort = "587";

        // Try environment variables first (recommended for production)
        String envSender = System.getenv("EMAIL_SENDER");
        String envPassword = System.getenv("EMAIL_PASSWORD");
        String envRecipient = System.getenv("EMAIL_RECIPIENT");

        if (envSender != null && envPassword != null && envRecipient != null) {
            this.senderEmail = envSender;
            this.senderPassword = envPassword;
            this.recipientEmail = envRecipient;
            this.isConfigured = true;
            System.out.println("📧 EmailService configured from environment variables");
        } else {
            // Fall back to properties file
            Properties emailProps = loadEmailProperties();
            this.senderEmail = emailProps.getProperty("email.sender", "");
            this.senderPassword = emailProps.getProperty("email.password", "");
            this.recipientEmail = emailProps.getProperty("email.recipient", "");
            this.isConfigured = !senderEmail.isEmpty() && !senderPassword.isEmpty();

            if (isConfigured) {
                System.out.println("📧 EmailService configured from properties file");
            } else {
                System.out.println("⚠️ EmailService not configured - feedback emails will be disabled");
            }
        }

        // Setup SMTP properties
        this.properties = new Properties();
        setupSMTPProperties();
    }

    private Properties loadEmailProperties() {
        Properties props = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/config/email.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            System.err.println("Could not load email.properties: " + e.getMessage());
        }
        return props;
    }

    public boolean isConfigured() {
        return isConfigured;
    }

    public static EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    /**
     * ⚙️ SETUP: Configure SMTP properties
     */
    private void setupSMTPProperties() {
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.ssl.trust", smtpHost);
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
    }

    /**
     * 📨 MAIN: Send feedback email with attachments
     */
    public boolean sendFeedbackEmail(FeedbackData feedbackData) {
        // Check if email service is configured
        if (!isConfigured) {
            System.err.println("❌ EmailService not configured - cannot send feedback");
            System.err.println("   Set EMAIL_SENDER, EMAIL_PASSWORD, EMAIL_RECIPIENT environment variables");
            return false;
        }

        try {
            // Create session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Algorithm Simulator Feedback - " + feedbackData.getIssueType());
            message.setSentDate(new Date());

            // Create multipart message
            Multipart multipart = new MimeMultipart();

            // Add text content
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(createEmailContent(feedbackData), "text/html; charset=utf-8");
            multipart.addBodyPart(textPart);

            // Add attachments
            for (File attachment : feedbackData.getAttachments()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                FileDataSource source = new FileDataSource(attachment);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(attachment.getName());
                multipart.addBodyPart(attachmentPart);
            }

            // Set content and send
            message.setContent(multipart);
            Transport.send(message);

            System.out.println("✅ Feedback email sent successfully");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Failed to send feedback email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 📄 CONTENT: Create HTML email content
     */
    private String createEmailContent(FeedbackData data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        html.append("<h2 style='color: #2c5aa0;'>🚀 Algorithm Simulator Feedback</h2>");

        html.append(
                "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse; width: 100%;'>");

        // User Information
        html.append(
                "<tr><th colspan='2' style='background-color: #f0f8ff; text-align: left;'>👤 User Information</th></tr>");
        html.append("<tr><td><strong>Name:</strong></td><td>").append(data.getName()).append("</td></tr>");
        html.append("<tr><td><strong>Email:</strong></td><td>").append(data.getEmail()).append("</td></tr>");
        html.append("<tr><td><strong>Location:</strong></td><td>").append(data.getLocation()).append("</td></tr>");
        html.append("<tr><td><strong>Operating System:</strong></td><td>").append(data.getOperatingSystem())
                .append("</td></tr>");

        // Issue Information
        html.append(
                "<tr><th colspan='2' style='background-color: #fff0f5; text-align: left;'>🐛 Issue Information</th></tr>");
        html.append("<tr><td><strong>Issue Type:</strong></td><td>").append(data.getIssueType()).append("</td></tr>");
        html.append("<tr><td><strong>Submitted:</strong></td><td>").append(dateFormat.format(new Date()))
                .append("</td></tr>");

        // System Information
        html.append(
                "<tr><th colspan='2' style='background-color: #f5fffa; text-align: left;'>💻 System Information</th></tr>");
        html.append("<tr><td><strong>App Version:</strong></td><td>").append(data.getAppVersion()).append("</td></tr>");
        html.append("<tr><td><strong>Java Version:</strong></td><td>").append(data.getJavaVersion())
                .append("</td></tr>");
        html.append("<tr><td><strong>Java Vendor:</strong></td><td>").append(data.getJavaVendor()).append("</td></tr>");

        // Attachments
        if (!data.getAttachments().isEmpty()) {
            html.append(
                    "<tr><th colspan='2' style='background-color: #fffacd; text-align: left;'>📎 Attachments</th></tr>");
            html.append("<tr><td><strong>Files:</strong></td><td>");
            for (File file : data.getAttachments()) {
                html.append("• ").append(file.getName()).append("<br>");
            }
            html.append("</td></tr>");
        }

        html.append("</table>");

        // Description
        html.append("<h3 style='color: #2c5aa0;'>📝 Description:</h3>");
        html.append("<div style='background-color: #f9f9f9; padding: 15px; border-left: 4px solid #2c5aa0;'>");
        html.append(data.getDescription().replaceAll("\n", "<br>"));
        html.append("</div>");

        html.append("<hr style='margin: 20px 0;'>");
        html.append(
                "<p style='color: #666; font-size: 12px;'>This feedback was automatically sent from Algorithm Simulator v")
                .append(data.getAppVersion()).append("</p>");

        html.append("</body></html>");

        return html.toString();
    }
}
