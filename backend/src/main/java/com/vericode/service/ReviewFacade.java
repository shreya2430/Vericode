package com.vericode.service;

import com.vericode.model.PullRequest;
import com.vericode.model.Review;
import com.vericode.repository.PullRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Facade Pattern: single entry point for all review lifecycle actions.
 *
 * WHY THIS EXISTS:
 * Before the Facade, ReviewController was doing everything inline: loading PRs,
 * creating Commands, pushing to history, saving, and notifying. That mixed HTTP
 * concerns (parsing request bodies, returning status codes) with business logic.
 * The Facade pulls all business logic into one place. The controller's only job
 * is now to parse the HTTP request and map the result to an HTTP response.
 *
 * WHAT IT COORDINATES (all three other groups):
 *   - Group 1: PullRequestRepository (load and save)
 *   - Group 2: (no direct check calls here — checks happen at PR creation time)
 *   - Group 3: State pattern (submit/merge), Command pattern (approve/reject/comment),
 *              CommandHistory (push/undo)
 *   - Group 4: NotificationService (notifyAll after every status change)
 *
 * CALL ORDER INSIDE EACH METHOD:
 *   1. Load PR from DB — fail fast if it doesn't exist
 *   2. Execute the action (state transition or command)
 *   3. Save — persist the new status before notifying
 *   4. Notify — observers always read the already-persisted state
 *
 * Note: comment() intentionally skips notifyAll because commenting does not
 * change PR status. The EmailNotifier filters by status anyway, but skipping
 * here keeps the contract explicit: notifyAll is for status changes only.
 */
@Service
public class ReviewFacade {

    private final PullRequestRepository prRepo;
    private final CommandHistory commandHistory;
    private final NotificationService notificationService;

    public ReviewFacade(PullRequestRepository prRepo,
                        CommandHistory commandHistory,
                        NotificationService notificationService) {
        this.prRepo = prRepo;
        this.commandHistory = commandHistory;
        this.notificationService = notificationService;
    }

    /**
     * Transitions a DRAFT PR to IN_REVIEW via the State pattern.
     * Throws IllegalStateException if the PR is not in DRAFT.
     */
    public PullRequest submit(Long prId) {
        PullRequest pr = load(prId);
        pr.getState().submit(pr);
        prRepo.save(pr);
        notificationService.notifyAll(pr);
        return pr;
    }

    /**
     * Executes an ApproveCommand, pushes it to history, and notifies observers.
     * Throws IllegalStateException if the PR is not in IN_REVIEW.
     */
    public ReviewActionResult approve(Long prId, String reviewer) {
        PullRequest pr = load(prId);

        // Author cannot approve their own PR — this is a common rule in code review processes
        if (pr.getAuthor().getUsername().equals(reviewer))
            throw new IllegalStateException("Author cannot approve their own PR");

        ReviewCommand cmd = new ApproveCommand(pr, reviewer);
        cmd.execute();
        commandHistory.push(cmd);
        prRepo.save(pr);
        notificationService.notifyAll(pr);
        return new ReviewActionResult(pr.getStatus().toString(), cmd.getDescription());
    }

    /**
     * Executes a RejectCommand (request changes), pushes it to history, and notifies observers.
     * Throws IllegalStateException if the PR is not in IN_REVIEW.
     */
    public ReviewActionResult requestChanges(Long prId, String reviewer) {
        PullRequest pr = load(prId);

        // Author cannot request changes on their own PR — this is a common rule in code review processes
        if (pr.getAuthor().getUsername().equals(reviewer))
            throw new IllegalStateException("Author cannot request changes on their own PR");

        ReviewCommand cmd = new RejectCommand(pr, reviewer);
        cmd.execute();
        commandHistory.push(cmd);
        prRepo.save(pr);
        notificationService.notifyAll(pr);
        return new ReviewActionResult(pr.getStatus().toString(), cmd.getDescription());
    }

    /**
     * Merges an APPROVED PR via the State pattern and notifies observers.
     * Throws IllegalStateException if the PR is not in APPROVED.
     */
    public ReviewActionResult merge(Long prId, String reviewer) {
        PullRequest pr = load(prId);

        // Author cannot merge their own PR — this is a common rule in code review processes
        if (pr.getAuthor().getUsername().equals(reviewer))
            throw new IllegalStateException("Author cannot merge their own PR");

        pr.getState().merge(pr, reviewer);
        prRepo.save(pr);
        notificationService.notifyAll(pr);
        return new ReviewActionResult(
                pr.getStatus().toString(),
                reviewer + " merged PR #" + prId
        );
    }

    /**
     * Executes a CommentCommand and pushes it to history.
     * Does NOT call notifyAll — commenting does not change PR status.
     */
    public String comment(Long prId, String author, int lineNumber, String content) {
        PullRequest pr = load(prId);
        Review review = new Review(author);
        ReviewCommand cmd = new CommentCommand(pr, review, author, lineNumber, content);
        cmd.execute();
        commandHistory.push(cmd);
        prRepo.save(pr);
        return cmd.getDescription();
    }

    /**
     * Pops the most recent command from history and calls its undo().
     * Throws IllegalStateException if history is empty.
     */
    public void undo() {
        PullRequest pr = commandHistory.undo();
        prRepo.save(pr);
    }

    /**
     * Returns all command descriptions in the history stack (most recent first).
     */
    public List<String> getHistory() {
        return commandHistory.getHistory();
    }

    // --- private helpers ---

    private PullRequest load(Long prId) {
        return prRepo.findById(prId)
                .orElseThrow(() -> new RuntimeException("PR not found: " + prId));
    }
}
