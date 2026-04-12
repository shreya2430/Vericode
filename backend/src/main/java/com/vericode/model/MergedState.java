package com.vericode.model;

/**
 * State Pattern: concrete terminal state representing a fully merged PR.
 *
 * WHY THIS EXISTS:
 * Once a PR is merged it is complete. No further actions are meaningful or
 * allowed. This class is the end stop of the lifecycle — every method throws
 * IllegalStateException to make it explicit that a merged PR cannot be
 * reopened, re-approved, or merged again.
 *
 * VALID TRANSITIONS:
 *   none — this is a terminal state
 *
 * INVALID TRANSITIONS (all throw IllegalStateException):
 *   submit(), approve(), requestChanges(), merge()
 */

public class MergedState implements PRState{
    @Override
    public void submit(PullRequest pr) {
        throw new IllegalStateException("PR is already merged. No further actions allowed.");
    }
    @Override
    public void approve(PullRequest pr, String reviewer) {
        throw new IllegalStateException("PR is already merged. No further actions allowed.");
    }
    @Override
    public void requestChanges(PullRequest pr, String reviewer) {
        throw new IllegalStateException("PR is already merged. No further actions allowed.");
    }
    @Override
    public void merge(PullRequest pr, String reviewer) {
        throw new IllegalStateException("PR is already merged. No further actions allowed.");
    }
}
