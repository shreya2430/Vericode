package com.vericode.model;

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
