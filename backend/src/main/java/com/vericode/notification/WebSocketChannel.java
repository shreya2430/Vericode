package com.vericode.notification;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Bridge Pattern: Concrete Implementor #3: WebSocket delivery via STOMP.
 *
 * Broadcasts a notification to all clients subscribed to /topic/pr-updates.
 * Unlike SSE (which targets a specific user by username), WebSocket broadcasts
 * to every connected browser tab — enabling live PR list updates for all users.
 *
 * NOTE: This class is separate from WebSocketNotifier (Observer pattern).
 * WebSocketNotifier reacts to status changes and decides what payload to build.
 * WebSocketChannel is purely responsible for the act of broadcasting over STOMP.
 * The Observer layer decides what to say; the Bridge layer decides how to send it.
 */
@Component
public class WebSocketChannel implements NotificationChannel {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketChannel(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Broadcasts a notification to all clients subscribed to /topic/pr-updates.
     *
     * @param recipient not used for WebSocket — broadcast goes to all subscribers
     * @param subject   used as the event type label in the JSON payload
     * @param message   the full notification body included in the payload
     */
    @Override
    public void send(String recipient, String subject, String message) {
        messagingTemplate.convertAndSend("/topic/pr-updates",
                Map.of("subject", subject, "message", message));
    }

    public void sendPRStatusUpdate(String subject, String message, Long prId, String status) {
        messagingTemplate.convertAndSend("/topic/pr-updates",
                Map.of("subject", subject, "message", message, "prId", prId, "status", status));
    }
}
