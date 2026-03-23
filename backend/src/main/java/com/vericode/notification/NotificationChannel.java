package com.vericode.notification;

/**
 * Bridge Pattern, the "implementor" side.
 *
 * This interface defines the delivery contract. Any class that implements it
 * is responsible for physically sending a message through one specific channel
 * (email, in-app, WebSocket, Slack, etc.).
 *
 * WHY THIS EXISTS (the Bridge idea):
 * Without Bridge, you'd end up with a class explosion:
 *   EmailStatusChangeNotification, WebSocketStatusChangeNotification,
 *   InAppApprovalNotification, EmailApprovalNotification, etc.
 * Bridge separates the notification type (what you're saying) from the delivery
 * channel (how you're saying it). The two hierarchies grow independently.
 *
 * HOW IT CONNECTS TO THE OTHER SIDE:
 * StatusChangeNotification is the "abstraction" side. It holds a reference to a
 * NotificationChannel and delegates the actual sending to it. The abstraction
 * never knows or cares which channel it's talking to.
 *
 * ADDING A NEW CHANNEL LATER:
 * Create a new class that implements this interface (e.g. SlackChannel).
 * Zero changes needed to StatusChangeNotification or anywhere else.
 */
public interface NotificationChannel {

    /**
     * Sends a message to the specified recipient through this channel.
     *
     * @param recipient who to send to: an email address, username, or user id
     *                  depending on the channel implementation.
     * @param subject   a short summary of the notification.
     * @param message   the full notification body.
     */
    void send(String recipient, String subject, String message);
}
