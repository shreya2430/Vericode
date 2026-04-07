package com.vericode.notification;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bridge Pattern + Observer Pattern: SSE connection registry.
 *
 * Holds one SseEmitter per connected username. InAppChannel uses this to push
 * events to the right browser tab. SseController uses this to register new
 * connections when a user opens the app.
 *
 * ConcurrentHashMap is used because emitters are registered and removed from
 * different threads (HTTP request threads vs. notification threads).
 *
 * Emitters are cleaned up automatically when the connection closes or times out,
 * so the map never accumulates stale entries.
 */
@Component
public class SseEmitterRegistry {

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Registers a new SSE connection for the given username.
     * Called by SseController when a user opens the /api/notifications/stream endpoint.
     * Timeout is set to 30 minutes; the frontend reconnects automatically on timeout.
     */
    public SseEmitter register(String username) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitters.put(username, emitter);
        emitter.onCompletion(() -> emitters.remove(username));
        emitter.onTimeout(() -> emitters.remove(username));
        return emitter;
    }

    /**
     * Pushes a notification event to the connected browser tab for the given username.
     * Called by InAppChannel after a PR status change.
     *
     * If the user is not connected (emitter not in map), the call is silently skipped —
     * the notification simply isn't delivered, which is acceptable for in-app toasts.
     *
     * If the send fails (broken pipe, client disconnected mid-stream), the emitter is
     * removed so future sends don't attempt to use a dead connection.
     *
     * @param username the PR author's username, used as the map key
     * @param subject  short label shown in the toast
     * @param message  full notification body
     */
    public void send(String username, String subject, String message) {
        SseEmitter emitter = emitters.get(username);
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(subject + ": " + message));
        } catch (IOException e) {
            emitters.remove(username);
        }
    }
}
