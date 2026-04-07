package com.vericode.notification;

import org.springframework.stereotype.Component;

/**
 * Bridge Pattern: Concrete Implementor #2: In-app delivery via SSE.
 *
 * Delivers notifications to the user's active browser tab by pushing a
 * Server-Sent Event through SseEmitterRegistry. The frontend NotificationContext
 * listens on /api/notifications/stream and passes received events to
 * NotificationBanner for display.
 *
 * If the user has no active SSE connection (not logged in or tab closed),
 * the send is silently skipped — acceptable behaviour for ephemeral toasts.
 *
 * NOTE: This class is separate from InAppNotifier (Observer pattern).
 * InAppNotifier reacts to status changes and decides what message to build.
 * InAppChannel is purely responsible for the act of delivering it in-app.
 * The Observer layer decides what to say; the Bridge layer decides how to send it.
 */
@Component
public class InAppChannel implements NotificationChannel {

    private final SseEmitterRegistry sseRegistry;

    public InAppChannel(SseEmitterRegistry sseRegistry) {
        this.sseRegistry = sseRegistry;
    }

    /**
     * Pushes a notification to the browser tab of the given recipient.
     *
     * @param recipient the PR author's username (used as the SSE emitter map key)
     * @param subject   short label shown in the notification toast
     * @param message   full notification body
     */
    @Override
    public void send(String recipient, String subject, String message) {
        sseRegistry.send(recipient, subject, message);
    }
}
