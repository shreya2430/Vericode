package com.vericode.notification;

/**
 * Bridge Pattern: Concrete Implementor #1: Email delivery.
 *
 * Handles sending notifications via email. Currently simulated with
 * System.out.println. Real delivery would use Mailtrap?
 *
 * NOTE: This is separate from EmailNotifier (Observer pattern).
 * EmailNotifier reacts to status changes and decides what message to build.
 * EmailChannel is purely responsible for the act of sending via email.
 * The Observer layer decides what to say; the Bridge layer decides how to send it.
 *
 * WHAT TO CHANGE LATER:
 * Inject Mailtrap and replace System.out.println with a real send call.
 * The method signature stays the same.
 */
public class EmailChannel implements NotificationChannel {

    /**
     * Simulates sending an email to the given recipient.
     *
     * @param recipient the author's email address.
     * @param subject   the email subject line.
     * @param message   the email body.
     */
    @Override
    public void send(String recipient, String subject, String message) {
        // TODO: Replace with Mailtrap once mail is configured.
        System.out.println(String.format(
            "[EMAIL CHANNEL] To: %s | Subject: %s | Message: %s",
            recipient, subject, message
        ));
    }
}
