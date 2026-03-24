package com.vericode.observer;

import com.vericode.model.PullRequest;
import com.vericode.model.PRStatus;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern: Concrete Observer #2: Email Notification.
 *
 * Sends email notifications when a PR status changes. Only fires for statuses
 * that are meaningful to the author: APPROVED, CHANGES_REQUESTED, and MERGED.
 * DRAFT and IN_REVIEW are internal transitions and do not warrant an email.
 *
 * WHY @COMPONENT:
 * Spring automatically adds this to List<PRStatusObserver> in NotificationService
 * at startup. No manual registration needed.
 *
 * WHAT TO CHANGE LATER:
 * 1. Add spring-boot-starter-mail to pom.xml
 * 2. Configure mail host/port/credentials in application.properties
 * 3. Inject JavaMailSender and replace System.out.println with real sends.
 * The filtering logic and method signature stay exactly the same.
 */
@Component
public class EmailNotifier implements PRStatusObserver {

    /**
     * Fires when any PR status changes. Checks whether the new status warrants
     * an email, then simulates sending it to the PR author.
     *
     * @param pr the updated PullRequest (read-only). Do not modify it here.
     */
    @Override
    public void onStatusChange(PullRequest pr) {
        if (!shouldEmail(pr.getStatus())) {
            return;
        }

        String recipient = pr.getAuthor().getEmail();
        String name      = pr.getAuthor().getName();
        String subject   = buildSubject(pr);
        String body      = buildBody(pr, name);

        // TODO: Replace with real delivery via JavaMailSender or SendGrid.
        System.out.println(String.format(
                "[EMAIL] To: %s (%s) | Subject: %s | Body: %s",
                recipient, name, subject, body
        ));
    }

    /**
     * Only email on statuses that require author awareness or signal a final outcome.
     * DRAFT and IN_REVIEW are internal so no email needed.
     */
    private boolean shouldEmail(PRStatus status) {
        return status == PRStatus.APPROVED
                || status == PRStatus.CHANGES_REQUESTED
                || status == PRStatus.MERGED;
    }

    private String buildSubject(PullRequest pr) {
        return String.format("[Vericode] PR #%d \"%s\" - %s",
                pr.getId(), pr.getTitle(), pr.getStatus());
    }

    private String buildBody(PullRequest pr, String authorName) {
        return switch (pr.getStatus()) {
            case APPROVED -> String.format(
                    "Hi %s! Your pull request \"%s\" has been approved. You can now merge.",
                    authorName, pr.getTitle());
            case CHANGES_REQUESTED -> String.format(
                    "Hi %s, your pull request \"%s\" has changes requested. Please review the feedback and update your code.",
                    authorName, pr.getTitle());
            case MERGED -> String.format(
                    "Hi %s! Your pull request \"%s\" has been successfully merged.",
                    authorName, pr.getTitle());
            default -> String.format(
                    "Hi %s, your pull request \"%s\" status has changed to: %s",
                    authorName, pr.getTitle(), pr.getStatus());
        };
    }
}