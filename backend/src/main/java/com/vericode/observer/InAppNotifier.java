package com.vericode.observer;

import com.vericode.model.PullRequest;
import com.vericode.notification.InAppChannel;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern: Concrete Observer #1: In-App Notification.
 *
 * Reacts to PR status changes and delivers in-app toast notifications by
 * delegating to InAppChannel (Bridge pattern). This is the connection point
 * between Observer and Bridge: the Observer decides what to say, the Bridge
 * decides how to send it.
 *
 * Delivery path:
 *   NotificationService.notifyAll(pr)
 *     → InAppNotifier.onStatusChange(pr)       [Observer: builds the message]
 *       → InAppChannel.send(username, ...)      [Bridge: routes to SSE registry]
 *         → SseEmitterRegistry.send(username)   [pushes event to browser tab]
 *
 * The recipient passed to InAppChannel is the PR author's username, which is
 * the key used by SseEmitterRegistry to find the active SSE connection.
 */
@Component
public class InAppNotifier implements PRStatusObserver {

    private final InAppChannel inAppChannel;

    public InAppNotifier(InAppChannel inAppChannel) {
        this.inAppChannel = inAppChannel;
    }

    @Override
    public void onStatusChange(PullRequest pr) {
        String subject = String.format("PR #%d status update", pr.getId());
        String message = String.format(
                "Your pull request \"%s\" is now: %s",
                pr.getTitle(),
                pr.getStatus()
        );
        inAppChannel.send(pr.getAuthor().getUsername(), subject, message);
    }
}
