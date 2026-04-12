package com.vericode.model;

/**
 * State Pattern: concrete state representing a PR that has passed review.
 *
 * WHY THIS EXISTS:
 * An approved PR has one remaining action — merge it into the codebase.
 * Re-approving or resubmitting makes no sense at this stage. The only
 * exception is that a reviewer can still request changes even after approval
 * if they catch something before the merge happens.
 *
 * VALID TRANSITIONS:
 *   merge() → MERGED
 *
 * INVALID TRANSITIONS (all throw IllegalStateException):
 *   submit(), approve(), requestChanges()
 */

public class ApprovedState implements PRState{
    @Override
    public void submit(PullRequest pr) {
        throw new IllegalStateException("PR is already approved.");
    }
    @Override
    public void approve(PullRequest pr, String reviewer) {
        throw new IllegalStateException("PR is already approved.");
    }
    @Override
    public void requestChanges(PullRequest pr, String reviewer) {
       // pr.setStatus(PRStatus.CHANGES_REQUESTED);
       // pr.setState(new ChangesRequestedState());
        throw new IllegalStateException("PR is already approved. It can only be merged.");

    }
    @Override
    public void merge(PullRequest pr, String reviewer) {
        pr.setStatus(PRStatus.MERGED);
        pr.setState(new MergedState());
        System.out.println(reviewer + " merged PR #" + pr.getId());
    }
}
