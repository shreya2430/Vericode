package com.vericode.model;

/**
 * State Pattern: concrete state representing a PR that has been sent back for fixes.
 *
 * WHY THIS EXISTS:
 * When a reviewer requests changes, the author needs to fix the code and
 * resubmit. The only valid action from this state is submit() which loops
 * the PR back to IN_REVIEW. Approving or merging without resubmitting is
 * not allowed — the reviewer must re-examine the updated code first.
 *
 * VALID TRANSITIONS:
 *   submit() → IN_REVIEW
 *
 * INVALID TRANSITIONS (all throw IllegalStateException):
 *   approve(), requestChanges(), merge()
 */

public class ChangesRequestedState implements PRState{
    @Override
    public void submit(PullRequest pr) {
        pr.setStatus(PRStatus.IN_REVIEW);
        pr.setState(new InReviewState());
    }
    @Override
    public void approve(PullRequest pr, String reviewer) {
        throw new IllegalStateException("Changes were requested. Author must resubmit first.");
    }
    @Override
    public void requestChanges(PullRequest pr, String reviewer) {
        throw new IllegalStateException("Changes already requested. Waiting for author to resubmit.");
    }
    @Override
    public void merge(PullRequest pr, String reviewer) {
        throw new IllegalStateException("Cannot merge. Changes are still pending.");
    }
}
