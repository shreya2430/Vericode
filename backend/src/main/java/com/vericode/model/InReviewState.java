package com.vericode.model;

/**
 * State Pattern: concrete state representing a PR that is actively under review.
 *
 * WHY THIS EXISTS:
 * This is the only state where a reviewer can take action. From here the PR
 * can either be approved (good to merge) or sent back for changes. Submitting
 * again or merging directly without approval are both invalid from this stage.
 *
 * VALID TRANSITIONS:
 *   approve()        → APPROVED
 *   requestChanges() → CHANGES_REQUESTED
 *
 * INVALID TRANSITIONS (all throw IllegalStateException):
 *   submit(), merge()
 */

public class InReviewState implements PRState{
    @Override
    public void submit(PullRequest pr) {
        throw new IllegalStateException("PR is already in review.");
    }
    @Override
    public void approve(PullRequest pr, String reviewer) {
        pr.setStatus(PRStatus.APPROVED);
        pr.setState(new ApprovedState());
        System.out.println(reviewer + " approved PR #" + pr.getId());
    }
    @Override
    public void requestChanges(PullRequest pr, String reviewer) {
        pr.setStatus(PRStatus.CHANGES_REQUESTED);
        pr.setState(new ChangesRequestedState());
        System.out.println(reviewer + " requested changes on PR #" + pr.getId());
    }
    @Override
    public void merge(PullRequest pr, String reviewer) {
        throw new IllegalStateException("Cannot merge. PR must be approved first.");
    }
}
