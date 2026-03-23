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

        // In a real implementation, pr.getAuthor() would be looked up against
        // a UserRepository to get the actual email address.
        String recipient = pr.getAuthor();
        String subject   = buildSubject(pr);
        String body      = buildBody(pr);

        // TODO: Replace with real delivery via JavaMailSender or SendGrid.
        System.out.println(String.format(
            "[EMAIL] To: %s | Subject: %s | Body: %s",
            recipient, subject, body
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

    private String buildBody(PullRequest pr) {
        return switch (pr.getStatus()) {
            case APPROVED -> String.format(
                "Good news! Your pull request \"%s\" has been approved. You can now merge.",
                pr.getTitle());
            case CHANGES_REQUESTED -> String.format(
                "Your pull request \"%s\" has changes requested. Please review the feedback and update your code.",
                pr.getTitle());
            case MERGED -> String.format(
                "Your pull request \"%s\" has been successfully merged.",
                pr.getTitle());
            default -> String.format(
                "Your pull request \"%s\" status has changed to: %s",
                pr.getTitle(), pr.getStatus());
        };
    }
}
