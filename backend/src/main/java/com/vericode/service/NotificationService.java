package com.vericode.service;

import com.vericode.model.PullRequest;
import com.vericode.observer.PRStatusObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Observer Pattern: the notification broadcaster.
 *
 * This service is the central hub that fans out a PR status change event to
 * every registered observer. It knows nothing about how notifications are
 * delivered, that is each observer's responsibility.
 *
 * HOW SPRING AUTO-WIRES THE OBSERVER LIST:
 * Spring scans the classpath at startup and collects every @Component-annotated
 * class that implements PRStatusObserver. It injects them all into the List below
 * automatically. Currently that list will contain:
 *   - InAppNotifier
 *   - EmailNotifier
 *   - WebSocketNotifier
 *
 * To add a new notification channel in the future (e.g. SlackNotifier), simply:
 *   1. Create a class that implements PRStatusObserver
 *   2. Annotate it with @Component
 *   That's it. No changes needed here or anywhere else.
 *
 * HOW THIS IS CALLED:
 * The ReviewFacade (Group 4, built last) calls notifyAll(pr) after every state
 * transition. Group 3's Command classes execute the transition; the Facade then
 * calls this service. This class never needs to know which command triggered it.
 *
 * IMPORTANT CALL ORDER:
 * notifyAll() should only be called AFTER the PullRequest's new status has been
 * persisted to the database. Observers read the PR's current state, if called
 * before saving, they will notify on stale data.
 */
@Service
public class NotificationService {

    /**
     * Spring injects all PRStatusObserver implementations here automatically.
     * The list is populated at application startup and does not change at runtime.
     */
    @Autowired
    private List<PRStatusObserver> observers;

    /**
     * Broadcasts a PR status change to every registered observer.
     *
     * Call this after a PR's status has been updated and saved. Each observer
     * receives the same PullRequest reference and decides independently what
     * to do with it (send email, push in-app message, broadcast via WebSocket).
     *
     * @param pr the PullRequest whose status just changed should already be
     *           in its new state when this method is called.
     */
    public void notifyAll(PullRequest pr) {
        for (PRStatusObserver observer : observers) {
            // Each observer handles its own errors internally.
            // If one fails it should not block the others, wrap
            // individual calls in try/catch here once real delivery is in place.
            observer.onStatusChange(pr);
        }
    }
}
