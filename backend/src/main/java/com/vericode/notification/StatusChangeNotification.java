package com.vericode.notification;

import com.vericode.model.PullRequest;

/**
 * Bridge Pattern: the "abstraction" side.
 *
 * Represents a status change notification. Knows what to say (builds the message
 * from the PR), but delegates how to send it to whichever NotificationChannel
 * was injected at construction time.
 *
 * HOW THE BRIDGE WORKS HERE:
 * The abstraction (this class) holds a reference to the implementor (NotificationChannel).
 * Callers pick a channel at the point of use and pass it in. The notification logic
 * never changes regardless of which channel is chosen.
 *
 * Example usage in the Facade:
 *
 *   new StatusChangeNotification(new EmailChannel()).send(pr);
 *   new StatusChangeNotification(new WebSocketChannel()).send(pr);
 *   new StatusChangeNotification(new InAppChannel()).send(pr);
 *
 * EXTENDING THIS LATER:
 * If you need a different kind of notification (e.g. CommentNotification), create
 * a new abstraction class that also takes a NotificationChannel. The channels
 * themselves don't change. Hence the Bridge pattern.
 */
public class StatusChangeNotification {

    // The channel this notification will be sent through.
    // Set at construction time and never changed, one notification per channel.
    private final NotificationChannel channel;

    /**
     * @param channel the delivery channel to use (EmailChannel, InAppChannel,
     *                WebSocketChannel, etc.)
     */
    public StatusChangeNotification(NotificationChannel channel) {
        this.channel = channel;
    }

    /**
     * Builds a notification message from the PR's current state and sends it
     * through the configured channel.
     *
     * Called by the ReviewFacade after a state transition is complete and persisted.
     *
     * @param pr the PullRequest whose status just changed. Must already be in
     *           its new state when this is called.
     */
    public void send(PullRequest pr) {
        String subject = String.format("PR #%d \"%s\" status update", pr.getId(), pr.getTitle());
        String message = String.format(
            "Pull request \"%s\" by %s is now: %s",
            pr.getTitle(),
            pr.getAuthor(),
            pr.getStatus()
        );

        // Delegate delivery entirely to the channel.
        // This class does not know or care whether it's sending email, WebSocket, etc.
        channel.send(pr.getAuthor(), subject, message);
    }
}
