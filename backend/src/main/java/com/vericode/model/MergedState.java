package com.vericode.model;

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
