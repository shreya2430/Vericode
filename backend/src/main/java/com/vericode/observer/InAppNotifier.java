package com.vericode.observer;

import com.vericode.model.PullRequest;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern: Concrete Observer #1: In-App Notification.
 *
 * Delivers in-app notifications when a PR status changes. "In-app" means the
 * user sees a banner or toast inside the Vericode UI without needing to check email.
 *
 * CURRENT STATE:
 * Delivery is simulated with System.out.println. The real implementation will push
 * a message to the frontend via NotificationContext (scaffolded at
 * frontend/src/context/NotificationContext.jsx) once WebSocket or SSE is wired up.
 *
 * WHY @COMPONENT:
 * Spring automatically adds this to List<PRStatusObserver> in NotificationService
 * at startup. No manual registration needed.
 *
 * WHAT TO CHANGE LATER:
 * Replace System.out.println with a WebSocket broadcast or SSE push.
 * The method signature stays the same only the body changes.
 */
@Component
public class InAppNotifier implements PRStatusObserver {

    /**
     * Fires when any PR status changes. Builds a human-readable in-app message
     * using the PR's id, title, author, and new status.
     *
     * @param pr the updated PullRequest (read-only). Do not modify it here.
     */
    @Override
    public void onStatusChange(PullRequest pr) {
        String message = String.format(
            "[IN-APP] PR #%d \"%s\" by %s is now: %s",
            pr.getId(),
            pr.getTitle(),
            pr.getAuthor(),
            pr.getStatus()
        );

        // TODO: Replace with real delivery, WebSocket broadcast or SSE push to
        // frontend/src/context/NotificationContext.jsx
        System.out.println(message);
    }
}
