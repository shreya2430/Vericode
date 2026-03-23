package com.vericode.observer;

import com.vericode.model.PullRequest;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern: Concrete Observer #3: WebSocket Notification.
 *
 * Pushes real-time status updates to connected browser clients over WebSocket.
 * Unlike email (pull), WebSocket is push-based so the client receives the update
 * instantly without polling.
 *
 * HOW THIS FITS WITH THE FRONTEND:
 * The frontend has NotificationContext scaffolded at:
 *   frontend/src/context/NotificationContext.jsx
 * That context is where React will subscribe to WebSocket messages and distribute
 * them to NotificationBanner. This class and NotificationContext are the two ends
 * of the same pipe, this class sends, NotificationContext receives.
 *
 * WHY @COMPONENT:
 * Spring automatically adds this to List<PRStatusObserver> in NotificationService
 * at startup. No manual registration needed.
 *
 * WHAT TO CHANGE LATER:
 * 1. Add spring-boot-starter-websocket to pom.xml
 * 2. Create a WebSocketConfig class registering a message broker and /ws endpoint
 * 3. Inject SimpMessagingTemplate here
 * 4. Replace System.out.println with:
 *      messagingTemplate.convertAndSend("/topic/pr-updates", payload);
 * The method signature stays the same only the delivery mechanism changes.
 */
@Component
public class WebSocketNotifier implements PRStatusObserver {

    /**
     * Fires when any PR status changes. Builds a payload with the PR id and new
     * status, then simulates broadcasting it to all connected WebSocket clients.
     *
     * @param pr the updated PullRequest (read-only). Do not modify it here.
     */
    @Override
    public void onStatusChange(PullRequest pr) {
        // Payload structure matches what NotificationContext on the frontend expects.
        // When replacing with real WebSocket delivery, send this as JSON:
        //   { "prId": <id>, "status": "<STATUS>" }
        String payload = String.format(
            "{\"prId\": %d, \"status\": \"%s\"}",
            pr.getId(),
            pr.getStatus()
        );

        // TODO: Replace with SimpMessagingTemplate.convertAndSend("/topic/pr-updates", payload)
        // once spring-boot-starter-websocket is configured.
        System.out.println("[WEBSOCKET] Broadcasting to /topic/pr-updates: " + payload);
    }
}
