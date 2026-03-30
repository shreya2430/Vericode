package com.vericode.model;

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
