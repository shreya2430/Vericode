package com.vericode.notification;

/**
 * Bridge Pattern: Concrete Implementor #2: In-app delivery.
 *
 * Handles sending notifications that appear inside the Vericode UI as a
 * banner or toast. Currently simulated with System.out.println.
 *
 * NOTE: This is separate from InAppNotifier (Observer pattern).
 * InAppNotifier reacts to status changes and decides what message to build.
 * InAppChannel is purely responsible for the act of delivering it in-app.
 * The Observer layer decides what to say; the Bridge layer decides how to send it.
 *
 * WHAT TO CHANGE LATER:
 * Wire this to the SSE or WebSocket endpoint that feeds NotificationContext.jsx
 * on the frontend. The method signature stays the same.
 */
public class InAppChannel implements NotificationChannel {

    /**
     * Simulates delivering an in-app notification to the given recipient.
     *
     * @param recipient the username of the user to notify.
     * @param subject   a short summary shown in the notification banner.
     * @param message   the full notification text.
     */
    @Override
    public void send(String recipient, String subject, String message) {
        // TODO: Replace with a push to SSE or WebSocket endpoint feeding
        // frontend/src/context/NotificationContext.jsx
        System.out.println(String.format(
            "[IN-APP CHANNEL] To: %s | Subject: %s | Message: %s",
            recipient, subject, message
        ));
    }
}
