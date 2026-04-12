package com.vericode.model;

/**
 * State Pattern: concrete state representing a PR that has not yet been submitted.
 *
 * WHY THIS EXISTS:
 * A Draft PR has only one valid action — submit. Approving, requesting changes,
 * or merging a draft makes no sense in the review lifecycle. This class enforces
 * that by allowing only submit() to proceed and throwing IllegalStateException
 * for everything else.
 *
 * VALID TRANSITIONS:
 *   submit() → IN_REVIEW
 *
 * INVALID TRANSITIONS (all throw IllegalStateException):
 *   approve(), requestChanges(), merge()
 */

public class DraftState implements PRState{
    @Override
    public void submit(PullRequest pr) {
        pr.setStatus(PRStatus.IN_REVIEW);
        pr.setState(new InReviewState());
    }
    @Override
    public void approve(PullRequest pr, String reviewer) {
        throw new IllegalStateException("Cannot approve a Draft. Submit it first.");
    }
    @Override
    public void requestChanges(PullRequest pr, String reviewer) {
        throw new IllegalStateException("Cannot request changes on a Draft.");
    }
    @Override
    public void merge(PullRequest pr, String reviewer) {
        throw new IllegalStateException("Cannot merge a Draft.");
    }
}
