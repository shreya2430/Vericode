package com.vericode.observer;

import com.vericode.model.PullRequest;
import com.vericode.notification.WebSocketChannel;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern: Concrete Observer #3: WebSocket Notification.
 *
 * Reacts to PR status changes and broadcasts a real-time update to all
 * connected browser clients by delegating to WebSocketChannel (Bridge pattern).
 * This is the connection point between Observer and Bridge: the Observer
 * decides what to say, the Bridge decides how to send it.
 *
 * Delivery path:
 *   NotificationService.notifyAll(pr)
 *     → WebSocketNotifier.onStatusChange(pr)       [Observer: builds the payload]
 *       → WebSocketChannel.send(...)                [Bridge: broadcasts via STOMP]
 *         → /topic/pr-updates                       [all subscribed clients receive it]
 *
 * Unlike InAppNotifier (which targets the PR author via SSE), WebSocketNotifier
 * broadcasts to every connected user — enabling live PR list updates without polling.
 */
@Component
public class WebSocketNotifier implements PRStatusObserver {

    private final WebSocketChannel webSocketChannel;

    public WebSocketNotifier(WebSocketChannel webSocketChannel) {
        this.webSocketChannel = webSocketChannel;
    }

    @Override
    public void onStatusChange(PullRequest pr) {
        String subject = String.format("PR #%d status update", pr.getId());
        String message = String.format(
                "PR \"%s\" by %s is now: %s",
                pr.getTitle(),
                pr.getAuthor().getName(),
                pr.getStatus()
        );
        webSocketChannel.send(null, subject, message);
    }
}
