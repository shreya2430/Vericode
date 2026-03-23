package com.vericode.observer;

import com.vericode.model.PullRequest;

/**
 * Observer Pattern: Subject/Observer contract.
 *
 * This is the Observer interface. Any class that wants to react to a PR status
 * change must implement this interface.
 *
 * WHY THIS INTERFACE EXISTS:
 * Instead of hardcoding "send email, then send in-app, then push WebSocket"
 * inside a single notification method, we define a contract here. Each delivery
 * mechanism is its own class that implements this interface independently.
 * This means:
 *   - Adding a new notifier (e.g. SlackNotifier) = create one new class, zero changes elsewhere
 *   - Removing a notifier = delete the class, zero changes elsewhere
 *   - Testing = mock this interface, no real emails or sockets needed
 *
 * HOW SPRING WIRES THIS:
 * Every class that implements PRStatusObserver and is annotated with @Component
 * gets automatically collected by Spring into a List<PRStatusObserver>.
 * NotificationService declares that list as @Autowired and Spring fills it at startup.
 * There are no requirements for manual registration, factory or switch statement on delivery type.
 *
 * WHEN onStatusChange IS CALLED:
 * It is called by NotificationService.notifyAll(pr) which is triggered by the
 * ReviewFacade after any state transition (approve, reject, request changes, merge).
 * Group 3 calls the Facade; the Facade calls NotificationService; NotificationService
 * fans out to every registered observer. This class never knows who triggered it.
 *
 * THE METHOD SIGNATURE:
 * We pass the full PullRequest object so each observer can decide what it needs -
 * the title, the author, the new status, etc. Observers should treat this as
 * read-only. They observe; they do not mutate.
 */
public interface PRStatusObserver {

    /**
     * Called when a PullRequest's status has changed.
     *
     * @param pr the PullRequest that was just updated, read its status, author,
     *           title, etc. to build the notification message. Do not modify it.
     */
    void onStatusChange(PullRequest pr);
}
