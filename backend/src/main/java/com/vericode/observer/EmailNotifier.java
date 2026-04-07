package com.vericode.observer;

import com.vericode.model.PullRequest;
import com.vericode.model.PRStatus;
import com.vericode.notification.EmailChannel;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern: Concrete Observer #2: Email Notification.
 *
 * Reacts to PR status changes and sends emails for statuses that require
 * author awareness: APPROVED, CHANGES_REQUESTED, and MERGED. DRAFT and
 * IN_REVIEW are internal transitions and do not warrant an email.
 *
 * Delivery path:
 *   NotificationService.notifyAll(pr)
 *     → EmailNotifier.onStatusChange(pr)      [Observer: filters and builds the message]
 *       → EmailChannel.send(email, ...)        [Bridge: sends via JavaMailSender → Mailtrap]
 *
 * The recipient passed to EmailChannel is the PR author's email address.
 */
@Component
public class EmailNotifier implements PRStatusObserver {

    private final EmailChannel emailChannel;

    public EmailNotifier(EmailChannel emailChannel) {
        this.emailChannel = emailChannel;
    }

    @Override
    public void onStatusChange(PullRequest pr) {
        if (!shouldEmail(pr.getStatus())) {
            return;
        }

        String recipient = pr.getAuthor().getEmail();
        String subject   = buildSubject(pr);
        String body      = buildBody(pr, pr.getAuthor().getName());

        emailChannel.send(recipient, subject, body);
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
