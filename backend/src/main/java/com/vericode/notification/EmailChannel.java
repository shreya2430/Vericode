package com.vericode.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Bridge Pattern: Concrete Implementor #1: Email delivery via JavaMailSender.
 *
 * Sends notifications as real emails through the configured SMTP server
 * (Mailtrap sandbox in development). Mailtrap intercepts outbound mail so
 * no real emails are delivered during development or testing — they are
 * visible only inside the Mailtrap inbox at mailtrap.io.
 *
 * NOTE: This class is separate from EmailNotifier (Observer pattern).
 * EmailNotifier reacts to status changes and decides what message to build.
 * EmailChannel is purely responsible for the act of sending via email.
 * The Observer layer decides what to say; the Bridge layer decides how to send it.
 *
 * Failures are caught and logged rather than re-thrown so that a mail delivery
 * problem does not crash the notification pipeline or roll back the PR action.
 */
@Component
public class EmailChannel implements NotificationChannel {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    public EmailChannel(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email to the given recipient via the configured SMTP server.
     *
     * @param recipient the PR author's email address
     * @param subject   the email subject line
     * @param message   the email body
     */
    @Override
    public void send(String recipient, String subject, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromAddress);
            mail.setTo(recipient);
            mail.setSubject(subject);
            mail.setText(message);
            mailSender.send(mail);
        } catch (Exception e) {
            // Log but do not rethrow — a mail failure should not fail the PR action
            System.err.println("[EMAIL CHANNEL] Failed to send email to " + recipient + ": " + e.getMessage());
        }
    }
}
