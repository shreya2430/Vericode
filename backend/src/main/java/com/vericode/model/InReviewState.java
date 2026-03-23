package com.vericode.model;

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
