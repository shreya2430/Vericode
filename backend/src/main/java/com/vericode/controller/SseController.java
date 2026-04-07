package com.vericode.controller;

import com.vericode.notification.SseEmitterRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Observer Pattern + Bridge Pattern: SSE endpoint for in-app notifications.
 *
 * The frontend opens a persistent GET connection to /api/notifications/stream
 * on login. This registers an SseEmitter for that user. Whenever InAppChannel
 * sends a notification, it looks up the emitter and pushes the event to the
 * open browser tab.
 *
 * The frontend uses the native EventSource API, which automatically reconnects
 * if the connection drops (e.g. on the 30-minute timeout).
 */
@RestController
@RequestMapping("/api/notifications")
public class SseController {

    private final SseEmitterRegistry registry;

    public SseController(SseEmitterRegistry registry) {
        this.registry = registry;
    }

    /**
     * Opens an SSE stream for the given username.
     * Called once by NotificationContext.jsx when the user logs in.
     *
     * @param username the logged-in user's username, used to route future notifications
     * @return a long-lived SseEmitter that Spring keeps open for the lifetime of the connection
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String username) {
        return registry.register(username);
    }
}
