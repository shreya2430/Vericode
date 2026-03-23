package com.vericode.model;

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
