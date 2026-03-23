package com.vericode.notification;

/**
 * Bridge Pattern: Concrete Implementor #3: WebSocket delivery.
 *
 * Handles sending notifications as real-time pushes to connected browser clients.
 * Currently simulated with System.out.println.
 *
 * NOTE: This is separate from WebSocketNotifier (Observer pattern).
 * WebSocketNotifier reacts to status changes and decides what payload to build.
 * WebSocketChannel is purely responsible for the act of broadcasting over WebSocket.
 * The Observer layer decides what to say; the Bridge layer decides how to send it.
 *
 * WHAT TO CHANGE LATER:
 * 1. Add spring-boot-starter-websocket to pom.xml
 * 2. Inject SimpMessagingTemplate here
 * 3. Replace System.out.println with:
 *      messagingTemplate.convertAndSend("/topic/pr-updates", message);
 * The method signature stays the same.
 */
public class WebSocketChannel implements NotificationChannel {

    /**
     * Simulates broadcasting a notification over WebSocket to a connected client.
     *
     * @param recipient the username or session id of the target client.
     * @param subject   a short label for the event type.
     * @param message   the full payload to broadcast.
     */
    @Override
    public void send(String recipient, String subject, String message) {
        // TODO: Replace with SimpMessagingTemplate.convertAndSend once
        // spring-boot-starter-websocket is configured.
        System.out.println(String.format(
            "[WEBSOCKET CHANNEL] To: %s | Subject: %s | Message: %s",
            recipient, subject, message
        ));
    }
}
